package org.eventb.theory.core.basis;

import org.eventb.core.basis.SCIdentifierElement;
import org.eventb.theory.core.ISCAxiomaticTypeDefinition;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;

public class SCAxiomaticTypeDefinition extends SCIdentifierElement implements ISCAxiomaticTypeDefinition {

	public SCAxiomaticTypeDefinition(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public IInternalElementType<? extends IInternalElement> getElementType() {
		return ELEMENT_TYPE;
	}

}
