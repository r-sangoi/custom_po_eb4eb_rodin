/*******************************************************************************
 * Copyright (c) 2012, 2020 University of Southampton and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.internal.ast.extensions.wd;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.BecomesEqualTo;
import org.eventb.core.ast.BecomesMemberOf;
import org.eventb.core.ast.BecomesSuchThat;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BoolExpression;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ISimpleVisitor2;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.PredicateVariable;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.UnaryExpression;

/**
 * Base class for the T and F computers.
 * 
 * @author im06r
 *
 */
public abstract class AbstractComputer implements ISimpleVisitor2{

	protected YComputer dComputer;
	
	protected FormulaBuilder fb;
	
	/**
	 * This is the condition, it is the result of the last visit.
	 */
	protected Predicate lemma;
	
	protected AbstractComputer(FormulaFactory formulaFactory) {
		this.fb = new FormulaBuilder(formulaFactory);
		dComputer = new YComputer(formulaFactory);
	}
	
	/**
	 * Returns the condition of the given formula.
	 * 
	 * <p> The returned formula is flattened.
	 * 
	 * @param formula the formula
	 * @return the condition
	 */
	public Predicate getLemma(Formula<?> formula) {
		assert formula.isTypeChecked();
		return condition(formula).flatten();
	}
	
	/**
	 * Returns the condition of the given formula.
	 * 
	 * @param formula the formula
	 * @return the condition
	 */
	protected Predicate condition(Formula<?> formula) {
		formula.accept(this);
		return lemma;
	}
	
	protected Predicate landLemmas(Predicate... children){
		Predicate[] lemmas = new Predicate[children.length];
		int i = 0;
		for (Predicate predicate : children){
			lemmas[i] = getLemma(predicate);
			i++;
		}
		return fb.land(lemmas);
	}
	
	protected Predicate lorLemmas(Predicate... children){
		Predicate[] lemmas = new Predicate[children.length];
		int i = 0;
		for (Predicate predicate : children){
			lemmas[i] = getLemma(predicate);
			i++;
		}
		return fb.lor(lemmas);
	}
	
	@Override
	public void visitBecomesEqualTo(BecomesEqualTo assignment) {
		throw new IllegalStateException("Cannot compute WD for assignment");
	}

	@Override
	public void visitBecomesMemberOf(BecomesMemberOf assignment) {
		throw new IllegalStateException("Cannot compute WD for assignment");
	}

	@Override
	public void visitBecomesSuchThat(BecomesSuchThat assignment) {
		throw new IllegalStateException("Cannot compute WD for assignment");
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
	public void visitAtomicExpression(AtomicExpression expression) {
		lemma = fb.btrue;
	}
	
	@Override
	public void visitAssociativeExpression(AssociativeExpression expression) {
		lemma = dComputer.getWDLemma(expression);
	}

	@Override
	public void visitBinaryExpression(BinaryExpression expression) {
		lemma = dComputer.getWDLemma(expression);
	}

	@Override
	public void visitBoolExpression(BoolExpression expression) {
		lemma = dComputer.getWDLemma(expression);
	}

	@Override
	public void visitQuantifiedExpression(QuantifiedExpression expression) {
		lemma = dComputer.getWDLemma(expression);
	}

	@Override
	public void visitSetExtension(SetExtension expression) {
		lemma = dComputer.getWDLemma(expression);
	}
	
	@Override
	public void visitExtendedExpression(ExtendedExpression expression) {
		lemma = dComputer.getWDLemma(expression);
	}
	
	@Override
	public void visitUnaryExpression(UnaryExpression expression) {
		lemma = dComputer.getWDLemma(expression);
	}
	
	@Override
	public void visitPredicateVariable(PredicateVariable predVar) {
		// TODO not sure what to do here
		throw new IllegalStateException("Cannot compute WD for predicate variable");
	}
	
}
