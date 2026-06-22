/*******************************************************************************
 * Copyright (c) 2016 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.reasoners;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IHypAction;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerInputReader;
import org.eventb.core.seqprover.IReasonerInputWriter;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.SerializeException;
import org.eventb.core.seqprover.eventbExtensions.DLib;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.theory.internal.rbp.reasoners.input.AutoRewriteInput;
import org.eventb.theory.internal.rbp.reasoners.input.IPRMetadata;
import org.eventb.theory.rbp.reasoning.AbstractRulesApplyer;
import org.eventb.theory.rbp.rulebase.IPOContext;
import org.eventb.theory.rbp.utils.ProverUtilities;

/**
 * <p>
 * Abstract implementation for automatic rewrite reasoner which are used for
 * automatic rewrite tactic, e.g., automatic rewrite reasoner and automatic XD
 * rewrite reasoner.
 * </p>
 *
 * @author htson
 * @version 0.1
 * @see AutoRewriteReasoner
 * @see XDReasoner
 * @since 4.0.0
 */
public abstract class AbstractAutoRewriteReasoner extends
		AbstractContextDependentReasoner implements IReasoner {

	// The set of instantiation expressions, in order to generate WD subgoal
	private Set<Expression> instantiations;
	
	public final IReasonerOutput apply(IProverSequent seq, IReasonerInput input, IProofMonitor pm) {
		// Assert precondition
		IPOContext context = ProverUtilities.getContext(seq);
		assert context != null;
		assert input instanceof AutoRewriteInput;
		
		instantiations = new LinkedHashSet<Expression>();
		AutoRewriteInput rewriteInput = (AutoRewriteInput) input;
		IPRMetadata prMetadata = rewriteInput.getPRMetadata();
		
		final FormulaFactory ff = seq.getFormulaFactory();
		final AbstractRulesApplyer rewriter = getRewriter(context, prMetadata);
		final List<IHypAction> hypActions = new ArrayList<IHypAction>();
		for (Predicate hyp : seq.visibleHypIterable()) {
			// Rewrite the hypothesis
			Predicate inferredHyp = recursiveRewrite(hyp, rewriter);
			Collection<Predicate> inferredHyps = Lib.breakPossibleConjunct(inferredHyp);
			// Check if rewriting made a change
			if (inferredHyp == hyp && inferredHyps.size() == 1)
				continue;
			// Check if rewriting generated something interesting
			inferredHyps.remove(DLib.True(ff));
			Collection<Predicate> originalHyps = Collections.singleton(hyp);
			
			// Hide the original if the inferredHyps is empty, i.e. the
			// hypothesis get rewritten to Lib.True.
			if (inferredHyps.isEmpty()) {
				hypActions.add(ProverFactory.makeHideHypAction(originalHyps));
				continue;
			}
			// Check if rewriting generated something new
			if (seq.containsHypotheses(inferredHyps)) {
				// IMPORTANT: Do NOT de-select the original if the inferred
				// hypotheses already exist.

				// Do NOT re-select the inferred hyps
				continue;
			}
			// make the forward inference action
			if (!inferredHyps.isEmpty())
				hypActions.add(ProverFactory.makeForwardInfHypAction(originalHyps, inferredHyps));
			hypActions.add(ProverFactory.makeHideHypAction(originalHyps));
		}
		Predicate goal = seq.goal();
		Predicate newGoal = recursiveRewrite(goal, rewriter);

		if (newGoal != goal) {
			// Add the WD sub-goal
			Predicate wdPredicate = ProverUtilities.getWDPredicate(
					seq.getFormulaFactory(), instantiations);
			Set<Predicate> wdHypotheses = Collections.singleton(wdPredicate);
			hypActions.add(ProverFactory.makeSelectHypAction(wdHypotheses));

			IAntecedent[] antecedent = new IAntecedent[] {
					ProverFactory.makeAntecedent(wdPredicate),
					ProverFactory.makeAntecedent(newGoal, wdHypotheses, null,
							hypActions) };
			return ProverFactory.makeProofRule(this, input, goal, null, null,
					rewriter.getDescription() + " " + getReasonerDisplayName(),
					antecedent);
		}
		if (!hypActions.isEmpty()) {
			// Add the WD sub-goal
			Predicate wdPredicate = ProverUtilities.getWDPredicate(
					seq.getFormulaFactory(), instantiations);
			Set<Predicate> wdHypotheses = Collections.singleton(wdPredicate);
			hypActions.add(ProverFactory.makeSelectHypAction(wdHypotheses));
			IAntecedent[] antecedent = new IAntecedent[] {
					ProverFactory.makeAntecedent(wdPredicate),
					ProverFactory.makeAntecedent(null, wdHypotheses, null, hypActions) };
			return ProverFactory.makeProofRule(this, input, null, null, null,
					rewriter.getDescription() + " " + getReasonerDisplayName(), antecedent);
		}
		return ProverFactory.reasonerFailure(this, input,
				"No rewrites applicable");
	}

	public abstract String getReasonerDisplayName();
	
	public abstract AbstractRulesApplyer getRewriter(IPOContext context,
			IPRMetadata prMetadata);

	/**
	 * An utility method which try to rewrite a predicate recursively until
	 * reaching a fix-point.
	 * <p>
	 * If no rewrite where performed on this predicate, then a reference to this
	 * predicate is returned (rather than a copy of this predicate). This allows
	 * to test efficiently (using <code>==</code>) whether rewriting made any
	 * change.
	 * </p>
	 * 
	 * <p>
	 * 
	 * @param pred
	 *            the input predicate
	 * @return the resulting predicate after rewrite.
	 */
	private Predicate recursiveRewrite(Predicate pred, AbstractRulesApplyer rewriter) {
		Predicate resultPred;
		resultPred = pred.rewrite(rewriter);
		Set<Expression> expressions;
		while (resultPred != pred) {
			expressions = rewriter.getInstantiations();
			instantiations.addAll(expressions);
			pred = resultPred;
			resultPred = pred.rewrite(rewriter);
		}
		return resultPred;
	}

	@Override
	public final void serializeInput(IReasonerInput input, IReasonerInputWriter writer)
			throws SerializeException {
		assert input instanceof AutoRewriteInput;
		
		((AutoRewriteInput) input).serialize(writer);
	}

	@Override
	public final IReasonerInput deserializeInput(IReasonerInputReader reader)
			throws SerializeException {
		return new AutoRewriteInput(reader);
	}
}
