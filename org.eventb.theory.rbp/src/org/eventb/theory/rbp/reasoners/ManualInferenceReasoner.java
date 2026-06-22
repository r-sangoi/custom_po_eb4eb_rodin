/*******************************************************************************
 * Copyright (c) 2010, 2021 University of Southampton and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.reasoners;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ISpecialization;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.extensions.pm.Matcher;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerInputReader;
import org.eventb.core.seqprover.IReasonerInputWriter;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.IVersionedReasoner;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.SerializeException;
import org.eventb.theory.core.IGeneralRule;
import org.eventb.theory.internal.rbp.reasoners.input.IPRMetadata;
import org.eventb.theory.internal.rbp.reasoners.input.InferenceInput;
import org.eventb.theory.rbp.plugin.RbPPlugin;
import org.eventb.theory.rbp.rulebase.BaseManager;
import org.eventb.theory.rbp.rulebase.IPOContext;
import org.eventb.theory.rbp.rulebase.basis.IDeployedGiven;
import org.eventb.theory.rbp.rulebase.basis.IDeployedInferenceRule;
import org.eventb.theory.rbp.utils.ProverUtilities;

/**
 * <p>
 * An implementation of a manual inference reasoner for the rule-based prover.
 * </p>
 * <p>
 * <i>htson</i>: This has been re-implemented based on the original version 1.0
 * and the (now removed)
 * {org.eventb.theory.rbp.reasoning.ManualInfer} class.
 * </p>
 * 
 * @author maamria
 * @author htson - re-implemented as a context dependent reasoner.
 * @version 2.0.1
 * @see InferenceInput
 * @since 3.1.0
 */
