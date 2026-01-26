package fr.enseeiht.eventb.eb4eb;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ICommentedElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

public interface IAvailableAnnotation extends ICommentedElement {
	
	IInternalElementType<IAvailableAnnotation> ELEMENT_TYPE = RodinCore
			.getInternalElementType(EB4EBPlugin.PLUGIN_ID + ".availableAnnotation");
	
	public boolean hasAnnotation() throws RodinDBException;
	
	public IAnnotation getAnnotation() throws RodinDBException;
	
	public void setAnnotation(IAnnotation annotation, IProgressMonitor monitor) throws RodinDBException;
}
