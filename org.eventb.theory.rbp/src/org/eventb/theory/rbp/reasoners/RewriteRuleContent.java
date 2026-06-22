/*******************************************************************************
 * Copyright (c) 2016 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University of Southampton - initial API and implementation
 *******************************************************************************/

package org.eventb.theory.rbp.reasoners;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.theory.core.ISCRewriteRule;
import org.eventb.theory.core.ISCRewriteRuleRightHandSide;
import org.eventb.theory.rbp.rulebase.basis.IDeployedRewriteRule;
import org.eventb.theory.rbp.rulebase.basis.IDeployedRuleRHS;
import org.eventb.theory.rbp.utils.ProverUtilities;

/**
 * <p>
 *
 * </p>
 *
 * @author htson
 * @version
 * @see
 * @since
 */
public class RewriteRuleContent implements IRewriteRuleContent {

	private Formula<?> lhsFormula;
	private Predicate[] conditions;
	private Formula<?>[] rhsFormulae;
	private String description;
	private boolean additionalAntecedentsRequired;

	/**
	 * @param rule
	 */
	public RewriteRuleContent(IDeployedRewriteRule rule) {
		this.lhsFormula = rule.getLeftHandSide();
		List<IDeployedRuleRHS> rhses = rule.getRightHandSides();
		int size = rhses.size();
		this.additionalAntecedentsRequired = !rule.isComplete()
				&& rule.isConditional();
		this.conditions = new Predicate[size];
		this.rhsFormulae = new Formula<?>[size];
		int i = 0;
		for (IDeployedRuleRHS rhs : rhses) {
			conditions[i] = rhs.getCondition();
			rhsFormulae[i] = rhs.getRHSFormula();
			i++;
		}
		this.description = rule.getDescription();
	}

	/**
	 * @param rule
	 * @param ff
	 * @throws CoreException
	 */
	public RewriteRuleContent(ISCRewriteRule rule, FormulaFactory ff)
			throws CoreException {
		ITypeEnvironment typeEnvironment = ProverUtilities.makeTypeEnvironment(
				ff, rule);
		this.lhsFormula = rule.getSCFormula(ff, typeEnvironment);

		additionalAntecedentsRequired = !rule.isComplete()
				&& ProverUtilities.isConditional(rule, ff, typeEnvironment);

		ISCRewriteRuleRightHandSide[] ruleRHSes = rule.getRuleRHSs();
		int length = ruleRHSes.length;
		this.conditions = new Predicate[length];
		this.rhsFormulae = new Formula<?>[length];
		int i = 0;
		for (ISCRewriteRuleRightHandSide ruleRHS : ruleRHSes) {
			conditions[i] = ruleRHS.getPredicate(typeEnvironment);
			rhsFormulae[i] = ruleRHS.getSCFormula(ff, typeEnvironment);
			i++;
		}
		this.description = rule.getDescription();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IRewriteRuleContent#getLeftHandSide()
	 */
	@Override
	public Formula<?> getLeftHandSide() {
		return lhsFormula;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IRewriteRuleContent#getConditions()
	 */
	@Override
	public Predicate[] getConditions() {
		return conditions;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IRewriteRuleContent#getRightHandSides()
	 */
	@Override
	public Formula<?>[] getRightHandSides() {
		return rhsFormulae;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IRewriteRuleContent#additionalAntecendentRequired()
	 */
	@Override
	public boolean additionalAntecendentRequired() {
		return additionalAntecedentsRequired;
	}

	/* (non-Javadoc)
	 * @see IRewriteRuleContent#getDescription()
	 */
	@Override
	public String getDescription() {
		return description;
	}

}
