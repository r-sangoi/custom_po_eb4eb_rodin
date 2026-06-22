/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.theory.core;

import org.eventb.core.ICommentedElement;
import org.eventb.core.IConfigurationElement;
import org.eventb.core.IEventBRoot;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for a theory root.
 * 
 * <p> A theory root may have a number of type parameters, datatype definitions, 
 * new operator definitions and proof rules and theorems.
 * 
 * <p> This interface is not intended to be implemented by clients.
 * 
 * @author maamria
 *
 */
public interface ITheoryRoot extends 
	IEventBRoot,ICommentedElement, IConfigurationElement {

	IInternalElementType<ITheoryRoot> ELEMENT_TYPE = RodinCore
		.getInternalElementType(TheoryPlugin.PLUGIN_ID + ".theoryRoot");
	
	/**
	 * Returns the import theory project with the given name
	 * @param name the name 
	 * @return the import theory project
	 */
	public IImportTheoryProject getImportTheoryProject(String name); 
	
	/**
	 * Returns all import theory projects of this element.
	 * @return all import theory projects
	 * @throws RodinDBException
	 */
	public IImportTheoryProject[] getImportTheoryProjects() throws RodinDBException;
	
	/**
	 * Returns a handle to the type parameter with the given element.
	 * @param name the name of the type parameter
	 * @return the type parameter
	 */
	public ITypeParameter getTypeParameter(String name);
	
	/**
	 * Returns all type parameters.
	 * @return all type parameters
	 * @throws RodinDBException
	 */
	public ITypeParameter[] getTypeParameters() throws RodinDBException;
	
	/**
	 * Returns a handle to the datatype with the given element.
	 * @param name the name of the datatype
	 * @return the datatype
	 */
	public IDatatypeDefinition getDatatypeDefinition(String name);
	
	/**
	 * Returns all datatype definitions.
	 * @return all datatype definitions
	 * @throws RodinDBException
	 */
	public IDatatypeDefinition[] getDatatypeDefinitions() throws RodinDBException;
	
	/**
	 * Returns a handle to the operator with the given element.
	 * @param name the name of the operator
	 * @return the operator
	 */
	public INewOperatorDefinition getNewOperatorDefinition(String name);
	
	/**
	 * Returns all operator definitions.
	 * @return all operator definitions
	 * @throws RodinDBException
	 */
	public INewOperatorDefinition[] getNewOperatorDefinitions() throws RodinDBException;
	
	/**
	 * Returns a handle to the proof block with the given element.
	 * @param name the name of the proof block
	 * @return the proof block
	 */
	public IProofRulesBlock getProofRulesBlock(String name);
	
	public IProofRulesBlock[] getProofRulesBlocks() throws RodinDBException;
	
	public IAxiomaticDefinitionsBlock getAxiomaticDefinitionsBlock(String name);
	
	public IAxiomaticDefinitionsBlock[] getAxiomaticDefinitionsBlocks() throws RodinDBException;
	
	/**
	 * Returns a handle to the theorem with the given element.
	 * @param name the name of the theorem
	 * @return the theorem
	 */
	public ITheorem getTheorem(String name);
	
	/**
	 * Returns all theorems.
	 * @return all theorems
	 * @throws RodinDBException
	 */
	public ITheorem[] getTheorems() throws RodinDBException;
	
	/**
	 * <p>Returns the SC theory file corresponding to the given <code>bareName</code>.</p>
	 * <p>This is handle-only method.</p>
	 * @param bareName
	 * @return the rodin file
	 */
	IRodinFile getSCTheoryFile(String bareName);
	/**
	 * <p>Returns the SC theory root corresponding to this element.</p>
	 * <p>This is handle-only method.</p>
	 * @return the SC theory root
	 */
	ISCTheoryRoot getSCTheoryRoot();
	/**
	 * <p>Returns the SC theory root corresponding to the given <code>bareName</code>.</p>
	 * <p>This is handle-only method.</p>
	 * @param bareName
	 * @return the SC theory root
	 */
	ISCTheoryRoot getSCTheoryRoot(String bareName);
	
	/**
	 * <p>Returns the deployed theory file corresponding to the given <code>bareName</code>.</p>
	 * <p>This is handle-only method.</p>
	 * @param bareName
	 * @return the rodin file
	 */
	IRodinFile getDeployedTheoryFile(String bareName);
	/**
	 * <p>Returns the deployed theory root corresponding to this element.</p>
	 * <p>This is handle-only method.</p>
	 * @return the deployed theory root
	 */
	IDeployedTheoryRoot getDeployedTheoryRoot();
	/**
	 * <p>Returns the deployed theory root corresponding to the given <code>bareName</code>.</p>
	 * <p>This is handle-only method.</p>
	 * @param bareName
	 * @return the deployed theory root
	 */
	IDeployedTheoryRoot getDeployedTheoryRoot(String bareName);
	
	/**
	 * Returns whether a deployed version of this theory exists in the database.
	 * @return whether a deployed version of this theory exists in the database
	 */
	boolean hasDeployedVersion();
	
}
