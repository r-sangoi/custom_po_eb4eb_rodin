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
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

/**
 * 
 * @author maamria
 *
 */
public interface IImportTheoryElement extends IInternalElement{

	public boolean hasImportTheory() throws RodinDBException;
	
	public IDeployedTheoryRoot getImportTheory() throws RodinDBException;
	
	public void setImportTheory(IDeployedTheoryRoot root, IProgressMonitor monitor) throws RodinDBException;
	
	public IRodinProject getImportTheoryProject() throws RodinDBException ;
	
}
