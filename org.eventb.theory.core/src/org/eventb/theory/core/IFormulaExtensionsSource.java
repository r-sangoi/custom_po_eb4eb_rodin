/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

/**
 * Common interface internal elements that can be a source of mathematical extensions.
 * 
 * <p> A mathematical extensions source may have a set of type parameters. These parameters
 * are the types on which its extensions are parameterised.
 * <p> A mathematical extensions source may have any number of datatype definitions.
 * <p> A mathematical extensions source may have any number of new operator definitions.
 * 
 * @see IDeployedTheoryRoot
 * @see ISCTheoryRoot
 * 
 * @author maamria
 *
 */
public interface IFormulaExtensionsSource extends IInternalElement{

	/**
	 * Returns a handle to the type parameter with the given name.
	 * @param name the parameter name
	 * @return a handle to the parameter
	 */
	public ISCTypeParameter getSCTypeParameter(String name);

	/**
	 * Returns all type parameters which are the children of this element.
	 * @return all type parameters
	 * @throws RodinDBException
	 */
	public ISCTypeParameter[] getSCTypeParameters() throws RodinDBException;

	/**
	 * Returns a handle to the datatype definition with the given name.
	 * @param name the datatype name
	 * @return a handle to the datatype
	 */
	public ISCDatatypeDefinition getSCDatatypeDefinition(String name);

	/**
	 * Returns all datatype definition which are the children of this element.
	 * @return all datatype definitions
	 * @throws RodinDBException
	 */
	public ISCDatatypeDefinition[] getSCDatatypeDefinitions() throws RodinDBException;

	/**
	 * Returns a handle to the operator definition with the given name.
	 * @param name the operator name
	 * @return a handle to the operator
	 */
	public ISCNewOperatorDefinition getSCNewOperatorDefinition(String name);

	/**
	 * Returns all operator definition which are the children of this element.
	 * @return all operator definitions
	 * @throws RodinDBException
	 */
	public ISCNewOperatorDefinition[] getSCNewOperatorDefinitions() throws RodinDBException;
	
	public ISCAxiomaticDefinitionsBlock getSCAxiomaticDefinitionsBlock(String name);
	
	public ISCAxiomaticDefinitionsBlock[] getSCAxiomaticDefinitionsBlocks() throws RodinDBException;
}
