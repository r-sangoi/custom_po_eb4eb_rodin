/*******************************************************************************
 * Copyright (c) 2011,2016 University of Southampton.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.ast.extensions.pm.engine.pred;

import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.ISpecialization;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.PredicateVariable;
import org.eventb.core.ast.extensions.pm.Matcher;
import org.eventb.core.ast.extensions.pm.engine.AbstractFormulaMatcher;
import org.eventb.core.ast.extensions.pm.engine.IFormulaMatcher;

/**
 * <p>
 * Implementation for matching binary predicates.
 * </p>
 *
 * @author maamria
 * @author htson: Re-implements using {@link IFormulaMatcher}.
 * @version 2.0
 * @since 1.0
 * @noextend This class is not intended to be sub-classed by clients.
 */
public class BinaryPredicateMatcher extends AbstractFormulaMatcher<BinaryPredicate>
		implements IFormulaMatcher {

	/*
	 * (non-Javadoc)
	 * 
	 * @see FormulaMatcher#gatherBindings(ISpecialization, Formula, Formula)
	 */
	@Override
	protected ISpecialization gatherBindings(ISpecialization specialization,
			BinaryPredicate formula, BinaryPredicate pattern) {
		Predicate fLeft = formula.getLeft();
		Predicate pLeft = pattern.getLeft();
		if (pLeft instanceof PredicateVariable) {
			throw new UnsupportedOperationException(
					"Predicate variable is unsupported");
		} else {
			specialization = Matcher.match(specialization, fLeft, pLeft);
			if(specialization == null){
				return null;
			}
		}

		Predicate fRight = formula.getRight();
		Predicate pRight = pattern.getRight();
		if (pRight instanceof PredicateVariable) {
			throw new UnsupportedOperationException(
					"Predicate variable is unsupported");
		} else
			return Matcher.match(specialization, fRight, pRight);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see FormulaMatcher#getFormula(Formula)
	 */
	@Override
	protected BinaryPredicate getFormula(Formula<?> formula) {
		return (BinaryPredicate) formula;
	}

}
