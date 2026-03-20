package fr.enseeiht.eventb.eb4eb.basis;

import static fr.enseeiht.eventb.eb4eb.EB4EBAttributes.AVAILABLE_ANNOTATION_ATTRIBUTE;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.basis.EventBElement;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

import fr.enseeiht.eventb.eb4eb.IAnnotation;
import fr.enseeiht.eventb.eb4eb.ISCAvailableAnnotation;

public class SCAvailableAnnotation extends EventBElement implements ISCAvailableAnnotation {

	public SCAvailableAnnotation(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public IInternalElementType<? extends IInternalElement> getElementType() {
		return ISCAvailableAnnotation.ELEMENT_TYPE;
	}

	@Override
	public IAnnotation getAnnotation() throws RodinDBException {
		return (IAnnotation) getAttributeValue(AVAILABLE_ANNOTATION_ATTRIBUTE);
	}

	@Override
	public void setAnnotation(IAnnotation value, IProgressMonitor monitor) throws RodinDBException {
		setAttributeValue(AVAILABLE_ANNOTATION_ATTRIBUTE, value, monitor);
	}

}
