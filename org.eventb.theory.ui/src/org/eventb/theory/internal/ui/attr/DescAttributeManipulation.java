package org.eventb.theory.internal.ui.attr;

import static org.eventb.theory.core.TheoryAttributes.DESC_ATTRIBUTE;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.internal.ui.eventbeditor.manipulation.AbstractAttributeManipulation;
import org.eventb.theory.core.IDescriptionElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

@SuppressWarnings("restriction")
public class DescAttributeManipulation extends AbstractAttributeManipulation
implements org.eventb.ui.manipulation.IAttributeManipulation {

	
	public String[] getPossibleValues(IRodinElement element,IProgressMonitor monitor) {
		return null;
	}

	
	public String getValue(IRodinElement element, IProgressMonitor monitor)throws RodinDBException {
		return asDesc(element).getDescription();
	}

	
	public boolean hasValue(IRodinElement element, IProgressMonitor monitor)throws RodinDBException {
		return asDesc(element).hasDescription();
	}

	
	public void removeAttribute(IRodinElement element, IProgressMonitor monitor)throws RodinDBException {
		logCantRemove(DESC_ATTRIBUTE);
	}

	
	public void setDefaultValue(IRodinElement element, IProgressMonitor monitor)throws RodinDBException {
		asDesc(element).setDescription("Describe Me!", monitor);

	}

	
	public void setValue(IRodinElement element, String value,IProgressMonitor monitor) throws RodinDBException {
		asDesc(element).setDescription(value, monitor);

	}

	private IDescriptionElement asDesc(IRodinElement element){
		return (IDescriptionElement) element;
	}
	
}
