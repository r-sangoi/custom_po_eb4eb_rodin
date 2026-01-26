package fr.enseeiht.eventb.eb4eb;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ITraceableElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

public interface ISCAvailableAnnotation extends ITraceableElement {

	IInternalElementType<ISCAvailableAnnotation> ELEMENT_TYPE = RodinCore
			.getInternalElementType(EB4EBPlugin.PLUGIN_ID + ".scAvailableAnnotation");
	
	public IAnnotation getAnnotation() throws RodinDBException;
	
	public void setAnnotation(IAnnotation annotation, IProgressMonitor monitor) throws RodinDBException;
	
}
