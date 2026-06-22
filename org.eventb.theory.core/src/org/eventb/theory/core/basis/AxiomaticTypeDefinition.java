package org.eventb.theory.core.basis;

import org.eventb.theory.core.IAxiomaticTypeDefinition;
import org.eventb.theory.core.TheoryElement;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;

public class AxiomaticTypeDefinition extends TheoryElement implements IAxiomaticTypeDefinition {

	public AxiomaticTypeDefinition(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public IInternalElementType<? extends IInternalElement> getElementType() {
		return ELEMENT_TYPE;
	}

}
