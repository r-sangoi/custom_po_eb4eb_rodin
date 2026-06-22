/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.basis;

import org.eventb.core.ITraceableElement;
import org.eventb.theory.core.ISCRecursiveDefinitionCase;
import org.eventb.theory.core.ISCRecursiveOperatorDefinition;
import org.eventb.theory.core.TheoryElement;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

public class SCRecursiveOperatorDefinition extends TheoryElement implements
		ISCRecursiveOperatorDefinition, ITraceableElement {

	public SCRecursiveOperatorDefinition(String name, IRodinElement parent) {
		super(name, parent);
		// TODO Auto-generated constructor stub
	}

	@Override
	public ISCRecursiveDefinitionCase getRecursiveDefinitionCase(String name) {
		// TODO Auto-generated method stub
		return getInternalElement(ISCRecursiveDefinitionCase.ELEMENT_TYPE, name);
	}

	@Override
	public ISCRecursiveDefinitionCase[] getRecursiveDefinitionCases()
			throws RodinDBException {
		// TODO Auto-generated method stub
		return getChildrenOfType(ISCRecursiveDefinitionCase.ELEMENT_TYPE);
	}

	@Override
	public IInternalElementType<? extends IInternalElement> getElementType() {
		// TODO Auto-generated method stub
		return ELEMENT_TYPE;
	}

	

}
