/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.basis;

import org.eventb.theory.core.IRecursiveDefinitionCase;
import org.eventb.theory.core.IRecursiveOperatorDefinition;
import org.eventb.theory.core.TheoryElement;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

public class RecursiveOperatorDefinition extends TheoryElement implements
		IRecursiveOperatorDefinition {

	public RecursiveOperatorDefinition(String name, IRodinElement parent) {
		super(name, parent);
		// TODO Auto-generated constructor stub
	}

	@Override
	public IInternalElementType<? extends IInternalElement> getElementType() {
		// TODO Auto-generated method stub
		return ELEMENT_TYPE;
	}

	@Override
	public IRecursiveDefinitionCase getRecursiveDefinitionCase(String name) {
		// TODO Auto-generated method stub
		return getInternalElement(IRecursiveDefinitionCase.ELEMENT_TYPE, name);
	}

	@Override
	public IRecursiveDefinitionCase[] getRecursiveDefinitionCases()
			throws RodinDBException {
		// TODO Auto-generated method stub
		return getChildrenOfType(IRecursiveDefinitionCase.ELEMENT_TYPE);
	}

}
