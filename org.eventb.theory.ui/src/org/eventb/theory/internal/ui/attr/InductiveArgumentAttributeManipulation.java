/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.internal.ui.attr;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.internal.ui.eventbeditor.manipulation.AbstractAttributeManipulation;
import org.eventb.theory.core.IInductiveArgumentElement;
import org.eventb.theory.core.INewOperatorDefinition;
import org.eventb.theory.core.IOperatorArgument;
import org.eventb.theory.core.TheoryAttributes;
import org.eventb.theory.internal.ui.TheoryUIUtils;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

@SuppressWarnings("restriction")
public class InductiveArgumentAttributeManipulation extends
		AbstractAttributeManipulation {

	@Override
	public void setDefaultValue(IRodinElement element, IProgressMonitor monitor)throws RodinDBException {

	}

	@Override
	public boolean hasValue(IRodinElement element, IProgressMonitor monitor)throws RodinDBException {
		return asInductiveArgumentElement(element).hasInductiveArgument();
	}

	@Override
	public String getValue(IRodinElement element, IProgressMonitor monitor)throws RodinDBException {
		return asInductiveArgumentElement(element).getInductiveArgument();
	}

	@Override
	public void setValue(IRodinElement element, String value,IProgressMonitor monitor) throws RodinDBException {
		asInductiveArgumentElement(element).setInductiveArgument(value, monitor);
	}

	@Override
	public void removeAttribute(IRodinElement element, IProgressMonitor monitor)throws RodinDBException {
		asInductiveArgumentElement(element).removeAttribute(TheoryAttributes.INDUCTIVE_ARGUMENT_ATTRIBUTE, monitor);

	}

	@Override
	public String[] getPossibleValues(IRodinElement element,IProgressMonitor monitor) {
		INewOperatorDefinition ancestor = element.getAncestor(INewOperatorDefinition.ELEMENT_TYPE);
		if (ancestor.exists()){
			try {
				IOperatorArgument[] arguments = ancestor.getOperatorArguments();
				List<String> list = new ArrayList<String>();
				for (IOperatorArgument arg : arguments){
					if (arg.hasIdentifierString()){
						list.add(arg.getIdentifierString());
					}
				}
				return list.toArray(new String[list.size()]);
				
			} catch(RodinDBException e){
				TheoryUIUtils.log(e, "Could not retrieve operator arguments");
			}
		}
		return new String[0];
	}
	
	private IInductiveArgumentElement asInductiveArgumentElement(IRodinElement element){
		return (IInductiveArgumentElement) element;
	}

}
