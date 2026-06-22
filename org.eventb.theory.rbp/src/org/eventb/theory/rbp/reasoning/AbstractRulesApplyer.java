/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.reasoning;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.BoolExpression;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.ExtendedPredicate;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IFormulaRewriter;
import org.eventb.core.ast.ISpecialization;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.MultiplePredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.ast.UnaryPredicate;
import org.eventb.core.ast.extensions.pm.Matcher;
import org.eventb.theory.core.IGeneralRule;
import org.eventb.theory.core.ISCRewriteRule;
import org.eventb.theory.internal.rbp.reasoners.input.IPRMetadata;
import org.eventb.theory.rbp.rulebase.IPOContext;
import org.eventb.theory.rbp.rulebase.basis.IDeployedRewriteRule;
import org.eventb.theory.rbp.utils.ProverUtilities;

/**
 * Common implementation of proof rules applyer.
 * 
 * <p>
 * This class is intended to provide fields that are common to rule applyers.
 * 
 * @author maamria
 * @author htson - re-implement as the basis for Automatic rewriter and
 *         Automatic XD rewriter.
 * @version 2.0
 * @see AutoRewriter
 * @see XDAutoRewriter
 * @since 1.0
 */
public abstract class AbstractRulesApplyer implements IFormulaRewriter {
	
	protected AbstractRulesApplyer(IPOContext context, IPRMetadata prMetadata){
		assert context != null;
		this.context = context;
		this.prMetadata = prMetadata;
		this.instantiations = new LinkedHashSet<Expression>();
	}
	
	private Set<Expression> instantiations;
	private String ruleDescription;
	protected IPRMetadata prMetadata;
	protected IPOContext context;
		
	/**
	 * <p>
	 * Applies automatic expression rewrite rules to the given expression.
	 * </p>
	 * 
	 * @param original
	 *            to rewrite
	 * @return the rewritten expression
	 */
	protected final Expression applyExpressionRewrites(Expression original){
		Expression expression = ((Expression) applyRule(original));
		if(original.equals(expression)){
			return original;
		}
		// return rewritten
		return expression;
	}

	/**
	 * <p>Applies automatic predicate rewrite rules to the given predicate.</p>
	 * @param original to rewrite
	 * @return the rewritten predicate
	 */
	protected final Predicate applyPredicateRewrites(Predicate original){
		Predicate pred = ((Predicate) applyRule(original));
		if(original.equals(pred)){
			return original;
		}
		// return rewritten
		return pred;
	}
	
	/**
	 * Returns the formula resulting from applying all the (applicable) 
	 * automatic unconditional rules.
	 * @param original the formula to rewrite
	 * @return the rewritten formula
	 */
	private Formula<?> applyRule(Formula<?> original){
		IGeneralRule rule = getRule(original);
		
		if (rule == null) 
			return original;
		
		Formula<?> ruleLhs = null;
		Formula<?> ruleRhs = null;
		FormulaFactory factory = original.getFactory();

		if (rule instanceof IDeployedRewriteRule) {
			ruleLhs = ((IDeployedRewriteRule) rule).getLeftHandSide();
			ruleRhs = ((IDeployedRewriteRule) rule).getRightHandSides().get(0).getRHSFormula();
			ruleLhs = ruleLhs.translate(factory);
			ruleRhs = ruleRhs.translate(factory);
			ruleDescription = ((IDeployedRewriteRule) rule).getDescription();
		}
		else { //if (rule instanceof ISCRewriteRule) {
			try {
				ITypeEnvironment typeEnvironment = ProverUtilities
						.makeTypeEnvironment(factory, (ISCRewriteRule) rule);
				ruleLhs = ((ISCRewriteRule) rule).getSCFormula(factory,
						typeEnvironment);
				ruleRhs = Arrays.asList(((ISCRewriteRule) rule).getRuleRHSs())
						.get(0).getSCFormula(factory, typeEnvironment);
			} catch (CoreException e) {
				e.printStackTrace();
				return original;
			}
			;
		}
		ISpecialization specialization = Matcher.match(original, ruleLhs);
		if (specialization == null) {
			return original;
		}
		// since rule is unconditional
		if (!ProverUtilities.canBeSpecialized(specialization, ruleRhs)) {
			return original;
		}

		Set<Expression> expressions = ProverUtilities
				.getInstantiatingExpressions(specialization);
		instantiations.addAll(expressions);
		Formula<?> boundRhs = ruleRhs.specialize(specialization);
		return boundRhs;
	}
	
