package fr.enseeiht.eventb.eb4eb;

import org.eventb.core.ICommentedElement;
import org.eventb.core.IConfigurationElement;
import org.eventb.core.IEventBRoot;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

public interface IAnnotationCollectionRoot extends IEventBRoot, ICommentedElement, IConfigurationElement {
	
	public final static String IMG = "Annotation Collection";
	public static final String IMG_PATH = "icons/sample.png";
	
	IInternalElementType<IAnnotationCollectionRoot> ELEMENT_TYPE = RodinCore
			.getInternalElementType(EB4EBPlugin.PLUGIN_ID + ".annotationCollectionRoot");
	
	public IAnnotation[] getAnnotations() throws RodinDBException;
	
}
