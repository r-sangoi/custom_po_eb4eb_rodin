package fr.enseeiht.eventb.eb4eb.basis;

import static fr.enseeiht.eventb.eb4eb.EB4EBAttributes.ANNOTATION_TARGET_ELEMENT;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.basis.EventBElement;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

import fr.enseeiht.eventb.eb4eb.IAnnotation;

public class Annotation extends EventBElement implements IAnnotation {

	public Annotation(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public IInternalElementType<? extends IInternalElement> getElementType() {
		return IAnnotation.ELEMENT_TYPE;
	}

	@Override
	public boolean hasTargetElementTypeId() throws RodinDBException {
		return hasAttribute(ANNOTATION_TARGET_ELEMENT);
	}

	@Override
	public String getTargetElementTypeId() throws RodinDBException {
		return getAttributeValue(ANNOTATION_TARGET_ELEMENT);
	}

	@Override
	public void setTargetElementTypeId(String id, IProgressMonitor monitor) throws RodinDBException {
		setAttributeValue(ANNOTATION_TARGET_ELEMENT, id, monitor);
	}

}
