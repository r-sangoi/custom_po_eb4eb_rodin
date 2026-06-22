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
 * Common protocol for a datatype constructor.
 * 
 * <p> A datatype constructor has an identifier, and a number (if any) of constructor arguments (see {@link IConstructorArgument}).
 * 
 * @author maamria
 *
 */
public interface IDatatypeConstructor extends IIdentifierElement, ICommentedElement{

	IInternalElementType<IDatatypeConstructor> ELEMENT_TYPE = 
		RodinCore.getInternalElementType(TheoryPlugin.PLUGIN_ID + ".datatypeConstructor");
	
	/**
	 * Returns the constructor argument of the given name.
	 * <p> This is handle-only method.
	 * @param name the argument name
	 * @return the constructor argument
	 */
	IConstructorArgument getConstructorArgument(String name);
	
	/**
	 * Returns the constructor arguments of this constructor.
	 * @return all constructor arguments
	 * @throws RodinDBException
	 */
	IConstructorArgument[] getConstructorArguments() throws RodinDBException;
	
	
}
