/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.theory.ui.explorer.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.eventb.core.IPOSequent;
import org.eventb.core.IPSStatus;
import org.eventb.core.seqprover.IConfidence;
import org.eventb.theory.core.IAxiomaticDefinitionAxiom;
import org.eventb.theory.core.IAxiomaticOperatorDefinition;
import org.eventb.theory.core.IInferenceRule;
import org.eventb.theory.core.INewOperatorDefinition;
import org.eventb.theory.core.IRewriteRule;
import org.eventb.theory.core.ITheorem;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinDBException;

import fr.systerel.internal.explorer.model.IModelElement;
import fr.systerel.internal.explorer.model.ModelPOContainer;

/**
 * An abstract class for any elements that may contain ProofObligations
 * (Machines, Invariants...)
 * 
 */
@SuppressWarnings("restriction")
public abstract class TheoryModelPOContainer extends ModelPOContainer implements IModelElement {

	// name for Label Provider
	public static final String DISPLAY_NAME = "Proof Obligations";

	protected IModelElement parent;

	protected HashMap<IPOSequent, TheoryModelProofObligation> proofObligations = new HashMap<IPOSequent, TheoryModelProofObligation>();

	public TheoryModelProofObligation[] getProofObligations() {
		TheoryModelProofObligation[] proofs = new TheoryModelProofObligation[proofObligations
				.values().size()];
		return proofObligations.values().toArray(proofs);
	}

	public void addProofObligation(TheoryModelProofObligation po) {
		proofObligations.put(po.getIPOSequent(), po);
	}

	public TheoryModelProofObligation getProofObligation(IPSStatus status) {
		return proofObligations.get(status.getPOSequent());
	}

	/**
	 * @return The IPSStatuses of the ProofObligations in this container in the
	 *         same order they appear in the file. It is possible that some
	 *         ProofObligatiosn don't have a status
	 */
	public IPSStatus[] getIPSStatuses() {
		List<IPSStatus> statuses = new LinkedList<IPSStatus>();
		TheoryModelProofObligation[] sorted = proofObligations.values().toArray(new TheoryModelProofObligation[proofObligations.size()]);
		Arrays.sort(sorted);
		for (TheoryModelProofObligation po : sorted) {
			if (po.getIPSStatus() != null) {
				statuses.add(po.getIPSStatus());
			}
		}
		IPSStatus[] results = new IPSStatus[statuses.size()];
		return statuses.toArray(results);
	}

	@Override
	public IModelElement getModelParent() {
		return parent;
	}

