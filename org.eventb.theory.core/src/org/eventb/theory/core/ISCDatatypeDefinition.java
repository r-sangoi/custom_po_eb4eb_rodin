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
 * Common protocol for statically checked datatype definitions.
 * 
 * <p> This interface is not intended to be implemented by clients.
 * 
 * @see IDatatypeDefinition
 * 
 * @author maamria
 *
 */
public interface ISCDatatypeDefinition extends ISCIdentifierElement, 
ITraceableElement, IHasErrorElement{

	IInternalElementType<ISCDatatypeDefinition> ELEMENT_TYPE = 
		RodinCore.getInternalElementType(TheoryPlugin.PLUGIN_ID + ".scDatatypeDefinition");
	
	/**
	 * Returns the type argument of the given name.
	 * <p> This is handle-only method
	 * @param name the name of the argument
	 * @return the type argument
	 */
	ISCTypeArgument getTypeArgument(String name);
	
	/**
	 * Returns all type arguments of this datatype.
	 * @return all type arguments
	 * @throws RodinDBException
	 */
	ISCTypeArgument[] getTypeArguments() throws RodinDBException;
	
	/**
	 * Returns the datatype constructor of the given name.
	 * <p> This is handle-only method
	 * @param name the name of the constructor
	 * @return the constructor
	 */
	ISCDatatypeConstructor getConstructor(String name);
	
	/**
	 * Returns all datatype constructors of this datatype.
	 * @return all datatype constructors
	 * @throws RodinDBException
	 */
	ISCDatatypeConstructor[] getConstructors() throws RodinDBException;
}
