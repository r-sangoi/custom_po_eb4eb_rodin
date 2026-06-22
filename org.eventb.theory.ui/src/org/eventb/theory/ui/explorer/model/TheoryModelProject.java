/**
 * 
 */
package org.eventb.theory.ui.explorer.model;

import java.util.HashMap;

import org.eventb.core.IEventBRoot;
import org.eventb.core.IPSRoot;
import org.eventb.core.IPSStatus;
import org.eventb.internal.ui.UIUtils;
import org.eventb.theory.core.ITheoryRoot;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

import fr.systerel.internal.explorer.model.IModelElement;
import fr.systerel.internal.explorer.navigator.ExplorerUtils;

/**
 * @author maamria
 * 
 */
@SuppressWarnings("restriction")
public class TheoryModelProject implements IModelElement {

	private IRodinProject internalProject;
	private HashMap<ITheoryRoot, ModelTheory> theories = new HashMap<ITheoryRoot, ModelTheory>();
	
	//indicates whether to projects needs to be processed freshly (process Machines etc.)
	public boolean needsProcessing =  true;

	public TheoryModelProject(IRodinProject project) {
		internalProject = project;
	}

	public Object[] getChildren(IInternalElementType<?> type, boolean complex) {
		
		try {
			if (type == ITheoryRoot.ELEMENT_TYPE) {
				return internalProject.getRootElementsOfType(type);
			}
			} catch (RodinDBException e) {
				UIUtils.log(e, "when getting machines or contexts of " +internalProject);
			}
		
		if (ExplorerUtils.DEBUG) {
			System.out.println("Did not find children of type: "+type +"for project " +internalProject);
		}
		return new Object[0];
	}

	/**
	 * @return The number of manually discharged Proof Obligations of this project
	 */
	public int getManuallyDischargedPOcount() {
		int result = 0;
		for (ModelTheory theory : theories.values()) {
			result += theory.getManuallyDischargedPOcount();
		}
		return result;
	}

	public IModelElement getModelElement(IRodinElement element) {

		if (element instanceof ITheoryRoot) {
			return getTheory((ITheoryRoot) element);
		}
		IEventBRoot parent= element.getAncestor(ITheoryRoot.ELEMENT_TYPE);
		ModelTheory theory = theories.get(parent);
		if (theory != null) {
			return theory.getModelElement(element);
		}
		return null;

	}

	/**
	 * @return the total number of Proof Obligations of this project
	 */
	public int getPOcount(){
		int result = 0;
		for (ModelTheory theory : theories.values()) {
			result += theory.getPOcount();
		}
		return result;
	}

	public TheoryModelProofObligation getProofObligation(IPSStatus status){
		IEventBRoot root = (IEventBRoot) status.getRoot();
		if (root instanceof IPSRoot) {
			
			ModelTheory theory = theories.get(root.getRoot());
			if (theory != null) {
				return theory.getProofObligation(status);
			}
		}
		return null;
	}
	
	/**
	 * @return The number of manually discharged Proof Obligations of this project
	 */
	public int getReviewedPOcount() {
		int result = 0;
		for (ModelTheory theory : theories.values()) {
			result += theory.getReviewedPOcount();
		}
		return result;
		
	}
	
	public HashMap<ITheoryRoot, ModelTheory> getTheories() {
		return theories;
	}

	public ModelTheory getTheory(ITheoryRoot theory) {
		return theories.get(theory);
	}
	
	/**
	 * @return The number of undischarged Proof Obligations of this project
	 */
	public int getUndischargedPOcount() {
		int result = 0;
		for (ModelTheory theory : theories.values()) {
			result += theory.getUndischargedPOcount();
		}
		return result;
		
	}
	
	public void processTheory(ITheoryRoot theory) {
		ModelTheory thy;
		if (!theories.containsKey(theory)) {
			thy = new ModelTheory(theory);
			theories.put(theory, thy);
		} else {
			thy = theories.get(theory);
		}
		thy.poNeedsProcessing = true;
		thy.psNeedsProcessing = true;
		thy.processChildren();
		
	}
	
	public void removeTheory(ITheoryRoot theoryRoot) {
		ModelTheory theory = theories.get(theoryRoot);
		if (theory != null) {
			theories.remove(theoryRoot);
		}
	}

	@Override
	public IModelElement getModelParent() {
		return null;
	}

	@Override
	public IRodinElement getInternalElement() {
		return internalProject;
	}

	@Override
	public Object getParent(boolean complex) {
		return null;
	}
}
