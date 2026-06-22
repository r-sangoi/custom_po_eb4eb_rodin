/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.internal.ui.attr;

import static org.eventb.theory.core.TheoryAttributes.COMMUTATIVE_ATTRIBUTE;
import static org.eventb.theory.internal.ui.Messages.operator_isCommutative;
import static org.eventb.theory.internal.ui.Messages.operator_isNotCommutative;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.internal.ui.eventbeditor.manipulation.AbstractAttributeManipulation;
import org.eventb.theory.core.ICommutativeElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author maamria
 *
 */
@SuppressWarnings("restriction")
public class CommutativeAttributeManipulation extends AbstractAttributeManipulation{

	@Override
	public void setDefaultValue(IRodinElement element, IProgressMonitor monitor)throws RodinDBException {
		asCommutativeElement(element).setCommutative(false, monitor);
	}

	@Override
	public boolean hasValue(IRodinElement element, IProgressMonitor monitor)throws RodinDBException {
		return asCommutativeElement(element).hasCommutativeAttribute();
	}

	
	@Override
	public String getValue(IRodinElement element, IProgressMonitor monitor)throws RodinDBException {
		return asCommutativeElement(element).isCommutative()?operator_isCommutative:operator_isNotCommutative;
	}
	
	@Override
	public void setValue(IRodinElement element, String value,IProgressMonitor monitor) throws RodinDBException {
		if(value.equals(operator_isCommutative)){
			asCommutativeElement(element).setCommutative(true, monitor);
		}
		else asCommutativeElement(element).setCommutative(false, monitor);
	}

	@Override
	public void removeAttribute(IRodinElement element, IProgressMonitor monitor)throws RodinDBException {
		asCommutativeElement(element).removeAttribute(COMMUTATIVE_ATTRIBUTE, monitor);
		
	}

	@Override
	public String[] getPossibleValues(IRodinElement element,IProgressMonitor monitor) {
		return new String[]{operator_isCommutative, operator_isNotCommutative};
	}

	protected ICommutativeElement asCommutativeElement(IRodinElement element){
		return (ICommutativeElement) element;
	}

}
