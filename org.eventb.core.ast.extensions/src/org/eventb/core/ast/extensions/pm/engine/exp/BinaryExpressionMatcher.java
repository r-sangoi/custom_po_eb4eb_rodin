/*******************************************************************************
 * Copyright (c) 2011,2016 University of Southampton.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.ast.extensions.pm.engine.exp;

import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ISpecialization;
import org.eventb.core.ast.extensions.pm.Matcher;
import org.eventb.core.ast.extensions.pm.engine.AbstractFormulaMatcher;
import org.eventb.core.ast.extensions.pm.engine.IFormulaMatcher;

/**
 * <p>
 * Implementation for matching binary expressions.
 * </p>
 *
 * @author maamria
 * @author htson Re-implemented based on {@link IFormulaMatcher} interface.
 * @version 2.0
 * @since 1.0
 */
public class BinaryExpressionMatcher extends
		AbstractFormulaMatcher<BinaryExpression> implements IFormulaMatcher {

	/*
	 * (non-Javadoc)
	 * 
	 * @see FormulaMatcher#gatherBindings(ISpecialization, Formula, Formula)
	 */
	@Override
	protected ISpecialization gatherBindings(ISpecialization specialization,
			BinaryExpression formula, BinaryExpression pattern) {
		// for left's
		Expression fLeft = formula.getLeft();
		Expression pLeft = pattern.getLeft();
		specialization = Matcher.unifyTypes(specialization, fLeft.getType(), pLeft.getType());
		if (specialization == null) {
			return null;
		}
		if (pLeft instanceof FreeIdentifier){
			specialization = Matcher.insert(specialization, (FreeIdentifier) pLeft, fLeft);
			if (specialization == null){
				return null;
			}
		}
		else{
			specialization = Matcher.match(specialization, fLeft, pLeft);
			if (specialization == null) {
				return null;
			}
		}
		// for right's
		Expression fRight = formula.getRight();
		Expression pRight = pattern.getRight();
		specialization = Matcher.unifyTypes(specialization, fRight.getType(), pRight.getType());
		if (specialization == null) {
			return null;
		}
		if(pRight instanceof FreeIdentifier){
			return Matcher.insert(specialization, (FreeIdentifier) pRight, fRight);
		}
		return Matcher.match(specialization, fRight, pRight);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see FormulaMatcher#getFormula(Formula)
	 */
	@Override
	protected BinaryExpression getFormula(Formula<?> formula) {
		return (BinaryExpression) formula;
	}	

}
