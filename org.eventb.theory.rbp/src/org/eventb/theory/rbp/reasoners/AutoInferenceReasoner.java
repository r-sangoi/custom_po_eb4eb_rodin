/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
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
import org.eventb.theory.internal.rbp.reasoners.input.AutoInferenceInput;
import org.eventb.theory.internal.rbp.reasoners.input.IPRMetadata;
import org.eventb.theory.rbp.plugin.RbPPlugin;
import org.eventb.theory.rbp.rulebase.BaseManager;
import org.eventb.theory.rbp.rulebase.IPOContext;
import org.eventb.theory.rbp.rulebase.basis.IDeployedGiven;
import org.eventb.theory.rbp.rulebase.basis.IDeployedInferenceRule;
import org.eventb.theory.rbp.utils.ProverUtilities;

/**
 * @author maamria
 * 
 */
public class AutoInferenceReasoner extends AbstractContextDependentReasoner
		implements IVersionedReasoner {

	private static final String REASONER_ID = RbPPlugin.PLUGIN_ID + ".autoInferenceReasoner";
	
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
		return 1;
	}
	
	/**
	 * @precondition The reasoner input must be an {@link AutoInferenceInput}.
	 */
	@Override
	public IReasonerOutput apply(IProverSequent sequent,
			IReasonerInput reasonerInput, IProofMonitor pm) {
		// PRECONDITION
		assert reasonerInput instanceof AutoInferenceInput;
		
		// Get information from the reasoner input
		final AutoInferenceInput input = (AutoInferenceInput) reasonerInput;
		final boolean forward = input.isForward();
		final IPRMetadata prMetadata = input.getPRMetadata();
		final String theoryName = prMetadata.getTheoryName();
		final String projectName = prMetadata.getProjectName();
		final String ruleName = prMetadata.getRuleName();

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

		if (forward) {
			return applyForward(sequent, input, rule, projectName, theoryName,
					ruleName);
		} else {
			return applyBackward(sequent, input, rule, projectName, theoryName,
					ruleName);
		}

	}
	
	/**
	 * @param sequent
	 * @param input
	 * @param rule
	 * @param projectName
	 * @param theoryName
	 * @param ruleName
	 * @return
	 */
	private IReasonerOutput applyForward(IProverSequent sequent,
			AutoInferenceInput input, IGeneralRule rule, String projectName,
			String theoryName, String ruleName) {
		if (rule instanceof IDeployedInferenceRule) {
			// if expected forward application but rule is not suitable
			IDeployedInferenceRule deployedRule = (IDeployedInferenceRule) rule;
			if (!deployedRule.isSuitableForForwardReasoning()) {
				return ProverFactory.reasonerFailure(this, input,
						"Cannot use the inference rule " + projectName
								+ "::" + theoryName + "::" + ruleName
								+ " for forward reasoning");
			}
			
			List<IDeployedGiven> hypGivens = deployedRule.getHypGivens();
			if (hypGivens.size() == 0)
				return ProverFactory
						.reasonerFailure(this, input,
								"Inference rule for forward reasoning must have at least one given hypothesis");
			FormulaFactory factory = sequent.getFormulaFactory();
			ISpecialization specialization = factory.makeSpecialization();
			
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
	
			// Ensure that we can specialize the infer
			Predicate infer = deployedRule.getInfer().getInferClause();
			if (!ProverUtilities.canBeSpecialized(specialization, infer)) {
				return ProverFactory.reasonerFailure(this, input,
						"Fails to specialize " + infer + " with "
								+ specialization);
			}

			// Ensure that the specialised infer is new hypothesis
			Predicate specialisedInfer = infer.specialize(specialization);
			if (sequent.containsHypothesis(specialisedInfer)) {
				return ProverFactory.reasonerFailure(this, input,
						"Hypothesis " + specialisedInfer + " exists");
			}
			
			// Ensure that we can specialize the other non-hypothesis givens
			Predicate pred = ProverUtilities.canGivensBeSpecialized(
					deployedRule, specialization);
			if (pred != null) {
				return ProverFactory.reasonerFailure(this, input,
						"Fails to specialize " + pred + " with "
								+ specialization);
			}
	
	
			// We will have (#givens+1) antecedents.
			IAntecedent[] antecedents = ProverUtilities.forwardReasoning(sequent,
					deployedRule, specialization);
	
			String description = deployedRule.getDescription();
			return ProverFactory.makeProofRule(this, input, null, neededHyps,
					description + " (auto forward)", antecedents);
	
		} else { // Statically checked theory
			throw new UnsupportedOperationException(
					"Rule from Statically checked theory is unsupported");
		}
	}

	/**
	 * @param sequent
	 * @param input
	 * @param rule
	 * @param projectName
	 * @param theoryName
	 * @param ruleName
	 * @return
	 */
	private IReasonerOutput applyBackward(IProverSequent sequent,
			AutoInferenceInput input, IGeneralRule rule, String projectName,
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
			inferPredicate = inferPredicate.translate(factory);
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

			IAntecedent[] antecedents = ProverUtilities.backwardReasoning(sequent,
					deployedRule, specialization);
			String description = deployedRule.getDescription();
			return ProverFactory.makeProofRule(this, input, goal, neededHyps,
					description + " (auto backward) on goal", antecedents);

		} else { // Statically checked theory
			throw new UnsupportedOperationException(
					"Rule from Statically checked theory is unsupported");
		}

	}

	@Override
	public void serializeInput(IReasonerInput input, IReasonerInputWriter writer)
			throws SerializeException {
		// PRECONDITION
		assert input instanceof AutoInferenceInput;
		((AutoInferenceInput) input).serialize(writer);
	}

	@Override
	public IReasonerInput deserializeInput(IReasonerInputReader reader)
			throws SerializeException {
		return new AutoInferenceInput(reader);
	}

}
