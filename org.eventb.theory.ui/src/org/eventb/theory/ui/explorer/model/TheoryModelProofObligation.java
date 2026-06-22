package org.eventb.theory.ui.explorer.model;

import java.util.LinkedList;
import java.util.List;

import org.eventb.core.IPOSequent;
import org.eventb.core.IPSStatus;
import org.eventb.core.seqprover.IConfidence;
import org.rodinp.core.RodinDBException;

import fr.systerel.internal.explorer.model.ModelProofObligation;


/**
 * Represents a Proof Obligation in the Model.
 * 
 */
@SuppressWarnings("restriction")
public class TheoryModelProofObligation extends ModelProofObligation{
	private boolean broken = false;

	private boolean discharged = false;
	private IPOSequent internal_sequent;
	
	private IPSStatus internal_status;
	private boolean manual = false;
	private int position;
	private boolean reviewed = false;
	private ModelTheory theory;
	
	private List<ModelAxiomaticOperator> axOperators = new LinkedList<ModelAxiomaticOperator>();
	private List<ModelAxiomaticDefinitionAxiom> axAxioms = new LinkedList<ModelAxiomaticDefinitionAxiom>();
	private List<ModelTheorem> theorems = new LinkedList<ModelTheorem>();
	private List<ModelOperator> operators = new LinkedList<ModelOperator>();
	private List<ModelRewriteRule> rewRules = new LinkedList<ModelRewriteRule>();
	private List<ModelInferenceRule> infRules = new LinkedList<ModelInferenceRule>();
	
	public TheoryModelProofObligation(IPOSequent sequent, int position) {
		super(sequent, position);
		this.internal_sequent = sequent;
		this.position = position;
	}

	/**
	 * Compare according to the <code>position</code> of the proof obligations
	 */
	public int compareTo(TheoryModelProofObligation o) {
		return getPosition() - o.getPosition();
	}



	public String getElementName() {
		return internal_sequent.getElementName();
	}

	public IPOSequent getIPOSequent() {
		return internal_sequent;
	}

	public IPSStatus getIPSStatus() {
		return internal_status;
	}

	public String getName() {
		return internal_sequent.getElementName();
	}

	/**
	 * 
	 * @return the position of this proof obligation in relation to other other
	 *         proof obligations. The lower the number, the higher on the list.
	 */
	public int getPosition() {
		return position;
	}

	public ModelTheory getTheory() {
		return theory;
	}

	public boolean isBroken() {
		return broken;
	}

	/**
	 * 
	 * @return <code>true</code> if this PO is discharged <code>false</code>
	 *         otherwise.
	 */
	public boolean isDischarged() {
		return discharged;
	}

	public boolean isManual() {
		return manual;
	}

	public boolean isReviewed() {
		return reviewed;
	}

	/**
	 * Set the status of this proof obligation. Updates stored attributes such
	 * as discharged or reviewed
	 * 
	 * @param status
	 *            The new status of this proof obligation
	 */
	public void setIPSStatus(IPSStatus status) {
		internal_status = status;
		try {
			int confidence = status.getConfidence();
			discharged = (status.getConfidence() > IConfidence.REVIEWED_MAX)
					&& !status.isBroken();
			reviewed = (confidence > IConfidence.PENDING && confidence <= IConfidence.REVIEWED_MAX);
			broken = status.isBroken();
			manual = status.getHasManualProof();
		} catch (RodinDBException e) {
			//UIUtils.log(e, "when acessing " +status);
		}
	}

	public void setTheory(ModelTheory thy) {
		theory = thy;
	}

	public ModelTheorem[] getTheorems() {
		return theorems.toArray(new ModelTheorem[theorems.size()]);
	}

	public void addTheorem(ModelTheorem thm) {
		theorems.add(thm);
	}

	public void removeTheorems(ModelTheorem thm) {
		theorems.remove(thm);
	}
	
	public ModelOperator[] getOperators() {
		return operators.toArray(new ModelOperator[operators.size()]);
	}

	public void addOperator(ModelOperator op) {
		operators.add(op);
	}

	public void removeOperators(ModelOperator op) {
		operators.remove(op);
	}
	
	public ModelRewriteRule[] getRewRules() {
		return rewRules.toArray(new ModelRewriteRule[rewRules.size()]);
	}

	public void addRewRule(ModelRewriteRule rew) {
		rewRules.add(rew);
	}

	public void removeRewRules(ModelRewriteRule rule) {
		rewRules.remove(rule);
	}
	
	public ModelInferenceRule[] getInfRules() {
		return infRules.toArray(new ModelInferenceRule[infRules.size()]);
	}

	public void addInfRule(ModelInferenceRule rule) {
		infRules.add(rule);
	}

	public void removeInfRules(ModelInferenceRule rule) {
		infRules.remove(rule);
	}
	
	public ModelAxiomaticOperator[] getAxiomaticOperators(){
		return axOperators.toArray(new ModelAxiomaticOperator[axOperators.size()]);
	}
	
	public void addAxiomaticOperator(ModelAxiomaticOperator op) {
		axOperators.add(op);
	}

	public void removeAxiomaticOperator(ModelAxiomaticOperator op) {
		axOperators.remove(op);
	}
	
	public ModelAxiomaticDefinitionAxiom[] getAxiomaticAxioms(){
		return axAxioms.toArray(new ModelAxiomaticDefinitionAxiom[axAxioms.size()]);
	}
	
	public void addAxiomaticAxiom(ModelAxiomaticDefinitionAxiom ax) {
		axAxioms.add(ax);
	}

	public void removeAxiomaticAxiom(ModelAxiomaticDefinitionAxiom ax) {
		axAxioms.remove(ax);
	}
}
