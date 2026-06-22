/*******************************************************************************
 * Copyright (c) 2011, 2020 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *     University of Southampton - Adaptation for the D-library
 *     CentraleSupélec - refactoring of common parts of D and Y computers
 *******************************************************************************/
package org.eventb.core.internal.ast.extensions.wd;

import static org.eventb.core.ast.Formula.CSET;
import static org.eventb.core.ast.Formula.DIV;
import static org.eventb.core.ast.Formula.EXPN;
import static org.eventb.core.ast.Formula.FUNIMAGE;
import static org.eventb.core.ast.Formula.KCARD;
import static org.eventb.core.ast.Formula.KID_GEN;
import static org.eventb.core.ast.Formula.KINTER;
import static org.eventb.core.ast.Formula.KMAX;
import static org.eventb.core.ast.Formula.KMIN;
import static org.eventb.core.ast.Formula.KPRED;
import static org.eventb.core.ast.Formula.KPRJ1_GEN;
import static org.eventb.core.ast.Formula.KPRJ2_GEN;
import static org.eventb.core.ast.Formula.KSUCC;
import static org.eventb.core.ast.Formula.MOD;
import static org.eventb.core.ast.Formula.QINTER;
import static org.eventb.core.ast.Formula.QUNION;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BoolExpression;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.ExtendedPredicate;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ISimpleVisitor2;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.MultiplePredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.ast.UnaryPredicate;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.core.ast.extension.IExtendedFormula;
import org.eventb.core.ast.extension.IPredicateExtension;

/**
 * Base class for the D and Y computer.
 */
public abstract class AbstractDYComputer implements ISimpleVisitor2 {

	protected FormulaBuilder fb;

	/**
	 * This is the WD condition, it is the result of the last visit.
	 */
	protected Predicate lemma;

	public AbstractDYComputer(FormulaFactory formulaFactory) {
		this.fb = new FormulaBuilder(formulaFactory);
	}

	/**
	 * Returns the well-definedness condition of the given formula.
	 *
	 * <p> The returned formula is flattened.
	 *
	 * @param formula the formula
	 * @return the WD condition
	 */
	public Predicate getWDLemma(Formula<?> formula) {
		assert formula.isTypeChecked();
		return wd(formula).flatten();
	}

	/**
	 * Returns the well-definedness condition of the given formula.
	 *
	 * @param formula the formula
	 * @return the WD condition
	 */
	protected Predicate wd(Formula<?> formula) {
		formula.accept(this);
		return lemma;
	}

	/**
	 * Conjuncts the well-definedness condition of <code>left</code> and <code>right</code>.
	 * @param left the left predicate
	 * @param right the right predicate
	 * @return the conjunction of well-definedness of <code>left</code> and <code>right</code>
	 */
	protected Predicate wd(Formula<?> left, Formula<?> right) {
		return fb.land(wd(left), wd(right));
	}

	/**
	 * Conjuncts the well-definedness condition of <code>children</code>.
	 * @param children the children predicates
	 * @return the conjunction of well-definedness of <code>children</code>
	 */
	protected Predicate wd(Formula<?>... children) {
		final int length = children.length;
		final Predicate[] wds = new Predicate[length];
		for (int i = 0; i < length; i++) {
			wds[i] = wd(children[i]);
		}
		return fb.land(wds);
	}

	/**
	 * Generates a predicate that can be added as part of the well-definedness condition of a binary expression.
	 * @param expr the binary expression
	 * @param left the left expression
	 * @param right the right expression
	 * @return the additional WD predicate
	 */
	protected Predicate binExprWD(BinaryExpression expr, Expression left, Expression right) {
		switch (expr.getTag()) {
		case DIV:
			return fb.notZero(right);
		case MOD:
			return fb.land(fb.nonNegative(left), fb.positive(right));
		case EXPN:
			return fb.land(fb.nonNegative(left), fb.nonNegative(right));
		case FUNIMAGE:
			if (isBuiltinTotalFunction(left)) {
				return fb.btrue;
			}
			return fb.land(fb.inDomain(left, right), fb.partial(left));
		default:
			return fb.btrue;
		}
	}

	private boolean isBuiltinTotalFunction(Expression expr) {
		final int tag = expr.getTag();
		return tag == KPRED || tag == KSUCC || tag == KPRJ1_GEN || tag == KPRJ2_GEN || tag == KID_GEN;
	}

