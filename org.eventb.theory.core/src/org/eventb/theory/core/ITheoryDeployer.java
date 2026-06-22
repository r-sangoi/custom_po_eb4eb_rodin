/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Common protocol for theories deployer.
 * 
 * <p> A theory deployer can deploy statically checked theory files to a certain project.
 * <p> Information about the success of the deployment is also provided by the deployer.
 * 
 * <p> This interface is not intended to be implemented by clients.
 * 
 * @see IDeployedTheoryRoot
 * @see IDeploymentResult
 * 
 * @author maamria
 *
 */
public interface ITheoryDeployer extends IWorkspaceRunnable{
	
	/**
	 * Deploys a given statically checked theory to the current project.
	 * @param monitor the progress monitor
	 * @return whether deployment has been attempted
	 * @throws CoreException
	 */
	public boolean deploy(IProgressMonitor monitor) throws CoreException;
	
	/**
	 * Returns the deployment result of this operation.
	 * @return the deployment result
	 */
	public IDeploymentResult getDeploymentResult();
	
}
