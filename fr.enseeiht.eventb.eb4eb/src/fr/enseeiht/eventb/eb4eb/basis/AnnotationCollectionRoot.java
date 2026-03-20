package fr.enseeiht.eventb.eb4eb.basis;

import org.eventb.core.basis.EventBRoot;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

import fr.enseeiht.eventb.eb4eb.IAnnotation;
import fr.enseeiht.eventb.eb4eb.IAnnotationCollectionRoot;

public class AnnotationCollectionRoot extends EventBRoot implements IAnnotationCollectionRoot {

	public AnnotationCollectionRoot(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public IInternalElementType<? extends IInternalElement> getElementType() {
		return IAnnotationCollectionRoot.ELEMENT_TYPE;
	}

	@Override
	public IAnnotation[] getAnnotations() throws RodinDBException {
		return getChildrenOfType(IAnnotation.ELEMENT_TYPE);
	}

}
