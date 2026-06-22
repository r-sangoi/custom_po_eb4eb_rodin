/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core;

import org.eventb.core.ICommentedElement;
import org.eventb.core.IIdentifierElement;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for datatype definition internal element. 
 * 
 * <p>A datatype definition has a name, some type arguments and a set of constructors.
 * 
 * <p> The type arguments define the types on which this particular datatype definition is parametric on.
 * 
 * @see IDatatypeConstructor
 * @see ITypeArgument
 * 
 * @author maamria
 *
 */
public interface IDatatypeDefinition extends IIdentifierElement, 
ICommentedElement{

	IInternalElementType<IDatatypeDefinition> ELEMENT_TYPE = 
		RodinCore.getInternalElementType(TheoryPlugin.PLUGIN_ID + ".datatypeDefinition");
	
	/**
	 * Returns the type argument of the given name.
	 * <p> This is handle-only method
	 * @param name the name of the argument
	 * @return the type argument
	 */
	ITypeArgument getTypeArgument(String name);
	
	/**
	 * Returns all type arguments of this datatype.
	 * @return all type arguments
	 * @throws RodinDBException
	 */
	ITypeArgument[] getTypeArguments() throws RodinDBException;
	
	/**
	 * Returns the datatype constructor of the given name.
	 * <p> This is handle-only method
	 * @param name the name of the constructor
	 * @return the constructor
	 */
	IDatatypeConstructor getDatatypeConstructor(String name);
	
	/**
	 * Returns all datatype constructors of this datatype.
	 * @return all datatype constructors
	 * @throws RodinDBException
	 */
	IDatatypeConstructor[] getDatatypeConstructors() throws RodinDBException;
	
}
