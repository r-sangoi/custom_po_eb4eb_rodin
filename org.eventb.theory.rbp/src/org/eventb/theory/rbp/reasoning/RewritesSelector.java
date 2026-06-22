/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.reasoning;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.BoolExpression;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.ExtendedPredicate;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IAccumulator;
import org.eventb.core.ast.IFormulaInspector;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.ISpecialization;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.MultiplePredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.PredicateVariable;
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
import org.eventb.theory.internal.rbp.reasoners.input.PRMetadata;
import org.eventb.theory.internal.rbp.reasoners.input.RewriteInput;
import org.eventb.theory.rbp.rulebase.BaseManager;
import org.eventb.theory.rbp.rulebase.IPOContext;
import org.eventb.theory.rbp.rulebase.basis.IDeployedRewriteRule;
import org.eventb.theory.rbp.tactics.applications.RewriteTacticApplication;
import org.eventb.theory.rbp.utils.ProverUtilities;
import org.eventb.ui.prover.ITacticApplication;

/**
 * A selector of applicable interactive rewrite rules.
 * 
 * @since 1.0
 * 
 * @author maamria
 * 
 */
public class RewritesSelector implements IFormulaInspector<ITacticApplication> {

	protected final Predicate predicate;
	protected final BaseManager manager;
	protected final boolean isGoal;

	private IPOContext context;

	public RewritesSelector(Predicate predicate, boolean isGoal, IPOContext context) {
		this.predicate = predicate;
		this.isGoal = isGoal;
		this.manager = BaseManager.getDefault();
		this.context = context;
	}

