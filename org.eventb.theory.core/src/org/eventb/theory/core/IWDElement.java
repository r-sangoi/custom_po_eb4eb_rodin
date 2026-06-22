/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for an element which has a well-definedness condition.
 * 
 * <p> Well-definedness conditions stored are all generated using the D operator (rather than L).
 * @author maamria
 *	@since 1.0
 */
public interface IWDElement extends IInternalElement{

	/**
	 * Returns whether the WD attribute is defined on this element.
	 * @return whether WD is defined
	 * @throws RodinDBException
	 */
	public boolean hasWDAttribute() throws RodinDBException;
	
	/**
	 * Returns the WD condition of this element.
	 * @param factory the formula factory
	 * @param typeEnvironment the type environment
	 * @return the WD condition
	 * @throws CoreException 
	 */
	public Predicate getWDCondition(FormulaFactory factory, 
			ITypeEnvironment typeEnvironment) throws CoreException;
	
	/**
	 * Sets the WD condition of this element to the given value.
	 * @param newWD the WD condition
	 * @param monitor the progress monitor
	 * @throws RodinDBException
	 */
	public void setWDCondition(Predicate newWD, IProgressMonitor monitor) throws RodinDBException;
	
}
