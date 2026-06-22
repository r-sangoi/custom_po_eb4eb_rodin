/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core;

import org.eventb.theory.core.plugin.TheoryPlugin;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for statically checked inference rules.
 * 
 * <p> This interface is not intended to be implemented by clients.
 * 
 * @see IInferenceRule
 * 
 * @author maamria
 *
 */
public interface ISCInferenceRule extends ISCRule, IReasoningTypeElement{

	IInternalElementType<ISCInferenceRule> ELEMENT_TYPE = 
		RodinCore.getInternalElementType(TheoryPlugin.PLUGIN_ID + ".scInferenceRule");
	
	/**
	 * Returns a handle to the given with the given name.
	 * @param name the name
	 * @return a handle to the given
	 */
	ISCGiven getGiven(String name);
	
	/**
	 * Returns all givens whose parent is this element.
	 * @return all child givens
	 * @throws RodinDBException
	 */
	ISCGiven[] getGivens() throws RodinDBException;
	
	/**
	 * Returns a handle to the infer with the given name.
	 * @param name the name
	 * @return a handle to the infer
	 */
	ISCInfer getInfer(String name);
	
	/**
	 * Returns all infers whose parent is this element.
	 * @return all child infers
	 * @throws RodinDBException
	 */
	ISCInfer[] getInfers() throws RodinDBException;
}
