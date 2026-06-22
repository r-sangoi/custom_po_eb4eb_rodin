/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.basis;

import static org.eventb.theory.core.TheoryAttributes.IMPORT_THEORY_ATTRIBUTE;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.theory.core.IImportTheory;
import org.eventb.theory.core.ISCImportTheory;
import org.eventb.theory.core.TheoryElement;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

public class SCImportTheory extends TheoryElement implements ISCImportTheory {

	public SCImportTheory(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public IInternalElementType<? extends IInternalElement> getElementType() {
		// TODO Auto-generated method stub
		return ELEMENT_TYPE;
	}

	@Override
	public void setImportTheory(IImportTheory root, IProgressMonitor monitor)
			throws RodinDBException {
		setAttributeValue(IMPORT_THEORY_ATTRIBUTE, root, monitor);
	}

}
