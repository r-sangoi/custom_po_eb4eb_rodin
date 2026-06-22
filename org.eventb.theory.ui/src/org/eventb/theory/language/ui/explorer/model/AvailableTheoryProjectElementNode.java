/**
 * 
 */
package org.eventb.theory.language.ui.explorer.model;

import org.eventb.core.IEventBRoot;
import org.eventb.internal.ui.UIUtils;
import org.eventb.theory.core.IAvailableTheory;
import org.eventb.theory.core.IAvailableTheoryProject;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

import fr.systerel.explorer.IElementNode;
import fr.systerel.internal.explorer.model.IModelElement;

/**
 * @author Renato Silva
 *
 */
@SuppressWarnings("restriction")
public class AvailableTheoryProjectElementNode implements IModelElement,
		IElementNode {
	
	private IInternalElementType<?> type;
	private ModelAvailableTheoryProject parent;
	private IAvailableTheoryProject parentElement;	
	private static String AVAILABLE_THEORY_TYPE = "Theories";

	public AvailableTheoryProjectElementNode(IInternalElementType<?> type, ModelAvailableTheoryProject parent) {
		this.type = type;
		this.parent = parent;
		if(parent instanceof ModelAvailableTheoryProject){
			parentElement = (IAvailableTheoryProject) parent.getInternalElement();
		}
	}

	/* (non-Javadoc)
	 * @see fr.systerel.explorer.IElementNode#getParent()
	 */
	@Override
	public IEventBRoot getParent() {
		return null;
	}

	/* (non-Javadoc)
	 * @see fr.systerel.explorer.IElementNode#getChildrenType()
	 */
	@Override
	public IInternalElementType<?> getChildrenType() {
		return type;
	}

	/* (non-Javadoc)
	 * @see fr.systerel.explorer.IElementNode#getLabel()
	 */
	@Override
	public String getLabel() {
		if (type.equals(IAvailableTheory.ELEMENT_TYPE)) {
			return AVAILABLE_THEORY_TYPE;
		}
		
		return null;
	}

	/* (non-Javadoc)
	 * @see fr.systerel.internal.explorer.model.IModelElement#getModelParent()
	 */
	@Override
	public IModelElement getModelParent() {
		return parent;
	}

	/* (non-Javadoc)
	 * @see fr.systerel.internal.explorer.model.IModelElement#getInternalElement()
	 */
	@Override
	public IRodinElement getInternalElement() {
		return null;
	}

	/* (non-Javadoc)
	 * @see fr.systerel.internal.explorer.model.IModelElement#getParent(boolean)
	 */
	@Override
	public Object getParent(boolean complex) {
		return parent;
	}

	/* (non-Javadoc)
	 * @see fr.systerel.internal.explorer.model.IModelElement#getChildren(org.rodinp.core.IInternalElementType, boolean)
	 */
	@Override
	public Object[] getChildren(IInternalElementType<?> element_type, boolean complex) {
		if (type != element_type) {
			return new Object[0];
		} 
		else {
			try {
				return parentElement.getChildrenOfType(type);
			} catch (RodinDBException e) {
				UIUtils.log(e, "when accessing children of type " +type +" of " +parent);
			}
		}
		return new Object[0];
	}

}
