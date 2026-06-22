/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.internal.ui.attr;

import static org.eventb.theory.core.TheoryAttributes.FORMULA_TYPE_ATTRIBUTE;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.extension.IOperatorProperties.FormulaType;
import org.eventb.internal.ui.eventbeditor.manipulation.AbstractBooleanManipulation;
import org.eventb.theory.core.IFormulaTypeElement;
import org.eventb.theory.internal.ui.Messages;
import org.eventb.theory.internal.ui.TheoryUIUtils;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author maamria
 *
 */
@SuppressWarnings("restriction")
public class FormulaTypeAttributeManipulation extends AbstractBooleanManipulation{

	public FormulaTypeAttributeManipulation() {
		super(Messages.operator_isExpression, Messages.operator_isPredicate);
	}

	
	public String getValue(IRodinElement element, IProgressMonitor monitor)throws RodinDBException {
		return getText(asFormulaTypeElement(element).getAttributeValue(FORMULA_TYPE_ATTRIBUTE));
	}

	
	public boolean hasValue(IRodinElement element, IProgressMonitor monitor)throws RodinDBException {
		return asFormulaTypeElement(element).hasFormulaType();
	}

	
	public void removeAttribute(IRodinElement element, IProgressMonitor monitor)throws RodinDBException {
		asFormulaTypeElement(element).removeAttribute(FORMULA_TYPE_ATTRIBUTE, monitor);

	}

	
	public void setDefaultValue(IRodinElement element, IProgressMonitor monitor)throws RodinDBException {
		asFormulaTypeElement(element).setFormulaType(FormulaType.EXPRESSION, monitor);

	}

	
	public void setValue(IRodinElement element, String value,IProgressMonitor monitor) throws RodinDBException {
		if(value.equals(TRUE)){
			asFormulaTypeElement(element).setFormulaType(TheoryUIUtils.getFormulaType(true), monitor);
		}
		else if(value.equals(FALSE)){
			asFormulaTypeElement(element).setFormulaType(TheoryUIUtils.getFormulaType(false), monitor);
		}
		else {
			logNotPossibleValues(FORMULA_TYPE_ATTRIBUTE, value);
		}

	}

	IFormulaTypeElement asFormulaTypeElement(IRodinElement element){
		return (IFormulaTypeElement) element;
	}
	
}
