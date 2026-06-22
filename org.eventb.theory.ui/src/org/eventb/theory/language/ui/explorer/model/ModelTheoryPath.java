/**
 * 
 */
package org.eventb.theory.language.ui.explorer.model;

import java.util.HashMap;

import org.eventb.core.IEventBRoot;
import org.eventb.internal.ui.UIUtils;
import org.eventb.theory.core.IAvailableTheoryProject;
import org.eventb.theory.core.ITheoryPathRoot;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

import fr.systerel.internal.explorer.model.IModelElement;
import fr.systerel.internal.explorer.navigator.ExplorerUtils;

/**
 * @author RenatoSilva
 *
 */
@SuppressWarnings("restriction")
public class ModelTheoryPath implements IModelElement {
	
	/**
	 * The nodes are used by the ContentProviders to present a node in the tree
	 * above elements such as Axioms or Theorems.
	 */
	public final TheoryPathModelElementNode availableTheoryProject_node;
	
	private ITheoryPathRoot theoryPathRoot;

	//indicate whether the poRoot or the psRoot should be processed freshly
	public boolean psNeedsProcessing = true;
	public boolean poNeedsProcessing = true;

	private HashMap<IAvailableTheoryProject, ModelAvailableTheoryProject> availableTheoryProjects= new HashMap<IAvailableTheoryProject, ModelAvailableTheoryProject>();
	
	/**
	 * Creates a ModelContext from a given IContextRoot
	 * @param root	The ContextRoot that this ModelContext is based on.
	 */
	public ModelTheoryPath(ITheoryPathRoot root){
		this.theoryPathRoot = root;
		availableTheoryProject_node = new TheoryPathModelElementNode(IAvailableTheoryProject.ELEMENT_TYPE, this);
	}


	/* (non-Javadoc)
	 * @see fr.systerel.internal.explorer.model.IModelElement#getModelParent()
	 */
	@Override
	public IModelElement getModelParent() {
		return TheoryPathModelController.getProject(theoryPathRoot.getRodinProject());
	}

	/* (non-Javadoc)
	 * @see fr.systerel.internal.explorer.model.IModelElement#getInternalElement()
	 */
	@Override
	public IRodinElement getInternalElement() {
		return theoryPathRoot;
	}

	/* (non-Javadoc)
	 * @see fr.systerel.internal.explorer.model.IModelElement#getParent(boolean)
	 */
	@Override
	public Object getParent(boolean complex) {
		return theoryPathRoot.getRodinProject();
	}
	
	/**
	 * Processes the PORoot that belongs to this context.
	 * It creates a ModelProofObligation for each sequent
	 * and adds it to this context as well as to the
	 * concerned Theorems and Axioms.
	 */
	public void processPORoot() {
//		if (poNeedsProcessing) {
//			try {
//				//clear old POs
//				proofObligations.clear();
//				IPORoot root = this.theoryRoot.getPORoot();
//				if (root.exists()) {
//					IPOSequent[] sequents = root.getSequents();
//					int pos = 1;
//					for (IPOSequent sequent : sequents) {
//						TheoryModelProofObligation po = new TheoryModelProofObligation(sequent, pos);
//						pos++;
//						po.setTheory(this);
//						proofObligations.put(sequent, po);
//			
//						IPOSource[] sources = sequent.getSources();
//						for (int j = 0; j < sources.length; j++) {
//							IRodinElement source = sources[j].getSource();
//							//only process sources that belong to this context.
//							if (theoryRoot.isAncestorOf(source)) {
//								processSource(source, po);
//							}
//						}
//					}
//				}
//			} catch (RodinDBException e) {
//				UIUtils.log(e, "when processing proof obligations of " +theoryRoot);
//			}
//			poNeedsProcessing = false;
//		}
	}
	
	
	/**
	 * Processes the PSRoot that belongs to this Context. Each status is added to
	 * the corresponding Proof Obligation, if that ProofObligation is present.
	 */
	public void processPSRoot(){
//		if (psNeedsProcessing) {
//			try {
//				IPSRoot root = this.theoryRoot.getPSRoot();
//				if (root.exists()) {
//					IPSStatus[] stats = root.getStatuses();
//					for (IPSStatus status : stats) {
//						IPOSequent sequent = status.getPOSequent();
//						// check if there is a ProofObligation for this status (there should be one!)
//						if (proofObligations.containsKey(sequent)) {
//							proofObligations.get(sequent).setIPSStatus(status);
//						}
//					}
//				}
//			} catch (RodinDBException e) {
//				// nothing serious
//				//UIUtils.log(e, "when processing proof statuses of " +theoryRoot);
//			}
//			psNeedsProcessing = false;
//		}
	}

	/* (non-Javadoc)
	 * @see fr.systerel.internal.explorer.model.IModelElement#getChildren(org.rodinp.core.IInternalElementType, boolean)
	 */
	@Override
	public Object[] getChildren(IInternalElementType<?> type, boolean complex) {
		if (poNeedsProcessing || psNeedsProcessing) {
			processPORoot();
			processPSRoot();
		}
		
		if (type == IAvailableTheoryProject.ELEMENT_TYPE) {
			return new Object[]{availableTheoryProject_node};
		}
		if (ExplorerUtils.DEBUG) {
			System.out.println("Unsupported children type for theory: " +type);
		}
		return new Object[0];
	}
	
	/**
	 * Processes the children of this element:
	 */
	public void processChildren(){
		availableTheoryProjects.clear();
		try {
			for (IAvailableTheoryProject avt : theoryPathRoot.getAvailableTheoryProjects()) {
				addAvailableTheoryProject(avt);
			}
		} catch (RodinDBException e) {
			UIUtils.log(e, "when accessing datatypes of "+theoryPathRoot);
		}
	}
	
	private void addAvailableTheoryProject(IAvailableTheoryProject avt) {
		availableTheoryProjects.put(avt, new ModelAvailableTheoryProject(avt, this));
	}

	/**
	 * @return
	 */
	public IEventBRoot getTheoryPathRoot() {
		return theoryPathRoot;
	}


	public IModelElement getModelElement(IRodinElement element) {
		if (element instanceof IAvailableTheoryProject)
			return availableTheoryProjects.get(element);
		
		return null;
	}


	public Object[] getIPSStatuses() {
		// TODO Auto-generated method stub
		return null;
	}

}
