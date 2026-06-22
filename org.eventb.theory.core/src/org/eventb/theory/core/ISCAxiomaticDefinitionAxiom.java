package org.eventb.theory.core;

import org.eventb.core.ILabeledElement;
import org.eventb.core.ISCPredicateElement;
import org.eventb.core.ITraceableElement;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;

/**
 * 
 * @author maamria
 *
 */
public interface ISCAxiomaticDefinitionAxiom extends ILabeledElement, ISCPredicateElement, ITraceableElement{
	
	IInternalElementType<ISCAxiomaticDefinitionAxiom> ELEMENT_TYPE = RodinCore.getInternalElementType(
			TheoryPlugin.PLUGIN_ID + ".scAxiomaticDefinitionAxiom");
}
