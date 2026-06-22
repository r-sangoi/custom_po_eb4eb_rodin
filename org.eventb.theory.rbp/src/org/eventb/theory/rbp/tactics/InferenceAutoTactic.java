/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.tactics;

import static org.eventb.theory.rbp.tactics.CombinableTactic.repeat;
import static org.eventb.theory.rbp.tactics.CombinableTactic.sequentialCompose;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.tactics.BasicTactics;
import org.eventb.theory.core.IGeneralRule;
import org.eventb.theory.core.IReasoningTypeElement.ReasoningType;
import org.eventb.theory.internal.rbp.reasoners.input.AutoInferenceInput;
import org.eventb.theory.internal.rbp.reasoners.input.IPRMetadata;
import org.eventb.theory.internal.rbp.reasoners.input.PRMetadata;
import org.eventb.theory.internal.rbp.reasoners.input.PRMetadataReasonerInput;
import org.eventb.theory.rbp.reasoners.AutoInferenceReasoner;
import org.eventb.theory.rbp.rulebase.BaseManager;
import org.eventb.theory.rbp.rulebase.IPOContext;
import org.eventb.theory.rbp.rulebase.basis.IDeployedInferenceRule;

/**
 * The automatic tactic for applying inference rules.
 * 
 * <p>
 * Only inference rules that can be applied backwardly can be automatic.
 * 
 * @since 1.0
 * 
 * @author maamria
 * @author htson - Re-implemented for 4.0.0
 */
public class InferenceAutoTactic extends ContextDependentTactic implements
		ITactic {

	/*
	 * (non-Javadoc)
	 * 
	 * @see ContextDependentTactic#getTactic(IPOContext)
	 */
	@Override
	protected ITactic getTactic(IPOContext context) {
		BaseManager manager = BaseManager.getDefault();

		List<ICombinableTactic> fwTactics = new ArrayList<ICombinableTactic>();
		// Get the list of forward inference rules. For each rule, create a
		// combinable tactic associated with it.
		List<IGeneralRule> fRules = manager.getInferenceRules(true,
				ReasoningType.FORWARD, context);
		for (IGeneralRule fRule : fRules) {
			IDeployedInferenceRule deployedRule = (IDeployedInferenceRule) fRule;
			ICombinableTactic tactic = getForwardInferenceTactic(deployedRule);
			if (tactic != null)
				fwTactics.add(tactic);
		}

		// Get the list of backward inference rules. For each rule, create a
		// combinable tactic associated with it.
		List<ICombinableTactic> bwTactics = new ArrayList<ICombinableTactic>();
		List<IGeneralRule> bRules = manager.getInferenceRules(true,
				ReasoningType.BACKWARD, context);
		for (IGeneralRule bRule : bRules) {
			IDeployedInferenceRule deployedRule = (IDeployedInferenceRule) bRule;
			ICombinableTactic tactic = getBackwardInferenceTactic(deployedRule);
			if (tactic != null)
				bwTactics.add(tactic);
		}

		// If there is some inference rule tactic then create the composed tactic.
		if (bwTactics.size() != 0) {
			ICombinableTactic[] bwTacticsArray = bwTactics
					.toArray(new ICombinableTactic[bwTactics.size()]);
			ICombinableTactic bwTactic = repeat(sequentialCompose(bwTacticsArray));
			fwTactics.add(bwTactic);
		} 
		if (fwTactics.size() != 0) {
			return sequentialCompose(fwTactics.toArray(new ICombinableTactic[fwTactics
					.size()]));
		} else {
			return BasicTactics
					.failTac("There are no applicable inference rules");
		}		
	}

	/**
	 * Utility method to get a forward inference tactic corresponding to the
	 * input inference rule.
	 * 
	 * @param rule
	 *            the inference rule.
	 * @return the combinable tactic corresponding to the input forward
	 *         inference rule.
	 */
	private ICombinableTactic getForwardInferenceTactic(
			IDeployedInferenceRule rule) {
		
		// Create the auto inference reasoner and input.
		final IReasoner reasoner = new AutoInferenceReasoner();
		String projectName = rule.getProjectName();
		String theoryName = rule.getTheoryName();
		String ruleName = rule.getRuleName();
		IPRMetadata prMetadata = new PRMetadata(projectName, theoryName,
				ruleName);
		final AutoInferenceInput reasonerInput = new AutoInferenceInput(
				prMetadata, true);
		
		// Construct and return the proof rule with the reasoner and input.
		return new ProofRuleTactic() {

			@Override
			public PRMetadataReasonerInput getReasonerInput() {
				return reasonerInput;
			}

			@Override
			public IReasoner getReasoner() {
				return reasoner;
			}
		};
	}

	/**
	 * Utility method to get a backward inference tactic corresponding to the
	 * input inference rule.
	 * 
	 * @param rule
	 *            the inference rule.
	 * @return the combinable tactic corresponding to the input backward
	 *         inference rule.
	 */
	private ICombinableTactic getBackwardInferenceTactic(
			IDeployedInferenceRule rule) {
		final IReasoner reasoner = new AutoInferenceReasoner();
		String projectName = rule.getProjectName();
		String theoryName = rule.getTheoryName();
		String ruleName = rule.getRuleName();
		IPRMetadata prMetadata = new PRMetadata(projectName, theoryName,
				ruleName);
		final AutoInferenceInput reasonerInput = new AutoInferenceInput(
				prMetadata, false);

		return new ProofRuleTactic() {
			
			@Override
			public PRMetadataReasonerInput getReasonerInput() {
				return reasonerInput;
			}

			@Override
			public IReasoner getReasoner() {
				return reasoner;
			}
		};
	}

}
