package org.eventb.theory.ui.explorer.model;

import org.eventb.core.IPSStatus;
import org.eventb.theory.core.IAxiomaticOperatorDefinition;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;

import fr.systerel.internal.explorer.model.IModelElement;
import fr.systerel.internal.explorer.navigator.ExplorerUtils;

/**
 * 
 * @author maamria
 *
 */
@SuppressWarnings("restriction")
public class ModelAxiomaticOperator extends TheoryModelPOContainer{

	public ModelAxiomaticOperator(IAxiomaticOperatorDefinition opDef, IModelElement parent){
		this.internalOp = opDef;
		this.parent = parent;
	}

	private IAxiomaticOperatorDefinition internalOp;
	
	@Override
	public IRodinElement getInternalElement() {
		return internalOp;
	}

	@Override
	public Object getParent(boolean complex) {
		return parent;
	}

	@Override
	public Object[] getChildren(IInternalElementType<?> type, boolean complex) {
		if (type != IPSStatus.ELEMENT_TYPE) {
			if (ExplorerUtils.DEBUG) {
				System.out.println("Unsupported children type for rule: " +type);
			}
			return new Object[0];
		}
		return getIPSStatuses();
	}

}
