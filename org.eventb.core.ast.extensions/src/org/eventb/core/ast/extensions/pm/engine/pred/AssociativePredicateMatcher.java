/*******************************************************************************
 * Copyright (c) 2011,2016 University of Southampton.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.ast.extensions.pm.engine.pred;

import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.ISpecialization;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.extensions.pm.assoc.ACPredicateProblem;
import org.eventb.core.ast.extensions.pm.assoc.IAssociativityProblem;
import org.eventb.core.ast.extensions.pm.engine.AbstractFormulaMatcher;
import org.eventb.core.ast.extensions.pm.engine.IFormulaMatcher;

/**
 * <p>
 * Implementation for matching associative predicates.
 * </p>
 *
 * @author maamria
 * @author htson: Re-implements using {@link IFormulaMatcher}.
 * @version 2.0
 * @since 1.0
 * @noextend This class is not intended to be sub-classed by clients.
 */
public class AssociativePredicateMatcher extends
		AbstractFormulaMatcher<AssociativePredicate> implements IFormulaMatcher {


	/*
	 * (non-Javadoc)
	 * 
	 * @see FormulaMatcher#gatherBindings(ISpecialization, Formula, Formula)
	 */
	@Override
	protected ISpecialization gatherBindings(ISpecialization specialization,
			AssociativePredicate formula, AssociativePredicate pattern) {
		// get the children
		Predicate[] formChildren = formula.getChildren();
		Predicate[] patternChildren = pattern.getChildren();
		IAssociativityProblem problem = new ACPredicateProblem(formula.getTag(), formChildren, patternChildren);
		return problem.solve(specialization);		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see FormulaMatcher#getFormula(Formula)
	 */
	@Override
	protected AssociativePredicate getFormula(Formula<?> formula) {
		return (AssociativePredicate) formula;
	}

}
