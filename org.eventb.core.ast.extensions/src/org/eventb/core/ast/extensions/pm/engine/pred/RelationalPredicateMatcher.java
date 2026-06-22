/*******************************************************************************
 * Copyright (c) 2011,2016 University of Southampton.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.ast.extensions.pm.engine.pred;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ISpecialization;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.extensions.pm.Matcher;
import org.eventb.core.ast.extensions.pm.engine.AbstractFormulaMatcher;
import org.eventb.core.ast.extensions.pm.engine.IFormulaMatcher;

/**
 * <p>
 * Implementation for matching relational predicates.
 * </p>
 *
 * @author maamria
 * @author htson: Re-implements using {@link IFormulaMatcher}.
 * @version 2.0
 * @since 1.0
 * @noextend This class is not intended to be sub-classed by clients.
 */
public class RelationalPredicateMatcher extends
		AbstractFormulaMatcher<RelationalPredicate> implements IFormulaMatcher {

	/*
	 * (non-Javadoc)
	 * 
	 * @see FormulaMatcher#gatherBindings(ISpecialization, Formula, Formula)
	 */
	@Override
	protected ISpecialization gatherBindings(ISpecialization specialization,
			RelationalPredicate formula, RelationalPredicate pattern) {
		Expression pLeft = pattern.getLeft();
		Expression fLeft = formula.getLeft();
		specialization = Matcher.unifyTypes(specialization, fLeft.getType(),
				pLeft.getType());
		if (specialization == null) {
			return null;
		}
		if (pLeft instanceof FreeIdentifier) {
			specialization = Matcher.insert(specialization,
					(FreeIdentifier) pLeft, fLeft);
			if (specialization == null) {
				return null;
			}
		} else {
			specialization = Matcher.match(specialization, fLeft, pLeft);
			if (specialization == null) {
				return null;
			}
		}
		Expression pRight = pattern.getRight();
		Expression fRight = formula.getRight();
		specialization = Matcher.unifyTypes(specialization, fRight.getType(),
				pRight.getType());
		if (specialization == null) {
			return null;
		}
		if (pRight instanceof FreeIdentifier) {
			return Matcher.insert(specialization, (FreeIdentifier) pRight,
					fRight);
		}
		return Matcher.match(specialization, fRight, pRight);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see FormulaMatcher#getFormula(Formula)
	 */
	@Override
	protected RelationalPredicate getFormula(Formula<?> formula) {
		return (RelationalPredicate) formula;
	}

}
