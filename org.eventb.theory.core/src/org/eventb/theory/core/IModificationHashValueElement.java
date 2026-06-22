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
 * @author asiehsalehi
 * @since 1.0
 *
 */
public interface IModificationHashValueElement extends IInternalElement{

	/**
	 * Returns whether the element has the modificationHashValue attribute.
	 * @return whether the element has the modificationHashValue attribute
	 * @throws RodinDBException
	 */
	public boolean hasModificationHashValueAttribute() throws RodinDBException;
	
	/**
	 * Returns the element modificationHashValue attribute.
	 * @return the element modificationHashValue attribute
	 * @throws RodinDBException
	 */
	public String getModificationHashValue() throws RodinDBException;
	
	/**
	 * Sets the element modificationHashValue attribute to the given value.
	 * @param modificationHashValue the element is to be set modificationHashValue
	 * @param monitor the progress monitor
	 * @throws RodinDBException
	 */
	public void setModificationHashValue(String modificationHashValue, IProgressMonitor monitor) throws RodinDBException;
	
}
