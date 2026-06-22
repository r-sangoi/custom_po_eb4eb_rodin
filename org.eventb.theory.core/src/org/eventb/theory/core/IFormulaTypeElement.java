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
import org.eventb.core.ast.extension.IOperatorProperties.FormulaType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for a formula type element.
 * 
 * @see IOperatorProperties.FormulaType
 * 
 * @author maamria
 *
 */
public interface IFormulaTypeElement extends IInternalElement{

	/**
	 * Returns whether the formula type attribute is set.
	 * @return whether formula type is set
	 * @throws RodinDBException
	 */
	boolean hasFormulaType() throws RodinDBException;
	
	/**
	 * Returns the formula type of this element
	 * @return the formula type
	 * @throws RodinDBException
	 */
	FormulaType getFormulaType() throws RodinDBException;
	
	/**
	 * Sets the formula type of this element to the given type.
	 * @param type the formula type
	 * @param monitor the progress monitor
	 * @throws RodinDBException
	 */
	void setFormulaType(FormulaType type, IProgressMonitor monitor) throws RodinDBException;
	
}
