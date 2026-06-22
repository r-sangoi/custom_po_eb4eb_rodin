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

package org.eventb.theory.rbp.reasoners;

import org.eventb.core.ast.Formula;
import org.eventb.core.ast.Predicate;

/**
 * <p>
 *
 * </p>
 *
 * @author htson
 * @version
 * @see
 * @since
 */
public interface IRewriteRuleContent {

	/**
	 * @return
	 */
	public Formula<?> getLeftHandSide();

	/**
	 * @return
	 */
	public Predicate[] getConditions();

	/**
	 * @return
	 */
	public Formula<?>[] getRightHandSides();

	/**
	 * @return
	 */
	public boolean additionalAntecendentRequired();

	/**
	 * @return
	 */
	public String getDescription();

}
