/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.basis;

import org.eventb.theory.core.IDirectOperatorDefinition;
import org.eventb.theory.core.INewOperatorDefinition;
import org.eventb.theory.core.IOperatorArgument;
import org.eventb.theory.core.IOperatorWDCondition;
import org.eventb.theory.core.IRecursiveOperatorDefinition;
import org.eventb.theory.core.TheoryElement;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author maamria
 *
 */
public class NewOperatorDefinition extends TheoryElement implements INewOperatorDefinition{

	
	/**
	 * @param name
	 * @param parent
	 */
	public NewOperatorDefinition(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public IInternalElementType<? extends IInternalElement> getElementType() {
		return ELEMENT_TYPE;
	}

	
	@Override
	public IOperatorArgument getOperatorArgument(String name) {
		return getInternalElement(IOperatorArgument.ELEMENT_TYPE, name);
	}

	@Override
	public IOperatorArgument[] getOperatorArguments() throws RodinDBException {
		return getChildrenOfType(IOperatorArgument.ELEMENT_TYPE);
	}
	
	@Override
	public IDirectOperatorDefinition getDirectOperatorDefinition(String name) {
		return getInternalElement(IDirectOperatorDefinition.ELEMENT_TYPE, name);
	}

	@Override
	public IDirectOperatorDefinition[] getDirectOperatorDefinitions()
			throws RodinDBException {
		// there should be just one SC
		return getChildrenOfType(IDirectOperatorDefinition.ELEMENT_TYPE);
	}

	@Override
	public IOperatorWDCondition[] getOperatorWDConditions()
			throws RodinDBException {
		// there should be just one SC
		return getChildrenOfType(IOperatorWDCondition.ELEMENT_TYPE);
	}

	
	@Override
	public IOperatorWDCondition getOperatorWDCondition(String name) {
		return getInternalElement(IOperatorWDCondition.ELEMENT_TYPE, name);
	}

	@Override
	public IRecursiveOperatorDefinition getRecursiveOperatorDefinition(
			String name) {
		// TODO Auto-generated method stub
		return getInternalElement(IRecursiveOperatorDefinition.ELEMENT_TYPE, name);
	}

	@Override
	public IRecursiveOperatorDefinition[] getRecursiveOperatorDefinitions()
			throws RodinDBException {
		// TODO Auto-generated method stub
		return getChildrenOfType(IRecursiveOperatorDefinition.ELEMENT_TYPE);
	}

}
