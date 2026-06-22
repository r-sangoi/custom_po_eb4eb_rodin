package org.eventb.theory.core;

import org.eventb.core.ICommentedElement;
import org.eventb.core.IIdentifierElement;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;

/**
 * 
 * @author maamria
 *
 */
public interface IAxiomaticTypeDefinition extends ICommentedElement, IIdentifierElement {

	IInternalElementType<IAxiomaticTypeDefinition> ELEMENT_TYPE = RodinCore
			.getInternalElementType(TheoryPlugin.PLUGIN_ID + ".axiomaticTypeDefinition");
	
}
