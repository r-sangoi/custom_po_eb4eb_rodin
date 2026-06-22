/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for an element that may have a statically checked formula.
 * 
 * @author maamria
 *
 */
public interface ISCFormulaElement extends IInternalElement{
	
	/**
	 * Returns whether the formula attribute is set.
	 * @return whether the formula attribute is set
	 * @throws RodinDBException
	 */
	boolean hasSCFormula() throws RodinDBException;
	
	/**
	 * Returns the SC formula associated with this element.
	 * @param ff the formula factory
	 * @param typeEnvironment the type environment
	 * @return the SC formula
	 * @throws CoreException 
	 */
	Formula<?> getSCFormula(FormulaFactory ff, ITypeEnvironment typeEnvironment) throws CoreException;
	
	/**
	 * Sets the formula attribute of this element to the given formula.
	 * @param formula the SC formula
	 * @param monitor the progress monitor
	 * @throws RodinDBException
	 */
	void setSCFormula(Formula<?> formula, IProgressMonitor monitor) throws RodinDBException;

}
