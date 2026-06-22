package org.eventb.theory.core.basis;

import org.eventb.theory.core.ISCAxiomaticDefinitionAxiom;
import org.eventb.theory.core.ISCAxiomaticDefinitionsBlock;
import org.eventb.theory.core.ISCAxiomaticOperatorDefinition;
import org.eventb.theory.core.ISCAxiomaticTypeDefinition;
import org.eventb.theory.core.TheoryElement;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * 
 * @author maamria
 *
 */
public class SCAxiomaticDefinitionsBlock extends TheoryElement implements ISCAxiomaticDefinitionsBlock {

	public SCAxiomaticDefinitionsBlock(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public ISCAxiomaticTypeDefinition getAxiomaticTypeDefinition(String name) {
		return getInternalElement(ISCAxiomaticTypeDefinition.ELEMENT_TYPE, name);
	}

	@Override
	public ISCAxiomaticTypeDefinition[] getAxiomaticTypeDefinitions() throws RodinDBException {
		return getChildrenOfType(ISCAxiomaticTypeDefinition.ELEMENT_TYPE);
	}

	@Override
	public ISCAxiomaticOperatorDefinition getAxiomaticOperatorDefinition(String name) {
		return getInternalElement(ISCAxiomaticOperatorDefinition.ELEMENT_TYPE, name);
	}

	@Override
	public ISCAxiomaticOperatorDefinition[] getAxiomaticOperatorDefinitions() throws RodinDBException {
		return getChildrenOfType(ISCAxiomaticOperatorDefinition.ELEMENT_TYPE);
	}

	@Override
	public ISCAxiomaticDefinitionAxiom getAxiomaticDefinitionAxiom(String name) {
		return getInternalElement(ISCAxiomaticDefinitionAxiom.ELEMENT_TYPE, name);
	}

	@Override
	public ISCAxiomaticDefinitionAxiom[] getAxiomaticDefinitionAxioms() throws RodinDBException {
		return getChildrenOfType(ISCAxiomaticDefinitionAxiom.ELEMENT_TYPE);
	}

	@Override
	public IInternalElementType<? extends IInternalElement> getElementType() {
		return ELEMENT_TYPE;
	}

}
