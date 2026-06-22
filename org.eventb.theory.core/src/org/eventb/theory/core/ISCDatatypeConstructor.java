/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core;

import org.eventb.core.ISCIdentifierElement;
import org.eventb.core.ITraceableElement;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for statically checked datatype constructors.
 * 
 * <p> This interface is not intended to be implemented by clients.
 * 
 * @see IDatatypeConstructor
 * 
 * @author maamria
 *
 */
public interface ISCDatatypeConstructor extends ISCIdentifierElement, ITraceableElement{
	
	IInternalElementType<ISCDatatypeConstructor> ELEMENT_TYPE = 
		RodinCore.getInternalElementType(TheoryPlugin.PLUGIN_ID + ".scDatatypeConstructor");
	
	/**
	 * Returns a handle to the constructor argument with the given name.
	 * @param name the name 
	 * @return a handle to the constructor argument
	 */
	ISCConstructorArgument getConstructorArgument(String name);
	
	/**
	 * Returns all the constructor arguments whose parent is this element.
	 * @return all child constructor arguments
	 * @throws RodinDBException
	 */
	ISCConstructorArgument[] getConstructorArguments() throws RodinDBException;

}