	/**
	 * Returns the list of rewrite rules to apply.
	 * 
	 * <p> Override this method to specify different list of rules.
	 * @param original
	 * @return
	 */
	public abstract IGeneralRule getRule(Formula<?> original);
	
	@Override
	public final Expression rewrite(AssociativeExpression expression) {
		return applyExpressionRewrites(expression);
	}
	
	@Override
	public final Predicate rewrite(AssociativePredicate predicate) {
		return applyPredicateRewrites(predicate);
	}
	
	@Override
	public final Expression rewrite(AtomicExpression expression) {
		return applyExpressionRewrites(expression);
	}

	@Override
	public final Expression rewrite(BinaryExpression expression) {
		return applyExpressionRewrites(expression);
	}

	@Override
	public final Predicate rewrite(BinaryPredicate predicate) {
		return applyPredicateRewrites(predicate);
	}

	@Override
	public final Expression rewrite(BoolExpression expression) {
		return applyExpressionRewrites(expression);
	}

	@Override
	public final Expression rewrite(BoundIdentifier identifier) {
		// DO NOT REWRITE IDENTIFIER
		return identifier;
	}

	@Override
	public final Expression rewrite(FreeIdentifier identifier) {
		// DO NOT REWRITE IDENTIFIER
		return identifier;
	}

	@Override
	public final Expression rewrite(IntegerLiteral literal) {
		return applyExpressionRewrites(literal);
	}

	@Override
	public final Predicate rewrite(LiteralPredicate predicate) {
		return applyPredicateRewrites(predicate);
	}

	@Override
	public final Predicate rewrite(MultiplePredicate predicate) {
		return applyPredicateRewrites(predicate);
	}

	@Override
	public final Expression rewrite(QuantifiedExpression expression) {
		return applyExpressionRewrites(expression);
	}

	@Override
	public final Predicate rewrite(QuantifiedPredicate predicate) {
		return applyPredicateRewrites(predicate);
	}

	@Override
	public final Predicate rewrite(RelationalPredicate predicate) {
		return applyPredicateRewrites(predicate);
	}

	@Override
	public final Expression rewrite(SetExtension expression) {
		return applyExpressionRewrites(expression);
	}

	@Override
	public final Predicate rewrite(SimplePredicate predicate) {
		return applyPredicateRewrites(predicate);
	}
	
	@Override
	public final Predicate rewrite(ExtendedPredicate predicate) {
		return applyPredicateRewrites(predicate);
	}
	
	@Override
	public final Expression rewrite(ExtendedExpression expression) {
		return applyExpressionRewrites(expression);
	}

	@Override
	public final Expression rewrite(UnaryExpression expression) {
		return applyExpressionRewrites(expression);
	}

	@Override
	public final Predicate rewrite(UnaryPredicate predicate) {
		return applyPredicateRewrites(predicate);
	}

	@Override
	public boolean autoFlatteningMode() {
		return false;
	}
	
	@Override
	public void enteringQuantifier(int nbOfDeclarations) {
		// nothing to do
	}

	@Override
	public void leavingQuantifier(int nbOfDeclarations) {
		// nothing to do
	}

	/**
	 * @return
	 */
	public Set<Expression> getInstantiations() {
		return instantiations;
	}

	/**
	 * @return
	 */
	public String getDescription() {
		return ruleDescription;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return "using " + prMetadata + " in the context of " + context;
	}

	
}
