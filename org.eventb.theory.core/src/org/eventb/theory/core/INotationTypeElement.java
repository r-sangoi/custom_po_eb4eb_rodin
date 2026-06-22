/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.extension.IOperatorProperties;
import org.eventb.core.ast.extension.IOperatorProperties.Notation;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for elements that can have a notation type attribute.
 * 
 * <p> A notation type is a parser information that enables the correct parsing of extended formulae.
 * 
 * @see IOperatorProperties.Notation
 * 
 * @author maamria
 *
 */
public interface INotationTypeElement extends IInternalElement{

	/**
	 * Returns whether the notation type attribute is set.
	 * @return whether notation type is set
	 * @throws RodinDBException
	 */
	boolean hasNotationType() throws RodinDBException;
	
	/**
	 * Returns the notation type of this element
	 * @return the notation type
	 * @throws RodinDBException
	 */
	Notation getNotationType() throws RodinDBException;
	
	/**
	 * Sets the notation type of this element to the given type.
	 * @param type the notation type
	 * @param monitor the progress monitor
	 * @throws RodinDBException
	 */
	void setNotationType(String notation, IProgressMonitor monitor) throws RodinDBException;
	
}
