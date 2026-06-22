/*******************************************************************************
 * Copyright (c) 2010, 2020 University of Southampton and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.basis;

import static org.eventb.theory.core.TheoryAttributes.HYP_ATTRIBUTE;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.theory.core.ISCGiven;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author maamria
 *
 */
public class SCGiven extends SCPredicatePatternElement implements ISCGiven {

	public SCGiven(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public IInternalElementType<? extends IInternalElement> getElementType() {
		return ELEMENT_TYPE;
	}

	@Override
	public boolean hasHypAttribute() throws RodinDBException {
		return hasAttribute(HYP_ATTRIBUTE);
	}
	
	@Override
	public boolean isHyp() throws RodinDBException {
		return getAttributeValue(HYP_ATTRIBUTE);
	}
	
	@Override
	public void setHyp(boolean isHyp, IProgressMonitor monitor) throws RodinDBException {
		setAttributeValue(HYP_ATTRIBUTE, isHyp, monitor);
	}

}
