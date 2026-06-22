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

import static org.eventb.core.ast.Formula.LAND;
import static org.eventb.core.ast.Formula.LEQV;
import static org.eventb.core.ast.Formula.LIMP;
import static org.eventb.core.ast.Formula.LOR;

import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.BecomesEqualTo;
import org.eventb.core.ast.BecomesMemberOf;
import org.eventb.core.ast.BecomesSuchThat;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.PredicateVariable;
import org.eventb.core.ast.QuantifiedPredicate;

/**
 * An implementation of well-definedness conditions computer for the D operator.
 * 
 * <p> This implementation is based on the rather cumbersome definition of D as found for example in
 * 
 * "On Using Conditional Definitions in Formal Theories" by J-R Abrial and L. Mussat.
 * 
 * @author maamria, Laurent Voisin
 * @since 1.0
 */
public class DComputer extends AbstractDYComputer {

	public DComputer(FormulaFactory formulaFactory) {
		super(formulaFactory);
	}

	@Override
	public void visitBecomesEqualTo(BecomesEqualTo assignment) {
		lemma = wd(assignment.getExpressions());
	}

	@Override
	public void visitBecomesMemberOf(BecomesMemberOf assignment) {
		lemma = wd(assignment.getSet());
	}

	@Override
	public void visitBecomesSuchThat(BecomesSuchThat assignment) {
		final BoundIdentDecl[] primedIdents = assignment.getPrimedIdents();
		lemma = fb.forall(primedIdents, wd(assignment.getCondition()));
	}

	@Override
	public void visitPredicateVariable(PredicateVariable predVar) {
		lemma = fb.btrue;
	}
	
	@Override
	public void visitAssociativePredicate(AssociativePredicate predicate) {
		final Predicate[] children = predicate.getChildren();
		switch (predicate.getTag()) {
		case LAND:
			lemma = landWD(children);
			break;
		case LOR:
			lemma = lorWD(children);
			break;
		default:
			assert false;
			lemma = null;
			break;
		}
	}
	
	protected Predicate lorWD(Predicate[] children) {
		if (children.length == 0) {
			return fb.btrue;
		}
		if (children.length == 1) {
			return wd(children[0]);
		}
		Predicate[] subPredicates = getSubPredicates(children, 1);
		Predicate subWD = landWD(subPredicates);
		final Predicate subPred = fb.lor(subPredicates);
		final Predicate child = children[0];
		subWD = fb.lor(fb.land(wd(child), fb.limp(fb.negate(child), subWD)), fb.land(subWD, fb.limp(fb.negate(subPred), wd(child))));
		return subWD;
	}

	protected Predicate landWD(Predicate[] children) {
		if (children.length == 0) {
			return fb.btrue;
		}
		if (children.length == 1) {
			return wd(children[0]);

		}
		Predicate[] subPredicates = getSubPredicates(children, 1);
		Predicate subWD = landWD(subPredicates);
		final Predicate subPred = fb.land(subPredicates);
		final Predicate child = children[0];
		subWD = fb.lor(fb.land(wd(child), fb.limp(child, subWD)), fb.land(subWD, fb.limp(subPred, wd(child))));
		return subWD;
	}

	@Override
	public void visitBinaryPredicate(BinaryPredicate predicate) {
		final Predicate left = predicate.getLeft();
		final Predicate right = predicate.getRight();
		switch (predicate.getTag()) {
		case LIMP:
			lemma = fb.lor(fb.land(wd(left), wd(right)), fb.land(wd(left), fb.negate(left)), fb.land(wd(right), right));
			break;
		case LEQV:
			lemma = fb.land(wd(left), wd(right));
			break;
		default:
			assert false;
			lemma = null;
			break;
		}
	}
	
	@Override
	public void visitQuantifiedPredicate(QuantifiedPredicate predicate) {
		final BoundIdentDecl[] decls = predicate.getBoundIdentDecls();
		final Predicate child = predicate.getPredicate();
		// the WD is \forall child OR \exists (wd(child) AND child)
		if (predicate.getTag() == Formula.EXISTS) {
			lemma = fb.lor(fb.forall(decls, wd(child)), fb.exists(decls, fb.land(wd(child), child)));
		}
		// the WD is \forall child OR \exists (wd(child) AND not child)
		else {
			lemma = fb.lor(fb.forall(decls, wd(child)), fb.exists(decls, fb.land(wd(child), fb.negate(child))));
		}
	}
	
	protected Predicate[] getSubPredicates(Predicate[] predicates, int startingIndex) {
		if (startingIndex >= predicates.length || startingIndex < 0) {
			return new Predicate[0];
		}
		Predicate[] newPreds = new Predicate[predicates.length - startingIndex];
		for (int k = startingIndex; k < predicates.length; k++) {
			newPreds[k - startingIndex] = predicates[k];
		}
		return newPreds;
	}
}
