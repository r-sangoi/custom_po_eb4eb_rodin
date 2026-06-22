/*******************************************************************************
 * Copyright (c) 2011,2016 University of Southampton.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.ast.extensions.pm.engine.exp;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.ISpecialization;
import org.eventb.core.ast.extensions.pm.assoc.AExpressionProblem;
import org.eventb.core.ast.extensions.pm.assoc.IAssociativityProblem;
import org.eventb.core.ast.extensions.pm.engine.AbstractFormulaMatcher;
import org.eventb.core.ast.extensions.pm.engine.IFormulaMatcher;

/**
 * <p>
 * Implementation for matching associative expressions.
 * </p>
 *
 * @author maamria
 * @author htson: Re-implements using {@link IFormulaMatcher}.
 * @version 2.0
 * @since 1.0
 * @noextend This class is not intended to be sub-classed by clients.
 */
public class AssociativeExpressionMatcher extends
		AbstractFormulaMatcher<AssociativeExpression> implements
		IFormulaMatcher {

	/**
	 * <p>
	 * This method checks whether the operator is AC.
	 * </p>
	 * 
	 * @param tag
	 *            the tag of the input formula
	 * @return <code>true</code> if the tag is associative and commutative.
	 * @precondition the input tag is for an associative expression
	 */
	protected static boolean isAssociativeCommutative(int tag) {
		if (tag == AssociativeExpression.BCOMP
				|| tag == AssociativeExpression.FCOMP) {
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see FormulaMatcher#gatherBindings(ISpecialization, Formula, Formula)
	 */
	@Override
	protected ISpecialization gatherBindings(ISpecialization specialization,
			AssociativeExpression formula, AssociativeExpression pattern) {
		boolean isAC = isAssociativeCommutative(formula.getTag());
		// get the children
		Expression[] formChildren = formula.getChildren();
		Expression[] patternChildren = pattern.getChildren();
		IAssociativityProblem problem = null;
		if (isAC){
			problem = new AExpressionProblem(formula.getTag(), formChildren, patternChildren); 
//			problem = new ACExpressionProblem(formula.getTag(), formChildren, patternChildren); 
		}
		else {
			problem = new AExpressionProblem(formula.getTag(), formChildren, patternChildren);
		}
		return problem.solve(specialization);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see FormulaMatcher#getFormula(Formula)
	 */
	@Override
	protected AssociativeExpression getFormula(Formula<?> formula) {
		return (AssociativeExpression) formula;
	}
	
}
