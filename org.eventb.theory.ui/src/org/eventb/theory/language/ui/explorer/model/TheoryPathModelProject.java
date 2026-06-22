/**
 * 
 */
package org.eventb.theory.language.ui.explorer.model;

import java.util.HashMap;

import org.eventb.core.IEventBRoot;
import org.eventb.internal.ui.UIUtils;
import org.eventb.theory.core.ITheoryPathRoot;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

import fr.systerel.internal.explorer.model.IModelElement;
import fr.systerel.internal.explorer.navigator.ExplorerUtils;

/**
 * @author RenatoSilva
 *
 */
@SuppressWarnings("restriction")
public class TheoryPathModelProject implements IModelElement {
	
	private IRodinProject internalProject;
	private HashMap<ITheoryPathRoot, ModelTheoryPath> theories = new HashMap<ITheoryPathRoot, ModelTheoryPath>();
	
	//indicates whether to projects needs to be processed freshly (process Machines etc.)
	public boolean needsProcessing =  true;

	public TheoryPathModelProject(IRodinProject project) {
		internalProject = project;
	}
	
	/* (non-Javadoc)
	 * @see fr.systerel.internal.explorer.model.IModelElement#getModelParent()
	 */
	@Override
	public IModelElement getModelParent() {
		return null;
	}

	/* (non-Javadoc)
	 * @see fr.systerel.internal.explorer.model.IModelElement#getInternalElement()
	 */
	@Override
	public IRodinElement getInternalElement() {
		return internalProject;
	}

	/* (non-Javadoc)
	 * @see fr.systerel.internal.explorer.model.IModelElement#getParent(boolean)
	 */
	@Override
	public Object getParent(boolean complex) {
		return null;
	}

	/* (non-Javadoc)
	 * @see fr.systerel.internal.explorer.model.IModelElement#getChildren(org.rodinp.core.IInternalElementType, boolean)
	 */
	@Override
	public Object[] getChildren(IInternalElementType<?> type, boolean complex) {
		try {
			if (type == ITheoryPathRoot.ELEMENT_TYPE) {
				return internalProject.getRootElementsOfType(type);
			}
			} catch (RodinDBException e) {
				UIUtils.log(e, "when getting " + ITheoryPathRoot.ELEMENT_TYPE + " of " +internalProject);
			}
		
		if (ExplorerUtils.DEBUG) {
			System.out.println("Did not find children of type: "+type +"for project " +internalProject);
		}
		return new Object[0];
	}
	
	public ModelTheoryPath getTheoryPath(ITheoryPathRoot theory) {
		return theories.get(theory);
	}
	
	public void processTheoryPath(ITheoryPathRoot theory) {
		ModelTheoryPath thy;
		if (!theories.containsKey(theory)) {
			thy = new ModelTheoryPath(theory);
			theories.put(theory, thy);
		} else {
			thy = theories.get(theory);
		}
		thy.poNeedsProcessing = true;
		thy.psNeedsProcessing = true;
		thy.processChildren();
	}
	
	
	public void removeTheoryPath(ITheoryPathRoot theoryPathRoot) {
		ModelTheoryPath theory = theories.get(theoryPathRoot);
		if (theory != null) {
			theories.remove(theoryPathRoot);
		}
	}

	public IModelElement getModelElement(IRodinElement element) {
		
		if (element instanceof ITheoryPathRoot) {
			return getTheoryPath((ITheoryPathRoot) element);
		}
		IEventBRoot parent= element.getAncestor(ITheoryPathRoot.ELEMENT_TYPE);
		ModelTheoryPath theory = theories.get(parent);
		if (theory != null) {
			return theory.getModelElement(element);
		} 
		return null;
	}

}
