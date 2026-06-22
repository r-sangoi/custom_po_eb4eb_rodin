/*******************************************************************************
 * Copyright (c) 2011,2016 University of Southampton.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.ast.extensions.pm.engine.exp;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ISpecialization;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.ast.extensions.pm.Matcher;
import org.eventb.core.ast.extensions.pm.engine.AbstractFormulaMatcher;
import org.eventb.core.ast.extensions.pm.engine.IFormulaMatcher;

/**
 * <p>
 * Implementation for matching unary expressions.
 * </p>
 *
 * @author maamria
 * @author htson Re-implemented based on {@link IFormulaMatcher} interface.
 * @version 2.0
 * @since 1.0
 */
public class UnaryExpressionMatcher extends
		AbstractFormulaMatcher<UnaryExpression> implements IFormulaMatcher {

	/*
	 * (non-Javadoc)
	 * 
	 * @see FormulaMatcher#gatherBindings(ISpecialization, Formula, Formula)
	 */
	@Override
	protected ISpecialization gatherBindings(ISpecialization specialization,
			UnaryExpression formula, UnaryExpression pattern) {
		Expression formulaExp = formula.getChild();
		Expression patternExp = pattern.getChild();
		specialization = Matcher.unifyTypes(specialization,
				formulaExp.getType(), patternExp.getType());
		if (specialization == null)
			return null;
		if (patternExp instanceof FreeIdentifier) {
			return Matcher.insert(specialization, (FreeIdentifier) patternExp,
					formulaExp);
		}
		return Matcher.match(specialization, formulaExp, patternExp);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see FormulaMatcher#getFormula(Formula)
	 */
	@Override
	protected UnaryExpression getFormula(Formula<?> formula) {
		return (UnaryExpression) formula;
	}

}
