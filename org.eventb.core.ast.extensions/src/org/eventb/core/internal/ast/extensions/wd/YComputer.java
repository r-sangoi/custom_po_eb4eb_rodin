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

import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.BecomesEqualTo;
import org.eventb.core.ast.BecomesMemberOf;
import org.eventb.core.ast.BecomesSuchThat;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.PredicateVariable;
import org.eventb.core.ast.QuantifiedPredicate;

/**
 * An implementation of well-definedness conditions computer for the D operator.
 * 
 * <p> Based implementation on paper : <q>Efficient Well-definedness Checking</q>.
 * @author maamria
 * @since 2.0
 */
public class YComputer extends AbstractDYComputer {

	public YComputer(FormulaFactory formulaFactory) {
		super(formulaFactory);;
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
	public void visitPredicateVariable(PredicateVariable predVar) {
		throw new IllegalStateException("Cannot compute WD for predicate variable");
	}
	
	@Override
	public void visitAssociativePredicate(AssociativePredicate predicate) {
		Predicate lemma1 = new TComputer(fb.ff).getLemma(predicate);
		Predicate lemma2 = new FComputer(fb.ff).getLemma(predicate);
		lemma = fb.lor(lemma1, lemma2);
	}

	@Override
	public void visitBinaryPredicate(BinaryPredicate predicate) {
		lemma = fb.lor(new TComputer(fb.ff).getLemma(predicate), new FComputer(fb.ff).getLemma(predicate));
	}
	
	@Override
	public void visitQuantifiedPredicate(QuantifiedPredicate predicate) {
		lemma = fb.lor(new TComputer(fb.ff).getLemma(predicate), new FComputer(fb.ff).getLemma(predicate));
	}

	public static void main(String[] args) {
		FormulaFactory ff = FormulaFactory.getDefault();
		IParseResult parsePredicate = ff.parsePredicate("card({1,2}) = 2 ∨ 1÷0=3 ", null);
		Predicate pred = parsePredicate.getParsedPredicate();
		YComputer comp = new YComputer(ff);
		Predicate wdLemma = comp.getWDLemma(pred);
		System.out.println("Y:     "+wdLemma);
		System.out.println("L:     "+pred.getWDPredicate());
		System.out.println("D:     "+new YComputer(ff).getWDLemma(pred));
	}
	
}
