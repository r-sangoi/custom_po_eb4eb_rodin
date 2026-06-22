package org.eventb.theory.ui.explorer.model;

import java.util.HashMap;

import org.eventb.core.IEventBRoot;
import org.eventb.core.IPORoot;
import org.eventb.core.IPOSequent;
import org.eventb.core.IPOSource;
import org.eventb.core.IPSRoot;
import org.eventb.core.IPSStatus;
import org.eventb.internal.ui.UIUtils;
import org.eventb.theory.core.IAxiomaticDefinitionAxiom;
import org.eventb.theory.core.IAxiomaticDefinitionsBlock;
import org.eventb.theory.core.IAxiomaticOperatorDefinition;
import org.eventb.theory.core.IDatatypeDefinition;
import org.eventb.theory.core.IInferenceRule;
import org.eventb.theory.core.INewOperatorDefinition;
import org.eventb.theory.core.IProofRulesBlock;
import org.eventb.theory.core.IRewriteRule;
import org.eventb.theory.core.ITheorem;
import org.eventb.theory.core.ITheoryRoot;
import org.eventb.theory.core.ITypeParameter;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

import fr.systerel.internal.explorer.model.IModelElement;
import fr.systerel.internal.explorer.navigator.ExplorerUtils;


@SuppressWarnings("restriction")
public class ModelTheory extends TheoryModelPOContainer{
	
	/**
	 * The nodes are used by the ContentProviders to present a node in the tree
	 * above elements such as Axioms or Theorems.
	 */
	public final TheoryModelElementNode typepar_node;
	public final TheoryModelElementNode datatype_node;
	public final TheoryModelElementNode po_node;
	public final TheoryModelElementNode op_node;
	public final TheoryModelElementNode pb_node;
	public final TheoryModelElementNode thm_node;
	public final TheoryModelElementNode axb_node;
	
	private HashMap<IDatatypeDefinition, ModelDatatype> datatypes = new HashMap<IDatatypeDefinition, ModelDatatype>();
	public HashMap<IProofRulesBlock, ModelRulesBlock> blocks = new HashMap<IProofRulesBlock, ModelRulesBlock>();
	private HashMap<ITheorem, ModelTheorem> theorems = new HashMap<ITheorem, ModelTheorem>();
	private HashMap<INewOperatorDefinition, ModelOperator> operators = new HashMap<INewOperatorDefinition, ModelOperator>();
	private HashMap<IAxiomaticDefinitionsBlock, ModelAxiomaticBlock> axBlocks =
			new HashMap<IAxiomaticDefinitionsBlock, ModelAxiomaticBlock>();

	
	private ITheoryRoot theoryRoot;

	//indicate whether the poRoot or the psRoot should be processed freshly
	public boolean psNeedsProcessing = true;
	public boolean poNeedsProcessing = true;

	
	/**
	 * Creates a ModelContext from a given IContextRoot
	 * @param root	The ContextRoot that this ModelContext is based on.
	 */
	public ModelTheory(ITheoryRoot root){
		this.theoryRoot = root;
		typepar_node = new TheoryModelElementNode(ITypeParameter.ELEMENT_TYPE, this);
		datatype_node = new TheoryModelElementNode(IDatatypeDefinition.ELEMENT_TYPE, this);
		po_node = new TheoryModelElementNode(IPSStatus.ELEMENT_TYPE, this);
		op_node = new TheoryModelElementNode(INewOperatorDefinition.ELEMENT_TYPE, this);
		thm_node = new TheoryModelElementNode(ITheorem.ELEMENT_TYPE, this);
		pb_node = new TheoryModelElementNode(IProofRulesBlock.ELEMENT_TYPE, this);
		axb_node = new TheoryModelElementNode(IAxiomaticDefinitionsBlock.ELEMENT_TYPE, this);
	}


	
	/**
	 * Processes the children of this Context:
	 * Clears existing axioms and theorems.
	 * Adds all axioms and theorems found in the internalContext root.
	 */
	public void processChildren(){
		// TODO children which have proof obligations needs to be processed
		datatypes.clear();
		blocks.clear();
		theorems.clear();
		operators.clear();
		axBlocks.clear();
		try {
			for (IDatatypeDefinition dtd : theoryRoot.getDatatypeDefinitions()) {
				addDatatype(dtd);
			}
			for (IProofRulesBlock block : theoryRoot.getProofRulesBlocks()){
				addBlock(block);
				processBlock(block);
			}
			for(ITheorem thm : theoryRoot.getTheorems()){
				addThm(thm);
			}
			for(INewOperatorDefinition def: theoryRoot.getNewOperatorDefinitions()){
				addOp(def);
			}
			for (IAxiomaticDefinitionsBlock block : theoryRoot.getAxiomaticDefinitionsBlocks()){
				addAxBlock(block);
				processBlock(block);
			}
		} catch (RodinDBException e) {
			UIUtils.log(e, "when accessing datatypes of "+theoryRoot);
		}
	}
	
