/*******************************************************************************
 * Copyright (c) 2016 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University of Southampton - initial API and implementation
 *******************************************************************************/

package org.eventb.theory.rbp.tactics;

import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.theory.internal.rbp.reasoners.input.PRMetadataReasonerInput;
import org.eventb.theory.rbp.utils.ProverUtilities;

/**
 * <p>
 * Abstract implementation for proof rule tactic which is an extension of
 * combinable tactic. Clients must implement the following abstract methods
 * <ul>
 * <li>{@link #getReasonerInput()}: To return the reasoner input which include
 * PRMetadata of the proof rule.</li>
 * <li>{@link #getReasoner()}: To return the actual reasoner to apply the proof
 * rule.</li>
 * </ul>
 * </p>
 *
 * @author htson
 * @version 0.1
 * @see InferenceAutoTactic
 * @since 4.0.0
 */
public abstract class ProofRuleTactic extends CombinableTactic implements
		ICombinableTactic {

	/**
	 * Abstract method to return the reasoner input, which must contain the
	 * PRMetadata of a proof rule.
	 * 
	 * @return the PRMetadata reasoner input.
	 */
	public abstract PRMetadataReasonerInput getReasonerInput();

	/**
	 * Abstract method to return the reasoner.
	 * 
	 * @return the reasoner.
	 */
	public abstract IReasoner getReasoner();

	/*
	 * (non-Javadoc)
	 * 
	 * @see CombinableTactic#performApply(IProofTreeNode, IProofMonitor)
	 */
	@Override
	protected Object performApply(IProofTreeNode ptNode, IProofMonitor pm) {
		final IReasonerOutput reasonerOutput;
		IReasoner reasoner = getReasoner();
		IReasonerInput reasonerInput = getReasonerInput();
		try {
			reasonerOutput = reasoner.apply(ptNode.getSequent(), reasonerInput,
					pm);
		} catch (Exception e) {
			final String msg = "while applying the reasoner: "
					+ reasoner.getReasonerID();
			ProverUtilities.log(e, msg);
			return "Reasoner failed unexpectedly, see error log";
		}
		if (reasonerOutput == null)
			return "! Plugin returned null !";
		if (!(reasonerOutput instanceof IProofRule))
			return reasonerOutput;
		IProofRule rule = (IProofRule) reasonerOutput;
		if (ptNode.applyRule(rule))
			return null;
		else
			return "Rule " + rule.getDisplayName() + " is not applicable";
	}

}
