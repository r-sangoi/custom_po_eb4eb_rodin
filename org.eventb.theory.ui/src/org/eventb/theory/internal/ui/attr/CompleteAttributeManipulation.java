package org.eventb.theory.internal.ui.attr;

import static org.eventb.theory.core.TheoryAttributes.COMPLETE_ATTRIBUTE;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.internal.ui.eventbeditor.manipulation.AbstractBooleanManipulation;
import org.eventb.theory.core.ICompleteElement;
import org.eventb.theory.internal.ui.Messages;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

@SuppressWarnings("restriction")
public class CompleteAttributeManipulation extends AbstractBooleanManipulation{

	public CompleteAttributeManipulation() {
		super(Messages.rewriteRule_isComplete, Messages.rewriteRule_isIncomplete);
	}

	public String getValue(IRodinElement element, IProgressMonitor monitor)throws RodinDBException {
		return getText(asComplete(element).isComplete());
	}

	
	public boolean hasValue(IRodinElement element, IProgressMonitor monitor)throws RodinDBException {
		return asComplete(element).hasComplete();
	}

	
	public void removeAttribute(IRodinElement element, IProgressMonitor monitor)throws RodinDBException {
		asComplete(element).removeAttribute(COMPLETE_ATTRIBUTE, monitor);

	}

	
	public void setDefaultValue(IRodinElement element, IProgressMonitor monitor)throws RodinDBException {
		asComplete(element).setComplete(false, monitor);

	}

	
	public void setValue(IRodinElement element, String value,IProgressMonitor monitor) throws RodinDBException {
		if(value.equals(TRUE)){
			asComplete(element).setComplete(true, monitor);
		}
		else if(value.equals(FALSE)){
			asComplete(element).setComplete(false, monitor);
		}
		else {
			logNotPossibleValues(COMPLETE_ATTRIBUTE, value);
		}

	}

	ICompleteElement asComplete(IRodinElement element){
		return (ICompleteElement) element;
	}
}
