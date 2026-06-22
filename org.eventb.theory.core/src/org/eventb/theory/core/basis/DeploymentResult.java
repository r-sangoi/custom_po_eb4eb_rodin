/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.basis;

import org.eventb.theory.core.IDeploymentResult;

/**
 * @author maamria
 *
 */
public class DeploymentResult implements IDeploymentResult{

	private boolean succeeded;
	private String errorMessage;

	public DeploymentResult(boolean succeeded, String errorMessage){
		this.succeeded = succeeded;
		this.errorMessage = errorMessage;
	}
	
	@Override
	public boolean succeeded() {
		// TODO Auto-generated method stub
		return succeeded;
	}

	
	@Override
	public String getErrorMessage() {
		// TODO Auto-generated method stub
		return errorMessage;
	}

}
