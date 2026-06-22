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
 * Common protocol for an element describing an operator that can be commutative.
 * <p> An operator <code>op</code> is said to be commutative if and only if <p>
 * <code>x op y = y op x</code>.
 * <p> This interface triggers special treatment by the proof obligation generator.
 * 
 * @author maamria
 *
 */
public interface ICommutativeElement extends IInternalElement {

	/**
	 * Returns whether this element has the commutative attribute.
	 * @return whether this element has the commutative attribute
	 * @throws RodinDBException
	 */
	boolean hasCommutativeAttribute() throws RodinDBException;
	
	/**
	 * Returns whether this element is set to be commutative.
	 * @return whether this element is set to be commutative
	 * @throws RodinDBException
	 */
	boolean isCommutative() throws RodinDBException;
	
	/**
	 * Sets this element commutativity to the given value.
	 * @param isCommutative whether the element is commutative
	 * @param monitor the progress monitor
	 * @throws RodinDBException
	 */
	void setCommutative(boolean isCommutative, IProgressMonitor monitor) throws RodinDBException;
	
}
