package org.eventb.theory.core;

import org.eventb.core.ICommentedElement;
import org.eventb.core.ILabeledElement;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * 
 * @author maamria
 *
 */
public interface IAxiomaticDefinitionsBlock extends ICommentedElement, ILabeledElement{

	IInternalElementType<IAxiomaticDefinitionsBlock> ELEMENT_TYPE = 
			RodinCore.getInternalElementType(TheoryPlugin.PLUGIN_ID + ".axiomaticDefinitionsBlock");
	
	
	public IAxiomaticTypeDefinition getAxiomaticTypeDefinition(String name);
	
	public IAxiomaticTypeDefinition[] getAxiomaticTypeDefinitions() throws RodinDBException;
	
	public IAxiomaticOperatorDefinition getAxiomaticOperatorDefinition(String name);
	
	public IAxiomaticOperatorDefinition[] getAxiomaticOperatorDefinitions() throws RodinDBException;
	
	public IAxiomaticDefinitionAxiom getAxiomaticDefinitionAxiom(String name);
	
	public IAxiomaticDefinitionAxiom[] getAxiomaticDefinitionAxioms() throws RodinDBException;
	
}
