/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

public interface IInductiveArgumentElement extends IInternalElement{

	boolean hasInductiveArgument() throws RodinDBException;
	
	String getInductiveArgument() throws RodinDBException;
	
	void setInductiveArgument(String inductiveArgument, IProgressMonitor monitor) throws RodinDBException;
	
}
