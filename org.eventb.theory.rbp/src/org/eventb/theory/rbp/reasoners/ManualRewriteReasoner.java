/*******************************************************************************
 * Copyright (c) 2010, 2021 University of Southampton and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     University of Southampton - initial API and implementation
 *     Systerel - repair missing project key
 *******************************************************************************/
package org.eventb.theory.rbp.reasoners;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.ISpecialization;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.extensions.pm.Matcher;
import org.eventb.core.seqprover.IHypAction;
import org.eventb.core.seqprover.IHypAction.ISelectionHypAction;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerInputReader;
import org.eventb.core.seqprover.IReasonerInputWriter;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.SerializeException;
import org.eventb.theory.core.IGeneralRule;
import org.eventb.theory.core.ISCRewriteRule;
import org.eventb.theory.internal.rbp.reasoners.input.IPRMetadata;
import org.eventb.theory.internal.rbp.reasoners.input.RewriteInput;
import org.eventb.theory.rbp.plugin.RbPPlugin;
import org.eventb.theory.rbp.rulebase.BaseManager;
import org.eventb.theory.rbp.rulebase.IPOContext;
import org.eventb.theory.rbp.rulebase.basis.IDeployedRewriteRule;
import org.eventb.theory.rbp.utils.ProverUtilities;

/**
 * <p>
 * An implementation of a manual rewrite reasoner for the rule-based prover.
 * </p>
 * <p>
 * <i>htson</i>: This has been re-implemented based on the original version 1.0
 * and the (now removed) {org.eventb.theory.rbp.reasoning.ManualRewriter} class.
 * </p>
 * 
 * @author maamria
 * @author htson - re-implemented as a context dependent reasoner, added
 *         WD-subgoal
 * @version 2.0.2
 * @see RewriteInput
 * @see RewriteRuleContent
 * @since 3.1.0
 */
