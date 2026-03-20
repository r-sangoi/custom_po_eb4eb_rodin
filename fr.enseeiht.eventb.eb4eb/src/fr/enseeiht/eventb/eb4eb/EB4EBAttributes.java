package fr.enseeiht.eventb.eb4eb;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.RodinCore;

public class EB4EBAttributes {
	
	public static IAttributeType.Handle AVAILABLE_ANNOTATION_ATTRIBUTE = RodinCore
			.getHandleAttrType(EB4EBPlugin.PLUGIN_ID + ".availableAnnotation");
	
	public static IAttributeType.String ANNOTATION_TARGET_ELEMENT = RodinCore
			.getStringAttrType(EB4EBPlugin.PLUGIN_ID + ".annotationTargetElement");
}
