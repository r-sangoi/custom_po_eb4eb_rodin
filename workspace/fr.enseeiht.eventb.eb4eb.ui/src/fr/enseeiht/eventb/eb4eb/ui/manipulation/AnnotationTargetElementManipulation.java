package fr.enseeiht.eventb.eb4eb.ui.manipulation;

import static fr.enseeiht.eventb.eb4eb.EB4EBAttributes.ANNOTATION_TARGET_ELEMENT;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.ui.manipulation.IAttributeManipulation;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
//import org.rodinp.internal.core.ElementTypeManager;

import fr.enseeiht.eventb.eb4eb.IAnnotation;

public class AnnotationTargetElementManipulation implements IAttributeManipulation {

	private IAnnotation asAnnotation(IRodinElement element) {
		assert element instanceof IAnnotation;
		return (IAnnotation) element;
	}

	@Override
	public void setDefaultValue(IRodinElement element, IProgressMonitor monitor) throws RodinDBException {
		asAnnotation(element).setTargetElementTypeId("org.eventb.core.any", monitor);
	}

	@Override
	public boolean hasValue(IRodinElement element, IProgressMonitor monitor) throws RodinDBException {
		if (element == null)
			return false;
		return asAnnotation(element).hasTargetElementTypeId();
	}

	@Override
	public String getValue(IRodinElement element, IProgressMonitor monitor) throws CoreException {
		return asAnnotation(element).getTargetElementTypeId();
	}

	@Override
	public void setValue(IRodinElement element, String value, IProgressMonitor monitor) throws RodinDBException {
		asAnnotation(element).setTargetElementTypeId(value, monitor);
	}

	@Override
	public void removeAttribute(IRodinElement element, IProgressMonitor monitor) throws RodinDBException {
		asAnnotation(element).removeAttribute(ANNOTATION_TARGET_ELEMENT, monitor);
	}

	@Override
	public String[] getPossibleValues(IRodinElement element, IProgressMonitor monitor) {
//		final ElementTypeManager manager = ElementTypeManager.getInstance();
		String[] possibleValues = {"org.eventb.core.any", "org.eventb.core.machineFile", "org.eventb.core.invariant"};
		return possibleValues;
	}

}
