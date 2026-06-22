/*******************************************************************************
 * Copyright (c) 2011,2016 University of Southampton.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.ast.extensions.pm.engine.exp;

import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.ISpecialization;
import org.eventb.core.ast.extensions.pm.engine.AbstractFormulaMatcher;
import org.eventb.core.ast.extensions.pm.engine.IFormulaMatcher;

/**
 * <p>
 * Implementation for matching atomic expressions.
 * </p>
 *
 * @author maamria
 * @author htson Re-implemented based on {@link IFormulaMatcher} interface.
 * @version 2.0
 * @since 1.0
 * @noextend This class is not intended to be sub-classed by clients.
 */
public class AtomicExpressionMatcher extends
		AbstractFormulaMatcher<AtomicExpression> implements IFormulaMatcher {
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see FormulaMatcher#gatherBindings(ISpecialization, Formula, Formula)
	 */
	@Override
	protected ISpecialization gatherBindings(ISpecialization specialization,
			AtomicExpression formula, AtomicExpression pattern) {
		// no further action required
		return specialization;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see FormulaMatcher#getFormula(Formula)
	 */
	@Override
	protected AtomicExpression getFormula(Formula<?> formula) {
		return (AtomicExpression) formula;
	}

	
}
