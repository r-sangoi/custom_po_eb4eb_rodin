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
 *	Common protocol for an internal element that belongs to an operator group. 
 *
 *	<p> Each newly defined operator belongs to an operator group. This is accessed via the methods of this interface.
 * 
 * @author maamria
 *
 */
public interface IOperatorGroupElement extends IInternalElement{

	/**
	 * Returns whether the operator group attribute is set.
	 * @return whether the attribute is present
	 * @throws RodinDBException
	 */
	boolean hasOperatorGroup() throws RodinDBException;
	
	/**
	 * Returns the operator group to which this operator belongs.
	 * @return the operator group
	 * @throws RodinDBException
	 */
	String getOperatorGroup() throws RodinDBException;
	
	/**
	 * Sets the operator group of this operator to the given group.
	 * @param newGroup the operator group
	 * @param monitor the progress monitor
	 * @throws RodinDBException
	 */
	void setOperatorGroup(String newGroup, IProgressMonitor monitor)throws RodinDBException;
	
}
