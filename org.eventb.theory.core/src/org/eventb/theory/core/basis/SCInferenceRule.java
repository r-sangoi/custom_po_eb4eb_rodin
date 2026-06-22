/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.basis;

import org.eventb.theory.core.ISCGiven;
import org.eventb.theory.core.ISCInfer;
import org.eventb.theory.core.ISCInferenceRule;
import org.eventb.theory.core.TheoryElement;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author maamria
 *
 */
public class SCInferenceRule extends TheoryElement implements ISCInferenceRule{

	public SCInferenceRule(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public ISCGiven getGiven(String name) {
		// TODO Auto-generated method stub
		return getInternalElement(ISCGiven.ELEMENT_TYPE, name);
	}

	@Override
	public ISCGiven[] getGivens() throws RodinDBException {
		// TODO Auto-generated method stub
		return getChildrenOfType(ISCGiven.ELEMENT_TYPE);
	}

	@Override
	public ISCInfer getInfer(String name) {
		// TODO Auto-generated method stub
		return getInternalElement(ISCInfer.ELEMENT_TYPE, name);
	}

	@Override
	public ISCInfer[] getInfers() throws RodinDBException {
		// TODO Auto-generated method stub
		return getChildrenOfType(ISCInfer.ELEMENT_TYPE);
	}

	@Override
	public IInternalElementType<? extends IInternalElement> getElementType() {
		// TODO Auto-generated method stub
		return ELEMENT_TYPE;
	}

}