	/**
	 * Checks if any rules are applicable to the formula <code>form</code>.
	 * <p>
	 * Information about applicability are stored in the given accumulator.
	 * 
	 * @param formula
	 *            the formula
	 * @param accum
	 *            the accumulator
	 */
	protected void select(Formula<?> formula, IAccumulator<ITacticApplication> accum) {
		Class<?> clazz = formula.getClass();
		List<IGeneralRule> rules = manager.getRewriteRules(false, clazz, context);
		FormulaFactory factory = context.getFormulaFactory();
		for (IGeneralRule rule : rules) {
			if (rule instanceof IDeployedRewriteRule) {
				Formula<?> ruleLhs = ( (IDeployedRewriteRule) rule).getLeftHandSide();
				ruleLhs = ruleLhs.translate(factory);
				if (canFindABinding(formula, ruleLhs)) {
					IDeployedRewriteRule deployedRule = (IDeployedRewriteRule) rule;
					if (deployedRule.isConditional() && !predicate.isWDStrict(accum.getCurrentPosition())) {
						continue;
					}
					String projectName = deployedRule.getProjectName();
					String theoryName = deployedRule.getTheoryName();
					String ruleName = deployedRule.getRuleName();
					IPRMetadata prMetadata = new PRMetadata(projectName, theoryName, ruleName);
					IPosition position = accum.getCurrentPosition();
					RewriteInput input = new RewriteInput(isGoal ? null : predicate, position, prMetadata);
					accum.add(new RewriteTacticApplication(input, context));
				}
			} else { //if (rule instanceof ISCRewriteRule) {
				try {
					ISCRewriteRule scRule = (ISCRewriteRule) rule;
					ITypeEnvironment typeEnvironment = ProverUtilities.makeTypeEnvironment(factory, scRule);
					if (canFindABinding(formula, (scRule.getSCFormula(factory, typeEnvironment)))) {
						if (ProverUtilities.isConditional(scRule, factory, typeEnvironment) && !predicate.isWDStrict(accum.getCurrentPosition())) {
							continue;
						}
						String projectName = scRule.getRoot().getRodinProject().getElementName();
						String theoryName = scRule.getRoot().getElementName();
						String ruleName = scRule.getLabel();
						IPRMetadata prMetadata = new PRMetadata(projectName, theoryName, ruleName);
						IPosition position = accum.getCurrentPosition();
						RewriteInput rewriteInput = new RewriteInput( 
								isGoal ? null : predicate, position, prMetadata);
						accum.add(new RewriteTacticApplication(rewriteInput,
								context));
					}
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Returns whether the <code>pattern</code> is matchable to
	 * <code>form</code>.
	 * 
	 * @param form
	 *            the theory formula
	 * @param pattern
	 *            the pattern formula
	 * @return <code>true</code> iff <code>pattern</code> is matchable to
	 *         <code>form</code>
	 */
	protected boolean canFindABinding(Formula<?> form, Formula<?> pattern) {
		ISpecialization specialization = Matcher.match(form, pattern);
		return (specialization != null);
	}

	@Override
	public void inspect(AssociativeExpression expression, IAccumulator<ITacticApplication> accumulator) {
		select(expression, accumulator);
	}

	@Override
	public void inspect(AssociativePredicate predicate, IAccumulator<ITacticApplication> accumulator) {
		select(predicate, accumulator);

	}

	@Override
	public void inspect(AtomicExpression expression, IAccumulator<ITacticApplication> accumulator) {
		select(expression, accumulator);

	}

	@Override
	public void inspect(BinaryExpression expression, IAccumulator<ITacticApplication> accumulator) {
		select(expression, accumulator);

	}

	@Override
	public void inspect(BinaryPredicate predicate, IAccumulator<ITacticApplication> accumulator) {
		select(predicate, accumulator);

	}

	@Override
	public void inspect(BoolExpression expression, IAccumulator<ITacticApplication> accumulator) {
		select(expression, accumulator);
	}

	@Override
	public void inspect(BoundIdentDecl decl, IAccumulator<ITacticApplication> accumulator) {
		// nothing 
	}

	@Override
	public void inspect(BoundIdentifier identifier, IAccumulator<ITacticApplication> accumulator) {
		// nothing 
	}

	@Override
	public void inspect(ExtendedExpression expression, IAccumulator<ITacticApplication> accumulator) {
		select(expression, accumulator);

	}

	@Override
	public void inspect(ExtendedPredicate predicate, IAccumulator<ITacticApplication> accumulator) {
		select(predicate, accumulator);
	}

	@Override
	public void inspect(FreeIdentifier identifier, IAccumulator<ITacticApplication> accumulator) {
		// nothing
	}

	@Override
	public void inspect(IntegerLiteral literal, IAccumulator<ITacticApplication> accumulator) {
		select(literal, accumulator);

	}

	@Override
	public void inspect(LiteralPredicate predicate, IAccumulator<ITacticApplication> accumulator) {
		select(predicate, accumulator);

	}

	@Override
	public void inspect(MultiplePredicate predicate, IAccumulator<ITacticApplication> accumulator) {
		select(predicate, accumulator);

	}

	@Override
	public void inspect(PredicateVariable predicate, IAccumulator<ITacticApplication> accumulator) {
		// nothing
	}

	@Override
	public void inspect(QuantifiedExpression expression, IAccumulator<ITacticApplication> accumulator) {
		select(expression, accumulator);

	}

	@Override
	public void inspect(QuantifiedPredicate predicate, IAccumulator<ITacticApplication> accumulator) {
		select(predicate, accumulator);

	}

	@Override
	public void inspect(RelationalPredicate predicate, IAccumulator<ITacticApplication> accumulator) {
		select(predicate, accumulator);

	}

	@Override
	public void inspect(SetExtension expression, IAccumulator<ITacticApplication> accumulator) {
		select(expression, accumulator);

	}

	@Override
	public void inspect(SimplePredicate predicate, IAccumulator<ITacticApplication> accumulator) {
		select(predicate, accumulator);

	}

	@Override
	public void inspect(UnaryExpression expression, IAccumulator<ITacticApplication> accumulator) {
		select(expression, accumulator);

	}

	@Override
	public void inspect(UnaryPredicate predicate, IAccumulator<ITacticApplication> accumulator) {
		select(predicate, accumulator);

	}

}
