package fr.enseeiht.eventb.eb4eb.basis;

import static fr.enseeiht.eventb.eb4eb.EB4EBAttributes.AVAILABLE_ANNOTATION_ATTRIBUTE;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.basis.EventBElement;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

import fr.enseeiht.eventb.eb4eb.IAnnotation;
import fr.enseeiht.eventb.eb4eb.IAvailableAnnotation;

public class AvailableAnnotation extends EventBElement implements IAvailableAnnotation {

	public AvailableAnnotation(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public IInternalElementType<? extends IInternalElement> getElementType() {
		return IAvailableAnnotation.ELEMENT_TYPE;
	}

	@Override
	public boolean hasAnnotation() throws RodinDBException {
		return hasAttribute(AVAILABLE_ANNOTATION_ATTRIBUTE);
	}

	@Override
	public IAnnotation getAnnotation() throws RodinDBException {
		return (IAnnotation) getAttributeValue(AVAILABLE_ANNOTATION_ATTRIBUTE);// ? "DeadlockFreeness" : "None"
	}

	@Override
	public void setAnnotation(IAnnotation value, IProgressMonitor monitor) throws RodinDBException {
		setAttributeValue(AVAILABLE_ANNOTATION_ATTRIBUTE, value, monitor);
	}

}
