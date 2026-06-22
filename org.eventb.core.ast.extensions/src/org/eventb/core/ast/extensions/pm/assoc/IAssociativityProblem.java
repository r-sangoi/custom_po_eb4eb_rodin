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

package org.eventb.core.ast.extensions.pm.assoc;

import org.eventb.core.ast.ISpecialization;

/**
 * <p>
 * Common interface for Associativity problems.
 * </p>
 *
 * @author htson
 * @version 1.0
 * @see AssociativityProblem
 * @since 4.0
 */
public interface IAssociativityProblem {

	/**
	 * Solve the associativity problem given some initial specialization.
	 * 
	 * @return the resulting specialization if successful. Return
	 *         <code>null</code> otherwise.
	 */
	public ISpecialization solve(ISpecialization specialization);

}
