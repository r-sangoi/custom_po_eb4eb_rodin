package org.eventb.theory.internal.ui.attr;

import static org.eventb.theory.core.TheoryAttributes.HYP_ATTRIBUTE;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.internal.ui.eventbeditor.manipulation.AbstractBooleanManipulation;
import org.eventb.theory.core.IHypElement;
import org.eventb.theory.internal.ui.Messages;
import org.eventb.ui.manipulation.IAttributeManipulation;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

@SuppressWarnings("restriction")
public class HypAttributeManipulation extends AbstractBooleanManipulation implements IAttributeManipulation {

	public HypAttributeManipulation() {
		super(Messages.inferenceRule_given_isHyp, Messages.inferenceRule_given_isNotHyp);
	}

	@Override
	public void setDefaultValue(IRodinElement element, IProgressMonitor monitor) throws RodinDBException {
		asHyp(element).setHyp(false, monitor);
	}

	@Override
	public boolean hasValue(IRodinElement element, IProgressMonitor monitor) throws RodinDBException {
		return asHyp(element).hasHypAttribute();
	}

	@Override
	public String getValue(IRodinElement element, IProgressMonitor monitor) throws RodinDBException {
		return getText(asHyp(element).isHyp());
	}

	@Override
	public void setValue(IRodinElement element, String value, IProgressMonitor monitor) throws RodinDBException {
		if(value.equals(TRUE)){
			asHyp(element).setHyp(true, monitor);
		}
		else if(value.equals(FALSE)){
			asHyp(element).setHyp(false, monitor);
		}
		else {
			logNotPossibleValues(HYP_ATTRIBUTE, value);
		}

	}

	@Override
	public void removeAttribute(IRodinElement element, IProgressMonitor monitor) throws RodinDBException {
		asHyp(element).removeAttribute(HYP_ATTRIBUTE, monitor);
	}
	
	private IHypElement asHyp(IRodinElement element){
		return (IHypElement) element;
	}

}
