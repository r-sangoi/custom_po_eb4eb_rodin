/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
  *******************************************************************************/


package org.eventb.theory.ui.explorer.model;

import org.eventb.core.IPSStatus;
import org.eventb.theory.core.ITheorem;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;

import fr.systerel.internal.explorer.model.IModelElement;
import fr.systerel.internal.explorer.navigator.ExplorerUtils;

@SuppressWarnings("restriction")
public class ModelTheorem extends TheoryModelPOContainer {
	public ModelTheorem(ITheorem thm, IModelElement parent){
		internalTheorem = thm;
		this.parent = parent;
	}

	private ITheorem internalTheorem;
	
	
	public ITheorem getInternalTheorem() {
		return internalTheorem;
	}

	@Override
	public IRodinElement getInternalElement() {
		return internalTheorem;
	}

	@Override
	public Object getParent(boolean complex) {
		if (parent instanceof ModelTheory ) {
			return ((ModelTheory) parent).thm_node;
		}
		return parent;
	}


	@Override
	public Object[] getChildren(IInternalElementType<?> type, boolean complex) {
		if (type != IPSStatus.ELEMENT_TYPE) {
			if (ExplorerUtils.DEBUG) {
				System.out.println("Unsupported children type for event: " +type);
			}
			return new Object[0];
		}
		return getIPSStatuses();
	}
	
}