	private void processBlock(IAxiomaticDefinitionsBlock block) {
		ModelAxiomaticBlock blockModel = axBlocks.get(block);
		if (blockModel == null){
			blockModel = new ModelAxiomaticBlock(block, this);
			axBlocks.put(block, blockModel);
		}
		blockModel.processChildren();
	}

	private void processBlock(IProofRulesBlock block) {
		ModelRulesBlock blockModel = blocks.get(block);
		if (blockModel == null) {
			blockModel = new ModelRulesBlock(block, this);
			blocks.put(block, blockModel);
		}
		blockModel.processChildren();
		
	}

	public void addAxBlock(IAxiomaticDefinitionsBlock block) {
		axBlocks.put(block, new ModelAxiomaticBlock(block, this));
	}

	public void addDatatype(IDatatypeDefinition def) {
		datatypes.put(def, new ModelDatatype(def, this));
	}
	
	public void addBlock(IProofRulesBlock block) {
		blocks.put(block, new ModelRulesBlock(block, this));
	}
	
	public void addThm(ITheorem thm) {
		theorems.put(thm, new ModelTheorem(thm, this));
	}
	
	public void addOp(INewOperatorDefinition op) {
		operators.put(op, new ModelOperator(op, this));
	}
	
	/**
	 * Processes the PORoot that belongs to this context.
	 * It creates a ModelProofObligation for each sequent
	 * and adds it to this context as well as to the
	 * concerned Theorems and Axioms.
	 */
	public void processPORoot() {
		if (poNeedsProcessing) {
			try {
				//clear old POs
				proofObligations.clear();
				IPORoot root = this.theoryRoot.getPORoot();
				if (root.exists()) {
					IPOSequent[] sequents = root.getSequents();
					int pos = 1;
					for (IPOSequent sequent : sequents) {
						TheoryModelProofObligation po = new TheoryModelProofObligation(sequent, pos);
						pos++;
						po.setTheory(this);
						proofObligations.put(sequent, po);
			
						IPOSource[] sources = sequent.getSources();
						for (int j = 0; j < sources.length; j++) {
							IRodinElement source = sources[j].getSource();
							//only process sources that belong to this context.
							if (theoryRoot.isAncestorOf(source)) {
								processSource(source, po);
							}
						}
					}
				}
			} catch (RodinDBException e) {
				UIUtils.log(e, "when processing proof obligations of " +theoryRoot);
			}
			poNeedsProcessing = false;
		}
	}
	
	
	/**
	 * Processes the PSRoot that belongs to this Context. Each status is added to
	 * the corresponding Proof Obligation, if that ProofObligation is present.
	 */
	public void processPSRoot(){
		if (psNeedsProcessing) {
			try {
				IPSRoot root = this.theoryRoot.getPSRoot();
				if (root.exists()) {
					IPSStatus[] stats = root.getStatuses();
					for (IPSStatus status : stats) {
						IPOSequent sequent = status.getPOSequent();
						// check if there is a ProofObligation for this status (there should be one!)
						if (proofObligations.containsKey(sequent)) {
							proofObligations.get(sequent).setIPSStatus(status);
						}
					}
				}
			} catch (RodinDBException e) {
				// nothing serious
				//UIUtils.log(e, "when processing proof statuses of " +theoryRoot);
			}
			psNeedsProcessing = false;
		}
	}


	/**
	 * @return The Project that contains this Context.
	 */
	@Override
	public IModelElement getModelParent() {
		return TheoryModelController.getProject(theoryRoot.getRodinProject());
	}
	
	
	/**
	 * process the proof obligations if needed
	 * 
	 * @return the total number of Proof Obligations
	 */
	@Override
	public int getPOcount(){
		if (poNeedsProcessing || psNeedsProcessing) {
			processPORoot();
			processPSRoot();
		}
		return proofObligations.size();
	}
	
	/**
	 * process the proof obligations if needed
	 * 
	 * @return The number of undischarged Proof Obligations
	 */
	@Override
	public int getUndischargedPOcount() {
		if (poNeedsProcessing || psNeedsProcessing) {
			processPORoot();
			processPSRoot();
		}
		int result = 0;
		for (TheoryModelProofObligation po : proofObligations.values()) {
			if (!po.isDischarged()) {
				result++;
			}
		}
		return result;
		
	}
	

	@Override
	public IRodinElement getInternalElement() {
		return theoryRoot;
	}
	
