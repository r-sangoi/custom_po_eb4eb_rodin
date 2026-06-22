/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *     University of Southampton - Adaptation for the D-library
 *******************************************************************************/
package org.eventb.core.internal.ast.extensions.wd;

import static org.eventb.core.ast.Formula.BFALSE;
import static org.eventb.core.ast.Formula.BTRUE;
import static org.eventb.core.ast.Formula.EXISTS;
import static org.eventb.core.ast.Formula.FORALL;
import static org.eventb.core.ast.Formula.GE;
import static org.eventb.core.ast.Formula.IN;
import static org.eventb.core.ast.Formula.KDOM;
import static org.eventb.core.ast.Formula.KFINITE;
import static org.eventb.core.ast.Formula.LAND;
import static org.eventb.core.ast.Formula.LE;
import static org.eventb.core.ast.Formula.LIMP;
import static org.eventb.core.ast.Formula.LOR;
import static org.eventb.core.ast.Formula.LT;
import static org.eventb.core.ast.Formula.NOTEQUAL;
import static org.eventb.core.ast.Formula.PFUN;

import java.math.BigInteger;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.UnaryPredicate;

/**
 * A formula builder based on the formula builder used for <code>WDComputer</code> 
 * in the main AST project.
 * 
 * @author maamria, Laurent Voisin
 * @since 1.0
 *
 */
public class FormulaBuilder {

	public final FormulaFactory ff;
	public final Predicate btrue;
	public final Predicate bfalse;

	/**
	 * Caches constant zero. Shall always be accessed through method
	 * <code>zero()</code>.
	 */
	private Expression zero_cache;

	/**
	 * Caches type INTEGER. Shall always be accessed through method
	 * <code>Z()</code>.
	 */
	private Type Z_cache;

	public FormulaBuilder(FormulaFactory ff) {
		this.ff = ff;
		this.btrue = ff.makeLiteralPredicate(BTRUE, null);
		this.bfalse = ff.makeLiteralPredicate(BFALSE, null);
	}
	
	public Predicate bounded(Expression set, boolean lower) {
		final BoundIdentifier b0 = ff.makeBoundIdentifier(0, null, Z());
		final BoundIdentifier b1 = ff.makeBoundIdentifier(1, null, Z());
		final int tag = lower ? LE : GE;
		final Predicate rel = ff.makeRelationalPredicate(tag, b1, b0, null);
		final RelationalPredicate xInSet = ff.makeRelationalPredicate(IN, b0,
				set.shiftBoundIdentifiers(2), null);
		final Predicate impl = ff.makeBinaryPredicate(LIMP, xInSet, rel, null);
		final BoundIdentDecl[] b = new BoundIdentDecl[] { ff
				.makeBoundIdentDecl("b", null, Z()) };
		final BoundIdentDecl[] x = new BoundIdentDecl[] { ff
				.makeBoundIdentDecl("x", null, Z()) };
		final Predicate conj2 = ff.makeQuantifiedPredicate(EXISTS, b,
				ff.makeQuantifiedPredicate(FORALL, x, impl, null), null);
		return conj2;
	}

	public Predicate exists(BoundIdentDecl[] decls, Predicate pred) {
		if (pred.getTag() == BTRUE)
			return pred;
		return ff.makeQuantifiedPredicate(EXISTS, decls, pred, null);
	}

	public Predicate finite(Expression expr) {
		return ff.makeSimplePredicate(KFINITE, expr, null);
	}

	public Predicate forall(BoundIdentDecl[] decls, Predicate pred) {
		if (pred.getTag() == BTRUE)
			return pred;
		return ff.makeQuantifiedPredicate(FORALL, decls, pred, null);
	}
	
	public Predicate inDomain(Expression fun, Expression expr) {
		final Expression dom = ff.makeUnaryExpression(KDOM, fun, null);
		return ff.makeRelationalPredicate(IN, expr, dom, null);
	}
	
	public Predicate nonNegative(Expression expr) {
		return ff.makeRelationalPredicate(LE, zero(), expr, null);
	}

	public Predicate notEmpty(final Expression expr) {
		final Expression emptyset = ff.makeEmptySet(expr.getType(), null);
		return ff.makeRelationalPredicate(NOTEQUAL, expr, emptyset, null);
	}

	public RelationalPredicate notZero(Expression expr) {
		return ff.makeRelationalPredicate(NOTEQUAL, expr, zero(), null);
	}

	public Predicate partial(Expression fun) {
		final Type funType = fun.getType();
		final Expression src = funType.getSource().toExpression();
		final Expression trg = funType.getTarget().toExpression();
		final Expression pfun = ff.makeBinaryExpression(PFUN, src, trg, null);
		return ff.makeRelationalPredicate(IN, fun, pfun, null);
	}
	
	public RelationalPredicate positive(Expression expr) {
		return ff.makeRelationalPredicate(LT, zero(), expr, null);
	}

	public Type Z() {
		if (Z_cache == null) {
			Z_cache = ff.makeIntegerType();
		}
		return Z_cache;
	}

