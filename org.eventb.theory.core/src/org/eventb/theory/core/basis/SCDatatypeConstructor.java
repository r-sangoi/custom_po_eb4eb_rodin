/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.basis;

import org.eventb.core.basis.SCIdentifierElement;
import org.eventb.theory.core.ISCConstructorArgument;
import org.eventb.theory.core.ISCDatatypeConstructor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author maamria
 *
 */
public class SCDatatypeConstructor extends SCIdentifierElement implements ISCDatatypeConstructor{

	public SCDatatypeConstructor(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public ISCConstructorArgument getConstructorArgument(String name) {
		return getInternalElement(ISCConstructorArgument.ELEMENT_TYPE, name);
	}

	@Override
	public ISCConstructorArgument[] getConstructorArguments()
			throws RodinDBException {
		return getChildrenOfType(ISCConstructorArgument.ELEMENT_TYPE);
	}

	
	@Override
	public IInternalElementType<? extends IInternalElement> getElementType() {
		return ELEMENT_TYPE;
	}

}
