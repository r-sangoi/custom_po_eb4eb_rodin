/*******************************************************************************
 * Copyright (c) 2011,2016 University of Southampton.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.ast.extensions.pm.engine.exp;

import org.eventb.core.ast.Formula;
import org.eventb.core.ast.ISpecialization;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.extensions.pm.engine.AbstractFormulaMatcher;
import org.eventb.core.ast.extensions.pm.engine.IFormulaMatcher;

/**
 * <p>
 * Implementation for matching integer literals.
 * </p>
 *
 * @author maamria
 * @author htson Re-implemented based on {@link IFormulaMatcher} interface.
 * @version 2.0
 * @since 1.0
 */
public class IntegerLiteralMatcher extends
		AbstractFormulaMatcher<IntegerLiteral> implements IFormulaMatcher {

	/*
	 * (non-Javadoc)
	 * 
	 * @see FormulaMatcher#gatherBindings(ISpecialization, Formula, Formula)
	 */
	@Override
	protected ISpecialization gatherBindings(ISpecialization specialization,
			IntegerLiteral formula, IntegerLiteral pattern) {
		if (formula.equals(pattern))
			return specialization;
		else
			return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see FormulaMatcher#getFormula(Formula)
	 */
	@Override
	protected IntegerLiteral getFormula(Formula<?> formula) {
		return (IntegerLiteral) formula;
	}

}
