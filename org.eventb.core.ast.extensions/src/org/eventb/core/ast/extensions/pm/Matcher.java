/*******************************************************************************
 * Copyright (c) 2011,2016 University of Southampton.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.ast.extensions.pm;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.BoolExpression;
import org.eventb.core.ast.BooleanType;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.ExtendedPredicate;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.ISpecialization;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.IntegerType;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.MultiplePredicate;
import org.eventb.core.ast.ParametricType;
import org.eventb.core.ast.PowerSetType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.ProductType;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.ast.UnaryPredicate;
import org.eventb.core.ast.extensions.pm.engine.IFormulaMatcher;
import org.eventb.core.ast.extensions.pm.engine.exp.AssociativeExpressionMatcher;
import org.eventb.core.ast.extensions.pm.engine.exp.AtomicExpressionMatcher;
import org.eventb.core.ast.extensions.pm.engine.exp.BinaryExpressionMatcher;
import org.eventb.core.ast.extensions.pm.engine.exp.BoolExpressionMatcher;
import org.eventb.core.ast.extensions.pm.engine.exp.BoundIdentDeclMatcher;
import org.eventb.core.ast.extensions.pm.engine.exp.BoundIdentifierMatcher;
import org.eventb.core.ast.extensions.pm.engine.exp.DefaultExtendedExpressionMatcher;
import org.eventb.core.ast.extensions.pm.engine.exp.IntegerLiteralMatcher;
import org.eventb.core.ast.extensions.pm.engine.exp.QuantifiedExpressionMatcher;
import org.eventb.core.ast.extensions.pm.engine.exp.SetExtensionMatcher;
import org.eventb.core.ast.extensions.pm.engine.exp.UnaryExpressionMatcher;
import org.eventb.core.ast.extensions.pm.engine.pred.AssociativePredicateMatcher;
import org.eventb.core.ast.extensions.pm.engine.pred.BinaryPredicateMatcher;
import org.eventb.core.ast.extensions.pm.engine.pred.DefaultExtendedPredicateMatcher;
import org.eventb.core.ast.extensions.pm.engine.pred.LiteralPredicateMatcher;
import org.eventb.core.ast.extensions.pm.engine.pred.MultiplePredicateMatcher;
import org.eventb.core.ast.extensions.pm.engine.pred.QuantifiedPredicateMatcher;
import org.eventb.core.ast.extensions.pm.engine.pred.RelationalPredicateMatcher;
import org.eventb.core.ast.extensions.pm.engine.pred.SimplePredicateMatcher;
import org.eventb.core.ast.extensions.pm.engine.pred.UnaryPredicateMatcher;

/**
 * An implementation of a matching engine.
 * <p>
 * This class is use to start matching processes.
 * <p>
 * 
 * 
 * @author maamria
 * @author htson: Re-implements using ISpecialization
 * @version 2.0
 * @see #match(Formula, Formula)
 * @see #match(ISpecialization, Formula, Formula)
 * @see IFormulaMatcher
 * @since 1.0
 * @noextend This class is not intended to be sub-classed by clients.
 */
public final class Matcher {

	// The map of matchers for different formula classes.
	private static final Map<Class<?>, IFormulaMatcher> MATCHERS = new HashMap<Class<?>, IFormulaMatcher>();

	// Load the matchers for each formula class.
	static {
		MATCHERS.put(AssociativeExpression.class,
				new AssociativeExpressionMatcher());
		MATCHERS.put(AtomicExpression.class, new AtomicExpressionMatcher());
		MATCHERS.put(BinaryExpression.class, new BinaryExpressionMatcher());
		MATCHERS.put(BoolExpression.class, new BoolExpressionMatcher());
		MATCHERS.put(BoundIdentDecl.class, new BoundIdentDeclMatcher());
		MATCHERS.put(BoundIdentifier.class, new BoundIdentifierMatcher());
		MATCHERS.put(ExtendedExpression.class,
				new DefaultExtendedExpressionMatcher());
		MATCHERS.put(IntegerLiteral.class, new IntegerLiteralMatcher());
		MATCHERS.put(QuantifiedExpression.class,
				new QuantifiedExpressionMatcher());
		MATCHERS.put(SetExtension.class, new SetExtensionMatcher());
		MATCHERS.put(UnaryExpression.class, new UnaryExpressionMatcher());
		MATCHERS.put(AssociativePredicate.class, new AssociativePredicateMatcher());
		MATCHERS.put(BinaryPredicate.class, new BinaryPredicateMatcher());
		MATCHERS.put(RelationalPredicate.class,
				new RelationalPredicateMatcher());
		MATCHERS.put(ExtendedPredicate.class,
				new DefaultExtendedPredicateMatcher());
		MATCHERS.put(LiteralPredicate.class, new LiteralPredicateMatcher());
		MATCHERS.put(MultiplePredicate.class, new MultiplePredicateMatcher());
		MATCHERS.put(QuantifiedPredicate.class,
				new QuantifiedPredicateMatcher());
		MATCHERS.put(RelationalPredicate.class,
				new RelationalPredicateMatcher());
		MATCHERS.put(SimplePredicate.class, new SimplePredicateMatcher());
		MATCHERS.put(UnaryPredicate.class, new UnaryPredicateMatcher());
	}

