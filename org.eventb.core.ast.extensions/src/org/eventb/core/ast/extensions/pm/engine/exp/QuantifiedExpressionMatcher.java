/*******************************************************************************
 * Copyright (c) 2011,2016 University of Southampton.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.ast.extensions.pm.engine.exp;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ISpecialization;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.PredicateVariable;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.extensions.pm.Matcher;
import org.eventb.core.ast.extensions.pm.engine.AbstractFormulaMatcher;
import org.eventb.core.ast.extensions.pm.engine.IFormulaMatcher;

/**
 * <p>
 * Implementation for matching quantified expressions.
 * </p>
 *
 * @author maamria
 * @author htson Re-implemented based on {@link IFormulaMatcher} interface.
 * @version 2.0
 * @since 1.0
 */
public class QuantifiedExpressionMatcher extends
		AbstractFormulaMatcher<QuantifiedExpression> implements IFormulaMatcher {

	/*
	 * (non-Javadoc)
	 * 
	 * @see AbstractFormulaMatcher#gatherBindings(ISpecialization, Formula,
	 * Formula)
	 */
	@Override
	protected ISpecialization gatherBindings(ISpecialization specialization,
			QuantifiedExpression formula, QuantifiedExpression pattern) {
		if (formula.getTag() == Formula.CSET) {
			if (formula.getForm() != formula.getForm()){
				return null;
			}
		}
		// Match the bound identifier declaration
		BoundIdentDecl[] fDec = formula.getBoundIdentDecls();
		BoundIdentDecl[] pDec = pattern.getBoundIdentDecls();
		if (fDec.length != pDec.length) { 
			return null;
		}
		for (int i = 0; i != fDec.length; i++) {
			specialization = Matcher.match(specialization, fDec[i], pDec[i]);
			if (specialization == null) {
				return null;
			}
			
		}
		
		// Match the expression
		Expression fExp = formula.getExpression();
		Expression pExp = pattern.getExpression();
		specialization = Matcher.unifyTypes(specialization, fExp.getType(), pExp.getType());
		if (specialization == null) {
			return null;
		}
		if (pExp instanceof FreeIdentifier) {
			specialization = Matcher.insert(specialization, (FreeIdentifier) pExp, fExp);
			if (specialization == null) {
				return null;
			}
		} else {
			specialization = Matcher.match(specialization, fExp, pExp);
			if (specialization == null){
				return null;
			}
		}

		// Match the predicate
		Predicate fPred = formula.getPredicate();
		Predicate pPred = pattern.getPredicate();
		if (pPred instanceof PredicateVariable) {
			throw new UnsupportedOperationException(
					"Predicate variable is unsupported");
		}
		return Matcher.match(specialization, fPred, pPred);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see FormulaMatcher#getFormula(Formula)
	 */
	@Override
	protected QuantifiedExpression getFormula(Formula<?> formula) {
		return (QuantifiedExpression) formula;
	}

}
