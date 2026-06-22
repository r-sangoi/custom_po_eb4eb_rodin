/*******************************************************************************
 * Copyright (c) 2011,2016 University of Southampton.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.ast.extensions.pm.engine.pred;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ISpecialization;
import org.eventb.core.ast.MultiplePredicate;
import org.eventb.core.ast.extensions.pm.Matcher;
import org.eventb.core.ast.extensions.pm.engine.AbstractFormulaMatcher;
import org.eventb.core.ast.extensions.pm.engine.IFormulaMatcher;

/**
 * <p>
 * Implementation for matching multiple predicates.
 * </p>
 *
 * @author maamria
 * @author htson: Re-implements using {@link IFormulaMatcher}.
 * @version 2.0
 * @since 1.0
 * @noextend This class is not intended to be sub-classed by clients.
 */
public class MultiplePredicateMatcher extends
		AbstractFormulaMatcher<MultiplePredicate> implements IFormulaMatcher {

	/*
	 * (non-Javadoc)
	 * 
	 * @see FormulaMatcher#gatherBindings(ISpecialization, Formula, Formula)
	 */
	@Override
	protected ISpecialization gatherBindings(ISpecialization specialization,
			MultiplePredicate formula, MultiplePredicate pattern) {
		Expression[] fChildren = formula.getChildren();
		Expression[] pChildren = pattern.getChildren();
		if (fChildren.length != pChildren.length) {
			return null;
		}
		for (int i = 0; i != fChildren.length; i++) {
			Expression fExp = fChildren[i];
			Expression pExp = pChildren[i];
			if (pExp instanceof FreeIdentifier) {
				specialization = Matcher.unifyTypes(specialization,
						fExp.getType(), pExp.getType());
				if (specialization == null)
					return null;
				specialization = Matcher.insert(specialization,
						(FreeIdentifier) pExp, fExp);
				if (specialization == null) {
					return null;
				}
			} else {
				specialization = Matcher.match(specialization, fExp, pExp);
				if (specialization == null) {
					return null;
				}
			}
		}
		return specialization;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see FormulaMatcher#getFormula(Formula)
	 */
	@Override
	protected MultiplePredicate getFormula(Formula<?> formula) {
		return (MultiplePredicate) formula;
	}

}
