package org.eventb.theory.ui.explorer.model;

import org.eventb.core.IEventBRoot;
import org.eventb.core.IPSStatus;
import org.eventb.internal.ui.UIUtils;
import org.eventb.theory.core.IAxiomaticDefinitionsBlock;
import org.eventb.theory.core.IDatatypeDefinition;
import org.eventb.theory.core.INewOperatorDefinition;
import org.eventb.theory.core.IProofRulesBlock;
import org.eventb.theory.core.ITheorem;
import org.eventb.theory.core.ITypeParameter;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

import fr.systerel.internal.explorer.model.ModelElementNode;


/**
 * This is a helper class to show a parent node for all invariants,
 * theorems, events etc. in the navigator tree.
 *
 */
@SuppressWarnings("restriction")
public class TheoryModelElementNode extends ModelElementNode{
	public TheoryModelElementNode(IInternalElementType<?> type, TheoryModelPOContainer parent) {
		super(type, parent);
		this.type = type;
		this.parent = parent;
		if (parent instanceof ModelTheory) {
			this.parentRoot = ((ModelTheory) parent).getTheoryRoot();
		}
	}
	
	private IInternalElementType<?> type;
	private TheoryModelPOContainer parent;
	private IEventBRoot parentRoot;	

	@Override
	public TheoryModelPOContainer getModelParent() {
		return parent;
	}

	@Override
	public IInternalElementType<?> getChildrenType() {
		return type;
	}

	@Override
	public String getLabel() {
		if (type.equals(IDatatypeDefinition.ELEMENT_TYPE)) {
			return DATATYPE_TYPE;
		}
		if (type.equals(ITypeParameter.ELEMENT_TYPE)) {
			return TPAR_TYPE;
		}
		if (type.equals(INewOperatorDefinition.ELEMENT_TYPE)) {
			return OPERATOR_TYPE;
		}
		if (type.equals(IProofRulesBlock.ELEMENT_TYPE)) {
			return PRULES_TYPE;
		}
		if (type.equals(ITheorem.ELEMENT_TYPE)) {
			return THEOREM_TYPE;
		}
		if (type.equals(IAxiomaticDefinitionsBlock.ELEMENT_TYPE)) {
			return AXIOM_BLOCK_TYPE;
		}
		if(type.equals(IPSStatus.ELEMENT_TYPE))
			return PO_TYPE;
		return null;
	}
	
	private static String DATATYPE_TYPE = "Datatypes";
	private static String OPERATOR_TYPE = "Operators";
	private static String PRULES_TYPE = "Proof Rules";
	private static String THEOREM_TYPE = "Theorems";
	private static String TPAR_TYPE = "Type Parameters";
	private static String AXIOM_BLOCK_TYPE = "Axiomatic Definitions";
	private static String PO_TYPE = "Proof Obligations";


	@Override
	public IEventBRoot getParent() {
		return parentRoot;
	}

	/**
	 * does not have an internal element
	 */
	@Override
	public IRodinElement getInternalElement() {
		return null;
	}

	@Override
	public Object getParent(boolean complex) {
		return parentRoot;
	}


	@Override
	public Object[] getChildren(IInternalElementType<?> element_type, boolean complex) {
		
		if (type != element_type) {
			return new Object[0];
		} else {
			if (type == IPSStatus.ELEMENT_TYPE) {
				return parent.getIPSStatuses();
			} else {
				try {
					return parentRoot.getChildrenOfType(type);
				} catch (RodinDBException e) {
					UIUtils.log(e, "when accessing children of type " +type +" of " +parentRoot);
				}
			}
			
		}
		return new Object[0];
		
	}
	
	

}