	/**
	 * Processes a source belonging to a given Proof Obligation
	 * 
	 * @param source
	 *            The source to process
	 * @param po
	 *            The proof obligation the source belongs to
	 */
	protected void processSource (IRodinElement source, TheoryModelProofObligation po) {
		if (source instanceof ITheorem) {
			if (theorems.containsKey(source)) {
				ModelTheorem thm = theorems.get(source);
				po.addTheorem(thm);
				thm.addProofObligation(po);
			}
			
		}
		if(source instanceof INewOperatorDefinition){
			if(operators.containsKey(source)){
				ModelOperator op = operators.get(source);
				po.addOperator(op);
				op.addProofObligation(po);
			}
		}
		if(source instanceof IRewriteRule){
			IProofRulesBlock parent = (IProofRulesBlock) source.getParent();
			if(blocks.containsKey(parent)){
				ModelRulesBlock modelBlock = blocks.get(parent);
				if(modelBlock.rewRules.containsKey(source)){
					ModelRewriteRule modelRule = modelBlock.rewRules.get(source);
					po.addRewRule(modelRule);
					modelRule.addProofObligation(po);
					
				}
			}
		}
		
		if(source instanceof IInferenceRule){
			IProofRulesBlock parent = (IProofRulesBlock) source.getParent();
			if(blocks.containsKey(parent)){
				ModelRulesBlock modelBlock = blocks.get(parent);
				if(modelBlock.infRules.containsKey(source)){
					ModelInferenceRule modelRule = modelBlock.infRules.get(source);
					po.addInfRule(modelRule);
					modelRule.addProofObligation(po);
					
				}
			}
		}
		if (source instanceof IAxiomaticDefinitionAxiom){
			IAxiomaticDefinitionsBlock parent = (IAxiomaticDefinitionsBlock) source.getParent();
			if (axBlocks.containsKey(parent)){
				ModelAxiomaticBlock modelBlock = axBlocks.get(parent);
				if (modelBlock.axAxioms.containsKey(source)){
					ModelAxiomaticDefinitionAxiom modelAxiom = modelBlock.axAxioms.get(source);
					po.addAxiomaticAxiom(modelAxiom);
					modelAxiom.addProofObligation(po);
				}
			}
		}
		
		if (source instanceof IAxiomaticOperatorDefinition){
			IAxiomaticDefinitionsBlock parent = (IAxiomaticDefinitionsBlock) source.getParent();
			if (axBlocks.containsKey(parent)){
				ModelAxiomaticBlock modelBlock = axBlocks.get(parent);
				if (modelBlock.axOps.containsKey(source)){
					ModelAxiomaticOperator modelAxOp = modelBlock.axOps.get(source);
					po.addAxiomaticOperator(modelAxOp);
					modelAxOp.addProofObligation(po);
				}
			}
		}
		
	}
	
	public IModelElement getModelElement(IRodinElement element) {
		if(element instanceof IDatatypeDefinition)
			return datatypes.get(element);
		else if (element instanceof IProofRulesBlock)
			return blocks.get(element);
		else if(element instanceof ITheorem)
			return theorems.get(element);
		else if(element instanceof INewOperatorDefinition){
			return operators.get(element);
		}
		else if (element instanceof IAxiomaticDefinitionsBlock){
			return axBlocks.get(element);
		}
		else if(element instanceof IRewriteRule){
			IProofRulesBlock parent = (IProofRulesBlock) element.getParent();
			ModelRulesBlock model = blocks.get(parent);
			return model.rewRules.get(element);
		}
		else if(element instanceof IInferenceRule){
			IProofRulesBlock parent = (IProofRulesBlock) element.getParent();
			ModelRulesBlock model = blocks.get(parent);
			return model.infRules.get(element);
		}
		else if (element instanceof IAxiomaticDefinitionAxiom){
			IAxiomaticDefinitionsBlock parent = (IAxiomaticDefinitionsBlock) element.getParent();
			ModelAxiomaticBlock model = axBlocks.get(parent);
			return model.axAxioms.get(element);
		}
		else if (element instanceof IAxiomaticOperatorDefinition){
			IAxiomaticDefinitionsBlock parent = (IAxiomaticDefinitionsBlock) element.getParent();
			ModelAxiomaticBlock model = axBlocks.get(parent);
			return model.axOps.get(element);
		}
		return null;
	}

	/**
	 * In the complex version this gets the first abstract context of this
	 * context. If none exist or in the non-complex version this returns the
	 * containing project.
	 */
	@Override
	public Object getParent(boolean complex) {
		return theoryRoot.getRodinProject();
	}


	@Override
	public Object[] getChildren(IInternalElementType<?> type, boolean complex) {

		
		if (poNeedsProcessing || psNeedsProcessing) {
			processPORoot();
			processPSRoot();
		}
		if (type == IAxiomaticDefinitionsBlock.ELEMENT_TYPE){
			return new Object[]{axb_node};
		}
		if (type == IDatatypeDefinition.ELEMENT_TYPE) {
			return new Object[]{datatype_node};
		}
		if (type == ITypeParameter.ELEMENT_TYPE) {
			return new Object[]{typepar_node};
		}
		if (type == IPSStatus.ELEMENT_TYPE) {
			return new Object[]{po_node};
		}
		if(type == INewOperatorDefinition.ELEMENT_TYPE)
			return new Object[]{op_node};
		
		if(type == IProofRulesBlock.ELEMENT_TYPE)
			return new Object[]{pb_node};
		
		if(type == ITheorem.ELEMENT_TYPE)
			return new Object[]{thm_node};
		
		if (ExplorerUtils.DEBUG) {
			System.out.println("Unsupported children type for theory: " +type);
		}
		return new Object[0];
	}

	public IEventBRoot getTheoryRoot() {
		return theoryRoot;
	}
	

}
