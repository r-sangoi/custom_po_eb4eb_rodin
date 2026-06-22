/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
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
 * 
 * Common protocol for an ordered element. Order is determined by which element is defined before which.
 * 
 * @author maamria
 *
 */
public interface IOrderedElement extends IInternalElement{

	/**
	 * Returns whether the order attribute is defined.
	 * @return whether the order attribute is defined
	 * @throws RodinDBException
	 */
	public boolean hasOrderAttribute() throws RodinDBException;
	
	/**
	 * Returns the order of this element.
	 * @return the order
	 * @throws RodinDBException
	 */
	public int getOrder() throws RodinDBException;
	
	/**
	 * Sets the order of this element to the given value.
	 * @param newOrder the order
	 * @param monitor the progress monitor
	 * @throws RodinDBException
	 */
	public void setOrder(int newOrder, IProgressMonitor monitor) throws RodinDBException;
	
}
