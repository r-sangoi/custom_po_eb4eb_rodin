package fr.enseeiht.eventb.eb4eb.ui.manipulation;

import static fr.enseeiht.eventb.eb4eb.EB4EBAttributes.AVAILABLE_ANNOTATION_ATTRIBUTE;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.ui.manipulation.IAttributeManipulation;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

import fr.enseeiht.eventb.eb4eb.IAnnotation;
import fr.enseeiht.eventb.eb4eb.IAnnotationCollectionRoot;
import fr.enseeiht.eventb.eb4eb.IAvailableAnnotation;

public class AvailableAnnotationAttributeManipulation implements IAttributeManipulation {

	private IAvailableAnnotation asAvailableAnnotation(IRodinElement element) {
		assert element instanceof IAvailableAnnotation;
		return (IAvailableAnnotation) element;
	}
	
	@Override
	public void setDefaultValue(IRodinElement element, IProgressMonitor monitor) throws RodinDBException {
	}

	@Override
	public boolean hasValue(IRodinElement element, IProgressMonitor monitor) throws RodinDBException {
		if (element == null)
			return false;
		return asAvailableAnnotation(element).hasAnnotation();
	}

	@Override
	public String getValue(IRodinElement element, IProgressMonitor monitor) throws CoreException {
		return asAvailableAnnotation(element).getAnnotation().getIdentifierString();
	}

	@Override
	public void setValue(IRodinElement element, String value, IProgressMonitor monitor) throws RodinDBException {
		IAnnotation targetAnnotation = null;
		IRodinProject rodinProject = element.getRodinProject();
		try {
			IAnnotationCollectionRoot[] acs = rodinProject.getRootElementsOfType(IAnnotationCollectionRoot.ELEMENT_TYPE);
			for (IAnnotationCollectionRoot ac : acs) {
				for (IAnnotation annotation : ac.getAnnotations()) {
					if(annotation.getIdentifierString().equals(value))
						targetAnnotation = annotation;
				}
			}
		} catch (RodinDBException e) {
			e.printStackTrace(); //TODO
		}
		asAvailableAnnotation(element).setAnnotation(targetAnnotation, monitor);
	}

	@Override
	public void removeAttribute(IRodinElement element, IProgressMonitor monitor) throws RodinDBException {
		asAvailableAnnotation(element).removeAttribute(AVAILABLE_ANNOTATION_ATTRIBUTE, monitor);
	}

	@Override
	public String[] getPossibleValues(IRodinElement element, IProgressMonitor monitor) {
		//TODO : getParent de IRodinElement pour savoir quel type d'annotation récupérer

		IRodinProject rodinProject = element.getRodinProject();
		Set<String> possibleValues = new HashSet<String>();
		
		try {
			IAnnotationCollectionRoot[] acs = rodinProject.getRootElementsOfType(IAnnotationCollectionRoot.ELEMENT_TYPE);
			for (IAnnotationCollectionRoot ac : acs) {
				for (IAnnotation annotation : ac.getAnnotations()) {
					possibleValues.add(annotation.getIdentifierString());
					System.out.println(element.getParent().getElementType().getId());
					System.out.println(annotation.getTargetElementTypeId());
				}
			}
		} catch (RodinDBException e) {
			e.printStackTrace(); //TODO
		}
		
		return possibleValues.toArray(new String[possibleValues.size()]);
	}

}