	public Expression zero() {
		if (zero_cache == null) {
			zero_cache = ff.makeIntegerLiteral(BigInteger.ZERO, null);
		}
		return zero_cache;
	}
	
	/**
	 * Checks whether the given predicates are negations of each other.
	 * @param left the first predicate
	 * @param right the second predicate
	 * @return whether the given predicates are negations of each other
	 */
	public boolean isNegation(Predicate left, Predicate right){
		if (negate(left).equals(right) ||
				negate(right).equals(left)){
			return true;
		}
		return false;
	}
	
	/**
	 * Negates the given predicate.
	 * @param predicate the predicate to negate
	 * @return the negated predicate
	 */
	public Predicate negate(Predicate predicate){
		if(predicate.getTag() == BTRUE){
			return bfalse;
		}
		if(predicate.getTag() == BFALSE){
			return btrue;
		}
		if (predicate.getTag() == UnaryPredicate.NOT){
			return ((UnaryPredicate) predicate).getChild();
		}
		return ff.makeUnaryPredicate(Formula.NOT, predicate, null);
	}
	
	/**
	 * Returns the simplified conjunction of the given predicates. The
	 * simplifications made are based on the following properties:
	 * <ul>
	 * <li>BTRUE is a neutral element for conjunction</li>
	 * <li>The conjunction operator is idempotent.</li>
	 * </ul>
	 * 
	 * @param left
	 *            a predicate
	 * @param right
	 *            another predicate
	 * @return the simplified conjunction of the given predicates
	 */
	public Predicate land(Predicate left, Predicate right) {
		if (left.getTag() == BTRUE) {
			return right;
		}
		if (right.getTag() == BTRUE) {
			return left;
		}
		if(checkTautology(left, right)){
			return bfalse;
		}
		final Predicate[] children = new Predicate[] { left, right };
		return ff.makeAssociativePredicate(LAND, children, null);
	}
	
	/**
	 * Returns the simplified conjunction of the given predicates.
	 * @param children the children predicates
	 * @return the simplified conjunction of the given predicates
	 */
	public Predicate land(Predicate... children) {
		final Set<Predicate> conjuncts = new LinkedHashSet<Predicate>();
		Predicate firstPred = null;
		for (Predicate child : children) {
			if(child.getTag() == BFALSE)
				return bfalse;
			if (child.getTag() != BTRUE){
				conjuncts.add(child);
				firstPred = child;
			}
		}
		switch (conjuncts.size()) {
		case 0:
			return btrue;
		case 1:
			return firstPred;
		}
		if(checkTautology(children)){
			return bfalse;
		}
		return ff.makeAssociativePredicate(LAND, conjuncts, null);
	}
	
	/**
	 * Returns whether there is a tautology in the given predicates as far as conjunction/disjunction is concerned.
	 * @param children the predicate
	 * @return whether a tautology exists
	 */
	public boolean checkTautology(Predicate... children){
		int length = children.length;
		switch (length) {
		case 0:
			return true;
		case 1:
			return false;
		case 2:
			return isNegation(children[0], children[1]);
		}
		for (int i = 0 ; i < length - 1; i++){
			for (int k = i+1 ; k < length; k++){
				if(isNegation(children[i], children[k])){
					return true;
				}
			}
		}
		return false;
	}
	
	public Predicate limp(Predicate left, Predicate right) {
		if (left.getTag() == BTRUE || right.getTag() == BTRUE)
			return right;
		if(left.getTag() == BFALSE){
			return btrue;
		}
		if (right.getTag() == LIMP) {
			final Predicate rightLeft = ((BinaryPredicate) right).getLeft();
			final Predicate newRight = ((BinaryPredicate) right).getRight();
			final Predicate newLeft = land(left, rightLeft);
			return limp(newLeft, newRight);
		}
		if (left.equals(right)) {
			return btrue;
		}
		return ff.makeBinaryPredicate(LIMP, left, right, null);
	}
	
	public Predicate lor(Predicate left, Predicate right) {
		if (left.getTag() == BTRUE)
			return left;
		if (right.getTag() == BTRUE)
			return right;
		if (left.equals(right)){
			return left;
		}
		if(left.getTag() == BFALSE){
			return right;
		}
		if(right.getTag() == BFALSE){
			return left;
		}
		if(checkTautology(left, right)){
			return btrue;
		}
		final Predicate[] children = new Predicate[] { left, right };
		return ff.makeAssociativePredicate(LOR, children, null);
	}
	
	public Predicate lor(Predicate... children) {
		final Set<Predicate> disjuncts = new LinkedHashSet<Predicate>();
		Predicate firstPred = null;
		for (Predicate child : children) {
			if(child.getTag() == BTRUE){
				return btrue;
			}
			if (child.getTag() != BFALSE){
				disjuncts.add(child);
				firstPred = child;
			}
		}
		switch (disjuncts.size()) {
		case 0:
			return btrue;
		case 1:
			return firstPred;
		}
		if(checkTautology(children)){
			return btrue;
		}
		return ff.makeAssociativePredicate(LOR, disjuncts, null);
	}
	
}
