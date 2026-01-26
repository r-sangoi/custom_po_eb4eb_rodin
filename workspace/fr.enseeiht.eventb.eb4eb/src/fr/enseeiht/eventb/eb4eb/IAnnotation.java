package fr.enseeiht.eventb.eb4eb;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ICommentedElement;
import org.eventb.core.IExpressionElement;
import org.eventb.core.IIdentifierElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

public interface IAnnotation extends IIdentifierElement, IExpressionElement, ICommentedElement {
	IInternalElementType<IAnnotation> ELEMENT_TYPE = RodinCore
			.getInternalElementType(EB4EBPlugin.PLUGIN_ID + ".annotation");

	public boolean hasTargetElementTypeId() throws RodinDBException;
	
	public String getTargetElementTypeId() throws RodinDBException;
	
	public void setTargetElementTypeId(String id, IProgressMonitor monitor) throws RodinDBException;
	
}