public class ManualInferenceReasoner extends AbstractContextDependentReasoner
		implements IVersionedReasoner {

	public static final String REASONER_ID = RbPPlugin.PLUGIN_ID
			+ ".manualInferenceReasoner";

	@Override
	public String getReasonerID() {
		return REASONER_ID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IVersionedReasoner#getVersion()
	 */
	@Override
	public int getVersion() {
		return 2;
	}

	/**
	 * @precondition The reasoner input must be an {@link InferenceInput}.
	 */
	@Override
	public IReasonerOutput apply(IProverSequent sequent,
			IReasonerInput reasonerInput, IProofMonitor pm) {
		// PRECONDITION
		assert reasonerInput instanceof InferenceInput;

		// Get information from the reasoner input
		final InferenceInput input = (InferenceInput) reasonerInput;
		Predicate hyp = input.getHypothesis();
		// Convert the hypothesis automatically to the sequent's formula factory.
		if (hyp != null && hyp.getFactory() != sequent.getFormulaFactory()) {
			try {
				hyp = hyp.translate(sequent.getFormulaFactory());
			} catch (IllegalArgumentException e) {
				return ProverFactory.reasonerFailure(this, input,
						"Inferencing hypothesis has a mathematical language incompatible with the one of the proof sequent");
			}
		}
		
		final boolean forward = input.isForward();
		final IPRMetadata prMetadata = input.getPRMetadata();
		final String theoryName = prMetadata.getTheoryName();
		final String projectName = prMetadata.getProjectName();
		final String ruleName = prMetadata.getRuleName();

		// Get the context of the prover sequent.
		IPOContext context = ProverUtilities.getContext(sequent);
		if (context == null) {
			return ProverFactory.reasonerFailure(this, input,
					"Cannot determine the context of the sequent");
		}

		// Get the inference rule (given the meta-data) from the current context
		BaseManager manager = BaseManager.getDefault();
		IGeneralRule rule = manager.getInferenceRule(projectName, theoryName,
				ruleName, context);
		if (rule == null) {
			return ProverFactory.reasonerFailure(this, input,
					"Cannot find inference rule " + projectName + "::"
							+ theoryName + "::" + ruleName
							+ " within the given context for ");
		}

		// Check the flag and apply forward or backward respectively
		if (forward) {
			return applyForward(sequent, rule, hyp, input, projectName, theoryName,
					ruleName);
		} else {
			return applyBackward(sequent, rule, input, projectName, theoryName,
					ruleName);
		}
	}

	/**
	 * Utility method to apply an inference rule forwardly to a prover sequent.
	 * A predicate is passed to match the first given of the inference rule. The
	 * inference input, and proof rule meta-data, i.e., project name, theory
	 * name, rule name are passed to construct useful messages in the case of
	 * failure applications.
	 * 
	 * @param sequent
	 *            the prover sequent.
	 * @param rule
	 *            the inference rule
	 * @param hyp
	 *            the hypothesis to match the first given of the rule.
	 * @param input
	 *            the inference input.
	 * @param projectName
	 *            the project name.
	 * @param theoryName
	 *            the theory name.
	 * @param ruleName
	 *            the rule name.
	 * @return result of apply an inference rule forwardly (can be a "failure")
	 * @see ProverFactory
	 * @see ProverUtilities
	 */
	private IReasonerOutput applyForward(IProverSequent sequent,
			IGeneralRule rule, Predicate hyp, InferenceInput input, String projectName,
			String theoryName, String ruleName) {
		if (rule instanceof IDeployedInferenceRule) {
			// if expected forward application but rule is not suitable
			IDeployedInferenceRule deployedRule = (IDeployedInferenceRule) rule;
			if (!deployedRule.isSuitableForForwardReasoning()) {
				return ProverFactory.reasonerFailure(this, input,
						"Cannot use the inference rule " + projectName + "::"
								+ theoryName + "::" + ruleName
								+ " for forward reasoning");
			}

			if (!sequent.containsHypothesis(hyp))
				return ProverFactory.reasonerFailure(this, input, "Hypothesis "
						+ hyp + " does not exist");

			List<IDeployedGiven> hypGivens = deployedRule.getHypGivens();
			if (hypGivens.size() == 0)
				return ProverFactory
						.reasonerFailure(this, input,
								"Inference rule for forward reasoning must have at least one given hypothesis");
			
			// Match the input hypotheses with the givens of the rule.
			FormulaFactory factory = sequent.getFormulaFactory();
			List<Predicate> patterns = ProverUtilities
					.getGivenPredicates(factory, deployedRule);
			Set<Predicate> neededHyps = new HashSet<Predicate>();

			// Match the first given with the input hypothesis
			Predicate firstGiven = patterns.remove(0);
			ISpecialization specialization = Matcher.match(hyp, firstGiven);
			if (specialization == null)
				return ProverFactory
						.reasonerFailure(this, input,
								"Cannot match hypothesis " + hyp + " to " + firstGiven);
			neededHyps.add(hyp);
			
			// Match the other givens
			List<Predicate> formulae = ProverUtilities
					.getAllHypothesis(sequent);
			specialization = Matcher.match(specialization, neededHyps,
					formulae, patterns);
			
			if (specialization == null) {
				return ProverFactory.reasonerFailure(this, input,
						"Fails to match input hypotheses with givens");
			}
	
			// Ensure that we can specialize the other non-hypothesis givens
			Predicate pred = ProverUtilities.canGivensBeSpecialized(
					deployedRule, specialization);
			if (pred != null) {
				return ProverFactory.reasonerFailure(this, input,
						"Fails to specialize " + pred + " with "
								+ specialization);
			}
	
			// Ensure that we can specialize the infer
			Predicate infer = deployedRule.getInfer().getInferClause();
			if (!ProverUtilities.canBeSpecialized(specialization, infer)) {
				return ProverFactory.reasonerFailure(this, input,
						"Fails to specialize " + infer + " with "
								+ specialization);
			}
	
			// We will have (#givens+2) antecedents (including a WD-subgoal).
			IAntecedent[] antecedents = ProverUtilities.forwardReasoning(sequent,
					deployedRule, specialization);
	
			String description = deployedRule.getDescription();
			return ProverFactory.makeProofRule(this, input, null, neededHyps,
					description + " (manual forward) with " + hyp, antecedents);
	
		} else { // Statically checked theory
			throw new UnsupportedOperationException(
					"Rule from Statically checked theory is unsupported");
		}
	
	}

	/**
	 * Utility method to apply an inference rule backwardly to a prover sequent.
	 * The inference input, and proof rule meta-data, i.e., project name, theory
	 * name, rule name are passed to construct useful messages in the case of
	 * failure applications.
	 * 
	 * @param sequent
	 *            the prover sequent.
	 * @param rule
	 *            the inference rule
	 * @param input
	 *            the inference input.
	 * @param projectName
	 *            the project name.
	 * @param theoryName
	 *            the theory name.
	 * @param ruleName
	 *            the rule name.
	 * @return result of apply an inference rule backwardly (can be a "failure")
	 * @see ProverFactory
	 * @see ProverUtilities
	 */
	private IReasonerOutput applyBackward(IProverSequent sequent,
			IGeneralRule rule, InferenceInput input, String projectName,
			String theoryName, String ruleName) {
		if (rule instanceof IDeployedInferenceRule) {
			// if expected backward application but rule is not suitable
			IDeployedInferenceRule deployedRule = (IDeployedInferenceRule) rule;
			if (!deployedRule.isSuitableForBackwardReasoning()) {
				return ProverFactory.reasonerFailure(this, input,
						"Cannot use the inference rule " + projectName
								+ "::" + theoryName + "::" + ruleName
								+ " for backward reasoning");
			}
			FormulaFactory factory = sequent.getFormulaFactory();
			ISpecialization specialization = factory.makeSpecialization();
			Predicate goal = sequent.goal();
			// Match the goal and the infer clause
			Predicate inferPredicate = deployedRule.getInfer()
					.getInferClause();
			specialization = Matcher.match(specialization, goal,
					inferPredicate);
			if (specialization == null) {
				return ProverFactory.reasonerFailure(this, input,
						"Cannot match the goal " + goal
								+ " with the infer predicate "
								+ inferPredicate);
			}

			// Match the input hypotheses with the givens of the rule.
			List<Predicate> formulae = ProverUtilities
					.getAllHypothesis(sequent);
			List<Predicate> patterns = ProverUtilities
					.getGivenPredicates(factory, deployedRule);
			Set<Predicate> neededHyps = new HashSet<Predicate>();
			specialization = Matcher.match(specialization, neededHyps,
					formulae, patterns);
	
			if (specialization == null) {
				return ProverFactory.reasonerFailure(this, input,
						"Fails to match input hypotheses with given hypotheses");
			}

			IAntecedent[] antecedents = ProverUtilities.backwardReasoning(
					sequent, deployedRule, specialization);
			String description = deployedRule.getDescription();
			return ProverFactory.makeProofRule(this, input, goal, neededHyps,
					description + " (manual backward) on goal", antecedents);

		} else { // Statically checked theory
			throw new UnsupportedOperationException(
					"Rule from Statically checked theory is unsupported");
		}

	}

	@Override
	public void serializeInput(IReasonerInput input, IReasonerInputWriter writer)
			throws SerializeException {
		// PRECONDITION
		assert input instanceof InferenceInput;
		
		((InferenceInput) input).serialize(writer);
	}

	@Override
	public IReasonerInput deserializeInput(IReasonerInputReader reader)
			throws SerializeException {
		return new InferenceInput(reader);
	}
}
