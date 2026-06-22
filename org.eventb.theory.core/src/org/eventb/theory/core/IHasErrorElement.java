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
 * Common protocol for an internal element that can be said to have an error. 
 * <p>
 * This is useful for the Proof Obligation Generator to know about such elements so that it ignores any
 * ill-defined elements.
 * <p> This interface triggers special treatment by the proof obligation generator.
 * 
 * @author maamria
 *
 */
public interface IHasErrorElement extends IInternalElement{
	
	/**
	 * Returns whether the error attribute is present.
	 * @return whether the error attribute is set
	 * @throws RodinDBException
	 */
	boolean hasHasErrorAttribute() throws RodinDBException;
	
	/**
	 * Returns whether this element has an error.
	 * @return whether an error exists
	 * @throws RodinDBException
	 */
	boolean hasError() throws RodinDBException;
	
	/**
	 * Sets the error attribute of this element to the given value.
	 * @param hasError whether the element has an error
	 * @param monitor the progress monitor
	 * @throws RodinDBException
	 */
	void setHasError(boolean hasError, IProgressMonitor monitor) throws RodinDBException;

}
