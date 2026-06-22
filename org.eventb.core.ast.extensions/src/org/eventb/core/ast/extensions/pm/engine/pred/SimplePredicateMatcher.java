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
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.extensions.pm.Matcher;
import org.eventb.core.ast.extensions.pm.engine.AbstractFormulaMatcher;
import org.eventb.core.ast.extensions.pm.engine.IFormulaMatcher;

/**
 * <p>
 * Implementation for matching simple predicates.
 * </p>
 *
 * @author maamria
 * @author htson: Re-implements using {@link IFormulaMatcher}.
 * @version 2.0
 * @since 1.0
 * @noextend This class is not intended to be sub-classed by clients.
 */
public class SimplePredicateMatcher extends
		AbstractFormulaMatcher<SimplePredicate> implements IFormulaMatcher {

	/*
	 * (non-Javadoc)
	 * 
	 * @see FormulaMatcher#gatherBindings(ISpecialization, Formula, Formula)
	 */
	@Override
	protected ISpecialization gatherBindings(ISpecialization specialization,
			SimplePredicate formula, SimplePredicate pattern) {
		Expression fExp = formula.getExpression();
		Expression pExp = pattern.getExpression();
		specialization = Matcher.unifyTypes(specialization, fExp.getType(),
				pExp.getType());
		if (specialization == null)
			return null;
		if (pExp instanceof FreeIdentifier) {
			return Matcher.insert(specialization, (FreeIdentifier) pExp, fExp);
		}
		return Matcher.match(specialization, fExp, pExp);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see FormulaMatcher#getFormula(Formula)
	 */
	@Override
	protected SimplePredicate getFormula(Formula<?> formula) {
		return (SimplePredicate) formula;
	}
}
