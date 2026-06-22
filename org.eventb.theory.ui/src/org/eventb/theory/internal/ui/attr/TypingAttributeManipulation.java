package org.eventb.theory.internal.ui.attr;

import static org.eventb.theory.core.TheoryAttributes.TYPE_ATTRIBUTE;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.internal.ui.eventbeditor.manipulation.AbstractAttributeManipulation;
import org.eventb.theory.core.ITypeElement;
import org.eventb.ui.manipulation.IAttributeManipulation;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

@SuppressWarnings("restriction")
public class TypingAttributeManipulation extends AbstractAttributeManipulation 
implements IAttributeManipulation {

	public String[] getPossibleValues(IRodinElement element,IProgressMonitor monitor) {
		return null;
	}

	public String getValue(IRodinElement element, IProgressMonitor monitor)throws RodinDBException {
		return asTypingElmnt(element).getType();
	}

	
	public boolean hasValue(IRodinElement element, IProgressMonitor monitor)throws RodinDBException {
		return asTypingElmnt(element).hasType();
	}

	
	public void removeAttribute(IRodinElement element, IProgressMonitor monitor)throws RodinDBException {
		logCantRemove(TYPE_ATTRIBUTE);

	}

	
	public void setDefaultValue(IRodinElement element, IProgressMonitor monitor)throws RodinDBException {
		asTypingElmnt(element).setType("", monitor);

	}

	public void setValue(IRodinElement element, String value,IProgressMonitor monitor) throws RodinDBException {
		asTypingElmnt(element).setType(value, monitor) ;

	}
	
	private ITypeElement asTypingElmnt(IRodinElement e){
		return (ITypeElement) e;
	}

}
