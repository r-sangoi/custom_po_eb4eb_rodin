/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for a use theory directive. 
 * 
 * <p> Deployed theories use other deployed theories, and SC theories can use some deployed theories.
 * 
 * <p> This interface is not intended to be implemented by clients.
 * 
 * @author maamria
 *
 */
public interface IUseTheory extends IInternalElement{

	IInternalElementType<IUseTheory> ELEMENT_TYPE = 
		RodinCore.getInternalElementType(TheoryPlugin.PLUGIN_ID + ".useTheory");
	
	/**
	 * Returns whether the use attribute has been set.
	 * @return whether the use attribute has been set
	 * @throws RodinDBException
	 */
	public boolean hasUseTheory() throws RodinDBException;
	
	/**
	 * Returns the deployed theory pointed at by the use directive.
	 * @return the deployed theory
	 * @throws RodinDBException
	 */
	public IDeployedTheoryRoot getUsedTheory() throws RodinDBException;
	
	/**
	 * Sets the deployed theory pointed at by the directive to the given theory.
	 * @param theory the deployed theory
	 * @param monitor the progress monitor
	 * @throws RodinDBException
	 */
	public void setUsedTheory(IDeployedTheoryRoot theory, IProgressMonitor monitor) throws RodinDBException;
}
