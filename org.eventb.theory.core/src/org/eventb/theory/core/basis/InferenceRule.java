/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.basis;

import org.eventb.theory.core.IGiven;
import org.eventb.theory.core.IInfer;
import org.eventb.theory.core.IInferenceRule;
import org.eventb.theory.core.TheoryElement;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author maamria
 *
 */
public class InferenceRule extends TheoryElement implements IInferenceRule{

	public InferenceRule(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public IGiven getGiven(String name) {
		return getInternalElement(IGiven.ELEMENT_TYPE, name);
	}

	@Override
	public IGiven[] getGivens() throws RodinDBException {
		return getChildrenOfType(IGiven.ELEMENT_TYPE);
	}

	@Override
	public IInfer getInfer(String name) {
		return getInternalElement(IInfer.ELEMENT_TYPE, name);
	}

	@Override
	public IInfer[] getInfers() throws RodinDBException {
		return getChildrenOfType(IInfer.ELEMENT_TYPE);
	}

	@Override
	public IInternalElementType<? extends IInternalElement> getElementType() {
		return ELEMENT_TYPE;
	}

}
