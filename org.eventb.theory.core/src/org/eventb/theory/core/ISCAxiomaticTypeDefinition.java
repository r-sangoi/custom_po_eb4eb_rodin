package org.eventb.theory.core;

import org.eventb.core.ISCIdentifierElement;
import org.eventb.core.ITraceableElement;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;

/**
 * 
 * @author maamria
 *
 */
public interface ISCAxiomaticTypeDefinition extends ITraceableElement, ISCIdentifierElement{

	IInternalElementType<ISCAxiomaticTypeDefinition> ELEMENT_TYPE = 
			RodinCore.getInternalElementType(TheoryPlugin.PLUGIN_ID + ".scAxiomaticTypeDefinition");
	
}
