package org.eventb.theory.core;

import org.eventb.core.ICommentedElement;
import org.eventb.core.ILabeledElement;
import org.eventb.core.IPredicateElement;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;

/**
 * 
 * @author maamria
 *
 */
public interface IAxiomaticDefinitionAxiom extends ICommentedElement, ILabeledElement, IPredicateElement{

	IInternalElementType<IAxiomaticDefinitionAxiom> ELEMENT_TYPE = RodinCore
			.getInternalElementType(TheoryPlugin.PLUGIN_ID + ".axiomaticDefinitionAxiom");
}
