/*******************************************************************************
 * Copyright (c) 2016 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University of Southampton - initial API and implementation
 *******************************************************************************/
package org.eventb.core.ast.extensions.pm.engine.exp;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.ISpecialization;
import org.eventb.core.ast.extensions.pm.engine.AbstractFormulaMatcher;
import org.eventb.core.ast.extensions.pm.engine.IFormulaMatcher;

/**
 * <p>
 * Implementation for matching bound identifier declarations.
 * </p>
 *
 * @author htson Re-implemented based on {@link IFormulaMatcher} interface.
 * @version 1.0
 * @since 1.0
 */
public class BoundIdentDeclMatcher extends AbstractFormulaMatcher<BoundIdentDecl>
		implements IFormulaMatcher {

	/*
	 * (non-Javadoc)
	 * 
	 * @see FormulaMatcher#gatherBindings(ISpecialization, Formula, Formula)
	 */
	@Override
	protected ISpecialization gatherBindings(ISpecialization specialization,
			BoundIdentDecl formula, BoundIdentDecl pattern) {
		// Nothing to do
		return specialization;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see FormulaMatcher#getFormula(Formula)
	 */
	@Override
	protected BoundIdentDecl getFormula(Formula<?> formula) {
		return (BoundIdentDecl) formula;
	}

}
