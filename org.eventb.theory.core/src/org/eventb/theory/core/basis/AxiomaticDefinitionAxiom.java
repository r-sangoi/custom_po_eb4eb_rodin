package org.eventb.theory.core.basis;

import org.eventb.theory.core.IAxiomaticDefinitionAxiom;
import org.eventb.theory.core.TheoryElement;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;

/**
 * 
 * @author maamria
 *
 */
public class AxiomaticDefinitionAxiom extends TheoryElement implements IAxiomaticDefinitionAxiom {

	public AxiomaticDefinitionAxiom(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public IInternalElementType<? extends IInternalElement> getElementType() {
		return ELEMENT_TYPE;
	}

}
