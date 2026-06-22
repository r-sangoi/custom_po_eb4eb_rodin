/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.internal.ui.attr;

import static org.eventb.theory.core.TheoryAttributes.FORMULA_ATTRIBUTE;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.internal.ui.eventbeditor.manipulation.AbstractAttributeManipulation;
import org.eventb.theory.core.IFormulaElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author maamria
 *
 */
@SuppressWarnings("restriction")
public class FormulaAttributeManipulation extends AbstractAttributeManipulation{

	public String[] getPossibleValues(IRodinElement element,IProgressMonitor monitor) {
		return null;
	}

	public String getValue(IRodinElement element, IProgressMonitor monitor)throws RodinDBException {
		return asFormula(element).getFormula();
	}

	public boolean hasValue(IRodinElement element, IProgressMonitor monitor)throws RodinDBException {
		return asFormula(element).hasFormula();

	}

	public void removeAttribute(IRodinElement element, IProgressMonitor monitor)throws RodinDBException {
		logCantRemove(FORMULA_ATTRIBUTE);
	}

	public void setDefaultValue(IRodinElement element, IProgressMonitor monitor)throws RodinDBException {
		asFormula(element).setFormula("", monitor);
	}

	public void setValue(IRodinElement element, String newValue,IProgressMonitor monitor) throws RodinDBException {
		asFormula(element).setFormula(newValue, monitor);
	}

	private IFormulaElement asFormula(IRodinElement element) {
		return (IFormulaElement) element;
	}

}
