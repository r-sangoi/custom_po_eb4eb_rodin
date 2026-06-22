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

package org.eventb.theory.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.ISCPredicateElement;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.rodinp.core.RodinDBException;

/**
 * <p>
 *
 * </p>
 *
 * @author htson
 * @version
 * @see
 * @since 4.0
 */
public interface ISCPredicatePatternElement extends ISCPredicateElement {

	/**
	 * Returns the predicate pattern string contained in this element.
	 * 
	 * @return the string representation of the predicate pattern of this
	 *         element
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	@Override
	String getPredicateString() throws RodinDBException;
	
	/**
	 * Returns the typed predicate pattern contained in this element.
	 * <p>
	 * If a {@link EventBAttributes#SOURCE_ATTRIBUTE} is present, it is
	 * considered the predicate's origin.
	 * </p>
	 * 
	 * @param typenv
	 *            the type environment to use for building the result
	 * @return the predicate pattern of this element
	 * @throws CoreException
	 *             if there was a problem accessing the database, or if the
	 *             predicate does not parse or type-check
	 */
	@Override
	Predicate getPredicate(ITypeEnvironment typenv) throws CoreException;


	/**
	 * Sets the predicate pattern (i.e., can reference predicate variable)
	 * contained in this element.
	 * 
	 * @param predPattern
	 *            the predicate pattern to set (must be type-checked)
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress reporting
	 *            is not desired
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	@Override
	void setPredicate(Predicate predPattern, IProgressMonitor monitor) throws RodinDBException;

}