	/**
	 * 
	 * @return <code>true</code>, if there's an undischarged ProofObligation
	 *         in this container. <code>false</code> otherwise.
	 */
	public boolean hasUndischargedPOs() {
		for (TheoryModelProofObligation po : proofObligations.values()) {
			if (!po.isDischarged()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * @return the minimum confidence of the proof obligations in this container
	 */
	public int getMinConfidence() {
		int min = IConfidence.DISCHARGED_MAX;
		for (TheoryModelProofObligation po : proofObligations.values()) {
			if (po.getIPSStatus() != null) {
				try {
					if (po.getIPSStatus().getConfidence() < min) {
						min = po.getIPSStatus().getConfidence();
					}
					if (po.getIPSStatus().isBroken()) {
						if (min > IConfidence.PENDING) {
							min = IConfidence.PENDING;
						}
					}
				} catch (RodinDBException e) {
					//UIUtils.log(e, "when accessing IPSStatus " + po.getIPSStatus());
				}
				
			}
		}
		return min;
	}
	
	/**
	 * 
	 * @return the total number of Proof Obligations
	 */
	public int getPOcount() {
		return proofObligations.size();

	}

	/**
	 * Gets the total number of proof obligations that belong to a certain
	 * element type (e.g invariants)
	 * 
	 * @param aType
	 *            The type of the element (invariant, theorem, event...)
	 * @return the total number of proof obligations that have an element of the
	 *         given type as source
	 */
	public int getPOcount(IInternalElementType<?> aType) {
		int result = 0;
		if (aType == INewOperatorDefinition.ELEMENT_TYPE) {
			for (TheoryModelProofObligation po : proofObligations.values()) {
				if (po.getOperators().length > 0) {
					result++;
				}
			}
		}
		if (aType == ITheorem.ELEMENT_TYPE) {
			for (TheoryModelProofObligation po : proofObligations.values()) {
				if (po.getTheorems().length > 0) {
					result++;
				}
			}
		}
		if (aType == IRewriteRule.ELEMENT_TYPE) {
			for (TheoryModelProofObligation po : proofObligations.values()) {
				if (po.getRewRules().length > 0) {
					result++;
				}
			}
		}
		if (aType == IInferenceRule.ELEMENT_TYPE) {
			for (TheoryModelProofObligation po : proofObligations.values()) {
				if (po.getInfRules().length > 0) {
					result++;
				}
			}
		}
		if (aType == IAxiomaticOperatorDefinition.ELEMENT_TYPE) {
			for (TheoryModelProofObligation po : proofObligations.values()) {
				if (po.getAxiomaticOperators().length > 0) {
					result++;
				}
			}
		}
		if (aType == IAxiomaticDefinitionAxiom.ELEMENT_TYPE) {
			for (TheoryModelProofObligation po : proofObligations.values()) {
				if (po.getAxiomaticAxioms().length > 0) {
					result++;
				}
			}
		}
		// return all proof obligations.
		if (aType == IPSStatus.ELEMENT_TYPE) {
			result = getPOcount();
		}

		return result;
	}

	/**
	 * Gets the number of undischarged proof obligations that belong to a
	 * certain element type (e.g invariants)
	 * 
	 * @param aType
	 *            The type of the element (invariant, theorem, event...)
	 * @return The number of undischarged Proof Obligations (including Reviewed
	 *         POs)
	 */
	public int getUndischargedPOcount(IInternalElementType<?> aType) {
		int result = 0;
		if (aType == INewOperatorDefinition.ELEMENT_TYPE) {
			for (TheoryModelProofObligation po : proofObligations.values()) {
				if (!po.isDischarged() && po.getOperators().length > 0) {
					result++;
				}
			}
		}
		if (aType == ITheorem.ELEMENT_TYPE) {
			for (TheoryModelProofObligation po : proofObligations.values()) {
				if (!po.isDischarged() && po.getTheorems().length > 0) {
					result++;
				}
			}
		}
		if (aType == IRewriteRule.ELEMENT_TYPE) {
			for (TheoryModelProofObligation po : proofObligations.values()) {
				if (!po.isDischarged() && po.getRewRules().length > 0) {
					result++;
				}
			}
		}
		if (aType == IInferenceRule.ELEMENT_TYPE) {
			for (TheoryModelProofObligation po : proofObligations.values()) {
				if (!po.isDischarged() && po.getInfRules().length > 0) {
					result++;
				}
			}
		}
		if (aType == IAxiomaticOperatorDefinition.ELEMENT_TYPE) {
			for (TheoryModelProofObligation po : proofObligations.values()) {
				if (!po.isDischarged() && po.getAxiomaticOperators().length > 0) {
					result++;
				}
			}
		}
		if (aType == IAxiomaticDefinitionAxiom.ELEMENT_TYPE) {
			for (TheoryModelProofObligation po : proofObligations.values()) {
				if (!po.isDischarged() && po.getAxiomaticAxioms().length > 0) {
					result++;
				}
			}
		}
		// return all undischarged proof obligations.
		if (aType == IPSStatus.ELEMENT_TYPE) {
			result = getUndischargedPOcount();
		}

		return result;
	}

	/**
	 * 
	 * @return The number of undischarged Proof Obligations (including Reviewed
	 *         POs)
	 */
	public int getUndischargedPOcount() {
		int result = 0;
		for (TheoryModelProofObligation po : proofObligations.values()) {
			if (!po.isDischarged()) {
				result++;
			}
		}
		return result;
	}

	/**
	 * 
	 * @return The number of broken Proof Obligations
	 */
	public int getBrokenPOcount() {
		int result = 0;
		for (TheoryModelProofObligation po : proofObligations.values()) {
			if (po.isBroken()) {
				result++;
			}
		}
		return result;
	}

	/**
	 * 
	 * @return The number of manually discharged Proof Obligations (not
	 *         including reviewed POs)
	 */
	public int getManuallyDischargedPOcount() {
		int result = 0;
		for (TheoryModelProofObligation po : proofObligations.values()) {
			if (po.isManual() && po.isDischarged()) {
				result++;
			}
		}
		return result;
	}

	/**
	 * Gets the number of manually discharged proof obligations that belong to a
	 * certain element type (e.g invariants)
	 * 
	 * @param aType
	 *            The type of the element (invariant, theorem, event...)
	 * @return The number of manually discharged Proof Obligations (not
	 *         including reviewed POs)
	 */
	public int getManuallyDischargedPOcount(IInternalElementType<?> aType) {
		int result = 0;
		if (aType == INewOperatorDefinition.ELEMENT_TYPE) {
			for (TheoryModelProofObligation po : proofObligations.values()) {
				if (po.isManual() && po.isDischarged()
						&& po.getOperators().length > 0) {
					result++;
				}
			}
		}
		if (aType == ITheorem.ELEMENT_TYPE) {
			for (TheoryModelProofObligation po : proofObligations.values()) {
				if (po.isManual() && po.isDischarged()
						&& po.getTheorems().length > 0) {
					result++;
				}
			}
		}
		if (aType == IRewriteRule.ELEMENT_TYPE) {
			for (TheoryModelProofObligation po : proofObligations.values()) {
				if (po.isManual() && po.isDischarged()
						&& po.getRewRules().length > 0) {
					result++;
				}
			}
		}
		
		if (aType == IInferenceRule.ELEMENT_TYPE) {
			for (TheoryModelProofObligation po : proofObligations.values()) {
				if (po.isManual() && po.isDischarged()
						&& po.getInfRules().length > 0) {
					result++;
				}
			}
		}
		if (aType == IAxiomaticOperatorDefinition.ELEMENT_TYPE) {
			for (TheoryModelProofObligation po : proofObligations.values()) {
				if (po.isManual() && po.isDischarged()
						&& po.getAxiomaticOperators().length > 0) {
					result++;
				}
			}
		}
		if (aType == IAxiomaticDefinitionAxiom.ELEMENT_TYPE) {
			for (TheoryModelProofObligation po : proofObligations.values()) {
				if (po.isManual() && po.isDischarged()
						&& po.getAxiomaticAxioms().length > 0) {
					result++;
				}
			}
		}
		// return all manually discharged proof obligations.
		if (aType == IPSStatus.ELEMENT_TYPE) {
			result = getManuallyDischargedPOcount();
		}

		return result;
	}

	/**
	 * 
	 * @return The number of reviewed Proof Obligations
	 */
	public int getReviewedPOcount() {
		int result = 0;
		for (TheoryModelProofObligation po : proofObligations.values()) {
			if (po.isReviewed()) {
				result++;
			}
		}
		return result;
	}

	/**
	 * Gets the number of reviewed proof obligations that belong to a certain
	 * element type (e.g invariants)
	 * 
	 * @param aType
	 *            The type of the element (invariant, theorem, event...)
	 * @return The number of reviewed Proof Obligations
	 */
	public int getReviewedPOcount(IInternalElementType<?> aType) {
		int result = 0;
		if (aType == INewOperatorDefinition.ELEMENT_TYPE) {
			for (TheoryModelProofObligation po : proofObligations.values()) {
				if (po.isReviewed() && po.getOperators().length > 0) {
					result++;
				}
			}
		}
		if (aType == ITheorem.ELEMENT_TYPE) {
			for (TheoryModelProofObligation po : proofObligations.values()) {
				if (po.isReviewed() && po.getTheorems().length > 0) {
					result++;
				}
			}
		}
		if (aType == IRewriteRule.ELEMENT_TYPE) {
			for (TheoryModelProofObligation po : proofObligations.values()) {
				if (po.isReviewed() && po.getRewRules().length > 0) {
					result++;
				}
			}
		}
		if (aType == IInferenceRule.ELEMENT_TYPE) {
			for (TheoryModelProofObligation po : proofObligations.values()) {
				if (po.isReviewed() && po.getInfRules().length > 0) {
					result++;
				}
			}
		}
		if (aType == IAxiomaticOperatorDefinition.ELEMENT_TYPE) {
			for (TheoryModelProofObligation po : proofObligations.values()) {
				if (po.isReviewed() && po.getAxiomaticOperators().length > 0) {
					result++;
				}
			}
		}
		if (aType == IAxiomaticDefinitionAxiom.ELEMENT_TYPE) {
			for (TheoryModelProofObligation po : proofObligations.values()) {
				if (po.isReviewed() && po.getAxiomaticAxioms().length > 0) {
					result++;
				}
			}
		}
		// return all reviewed proof obligations.
		if (aType == IPSStatus.ELEMENT_TYPE) {
			result = getReviewedPOcount();
		}

		return result;
	}


}
