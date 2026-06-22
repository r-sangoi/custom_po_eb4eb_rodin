/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.ui.explorer.model;

import org.eventb.core.IPSStatus;
import org.eventb.theory.core.INewOperatorDefinition;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;

import fr.systerel.internal.explorer.model.IModelElement;
import fr.systerel.internal.explorer.navigator.ExplorerUtils;

/**
 * @author maamria
 *
 */
@SuppressWarnings("restriction")
public class ModelOperator extends TheoryModelPOContainer{

	public ModelOperator(INewOperatorDefinition op, IModelElement parent){
		internalOp = op;
		this.parent = parent;
	}

	private INewOperatorDefinition internalOp;
	
	
	public INewOperatorDefinition getInternalOp() {
		return internalOp;
	}

	@Override
	public IRodinElement getInternalElement() {
		return internalOp;
	}

	@Override
	public Object getParent(boolean complex) {
		if (parent instanceof ModelTheory ) {
			return ((ModelTheory) parent).op_node;
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
