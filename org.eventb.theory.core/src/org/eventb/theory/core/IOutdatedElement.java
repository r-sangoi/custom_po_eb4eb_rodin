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
 * Common protocol for an element that can be outdated.
 * 
 * <p> The semantics of being outdated is defined separately for each element.
 * 
 * @author maamria
 * @since 1.0
 *
 */
public interface IOutdatedElement extends IInternalElement{

	/**
	 * Returns whether the element has the outdated attribute.
	 * @return whether the element has the outdated attribute
	 * @throws RodinDBException
	 */
	public boolean hasOutdatedAttribute() throws RodinDBException;
	
	/**
	 * Returns whether the element is outdated.
	 * @return whether the element is outdated
	 * @throws RodinDBException
	 */
	public boolean isOutdated() throws RodinDBException;
	
	/**
	 * Sets the element outdated attribute to the given value.
	 * @param isOutdated whether the element is to be set outdated
	 * @param monitor the progress monitor
	 * @throws RodinDBException
	 */
	public void setOutdated(boolean isOutdated, IProgressMonitor monitor) throws RodinDBException;
	
}
