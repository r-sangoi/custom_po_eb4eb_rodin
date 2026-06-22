package org.eventb.theory.internal.ui.attr;

import static org.eventb.theory.core.TheoryAttributes.GIVEN_TYPE_ATTRIBUTE;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.internal.ui.eventbeditor.manipulation.AbstractAttributeManipulation;
import org.eventb.theory.core.IGivenTypeElement;
import org.eventb.theory.internal.ui.TheoryUIUtils;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

@SuppressWarnings("restriction")
public class GivenTypeAttributeManipulation extends AbstractAttributeManipulation {

	@Override
	public void setDefaultValue(IRodinElement element, IProgressMonitor monitor)throws RodinDBException {
	}

	@Override
	public boolean hasValue(IRodinElement element, IProgressMonitor monitor)throws RodinDBException {
		return asGivenTypeElement(element).hasGivenType();
	}

	@Override
	public String getValue(IRodinElement element, IProgressMonitor monitor)throws RodinDBException {
		return asGivenTypeElement(element).getGivenType();
	}

	@Override
	public void setValue(IRodinElement element, String value,IProgressMonitor monitor) throws RodinDBException {
		asGivenTypeElement(element).setGivenType(value, monitor);
	}

	@Override
	public void removeAttribute(IRodinElement element, IProgressMonitor monitor)throws RodinDBException {
		asGivenTypeElement(element).removeAttribute(GIVEN_TYPE_ATTRIBUTE, monitor);
		
	}

	@Override
	public String[] getPossibleValues(IRodinElement element,IProgressMonitor monitor) {
		try {
			return TheoryUIUtils.getUnusedTypeParameters(element);
		} catch (RodinDBException e) {
			TheoryUIUtils.log(e, "cannot get possible values for given type for element "+ element);
		}
		return new String[0];
	}

	private IGivenTypeElement asGivenTypeElement(IRodinElement element){
		return (IGivenTypeElement) element;
	}
}
