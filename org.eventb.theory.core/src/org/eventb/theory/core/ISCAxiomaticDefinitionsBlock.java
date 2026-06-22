package org.eventb.theory.core;

import org.eventb.core.ILabeledElement;
import org.eventb.core.ITraceableElement;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * 
 * @author maamria
 *
 */
public interface ISCAxiomaticDefinitionsBlock extends ITraceableElement, ILabeledElement{

	IInternalElementType<ISCAxiomaticDefinitionsBlock> ELEMENT_TYPE = 
			RodinCore.getInternalElementType(TheoryPlugin.PLUGIN_ID + ".scAxiomaticDefinitionsBlock");
	
	public ISCAxiomaticTypeDefinition getAxiomaticTypeDefinition(String name);
	
	public ISCAxiomaticTypeDefinition[] getAxiomaticTypeDefinitions() throws RodinDBException;
	
	public ISCAxiomaticOperatorDefinition getAxiomaticOperatorDefinition(String name);
	
	public ISCAxiomaticOperatorDefinition[] getAxiomaticOperatorDefinitions() throws RodinDBException;
	
	public ISCAxiomaticDefinitionAxiom getAxiomaticDefinitionAxiom(String name);
	
	public ISCAxiomaticDefinitionAxiom[] getAxiomaticDefinitionAxioms() throws RodinDBException;
	
}
