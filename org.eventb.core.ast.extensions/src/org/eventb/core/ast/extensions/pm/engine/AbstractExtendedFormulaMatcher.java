/*******************************************************************************
 * Copyright (c) 2016 University of Southampton.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.ast.extensions.pm.engine;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ISpecialization;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.PredicateVariable;
import org.eventb.core.ast.extension.IExtendedFormula;
import org.eventb.core.ast.extensions.pm.Matcher;
import org.eventb.core.ast.extensions.pm.engine.exp.DefaultExtendedExpressionMatcher;
import org.eventb.core.ast.extensions.pm.engine.pred.DefaultExtendedPredicateMatcher;

/**
 * <p>
 * Common abstract implementation for matching extended formula.
 * </p>
 *
 * @author htson
 * @version 1.0
 * @see DefaultExtendedExpressionMatcher
 * @see DefaultExtendedPredicateMatcher
 * @since 4.0
 * @noextend This class is not intended to be sub-classed by clients.
 */
public abstract class AbstractExtendedFormulaMatcher<T extends Formula<?> & IExtendedFormula>
		extends AbstractFormulaMatcher<T> implements IFormulaMatcher {

	/*
	 * (non-Javadoc)
	 * 
	 * @see FormulaMatcher#gatherBindings(ISpecialization, Formula, Formula)
	 */
	@Override
	protected ISpecialization gatherBindings(ISpecialization specialization,
			T formula, T pattern) {
		// The formula and the pattern must have the same number of child expressions.
		Expression[] formulaExpressions = formula.getChildExpressions();
		Expression[] patternExpressions =  pattern.getChildExpressions();
		if (formulaExpressions.length != patternExpressions.length){
			return null;
		}

		// The formula and the pattern must have the same number of child predicates.
		Predicate[] formulaPredicates = formula.getChildPredicates();
		Predicate[] patternPredicates = pattern.getChildPredicates();
		if (formulaPredicates.length != patternPredicates.length){
			return null;
		}

		// Matching each pattern child expression with the corresponding formula child expression.
		for (int i = 0; i < formulaExpressions.length; i++) {
			specialization = Matcher.unifyTypes(specialization,
					formulaExpressions[i].getType(),
					patternExpressions[i].getType());
			if (specialization == null) {
				return null;
			}
			if (patternExpressions[i] instanceof FreeIdentifier){
				specialization = Matcher.insert(specialization,
						(FreeIdentifier) patternExpressions[i],
						formulaExpressions[i]);
				if (specialization == null) {
					return null;
				}
			} else {
				specialization = Matcher.match(specialization,
						formulaExpressions[i], patternExpressions[i]);
				if (specialization == null) {
					return null;
				}
			}
		}
		
		
		// Matching each pattern child predicate with the corresponding formula child predicate.
		for (int i = 0; i < formulaPredicates.length; i++) {
			if (patternPredicates[i] instanceof PredicateVariable) {
				throw new UnsupportedOperationException(
						"Predicate variable is unsupported");
			} else {
				specialization = Matcher.match(specialization,
						formulaPredicates[i], patternPredicates[i]);
				if (specialization == null) {
					return null;
				}
			}
		}

		// Return the resulting specialization
		return specialization;
	}

}