	/**
	 * Returns the complement WD predicate of the unary expression with respect to its child.
	 * @param expr the unary expression
	 * @param child the child expression
	 * @return the additional WD predicate
	 */
	protected Predicate uExprWD(UnaryExpression expr, Expression child) {
		switch (expr.getTag()) {
		case KCARD:
			return fb.finite(child);
		case KMIN:
			return fb.land(fb.notEmpty(child), fb.bounded(child, true));
		case KMAX:
			return fb.land(fb.notEmpty(child), fb.bounded(child, false));
		case KINTER:
			return fb.notEmpty(child);
		default:
			return fb.btrue;
		}
	}

	/**
	 * Returns the additional WD predicate that corresponds to each child of <code>formula</code>.
	 * @param initialWD the initial WD
	 * @param formula the formula
	 * @return the additional WD condition
	 */
	protected Predicate addChildrenWD(Predicate initialWD, IExtendedFormula formula) {
		final Predicate exprWD = wd(formula.getChildExpressions());
		final Predicate predWD = wd(formula.getChildPredicates());
		final Predicate childWD = fb.land(exprWD, predWD);
		return fb.land(initialWD, childWD);
	}

	@Override
	public void visitBoundIdentDecl(BoundIdentDecl boundIdentDecl) {
		lemma = fb.btrue;
	}

	@Override
	public void visitBoundIdentifier(BoundIdentifier identifierExpression) {
		lemma = fb.btrue;
	}

	@Override
	public void visitFreeIdentifier(FreeIdentifier identifierExpression) {
		lemma = fb.btrue;
	}

	@Override
	public void visitIntegerLiteral(IntegerLiteral expression) {
		lemma = fb.btrue;
	}

	@Override
	public void visitLiteralPredicate(LiteralPredicate predicate) {
		lemma = fb.btrue;
	}

	@Override
	public void visitAssociativeExpression(AssociativeExpression expression) {
		lemma = wd(expression.getChildren());
	}

	@Override
	public void visitAtomicExpression(AtomicExpression expression) {
		lemma = fb.btrue;
	}

	@Override
	public void visitBinaryExpression(BinaryExpression expression) {
		final Expression left = expression.getLeft();
		final Expression right = expression.getRight();
		lemma = fb.land(wd(left), wd(right), binExprWD(expression, left, right));
	}

	@Override
	public void visitBoolExpression(BoolExpression expression) {
		lemma = wd(expression.getPredicate());
	}

	@Override
	public void visitQuantifiedExpression(QuantifiedExpression expression) {
		final BoundIdentDecl[] decls = expression.getBoundIdentDecls();
		final Predicate pred = expression.getPredicate();
		final Expression expr = expression.getExpression();
		// \forall (wd(pred) AND pred=>wd(expr))
		final Predicate childrenWD = fb.forall(decls,//
				fb.land(wd(pred), fb.limp(pred, wd(expr))));
		final Predicate localWD;
		switch (expression.getTag()) {
		case QUNION:
		case CSET:
			localWD = fb.btrue;
			break;
		case QINTER:
			localWD = fb.exists(decls, pred);
			break;
		default:
			assert false;
			localWD = null;
			break;
		}
		lemma = fb.land(childrenWD, localWD);
	}

	@Override
	public void visitSetExtension(SetExtension expression) {
		lemma = wd(expression.getMembers());
	}

	@Override
	public void visitUnaryExpression(UnaryExpression expression) {
		final Expression child = expression.getChild();
		lemma = fb.land(wd(child), uExprWD(expression, child));
	}

	@Override
	public void visitMultiplePredicate(MultiplePredicate predicate) {
		lemma = wd(predicate.getChildren());
	}

	@Override
	public void visitRelationalPredicate(RelationalPredicate predicate) {
		lemma = wd(predicate.getLeft(), predicate.getRight());
	}

	@Override
	public void visitSimplePredicate(SimplePredicate predicate) {
		lemma = wd(predicate.getExpression());
	}

	@Override
	public void visitExtendedPredicate(ExtendedPredicate predicate) {
		IPredicateExtension extension = predicate.getExtension();
		final YMediator wdMed = new YMediator(fb);
		final Predicate extensionWD = extension.getWDPredicate(predicate, wdMed);
		if (extension.conjoinChildrenWD()) {
			lemma = addChildrenWD(extensionWD, predicate);
		} else {
			lemma = extensionWD;
		}
	}

	@Override
	public void visitExtendedExpression(ExtendedExpression expression) {
		IExpressionExtension extension = expression.getExtension();
		final YMediator wdMed = new YMediator(fb);
		final Predicate extensionWD = extension.getWDPredicate(expression, wdMed);
		if (extension.conjoinChildrenWD()) {
			lemma = addChildrenWD(extensionWD, expression);
		} else {
			lemma = extensionWD;
		}
	}

	@Override
	public void visitUnaryPredicate(UnaryPredicate predicate) {
		lemma = wd(predicate.getChild());
	}

}
