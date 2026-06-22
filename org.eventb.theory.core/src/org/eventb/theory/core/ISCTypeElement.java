/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Type;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

/**
 * 
 * @author maamria
 *
 */
public interface ISCTypeElement extends IInternalElement{

	/**
	 * Returns whether the type attribute is set.
	 * @return whether the type attribute is set
	 * @throws RodinDBException
	 */
	boolean hasType() throws RodinDBException;
	
	/**
	 * Returns the type associated with this element.
	 * @param factory the formula factory
	 * @return the type
	 * @throws CoreException 
	 */
	Type getType(FormulaFactory factory) throws CoreException;
	
	/**
	 * Sets the type associated with this element to the given type.
	 * @param type the type
	 * @param monitor the progress monitor
	 * @throws RodinDBException
	 */
	void setType(Type type, IProgressMonitor monitor) throws RodinDBException;
	
}
