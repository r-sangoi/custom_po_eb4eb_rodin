/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.basis;

import org.eventb.theory.core.IDatatypeConstructor;
import org.eventb.theory.core.IDatatypeDefinition;
import org.eventb.theory.core.ITypeArgument;
import org.eventb.theory.core.TheoryElement;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author maamria
 *
 */
public class DatatypeDefinition extends TheoryElement implements
		IDatatypeDefinition {

	public DatatypeDefinition(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public IInternalElementType<? extends IInternalElement> getElementType() {
		return ELEMENT_TYPE;
	}

	@Override
	public IDatatypeConstructor getDatatypeConstructor(String name) {
		return getInternalElement(IDatatypeConstructor.ELEMENT_TYPE, name);
	}

	@Override
	public IDatatypeConstructor[] getDatatypeConstructors()
			throws RodinDBException {
		return getChildrenOfType(IDatatypeConstructor.ELEMENT_TYPE);
	}

	@Override
	public ITypeArgument getTypeArgument(String name) {
		return getInternalElement(ITypeArgument.ELEMENT_TYPE, name);
	}

	@Override
	public ITypeArgument[] getTypeArguments() throws RodinDBException {
		return getChildrenOfType(ITypeArgument.ELEMENT_TYPE);
	}

}
