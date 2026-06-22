package org.eventb.theory.ui.explorer.model;

import org.eventb.core.IPSStatus;
import org.eventb.theory.core.IAxiomaticDefinitionAxiom;
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
public class ModelAxiomaticDefinitionAxiom extends TheoryModelPOContainer {

	public ModelAxiomaticDefinitionAxiom(IAxiomaticDefinitionAxiom axiom, IModelElement parent){
		this.axiom = axiom;
		this.parent = parent;
	}

	private IAxiomaticDefinitionAxiom axiom;
	
	@Override
	public IRodinElement getInternalElement() {
		return axiom;
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
