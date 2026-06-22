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
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Type;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

/**
 *	Common protocol for a statically checked given type element.
 * 
 * @author maamria
 *
 */
public interface ISCGivenTypeElement extends IInternalElement{
	
	/**
	 * Returns the given type associated with this element.
	 * @param factory the formula factory
	 * @return the given type
	 * @throws CoreException 
	 */
	Type getSCGivenType(FormulaFactory factory) throws CoreException;
	
	/**
	 * Sets the given type attribute to the given type.
	 * @param type the given type
	 * @param monitor the progress monitor
	 * @throws RodinDBException
	 */
	void setSCGivenType(Type type, IProgressMonitor monitor) throws RodinDBException;

}
