/*******************************************************************************
 * Copyright (c) 2011,2016 University of Southampton.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.ast.extensions.pm.engine.pred;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.ISpecialization;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.PredicateVariable;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.extensions.pm.Matcher;
import org.eventb.core.ast.extensions.pm.engine.AbstractFormulaMatcher;
import org.eventb.core.ast.extensions.pm.engine.IFormulaMatcher;

/**
 * <p>
 * Implementation for matching quantified predicates.
 * </p>
 *
 * @author maamria
 * @author htson: Re-implements using {@link IFormulaMatcher}.
 * @version 2.0
 * @since 1.0
 * @noextend This class is not intended to be sub-classed by clients.
 */
public class QuantifiedPredicateMatcher extends
		AbstractFormulaMatcher<QuantifiedPredicate> implements IFormulaMatcher {

	/*
	 * (non-Javadoc)
	 * 
	 * @see FormulaMatcher#gatherBindings(ISpecialization, Formula, Formula)
	 */
	@Override
	protected ISpecialization gatherBindings(ISpecialization specialization,
			QuantifiedPredicate formula, QuantifiedPredicate pattern) {

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
	protected QuantifiedPredicate getFormula(Formula<?> formula) {
		return (QuantifiedPredicate) formula;
	}
}
