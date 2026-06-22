/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.internal.ui.attr;

import static org.eventb.theory.core.TheoryAttributes.APPLICABILITY_ATTRIBUTE;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.internal.ui.eventbeditor.manipulation.AbstractAttributeManipulation;
import org.eventb.theory.core.IApplicabilityElement;
import org.eventb.theory.core.IApplicabilityElement.RuleApplicability;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

@SuppressWarnings("restriction")
public class ApplicabilityAttributeManipulation extends AbstractAttributeManipulation {

	@Override
	public void setDefaultValue(IRodinElement element, IProgressMonitor monitor) throws RodinDBException {
		asApplicabilityElement(element).setApplicability(RuleApplicability.INTERACTIVE, monitor);
	}

	@Override
	public boolean hasValue(IRodinElement element, IProgressMonitor monitor) throws RodinDBException {
		return asApplicabilityElement(element).hasApplicabilityAttribute();
	}

	@Override
	public String getValue(IRodinElement element, IProgressMonitor monitor) throws RodinDBException {
		return asApplicabilityElement(element).getAttributeValue(APPLICABILITY_ATTRIBUTE);
	}

	@Override
	public void setValue(IRodinElement element, String value, IProgressMonitor monitor) throws RodinDBException {
		asApplicabilityElement(element).setApplicability(RuleApplicability.getRuleApplicability(value), monitor);
	}

	@Override
	public void removeAttribute(IRodinElement element, IProgressMonitor monitor) throws RodinDBException {
		asApplicabilityElement(element).removeAttribute(APPLICABILITY_ATTRIBUTE, monitor);
	}

	@Override
	public String[] getPossibleValues(IRodinElement element, IProgressMonitor monitor) {
		return RuleApplicability.getPossibleApplicabilitiesAsStrings();
	}

	private IApplicabilityElement asApplicabilityElement(IRodinElement element){
		return (IApplicabilityElement) element;
	}

}
