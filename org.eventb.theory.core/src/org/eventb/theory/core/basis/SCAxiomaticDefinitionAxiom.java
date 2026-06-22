package org.eventb.theory.core.basis;

import org.eventb.core.basis.SCPredicateElement;
import org.eventb.theory.core.ISCAxiomaticDefinitionAxiom;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;

/**
 * 
 * @author maamria
 *
 */
public class SCAxiomaticDefinitionAxiom extends SCPredicateElement implements ISCAxiomaticDefinitionAxiom {

	public SCAxiomaticDefinitionAxiom(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public IInternalElementType<? extends IInternalElement> getElementType() {
		return ELEMENT_TYPE;
	}

}
