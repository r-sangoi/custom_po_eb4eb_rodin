/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.basis;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.basis.SCPredicateElement;
import org.eventb.theory.core.ISCTheorem;
import org.eventb.theory.core.TheoryAttributes;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author maamria
 *
 */
public class SCTheorem extends SCPredicateElement implements ISCTheorem{

	public SCTheorem(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public IInternalElementType<? extends IInternalElement> getElementType() {
		// TODO Auto-generated method stub
		return ELEMENT_TYPE;
	}
	
	@Override
	public boolean hasOrderAttribute() throws RodinDBException {
		return hasAttribute(TheoryAttributes.ORDER_ATTRIBUTE);
	}
	
	@Override
	public int getOrder() throws RodinDBException {
		return getAttributeValue(TheoryAttributes.ORDER_ATTRIBUTE);
	}
	
	@Override
	public void setOrder(int newOrder, IProgressMonitor monitor) throws RodinDBException {
		setAttributeValue(TheoryAttributes.ORDER_ATTRIBUTE, newOrder, monitor);
	}


}