public class ManualRewriteReasoner extends AbstractContextDependentReasoner
		implements IReasoner {

	public static final String REASONER_ID = RbPPlugin.PLUGIN_ID
			+ ".manualRewriteReasoner";

	/*
	 * (non-Javadoc)
	 * 
	 * @see IReasoner#getReasonerID()
	 */
	public String getReasonerID() {
		return REASONER_ID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IReasoner#apply(IProverSequent, IReasonerInput, IProofMonitor)
	 */
	public IReasonerOutput apply(IProverSequent sequent,
			IReasonerInput reasonerInput, IProofMonitor pm) {
		// PRECONDITION
		assert reasonerInput instanceof RewriteInput;

		// Get information from the reasoner input
		RewriteInput input = (RewriteInput) reasonerInput;
		Predicate hyp = input.getPredicate();
		if (hyp != null && hyp.getFactory() != sequent.getFormulaFactory()) {
			try {
				hyp = hyp.translate(sequent.getFormulaFactory());
			} catch (IllegalArgumentException e) {
				return ProverFactory.reasonerFailure(this, input,
						"Rewrite hypothesis has a mathematical language incompatible with the one of the proof sequent");
			}
		}
		IPosition position = input.getPosition();
		IPRMetadata prMetadata = input.getPRMetadata();
		final String projectName = prMetadata.getProjectName();
		final String theoryName = prMetadata.getTheoryName();
		final String ruleName = prMetadata.getRuleName();

		// Get the PO Context for the input sequent.
		IPOContext context = ProverUtilities.getContext(sequent);
		if (context == null) {
			return ProverFactory.reasonerFailure(this, input,
					"Cannot determine the context of the sequent");
		}
		
		// Check if the it is goal or hypothesis rewriting.
		boolean isGoal = (hyp == null);
		Predicate predicate;
		if (isGoal) { // Goal rewrite
			predicate = sequent.goal();
		} else { // Hypothesis rewrite
			predicate = hyp;
			if (!sequent.containsHypothesis(hyp)) {
				return ProverFactory.reasonerFailure(this, input,
						"Nonexistent hypothesis: " + hyp);
			}
		}

		// Get the subformula at the specified location of the predicate
		Formula<?> formula = predicate.getSubFormula(position);
		if (formula == null) {
			return ProverFactory.reasonerFailure(this, input,
					"Invalid position " + position
							+ (isGoal ? " for goal " : " for hypothesis ")
							+ predicate);
		}

		// Get the rewrite rule (given the meta-data) from the current context
		BaseManager manager = BaseManager.getDefault();
		IGeneralRule rule = manager.getRewriteRule(false, projectName, ruleName,
				theoryName, context);
		if (rule == null) {
			return ProverFactory.reasonerFailure(this, input,
					"Cannot find rewrite rule " + projectName + "::"
							+ theoryName + "::" + ruleName
							+ " within the given context for "
							+ formula.getClass());
		}

		// Get the content of the found rewriting rule.
		FormulaFactory factory = formula.getFactory();
		IRewriteRuleContent ruleContent;
		if (rule instanceof IDeployedRewriteRule) {	
			ruleContent = new RewriteRuleContent((IDeployedRewriteRule) rule);
		} else {
			try {
				ruleContent = new RewriteRuleContent((ISCRewriteRule) rule,
						factory);
			} catch (CoreException e) {
				return ProverFactory.reasonerFailure(this, input,
						"Error fetching content of " + projectName + "::"
								+ theoryName + "::" + ruleName
								+ " within the given context");
			}
		}

		// calculate binding between rule lhs and subformula
		Formula<?> ruleLhs = ruleContent.getLeftHandSide();
		if (ruleLhs.isTranslatable(factory)) {
			ruleLhs = ruleLhs.translate(factory);
		} else {
			return null;
		}
		ISpecialization initialSpecialization = factory.makeSpecialization();
		ISpecialization specialization = Matcher.match(initialSpecialization,
				formula, ruleLhs);
		if (specialization == null) {
			return null;
		}

		Predicate[] conditions = ruleContent.getConditions();
		Formula<?>[] ruleRhses = ruleContent.getRightHandSides();
		boolean additionalAntecedentRequired = ruleContent
				.additionalAntecendentRequired();

		// may need to make an extra antecedent if rule incomplete
		IAntecedent[] antecedents = (additionalAntecedentRequired ? new IAntecedent[conditions.length + 2]
				: new IAntecedent[conditions.length + 1]);
		List<Predicate> allConditions = (additionalAntecedentRequired ? new ArrayList<Predicate>()
				: null);
		
		// Add the WD sub-goal
		Predicate wdPredicate = ProverUtilities.getWDPredicate(
				specialization);
		Set<Predicate> wdHypotheses = Collections.singleton(wdPredicate);
		ISelectionHypAction selectWDHyp = ProverFactory.makeSelectHypAction(wdHypotheses);
		antecedents[0] =ProverFactory.makeAntecedent(wdPredicate);

		for (int i = 0; i != conditions.length; i++) {
			// get the condition
			Predicate condition = conditions[i].specialize(specialization);
			// if rule is incomplete keep it till later as we will make negation
			// of disjunction of all conditions
			if (additionalAntecedentRequired)
				allConditions.add(condition);

			// get the new sub-formula
			Formula<?> rhsFormula = ruleRhses[i].specialize(specialization);
			// apply the rewriting at the given position
			Predicate newPred = predicate.rewriteSubFormula(position,
					rhsFormula);
			Predicate goal = (isGoal ? newPred : null);

			// add interesting hyps only (no T)
			Set<Predicate> addedHyps = new HashSet<Predicate>();
			List<IHypAction> hypActions = new ArrayList<IHypAction>();

			// add and select the WDHyp
			addedHyps.add(wdPredicate);
			hypActions.add(selectWDHyp);
			
			
			// If the condition is not T then add the condition as a hypothesis
			// and select it.
			if (!condition.equals(factory.makeLiteralPredicate(Predicate.BTRUE,
					null))) {
				addedHyps.add(condition);
				hypActions.add(ProverFactory.makeSelectHypAction(Collections
						.singleton(condition)));
			}

			// If the rewriting a hypothesis then:
			// 1. Hide the original hypothesis.
			// 2. If the rewritten hypothesis is not T then add the rewritten
			// hypothesis and select it.
			if (!isGoal) {
				hypActions.add(ProverFactory.makeHideHypAction(Collections
						.singleton(predicate)));
				if (!newPred.equals(factory.makeLiteralPredicate(
						Predicate.BTRUE, null))) {
					addedHyps.add(newPred);
					hypActions
							.add(ProverFactory.makeSelectHypAction(Collections
									.singleton(newPred)));
				}
			}
			addedHyps = addedHyps.size() > 0 ? addedHyps : null;
			antecedents[i+1] = ProverFactory.makeAntecedent(goal, addedHyps,
					null, hypActions);
		}

		if (additionalAntecedentRequired) {
			Predicate negOfDisj = factory.makeUnaryPredicate(
					Formula.NOT,
					allConditions.size() == 1 ? allConditions.get(0) : factory
							.makeAssociativePredicate(Formula.LOR,
									allConditions, null), null);
			Set<Predicate> addedHyps = new HashSet<Predicate>();
			addedHyps.add(wdPredicate);
			addedHyps.add(negOfDisj);
			
			Predicate goal = (isGoal ? predicate : null);
			antecedents[conditions.length+1] = ProverFactory.makeAntecedent(goal,
					addedHyps, ProverFactory
							.makeSelectHypAction(addedHyps));
		}
		String displayName = ruleContent.getDescription();
		return ProverFactory.makeProofRule(this, input, isGoal ? predicate
				: null, hyp, displayName + " on " + (isGoal ? "goal" : hyp), antecedents);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IReasoner#serializeInput(IReasonerInput, IReasonerInputWriter)
	 */
	public void serializeInput(IReasonerInput input, IReasonerInputWriter writer)
			throws SerializeException {
		((RewriteInput) input).serialise(writer);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IReasoner#deserializeInput(IReasonerInputReader)
	 */
	public IReasonerInput deserializeInput(IReasonerInputReader reader)
			throws SerializeException {
		return deserializeInput(reader, false);
	}

	private IReasonerInput deserializeInput(IReasonerInputReader reader,
			boolean repair) throws SerializeException {
		return new RewriteInput(reader);
	}

}
