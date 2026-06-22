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

package org.eventb.core.ast.extensions.pm.engine;

import org.eventb.core.ast.Formula;
import org.eventb.core.ast.ISpecialization;

/**
 * <p>
 * A common interface for formula matcher.
 * </p>
 *
 * @author htson
 * @version 1.0
 * @see AbstractFormulaMatcher
 * @see org.eventb.core.ast.extensions.pm.engine.exp
 * @see org.eventb.core.ast.extensions.pm.engine.pred
 * @since 4.0
 * @noimplement This interface is not intended to be implemented by client.
 */
public interface IFormulaMatcher {

	/**
	 * Matches the formula against the pattern and appending the result to the
	 * input specialization. Any resulting match must be compatible with the
	 * initial specialization.
	 * 
	 * @param specialization
	 *            the initial specialization.
	 * @param formula
	 *            the input formula.
	 * @param pattern
	 *            the input pattern.
	 * @return the resulting specialization if the matching is successful.
	 *         Return <code>null</code> otherwise.
	 * @precondition All arguments must not be <code>null</code> and having the
	 *               same formula factory. The formula and the pattern having
	 *               the same tags.
	 */
	public ISpecialization match(ISpecialization specialization,
			Formula<?> formula, Formula<?> pattern);

}
