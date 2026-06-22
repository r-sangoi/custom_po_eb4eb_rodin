/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.pog.modules;

import static org.eventb.core.seqprover.eventbExtensions.DLib.True;
import static org.eventb.core.seqprover.eventbExtensions.DLib.makeImp;

import java.util.ArrayList;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPOPredicateSet;
import org.eventb.core.IPORoot;
import org.eventb.core.IPOSource;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.extensions.maths.AstUtilities;
import org.eventb.core.pog.IPOGHint;
import org.eventb.core.pog.IPOGSource;
import org.eventb.core.pog.POGCore;
import org.eventb.core.pog.state.IPOGStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.internal.core.pog.POGNatureFactory;
import org.eventb.theory.core.ISCProofRulesBlock;
import org.eventb.theory.core.ISCRewriteRule;
import org.eventb.theory.core.ISCRewriteRuleRightHandSide;
import org.eventb.theory.core.TheoryAttributes;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.rodinp.core.IRodinElement;

/**
 * @author maamria
 * 
 */
@SuppressWarnings("restriction")
public class RewriteRulePOGModule extends UtilityPOGModule {

	private final IModuleType<RewriteRulePOGModule> MODULE_TYPE = POGCore
			.getModuleType(TheoryPlugin.PLUGIN_ID + ".rewriteRulePOGModule"); //$NON-NLS-1$

	protected ITypeEnvironment typeEnvironment;
	protected POGNatureFactory natureFactory;

	private final static String RULE_RHS_WD_SUFFIX = "/WD-S/";
	private final static String RULE_C_WD_SUFFIX = "/WD-C/";
	private final static String RULE_S_SUFFIX = "/S/";
	private final static String RULE_C_SUFFIX = "/C";

	private final static String RULE_C_WD_DESC = "Rule Condition WD-preservation";
	private final static String RULE_RHS_WD_DESC = "Rule RHS WD-preservation";
	private final static String RULE_SOUNDNESS_DESC = "Rule Soundness";
	private final static String RULE_COMPLETENESS_DESC = "Rule Completeness";

	@Override
	public void initModule(IRodinElement element,
			IPOGStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		super.initModule(element, repository, monitor);
		typeEnvironment = repository.getTypeEnvironment();
		natureFactory = POGNatureFactory.getInstance();
	}
	
