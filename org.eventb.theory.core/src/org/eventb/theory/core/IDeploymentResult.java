/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core;

/**
 * Common protocol for a result of a deployment operation.
 * 
 * <p> A deployment operation can end in success or failure. In the case of failure, the deployment
 * result must provide an error message indication the encountered problem.
 * 
 * <p> This interface is not intended to be implemented by clients.
 * 
 * @author maamria
 *
 */
public interface IDeploymentResult {
	
	/**
	 * Returns whether the deployment operation has succeeded.
	 * @return whether the deployment operation has succeeded
	 */
	public boolean succeeded();
	
	/**
	 * Returns the error message if the deployment has not succeeded. This method is
	 * required to return a <code>null</code> value if deployment indeed succeeded.
	 * @return the error message
	 */
	public String getErrorMessage();

}
