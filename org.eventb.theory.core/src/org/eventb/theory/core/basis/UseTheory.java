/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.basis;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.IUseTheory;
import org.eventb.theory.core.TheoryElement;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author maamria
 *
 */
public class UseTheory extends TheoryElement implements IUseTheory {

	public UseTheory(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public boolean hasUseTheory() throws RodinDBException {
		// TODO Auto-generated method stub
		return hasAttribute(EventBAttributes.SCTARGET_ATTRIBUTE);
	}

	@Override
	public IDeployedTheoryRoot getUsedTheory() throws RodinDBException {
		// TODO Auto-generated method stub
		return (IDeployedTheoryRoot) getAttributeValue(EventBAttributes.SCTARGET_ATTRIBUTE);
	}

	@Override
	public void setUsedTheory(IDeployedTheoryRoot theory,
			IProgressMonitor monitor) throws RodinDBException {
		setAttributeValue(EventBAttributes.SCTARGET_ATTRIBUTE, theory, monitor);
		
	}

	@Override
	public IInternalElementType<? extends IInternalElement> getElementType() {
		// TODO Auto-generated method stub
		return ELEMENT_TYPE;
	}

	
}
