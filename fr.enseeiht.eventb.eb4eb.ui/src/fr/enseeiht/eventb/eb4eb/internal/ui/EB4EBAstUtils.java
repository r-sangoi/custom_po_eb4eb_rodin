package fr.enseeiht.eventb.eb4eb.internal.ui;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.BecomesSuchThat;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.Type;

public class EB4EBAstUtils {
	
	/**
	 * Return a cartesian product of the type of each variable.
	 * 
	 * @param factory
	 * 				  The factory used to create the expression
	 * @param vars
	 * 			   The variable used to get the type
	 * @return The expression in a cartesian product form 
	 * @throws CoreException
	 */
	public static Expression makeProductType(FormulaFactory factory, Collection<Type> types) throws CoreException {
		Expression expr;
		
		Iterator<Type> it = types.iterator();
		
		if (it.hasNext()) {
			Type type = it.next();

			while (it.hasNext()) {
				type = factory.makeProductType(type, it.next());
			}
			expr = type.toExpression();
		} else {
			expr = factory.makeEmptySet(null, null);
		}
		
		return expr;
	}
	
	/**
	 * Return a cartesian product of the type of each variable.
	 * 
	 * @param factory
	 * 				  The factory used to create the expression
	 * @param vars
	 * 			   The variable used to get the type
	 * @return The expression in a cartesian product form 
	 * @throws CoreException
	 */
	public static Expression makeProductType(FormulaFactory factory, Type[] types) throws CoreException {
		return makeProductType(factory, Arrays.asList(types));
	}
	
	/**
	 * Merge all the assignments in one {@link BecomesSuchThat} (<code>:|</code>).
	 * 
	 * @param factory
	 * 				  The factory used to create the assignment
	 * @param assignments
	 * 					  The assignments to merge
	 * @return The merged assignment
	 */
	public static Assignment makeBABecomeSuchThat(FormulaFactory factory, Assignment... assignments) { //TODO : utilisé ?
		Collection<FreeIdentifier> fids = 
				Arrays.asList(assignments).stream()
									      .flatMap(a -> Arrays.asList(a.getAssignedIdentifiers()).stream())
									      .collect(Collectors.toList());
		
		Collection<BoundIdentDecl> bids = 
				fids.stream()
					.map(FreeIdentifier::asPrimedDecl)
					.collect(Collectors.toList());
		
		Collection<Predicate> predicates = 
				Arrays.asList(assignments).stream()
										  .map(Assignment::getBAPredicate)
										  .collect(Collectors.toList());
		
		Predicate combinedPredicate;
		if(predicates.size() == 0)
			return null; //TODO : faire quoi ? Check avant ?
		else if(predicates.size() == 1)
			combinedPredicate = predicates.iterator().next();
		else
			combinedPredicate = factory.makeAssociativePredicate(Formula.LAND, predicates, null);
		
		return factory.makeBecomesSuchThat(fids, bids, combinedPredicate, null);
	}
	
	/**
	 * Merge all the assignments in one BAP.
	 * 
	 * @param factory
	 * 				  The factory used to create the assignment
	 * @param assignments
	 * 					  The assignments to merge
	 * @return The merged predicate
	 */
	public static Predicate makeCombinedBAs(FormulaFactory factory, Collection<Assignment> assignments) {
		Collection<Predicate> predicates = 
				assignments.stream()
						   .map(Assignment::getBAPredicate)
						   .map(p -> p.translate(factory))
						   .collect(Collectors.toList());
		
		Predicate combinedPredicate;
		if(predicates.size() == 0)
			return null; //TODO : faire quoi ? Check avant ?
		else if(predicates.size() == 1)
			combinedPredicate = predicates.iterator().next();
		else
			combinedPredicate = factory.makeAssociativePredicate(Formula.LAND, predicates, null);
		
		return combinedPredicate;
	}
	
	/**
	 * Merge all the assignments in one BAP.
	 * 
	 * @param factory
	 * 				  The factory used to create the assignment
	 * @param assignments
	 * 					  The assignments to merge
	 * @return The merged predicate
	 */
	public static Predicate makeCombinedBAs(FormulaFactory factory, Assignment[] assignments) {
		return makeCombinedBAs(factory, Arrays.asList(assignments));
	}
	
	/**
	 * Merge the variables in one expression separated by maplets.
	 * 
	 * @param factory
	 * 				  The factory used to create the expression
	 * @param variables
	 * 					  The variables to merge
	 * @return The merged expression
	 */
	public static Expression makeMapletVars(FormulaFactory factory, Collection<FreeIdentifier> vars) {
		Expression mapletsExpression = null;
		Iterator<FreeIdentifier> mapletsIt = vars.iterator();
		if (mapletsIt.hasNext()) {
			mapletsExpression = mapletsIt.next();
			while (mapletsIt.hasNext()) {
				mapletsExpression = factory.makeBinaryExpression(Formula.MAPSTO, mapletsExpression, mapletsIt.next(), null);
			}
		}
		return mapletsExpression;
	}
	
	/**
	 * Merge the variables in one expression separated by maplets.
	 * 
	 * @param factory
	 * 				  The factory used to create the expression
	 * @param variables
	 * 					  The variables to merge
	 * @return The merged expression
	 */
	public static Expression makeMapletVars(FormulaFactory factory, FreeIdentifier[] vars) {
		return makeMapletVars(factory, Arrays.asList(vars));
	}
	
	/**
	 * Returns whether the given predicate is in one of the following forms
	 * (which correspond to a typing predicate):
	 * <ul>
	 * <li>E ∈ Type</li>
	 * <li>E ⊆ Type</li>
	 * </ul>
	 * 
	 * @param pred
	 *            a typed predicate
	 * @return <code>true</code> if the given predicate is a typing predicate
	 */
	public static boolean isATypePred(Predicate pred) {
		switch (pred.getTag()) {
		case Formula.IN:
		case Formula.SUBSETEQ:
			return ((RelationalPredicate) pred).getRight().isATypeExpression();
		default:
			return false;
		}
	}
	
}