	/**
	 * Matches the formula against the pattern and return the resulting
	 * specialization.
	 * 
	 * @param formula
	 *            The input formula
	 * @param pattern
	 *            The input pattern
	 * @return the resulting specialization if successful. Return
	 *         <code>null</code> otherwise.
	 * @throws IllegalArgumentException
	 *             if one of the argument is <code>null</code>, or the formula
	 *             factories of the inputs are different.
	 * @see #match(ISpecialization, Formula, Formula)
	 */
	public static ISpecialization match(Formula<?> formula, Formula<?> pattern) {
		// Exceptions for preconditions.
		if (formula == null)
			throw new IllegalArgumentException("Input formula cannot be null");
		if (pattern == null)
			throw new IllegalArgumentException("Input pattern cannot be null");

		if (!formula.getFactory().equals(pattern.getFactory()))
			throw new IllegalArgumentException("Formula " + formula
					+ " and pattern " + pattern
					+ " must have the same formula factory");

		FormulaFactory factory = formula.getFactory();
		ISpecialization specialization = factory.makeSpecialization();
		return match(specialization, formula, pattern);
	}

	/**
	 * Matches the formula against the pattern and appending the result to the
	 * input specialization. Any resulting match must be compatible with the
	 * initial specialization.
	 * 
	 * @param specialization
	 *            the initial specialization.
	 * @param formula
	 *            the input formula.
	 * @param pattern
	 *            the input pattern.
	 * @return The resulting specialization if the matching is successful.
	 *         Return <code>null</code> otherwise.
	 * @throws IllegalArgumentException
	 *             if one of the argument is <code>null</code>, or the formula
	 *             factories of the inputs are different.
	 */
	public static ISpecialization match(ISpecialization specialization,
			Formula<?> formula, Formula<?> pattern) {
		// Exceptions for preconditions.
		if (specialization == null)
			throw new IllegalArgumentException(
					"Initial specialization cannot be null");
		if (formula == null)
			throw new IllegalArgumentException("Input formula cannot be null");
		if (pattern == null)
			throw new IllegalArgumentException("Input pattern cannot be null");

		if (!formula.getFactory().equals(specialization.getFactory()))
			throw new IllegalArgumentException(
					"Formula and specialization must have the same formula factory");
		if (!formula.getFactory().equals(pattern.getFactory()))
			throw new IllegalArgumentException(
					"Formula and pattern must have the same formula factory");
		if (!specialization.getFactory().equals(pattern.getFactory()))
			throw new IllegalArgumentException(
					"Specialization and pattern must have the same formula factory");

		// if they are not having the same tag, do not bother
		if (formula.getTag() != (pattern.getTag())) {
			return null;
		}

		// Get the actual formula matcher for the formula class to carry out the
		// matching process.
		IFormulaMatcher formMatcher = MATCHERS.get(formula.getClass());
		if (formMatcher == null) {
			throw new UnsupportedOperationException("Cannot find matcher for "
					+ formula.getClass());
		}
		return (formMatcher.match(specialization, formula, pattern));
	}

	/**
	 * Utility method to unify the formula type and the pattern type given some
	 * initial specialization. The resulting matching information is returned.
	 * 
	 * @param specialization
	 *            the input specialization.
	 * @param fType
	 *            the input formula type.
	 * @param pType
	 *            the input pattern type.
	 * @return The resulting specialization if the types are unified-able given
	 *         the initial specialization. Return <code>null</code> otherwise.
	 */
	public static ISpecialization unifyTypes(ISpecialization specialization,
			Type fType, Type pType) {
		if (pType instanceof IntegerType) {
			if (fType instanceof IntegerType)
				return specialization;
			else
				return null;
		}
		if (pType instanceof BooleanType) {
			if (fType instanceof BooleanType)
				return specialization;
			else
				return null;
		}
		if (pType instanceof GivenType) {
			// Try to insert the type instantiation into the specialization.
			return insert(specialization, (GivenType) pType, fType);
		}
		if (pType instanceof PowerSetType) {
			if (fType instanceof PowerSetType) {
				Type pBaseType = pType.getBaseType();
				Type fBaseType = fType.getBaseType();
				return unifyTypes(specialization, fBaseType, pBaseType);
			}
			return null;
		}
		if (pType instanceof ProductType) {
			if (fType instanceof ProductType) {
				Type pLeft = ((ProductType) pType).getLeft();
				Type fLeft = ((ProductType) fType).getLeft();
				specialization = unifyTypes(specialization, fLeft, pLeft);
				if (specialization == null)
					return null;

				Type pRight = ((ProductType) pType).getRight();
				Type fRight = ((ProductType) fType).getRight();
				return unifyTypes(specialization, fRight, pRight);
			}
			return null;
		} else if (pType instanceof ParametricType) {
			if (fType instanceof ParametricType) {
				ParametricType pParametricType = (ParametricType) pType;
				ParametricType fParametricType = (ParametricType) fType;
				if (!pParametricType.getExprExtension().equals(
						fParametricType.getExprExtension())) {
					return null;
				}
				Type[] patTypes = pParametricType.getTypeParameters();
				Type[] expTypes = fParametricType.getTypeParameters();
				for (int i = 0; i < patTypes.length; i++) {
					specialization = unifyTypes(specialization, expTypes[i],
							patTypes[i]);
					if (specialization == null) {
						return null;
					}
				}
				return specialization;
			}
		}
		return null;
	}

