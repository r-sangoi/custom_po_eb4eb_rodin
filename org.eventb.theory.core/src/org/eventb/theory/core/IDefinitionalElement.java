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
 * Common protocol for a definitional element that can be ignored by the Proof Obligation Generator.
 * <p>This is relevant for rewrite and inference rules that are generated from operator definitions. This ensures that such rules
 * are not concerned with proof obligation generation.</p>
 * <p> This interface triggers special treatment by the proof obligation generator.
 * @author maamria
 *
 */
public interface IDefinitionalElement extends IInternalElement{
	
	/**
	 * Returns whether the definitional attribute is set or not.
	 * @return whether the attribute is present
	 * @throws RodinDBException
	 */
	boolean hasDefinitionalAttribute() throws RodinDBException;
	
	/**
	 * Returns whether this element is definitional.
	 * @return whether this element is definitional
	 * @throws RodinDBException
	 */
	boolean isDefinitional() throws RodinDBException;
	
	/**
	 * Sets this element definitional attribute to the given value.
	 * @param isDefinitional whether the element is definitional
	 * @param monitor the progress monitor
	 * @throws RodinDBException
	 */
	void setDefinitional(boolean isDefinitional, IProgressMonitor monitor) throws RodinDBException; 

}
