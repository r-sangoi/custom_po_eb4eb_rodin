package org.eventb.core.internal.ast.extensions.wd;

import static org.eventb.core.ast.Formula.LAND;
import static org.eventb.core.ast.Formula.LOR;

import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.ExtendedPredicate;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.MultiplePredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.UnaryPredicate;

/**
 * 
 * @author maamria
 * 
 */
public class TComputer extends AbstractComputer {
	
	public TComputer(FormulaFactory formulaFactory) {
		super(formulaFactory);
	}

	@Override
	public void visitAssociativePredicate(AssociativePredicate predicate) {
		final Predicate[] children = predicate.getChildren();
		switch (predicate.getTag()) {
		case LAND:
			lemma = landLemmas(children);
			break;
		case LOR:
			lemma = lorLemmas(children);
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
		switch (predicate.getTag()) {
		case Formula.EXISTS:
			lemma = fb.exists(decls, getLemma(child));
			break;
		case Formula.FORALL:
			lemma = fb.forall(decls, getLemma(child));
			break;
		default:
			assert false;
			lemma = null;
			break;
		}
	}

	@Override
	public void visitBinaryPredicate(BinaryPredicate predicate) {
		Predicate left = predicate.getLeft();
		Predicate right = predicate.getRight();
		switch (predicate.getTag()) {
		case Formula.LEQV:
			lemma = fb.land(fb.lor(new FComputer(fb.ff).getLemma(left), getLemma(right)), fb.lor(new FComputer(fb.ff).getLemma(right), getLemma(left)));
			break;
		case Formula.LIMP:
			lemma = fb.lor(new FComputer(fb.ff).getLemma(left), getLemma(right));
			break;
		default:
			assert false;
			lemma = null;
			break;
		}
	}

	@Override
	public void visitLiteralPredicate(LiteralPredicate predicate) {
		if (predicate.equals(fb.btrue)){
			lemma = fb.btrue;
		}
		else {
			lemma = fb.bfalse;
		}
	}

	@Override
	public void visitMultiplePredicate(MultiplePredicate predicate) {
		lemma = fb.land(dComputer.getWDLemma(predicate),predicate);
	}

	@Override
	public void visitRelationalPredicate(RelationalPredicate predicate) {
		lemma = fb.land(dComputer.getWDLemma(predicate) ,predicate);
	}

	@Override
	public void visitSimplePredicate(SimplePredicate predicate) {
		lemma = fb.land(dComputer.getWDLemma(predicate),predicate);
	}

	@Override
	public void visitUnaryPredicate(UnaryPredicate predicate) {
		lemma = new FComputer(fb.ff).getLemma(predicate.getChild());
	}

	@Override
	public void visitExtendedPredicate(ExtendedPredicate predicate) {
		lemma = fb.land(dComputer.getWDLemma(predicate),predicate);
	}
}
