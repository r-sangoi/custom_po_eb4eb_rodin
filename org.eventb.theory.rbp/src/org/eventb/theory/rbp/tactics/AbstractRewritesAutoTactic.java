/*******************************************************************************
 * Copyright (c) 2016 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.tactics;

import static org.eventb.theory.rbp.tactics.CombinableTactic.sequentialCompose;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.BoolExpression;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.ExtendedPredicate;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.MultiplePredicate;
import org.eventb.core.ast.PredicateVariable;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.ast.UnaryPredicate;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.tactics.BasicTactics;
import org.eventb.theory.core.IGeneralRule;
import org.eventb.theory.internal.rbp.reasoners.input.AutoRewriteInput;
import org.eventb.theory.internal.rbp.reasoners.input.IPRMetadata;
import org.eventb.theory.internal.rbp.reasoners.input.PRMetadata;
import org.eventb.theory.internal.rbp.reasoners.input.PRMetadataReasonerInput;
import org.eventb.theory.rbp.rulebase.IPOContext;
import org.eventb.theory.rbp.rulebase.basis.IDeployedRewriteRule;


/**
 * <p>Abstract implementation for rewrite automatic tactics.</p>
 * 
 * @author htson
 * @version 0.1
 * @see RewritesAutoTactic
 * @see XDAutoTactic
 * @since 4.0
 */
public abstract class AbstractRewritesAutoTactic extends ContextDependentTactic implements
		ITactic {

	private static Class<?>[] clazzes = {
		AssociativeExpression.class,
		AtomicExpression.class,
		BinaryExpression.class,
		BoolExpression.class,
		ExtendedExpression.class,
		BoundIdentifier.class,
		FreeIdentifier.class,
		IntegerLiteral.class,
		QuantifiedExpression.class,
		SetExtension.class,
		UnaryExpression.class,
		AssociativePredicate.class,
		BinaryPredicate.class,
		ExtendedPredicate.class,
		LiteralPredicate.class,
		MultiplePredicate.class,
		PredicateVariable.class,
		QuantifiedPredicate.class,
		RelationalPredicate.class,
		SimplePredicate.class,
		UnaryPredicate.class
	};
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ContextDependentTactic#getTactic(IPOContext)
	 */
	@Override
	protected final ITactic getTactic(IPOContext context) {
		List<ICombinableTactic> tactics = getRewriteTactics(context);
		// If there is some inference rule tactic then create the composed tactic.
		if (tactics.size() != 0) {
			return sequentialCompose(tactics
					.toArray(new ICombinableTactic[tactics.size()]));
		} else {
			return BasicTactics.failTac(getNoApplicableRuleMessage());
		}
	}

	public abstract String getNoApplicableRuleMessage(); 
	
	/**
	 * @param context
	 * @return
	 */
	private List<ICombinableTactic> getRewriteTactics(IPOContext context) {
		List<ICombinableTactic> tactics = new ArrayList<ICombinableTactic>();
		// Get the list of rewrite rules. For each rule, create a
		// combinable tactic associated with it.
		for (Class<?> clazz : clazzes) {
			tactics.addAll(getRewriteTactics(context, clazz));
		}

		return tactics;
	}

	/**
	 * @param context
	 * @param clazz
	 * @return
	 */
	private final Collection<? extends ICombinableTactic> getRewriteTactics(
			IPOContext context, Class<?> clazz) {
		List<ICombinableTactic> tactics = new ArrayList<ICombinableTactic>();

		List<IGeneralRule> rules = getRewritesRules(context, clazz);
		for (IGeneralRule rule : rules) {
			IDeployedRewriteRule rwRule = (IDeployedRewriteRule) rule;
			ICombinableTactic tactic = getRewriteTactic(rwRule);
			if (tactic != null)
				tactics.add(tactic);
		}
		return tactics;
	}

	public abstract List<IGeneralRule> getRewritesRules(IPOContext context, Class<?> clazz);
	
	/**
	 * @param rwRule
	 * @return
	 */
	private ICombinableTactic getRewriteTactic(IDeployedRewriteRule rule) {
		// Create the auto inference reasoner and input.
		
		final IReasoner reasoner = getReasoner();
		
		String projectName = rule.getProjectName();
		String theoryName = rule.getTheoryName();
		String ruleName = rule.getRuleName();
		IPRMetadata prMetadata = new PRMetadata(projectName, theoryName,
				ruleName);
		final AutoRewriteInput reasonerInput = new AutoRewriteInput(
				prMetadata);
		
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
	 * @return
	 */
	public abstract IReasoner getReasoner();

}
