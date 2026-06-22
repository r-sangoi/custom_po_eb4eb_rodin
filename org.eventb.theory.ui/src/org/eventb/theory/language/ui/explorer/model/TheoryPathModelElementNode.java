/**
 * 
 */
package org.eventb.theory.language.ui.explorer.model;

import org.eventb.core.IEventBRoot;
import org.eventb.core.IPSStatus;
import org.eventb.internal.ui.UIUtils;
import org.eventb.theory.core.IAvailableTheoryProject;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

import fr.systerel.explorer.IElementNode;
import fr.systerel.internal.explorer.model.IModelElement;

/**
 * @author RenatoSilva
 *
 */
@SuppressWarnings("restriction")
public class TheoryPathModelElementNode implements IModelElement, IElementNode {
	
	public TheoryPathModelElementNode(IInternalElementType<?> type, ModelTheoryPath parent) {
		this.type = type;
		this.parent = parent;
		if (parent instanceof ModelTheoryPath) {
			this.parentRoot = ((ModelTheoryPath) parent).getTheoryPathRoot();
		}
	}
	
	private IInternalElementType<?> type;
	private ModelTheoryPath parent;
	private IEventBRoot parentRoot;	
	
	private static String AVAILABLE_THEORY_PROJECT_TYPE = "Projects";
	private static String PO_TYPE = "Proof Obligations";

	@Override
	public ModelTheoryPath getModelParent() {
		return parent;
	}

	@Override
	public IInternalElementType<?> getChildrenType() {
		return type;
	}

	@Override
	public IEventBRoot getParent() {
		return parentRoot;
	}

	@Override
	public String getLabel() {
		if (type.equals(IAvailableTheoryProject.ELEMENT_TYPE)) {
			return AVAILABLE_THEORY_PROJECT_TYPE;
		}
		if (type.equals(IPSStatus.ELEMENT_TYPE)) {
			return PO_TYPE;
		}
		
		return null;
	}

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
		} 
		else {
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
