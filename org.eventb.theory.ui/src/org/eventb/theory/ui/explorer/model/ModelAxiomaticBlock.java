package org.eventb.theory.ui.explorer.model;

import java.util.HashMap;

import org.eventb.theory.core.IAxiomaticDefinitionAxiom;
import org.eventb.theory.core.IAxiomaticDefinitionsBlock;
import org.eventb.theory.core.IAxiomaticOperatorDefinition;
import org.eventb.theory.core.IAxiomaticTypeDefinition;
import org.eventb.theory.internal.ui.TheoryUIUtils;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

import fr.systerel.internal.explorer.model.IModelElement;
import fr.systerel.internal.explorer.navigator.ExplorerUtils;

/**
 * 
 * @author maamria
 * 
 */
@SuppressWarnings("restriction")
public class ModelAxiomaticBlock implements IModelElement {

	private IAxiomaticDefinitionsBlock block;
	private IModelElement parent;

	public HashMap<IAxiomaticOperatorDefinition, ModelAxiomaticOperator> axOps = new HashMap<IAxiomaticOperatorDefinition, ModelAxiomaticOperator>();
	public HashMap<IAxiomaticDefinitionAxiom, ModelAxiomaticDefinitionAxiom> axAxioms = new HashMap<IAxiomaticDefinitionAxiom, ModelAxiomaticDefinitionAxiom>();

	public ModelAxiomaticBlock(IAxiomaticDefinitionsBlock block, IModelElement parent) {
		this.block = block;
		this.parent = parent;
	}

	@Override
	public IModelElement getModelParent() {
		return parent;
	}

	@Override
	public IRodinElement getInternalElement() {
		return block;
	}

	@Override
	public Object getParent(boolean complex) {
		return parent;
	}

	@Override
	public Object[] getChildren(IInternalElementType<?> type, boolean complex) {
		if (type == IAxiomaticTypeDefinition.ELEMENT_TYPE) {
			try {
				return block.getAxiomaticTypeDefinitions();
			} catch (RodinDBException e) {
				TheoryUIUtils.log(e, e.getMessage());
			}
		}
		if (type == IAxiomaticOperatorDefinition.ELEMENT_TYPE) {
			try {
				return block.getAxiomaticOperatorDefinitions();
			} catch (RodinDBException e) {
				TheoryUIUtils.log(e, e.getMessage());
			}
		}
		if (type == IAxiomaticDefinitionAxiom.ELEMENT_TYPE) {
			try {
				return block.getAxiomaticDefinitionAxioms();
			} catch (RodinDBException e) {
				TheoryUIUtils.log(e, e.getMessage());
			}
		} else {
			if (ExplorerUtils.DEBUG) {
				System.out.println("Unsupported children type for datatype: " + type);
			}
		}

		return new Object[0];
	}

	public void processChildren() {
		axOps.clear();
		axAxioms.clear();
		try {
			for (IAxiomaticDefinitionAxiom ax : block.getAxiomaticDefinitionAxioms()) {
				addAxiom(ax);
			}
			for (IAxiomaticOperatorDefinition op : block.getAxiomaticOperatorDefinitions()) {
				addOperator(op);
			}
		} catch (RodinDBException e) {
			TheoryUIUtils.log(e, "error while processing children of defs block");
		}

	}

	private void addAxiom(IAxiomaticDefinitionAxiom ax) {
		axAxioms.put(ax, new ModelAxiomaticDefinitionAxiom(ax, this));

	}

	private void addOperator(IAxiomaticOperatorDefinition op) {
		axOps.put(op, new ModelAxiomaticOperator(op, this));
	}

}
