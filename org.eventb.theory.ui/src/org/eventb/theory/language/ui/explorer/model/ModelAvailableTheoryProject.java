/**
 * 
 */
package org.eventb.theory.language.ui.explorer.model;

import org.eventb.theory.core.IAvailableTheory;
import org.eventb.theory.core.IAvailableTheoryProject;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinDBException;

import fr.systerel.internal.explorer.model.IModelElement;
import fr.systerel.internal.explorer.navigator.ExplorerUtils;

/**
 * @author renatosilva
 *
 */
@SuppressWarnings("restriction")
public class ModelAvailableTheoryProject extends AbstractModelElement<IAvailableTheoryProject> {

	public AvailableTheoryProjectElementNode availableTheoryProject_node;

	public ModelAvailableTheoryProject(IAvailableTheoryProject avt,
			IModelElement modelTheoryPath) {
		super(avt, modelTheoryPath);
		availableTheoryProject_node = new AvailableTheoryProjectElementNode(IAvailableTheory.ELEMENT_TYPE, this);
	}

	public Object getParent(boolean complex) {
		if (parent instanceof ModelTheoryPath) {
			return ((ModelTheoryPath) parent).availableTheoryProject_node;
		}
		if (parent instanceof ModelAvailableTheoryProject) {
			return ((ModelAvailableTheoryProject) parent).availableTheoryProject_node;
		}
		return parent;
	}
	
	@Override
	public Object[] getChildren(IInternalElementType<?> type, boolean complex) {
		if(type == IAvailableTheoryProject.ELEMENT_TYPE){
			try {
				return internalElement.getTheories();
			} catch (RodinDBException e) {
				e.printStackTrace();
			}
		}
		else {
			if (ExplorerUtils.DEBUG) {
				System.out.println("Unsupported children type for datatype: " +type);
			}
		}
		
		return new Object[0];
	}

}
