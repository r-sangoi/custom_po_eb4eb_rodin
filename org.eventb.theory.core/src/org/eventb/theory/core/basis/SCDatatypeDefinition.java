/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.basis;

import static org.eventb.theory.core.TheoryAttributes.HAS_ERROR_ATTRIBUTE;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.basis.SCIdentifierElement;
import org.eventb.theory.core.ISCDatatypeConstructor;
import org.eventb.theory.core.ISCDatatypeDefinition;
import org.eventb.theory.core.ISCTypeArgument;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author maamria
 *
 */
public class SCDatatypeDefinition extends SCIdentifierElement implements ISCDatatypeDefinition{

	public SCDatatypeDefinition(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public ISCTypeArgument getTypeArgument(String name) {
		return getInternalElement(ISCTypeArgument.ELEMENT_TYPE, name);
	}

	@Override
	public ISCTypeArgument[] getTypeArguments() throws RodinDBException {
		return getChildrenOfType(ISCTypeArgument.ELEMENT_TYPE);
	}

	@Override
	public ISCDatatypeConstructor getConstructor(String name) {
		return getInternalElement(ISCDatatypeConstructor.ELEMENT_TYPE, name);
	}

	@Override
	public ISCDatatypeConstructor[] getConstructors() throws RodinDBException {
		return getChildrenOfType(ISCDatatypeConstructor.ELEMENT_TYPE);
	}

	@Override
	public IInternalElementType<? extends IInternalElement> getElementType() {
		return ELEMENT_TYPE;
	}
	
	@Override
	public boolean hasHasErrorAttribute() throws RodinDBException {
		return hasAttribute(HAS_ERROR_ATTRIBUTE);
	}

	@Override
	public boolean hasError() throws RodinDBException {
		return getAttributeValue(HAS_ERROR_ATTRIBUTE);
	}

	@Override
	public void setHasError(boolean hasError, IProgressMonitor monitor) throws RodinDBException {
		setAttributeValue(HAS_ERROR_ATTRIBUTE, hasError, monitor);
	}


}
