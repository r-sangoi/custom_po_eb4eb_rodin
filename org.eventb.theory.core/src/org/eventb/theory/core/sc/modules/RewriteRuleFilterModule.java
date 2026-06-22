/*******************************************************************************
 * Copyright (c) 2011, 2020 University of Southampton and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.sc.modules;

import static org.eventb.theory.core.TheoryAttributes.FORMULA_ATTRIBUTE;
import static org.eventb.theory.core.util.CoreUtilities.checkAgainstTypeParameters;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.DefaultVisitor;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.sc.GraphProblem;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.state.ILabelSymbolInfo;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.theory.core.IRewriteRule;
import org.eventb.theory.core.TheoryAttributes;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.TheoryGraphProblem;
import org.rodinp.core.IRodinElement;

/**
 * 
 * @author maamria
 * 
 */
public class RewriteRuleFilterModule extends RuleFilterModule<IRewriteRule> {

	private final IModuleType<RewriteRuleFilterModule> MODULE_TYPE = SCCore.getModuleType(TheoryPlugin.PLUGIN_ID
			+ ".rewriteRuleFilterModule");

	@Override
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	@Override
	protected IRewriteRule getRule(IRodinElement element) {
		return (IRewriteRule) element;
	}

	@Override
	protected boolean furtherCheck(IRewriteRule rule, ILabelSymbolInfo symbolInfo, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		// check formula attribute
		if (!checkFormulaAttribute(rule, symbolInfo, repository, monitor)){
			return false;
		}
		// Check complete attribute
		if (!checkCompleteAttribute(rule, symbolInfo, repository, monitor)) {
			return false;
		}
		return true;
	}

	private boolean checkFormulaAttribute(IRewriteRule rule, ILabelSymbolInfo symbolInfo,
			ISCStateRepository repository, IProgressMonitor monitor) throws CoreException {
		if (!rule.hasFormula()) {
			createProblemMarker(rule, TheoryAttributes.FORMULA_ATTRIBUTE, TheoryGraphProblem.MissingFormulaError);
			return false;
		}
		// parse the lhs pattern
		Formula<?> lhsForm = ModulesUtils.parseFormulaPattern(rule, repository.getFormulaFactory(), this);
		if (lhsForm == null) {
			return false;
		}
		ITypeEnvironment typeEnvironment = repository.getTypeEnvironment();
		lhsForm = ModulesUtils.checkFormula(rule, lhsForm, typeEnvironment, this);
		if (lhsForm == null) {
			return false;
		}
		// First check that there is no given type besides type parameters
		if (!checkAgainstTypeParameters(rule, lhsForm, typeEnvironment, this)) {
			return false;
		}
		// then check all idents of the lhs formula were actually declared BUG FIXED.
		for (FreeIdentifier identifier : lhsForm.getFreeIdentifiers()) {
			if (!typeEnvironment.contains(identifier.getName())) {
				createProblemMarker(rule, FORMULA_ATTRIBUTE, GraphProblem.UndeclaredFreeIdentifierError,
						identifier.getName());
				return false;
			}
		}
		// lhs is a free identifier or predicate variable
		if (lhsForm instanceof FreeIdentifier) {
			createProblemMarker(rule, FORMULA_ATTRIBUTE, TheoryGraphProblem.LHSIsIdentErr);
			return false;
		}
		// lhs does not contain structured predicates
		WDStrictChecker checker = new WDStrictChecker();
		lhsForm.accept(checker);
		if (!checker.wdStrict) {
			createProblemMarker(rule, FORMULA_ATTRIBUTE, TheoryGraphProblem.LHS_IsNotWDStrict);
			return false;
		}
		return true;
	}

	private boolean checkCompleteAttribute(IRewriteRule rule, ILabelSymbolInfo symbolInfo,
			ISCStateRepository repository, IProgressMonitor monitor) throws CoreException {
		boolean isComp = false;
		if (!rule.hasComplete()) {
			createProblemMarker(rule, TheoryAttributes.COMPLETE_ATTRIBUTE, TheoryGraphProblem.CompleteUndefWarning);
			// default is incomplete
			isComp = false;
		} else {
			isComp = rule.isComplete();
		}
		symbolInfo.setAttributeValue(TheoryAttributes.COMPLETE_ATTRIBUTE, isComp);
		return true;
	}
	
	static final class WDStrictChecker extends DefaultVisitor {

		private boolean wdStrict = true;

		/**
		 * Returns whether the check reached the conclusion that the visited
		 * formula may not be WD strict.
		 * 
		 * @return whether the visited formula is WD strict
		 */
		public boolean isWdStrict() {
			return wdStrict;
		}

		@Override
		public boolean enterEXISTS(QuantifiedPredicate pred) {
			wdStrict = false;
			return false;
		}

		@Override
		public boolean enterFORALL(QuantifiedPredicate pred) {
			wdStrict = false;
			return false;
		}

		@Override
		public boolean enterLAND(AssociativePredicate pred) {
			wdStrict = false;
			return false;
		}

		@Override
		public boolean enterLOR(AssociativePredicate pred) {
			wdStrict = false;
			return false;
		}

		@Override
		public boolean enterLIMP(BinaryPredicate pred) {
			wdStrict = false;
			return false;
		}

		@Override
		public boolean enterLEQV(BinaryPredicate pred) {
			wdStrict = false;
			return false;
		}

	}
}
