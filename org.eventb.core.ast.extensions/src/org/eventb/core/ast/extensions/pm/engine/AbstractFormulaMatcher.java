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

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.ISpecialization;
import org.eventb.core.ast.extensions.pm.Matcher;

/**
 * <p>
 * Common implementation of a formula matcher. Sub-classes must implement
 * {@link #gatherBindings(ISpecialization, Formula, Formula)} and
 * {@link #getFormula(Formula)} methods.
 * </p>
 *
 * @author htson
 * @version
 * @see org.eventb.core.ast.extensions.pm.engine.exp
 * @see org.eventb.core.ast.extensions.pm.engine.pred
 * @since 4.0
 * @noextend This class is not intended to be sub-classed by clients.
 */
public abstract class AbstractFormulaMatcher<T extends Formula<?>> implements
		IFormulaMatcher {

	/**
	 * Common implementation of the matching process.
	 * <ol>
	 * <li>Unify the types of the formula and the pattern if they are
	 * expression.</li>
	 * 
	 * <li>Call {@link #gatherBindings(ISpecialization, Formula, Formula)} to
	 * gather the actual bindings.</li>
	 * </ol>
	 */
	@Override
	public ISpecialization match(ISpecialization specialization,
			Formula<?> formula, Formula<?> pattern) {
		// unify types for expressions
		if (formula instanceof Expression) {
			specialization = Matcher.unifyTypes(specialization,
					((Expression) formula).getType(),
					((Expression) pattern).getType());
			if (specialization == null) {
				return null;
			}
		}
		
		// Convert the formula and pattern to type T
		T eFormula = getFormula(formula);
		T ePattern = getFormula(pattern);
		// by this point the expression have the same tag and types are
		// unified-able, try to gather the matchings.
		return gatherBindings(specialization, eFormula, ePattern);
	}



	/**
	 * Abstract worker method for gathering substitutions given some initial
	 * specialization. The input specialization can be modified to include
	 * (possibly partial in the case where the result is <code>false</code>)
	 * matching information. This method must be implemented by clients.
	 * 
	 * @param specialization
	 *            the initial specialization
	 * @param formula
	 *            the input formula.
	 * @param ePattern
	 *            the input pattern.
	 * @return the resulting specialization from matching the formula and the
	 *         pattern. Return <code>null</code> if the formula and the pattern
	 *         do not match.
	 * @see #match(ISpecialization, Formula, Formula)
	 * @precondition The inputs are not <code>null</code> and have the same
	 *               formula factory. The formula and pattern has the same tag.
	 *               The type of the input formula and pattern (if they are
	 *               expressions) have been unified and the result of the
	 *               unification are stored in the input specialization.
	 */
	protected abstract ISpecialization gatherBindings(
			ISpecialization specialization, T formula, T pattern);

	/**
	 * Method to convert a formula into the appropriate type.
	 * 
	 * @param formula
	 *            the input formula
	 * @return the formula casted into the type T.
	 */
	protected abstract T getFormula(Formula<?> formula);

}
