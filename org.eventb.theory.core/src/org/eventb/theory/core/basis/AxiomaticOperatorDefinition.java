package org.eventb.theory.core.basis;

import org.eventb.theory.core.IAxiomaticOperatorDefinition;
import org.eventb.theory.core.IOperatorArgument;
import org.eventb.theory.core.IOperatorWDCondition;
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
public class AxiomaticOperatorDefinition extends TheoryElement implements IAxiomaticOperatorDefinition {

	public AxiomaticOperatorDefinition(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public IOperatorArgument getOperatorArgument(String name) {
		return getInternalElement(IOperatorArgument.ELEMENT_TYPE, name);
	}

	@Override
	public IOperatorArgument[] getOperatorArguments() throws RodinDBException {
		return getChildrenOfType(IOperatorArgument.ELEMENT_TYPE);
	}

	@Override
	public IOperatorWDCondition[] getOperatorWDConditions() throws RodinDBException {
		return getChildrenOfType(IOperatorWDCondition.ELEMENT_TYPE);
	}

	
	@Override
	public IOperatorWDCondition getOperatorWDCondition(String name) {
		return getInternalElement(IOperatorWDCondition.ELEMENT_TYPE, name);
	}

	@Override
	public IInternalElementType<? extends IInternalElement> getElementType() {
		return ELEMENT_TYPE;
	}

}
