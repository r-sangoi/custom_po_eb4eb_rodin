/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for an element describing an operator that can be associative.
 * <p> An operator <code>op</code> is said to be associative if and only if <p>
 * <code>(x op y) op z = x op (y op z)</code>.
 * 
 * <p> This interface triggers special treatment by the proof obligation generator.
 * 
 * @author maamria
 *
 */
public interface IAssociativeElement extends IInternalElement{

	/**
	 * Returns whether this element has the associative attribute.
	 * @return whether this element has the associative attribute
	 * @throws RodinDBException
	 */
	boolean hasAssociativeAttribute() throws RodinDBException;
	
	/**
	 * Returns whether this element is set to be associative.
	 * @return whether this element is set to be associative
	 * @throws RodinDBException
	 */
	boolean isAssociative() throws RodinDBException;
	
	/**
	 * Sets this element associativity to the given value.
	 * @param isAssociative whether the element is associative
	 * @param monitor the progress monitor
	 * @throws RodinDBException
	 */
	void setAssociative(boolean isAssociative, IProgressMonitor monitor) throws RodinDBException;
}
