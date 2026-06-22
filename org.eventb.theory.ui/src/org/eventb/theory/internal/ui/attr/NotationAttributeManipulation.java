/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.internal.ui.attr;

import static org.eventb.theory.core.TheoryAttributes.NOTATION_TYPE_ATTRIBUTE;
import static org.eventb.core.ast.extension.IOperatorProperties.Notation;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.extensions.maths.AstUtilities;
import org.eventb.internal.ui.eventbeditor.manipulation.AbstractAttributeManipulation;
import org.eventb.theory.core.INotationTypeElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author maamria
 *
 */
@SuppressWarnings("restriction")
public class NotationAttributeManipulation extends AbstractAttributeManipulation{

	public String[] getPossibleValues(IRodinElement element,IProgressMonitor monitor) {
		return AstUtilities.POSSIBLE_NOTATION_TYPES;
	}

	
	public String getValue(IRodinElement element, IProgressMonitor monitor)throws RodinDBException {
		INotationTypeElement cat = asNotationTypeElement(element);
		return cat.getNotationType().toString();
	}

	
	public boolean hasValue(IRodinElement element, IProgressMonitor monitor)throws RodinDBException {
		return asNotationTypeElement(element).hasNotationType();
	}

	
	public void removeAttribute(IRodinElement element, IProgressMonitor monitor)throws RodinDBException {
		asNotationTypeElement(element).removeAttribute(NOTATION_TYPE_ATTRIBUTE, monitor);

	}

	
	public void setDefaultValue(IRodinElement element, IProgressMonitor monitor)throws RodinDBException {
		asNotationTypeElement(element).setNotationType(Notation.PREFIX.toString(), monitor);

	}

	
	public void setValue(IRodinElement element, String value,IProgressMonitor monitor) throws RodinDBException {
		asNotationTypeElement(element).setNotationType(value, monitor);

	}

	INotationTypeElement asNotationTypeElement(IRodinElement e){
		return (INotationTypeElement) e;
	}
}