	/**
	 * Utility method to insert a type instantiation into the input
	 * specialization.
	 * 
	 * @param specialization
	 *            the input specialization.
	 * @param type
	 *            the given type to be instantiated.
	 * @param value
	 *            the instantiating value type.
	 * @return The resulting specialization if the type substitution is
	 *         compatible with the initial specialization. Return
	 *         <code>null</code> otherwise.
	 * @see #unifyTypes(ISpecialization, Type, Type)
	 */
	protected static ISpecialization insert(ISpecialization specialization,
			GivenType type, Type value) {
		ISpecialization clone = specialization.clone();
		try {
			clone.put(type, value);
		} catch (IllegalArgumentException e) {
			return null;
		}
		return clone;
	}

	/**
	 * Utility method to insert an instantiation into the input specialization.
	 * 
	 * @param specialization
	 *            the initial specialization.
	 * @param ident
	 *            a typed identifier to be instantiated
	 * @param value
	 *            the instantiating value.
	 * @return The resulting specialization if the instantiation is compatible
	 *         with the initial specialization. Return <code>null</code>
	 *         otherwise.
	 * @see #unifyTypes(ISpecialization, Type, Type)
	 * @precondition the type of the input identifier and its instantiating
	 *               expression have been unified and the result is in the input
	 *               specialization.
	 */
	public static ISpecialization insert(ISpecialization specialization,
			FreeIdentifier ident, Expression value) {
		ISpecialization clone = specialization.clone();
		try {
			clone.put(ident, value);
		} catch (IllegalArgumentException e) {
			return null;
		}
		return clone;
	}

	/**
	 * Utility method to match a list of formulae and a list patterns, appending
	 * the result to the input specialization. Any resulting match must be
	 * compatible with the initial specialization. An initial list of matched
	 * formulae is passed as an argument of the method. Additional matched
	 * formulae will be added to this list.
	 * 
	 * @param specialization
	 *            the initial specialization object.
	 * @param matched
	 *            the list of matched formulae.
	 * @param formulae
	 *            the input list of formulae.
	 * @param patterns
	 *            the input list of patterns.
	 * @return the resulting specialization if the matching is successful.
	 *         Otherwise, return <code>null</code>.
	 * @throws IllegalArgumentException
	 *             if one of the argument is <code>null</code>, or the formula
	 *             factories of the inputs are different.
	 */
	public static ISpecialization match(final ISpecialization specialization,
			Collection<Predicate> matched, final List<Predicate> formulae,
			final List<Predicate> patterns) {
		return match(specialization, matched, formulae,
				patterns.toArray(new Predicate[patterns.size()]), 0);
	}

	/**
	 * Utility recursive method to match the pattern array starting from a
	 * specific index of an pattern array and a list of formulae, appending the
	 * result to the input specialization. Any resulting match must be
	 * compatible with the initial specialization. An initial list of matched
	 * formulae is passed as an argument of the method. Additional matched
	 * formula will be added to this list.
	 * 
	 * @param specialization
	 *            the input specialization object.
	 * @param matched
	 *            the initial collection of matched formulae.
	 * @param formulae
	 *            the formulae.
	 * @param patterns
	 *            the array of patterns.
	 * @param index
	 *            the index of the pattern to match. If the index is the same as
	 *            the length of the patterns array then the recursion
	 *            terminates.
	 * @return the resulting specialization object if matching successful.
	 *         Return <code>null</code> otherwise.
	 * @throws IllegalArgumentException
	 *             if one of the argument is <code>null</code>, or the formula
	 *             factories of the inputs are different.
	 */
	private static ISpecialization match(ISpecialization specialization,
			Collection<Predicate> matched, List<Predicate> formulae,
			Predicate[] patterns, int index) {
		if (patterns.length == index)
			// Nothing to match, then return the specialization
			return specialization;

		// Get the first pattern
		Predicate pattern = patterns[index];
		for (Predicate formula : formulae) { // For each formula
			// Clone the input specialization
			ISpecialization clone = specialization.clone();
			// Match the pattern with the formula
			clone = Matcher.match(clone, formula, pattern);
			// If does not match then continue (to the next available formula)
			if (clone == null)
				continue;

			// If match then add to the list of matched and try recursively to
			// match the rest of the pattern list.
			matched.add(formula);
			clone = match(clone, matched, formulae, patterns, index + 1);
			if (clone != null)
				return clone;
		}
		return null;
	}

}