	public void process(IRodinElement element, IPOGStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		IPORoot target = repository.getTarget();
		ISCProofRulesBlock rulesBlock = (ISCProofRulesBlock) element;
		ISCRewriteRule[] scRules = rulesBlock.getRewriteRules();
		IPOPredicateSet hyp = target
			.getPredicateSet(TypeParametersPOGModule.ABS_HYP_NAME);
		for (ISCRewriteRule rule : scRules) {
			if(rule.hasDefinitionalAttribute() &&
					rule.isDefinitional()){
				continue;
			}
			if (!rule.hasAttribute(TheoryAttributes.FORMULA_ATTRIBUTE)) {
				continue;
			}
			Formula<?> lhs = rule.getSCFormula(factory, typeEnvironment);
			// if lhs contains predicate variables, DONOT generate POs
//			if (lhs.hasPredicateVariable()) {
//				continue;
//			}
			Predicate lhsWD = getDWDCondition(lhs);
			
			ArrayList<Predicate> allConditions = new ArrayList<Predicate>();
			ArrayList<Predicate> wdAllConditions = new ArrayList<Predicate>();
			
			String ruleName = rule.getLabel();
			
			IPOGSource[] sources = new IPOGSource[] { makeSource(
					IPOSource.DEFAULT_ROLE, rule), makeSource(IPOSource.DEFAULT_ROLE, rule.getSource()) };
			
			ISCRewriteRuleRightHandSide[] scRHSs = rule.getRuleRHSs();
			for (ISCRewriteRuleRightHandSide rhs : scRHSs) {
				String rhsLabel = rhs.getLabel();
				Formula<?> rhsForm = rhs
						.getSCFormula(factory, typeEnvironment);
				Predicate rhsWD = getDWDCondition(rhsForm);
				Predicate condition = rhs
						.getPredicate(typeEnvironment);
				// since we will make a disjunction, disregard bottom?
				allConditions.add(condition);
				
				Predicate conditionWD = getDWDCondition(condition);
				
				wdAllConditions.add(conditionWD);
				
				Predicate soundnessPredicate = null;
				if (lhs instanceof Expression) {
					soundnessPredicate = factory.makeRelationalPredicate(
							Formula.EQUAL, (Expression) lhs,
							(Expression) rhsForm, null);
				} else {
					soundnessPredicate = factory.makeBinaryPredicate(
							Formula.LEQV, (Predicate) lhs, (Predicate) rhsForm,
							null);
				}
				// -------------------------------------------------------
				// --------------------------WD-Preservation of Condition
				// -------------------------------------------------------
				// lhsWD => conditionWD
				if (!isTrivial(conditionWD)) {
					Predicate poPredicate = makeImp(lhsWD, conditionWD);
					String poName = ruleName + RULE_C_WD_SUFFIX + rhsLabel;
					createPO(target, poName,
							natureFactory.getNature(RULE_C_WD_DESC),
							hyp, EMPTY_PREDICATES,
							makePredicate(makeClosedPredicate(poPredicate, typeEnvironment), rule.getSource()),
							sources, new IPOGHint[]{getLocalHypothesisSelectionHint(target, poName, hyp)},
							true, monitor);
				} else {
					if (DEBUG_TRIVIAL)
						debugTraceTrivial("WD-C");
				}
				// -------------------------------------------------------
				// ----------------------------WD-Preservation of RHS
				// -------------------------------------------------------
				// lhsWD & conditionWD & condition => rhsWD
				if (!isTrivial(rhsWD)) {
					Predicate poPredicate = AstUtilities.conjunctPredicates(lhsWD, conditionWD, condition);
					if(poPredicate.equals(True(factory))){
						poPredicate = rhsWD;
					}
					else{
						poPredicate = makeImp(poPredicate, rhsWD);
					}
					String poName = ruleName + RULE_RHS_WD_SUFFIX + rhsLabel;
					createPO(target, poName,
							natureFactory.getNature(RULE_RHS_WD_DESC),
							hyp, EMPTY_PREDICATES,
							makePredicate(makeClosedPredicate(poPredicate, typeEnvironment), rule.getSource()), sources,
							 new IPOGHint[]{getLocalHypothesisSelectionHint(target, poName, hyp)},
							true, monitor);
				} else {
					if (DEBUG_TRIVIAL)
						debugTraceTrivial("WD-RHS");
				}
				// -------------------------------------------------------
				// ------------------------------Soundness of RHS
				// -------------------------------------------------------
				// lhsWD & conditionWD & condition & rhsWD => lhs = rhs
				if (!isTrivial(soundnessPredicate)) {
					Predicate poPredicate = AstUtilities.conjunctPredicates(lhsWD, conditionWD, condition, rhsWD);
					if(poPredicate.equals(True(factory))){
						poPredicate = soundnessPredicate;
					}
					else{
						poPredicate = makeImp(poPredicate, soundnessPredicate);
					}
					String poName = ruleName + RULE_S_SUFFIX + rhsLabel;
					
					createPO(
							target,
							poName,
							POGNatureFactory.getInstance().getNature(RULE_SOUNDNESS_DESC),
							hyp,
							EMPTY_PREDICATES,
							makePredicate(makeClosedPredicate(poPredicate, typeEnvironment), rule.getSource()),
							sources, new IPOGHint[]{getLocalHypothesisSelectionHint(target, poName, hyp)},
							true, monitor);
				} else {
					if (DEBUG_TRIVIAL)
						debugTraceTrivial("S");
				}

			}
			// -------------------------------------------------------
			// ----------------------------------Completeness of Rule
			// -------------------------------------------------------
			// A conditionWD => V condition
			if (rule.isComplete() && allConditions.size() > 0) {
				String poName = ruleName + RULE_C_SUFFIX;
				Predicate goal = allConditions.size() == 1 ? allConditions
						.get(0) : factory.makeAssociativePredicate(Formula.LOR,
						allConditions, null);
			
				if(!isTrivial(goal)){
					Predicate hyps = AstUtilities.conjunctPredicates(wdAllConditions, factory);
					Predicate poPredicate = makeImp(hyps, goal);
					createPO(target, poName,
							natureFactory.getNature(RULE_COMPLETENESS_DESC),
							hyp, EMPTY_PREDICATES,
							makePredicate(makeClosedPredicate(poPredicate, typeEnvironment), rule.getSource()), sources,
							new IPOGHint[]{getLocalHypothesisSelectionHint(target, poName, hyp)},
							true, monitor);
				}
			}
		}

	}
	
	@Override
	public void endModule(IRodinElement element,
			IPOGStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		typeEnvironment = null;
		natureFactory = null;
		super.endModule(element, repository, monitor);
	}

	@Override
	public IModuleType<?> getModuleType() {
		// TODO Auto-generated method stub
		return MODULE_TYPE;
	}
}
