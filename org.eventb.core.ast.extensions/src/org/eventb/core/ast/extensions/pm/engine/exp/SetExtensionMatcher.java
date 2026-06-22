/*******************************************************************************
 * Copyright (c) 2011,2016 University of Southampton.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.ast.extensions.pm.engine.exp;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ISpecialization;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.extensions.pm.Matcher;
import org.eventb.core.ast.extensions.pm.engine.AbstractFormulaMatcher;
import org.eventb.core.ast.extensions.pm.engine.IFormulaMatcher;

/**
 * <p>
 * Implementation for matching set extensions.
 * </p>
 *
 * @author maamria
 * @author htson Re-implemented based on {@link IFormulaMatcher} interface.
 * @version 2.0
 * @since 1.0
 */
public class SetExtensionMatcher extends AbstractFormulaMatcher<SetExtension>
		implements IFormulaMatcher {

	/*
	 * (non-Javadoc)
	 * 
	 * @see FormulaMatcher#gatherBindings(ISpecialization, Formula, Formula)
	 */
	@Override
	protected ISpecialization gatherBindings(ISpecialization specialization,
			SetExtension formula, SetExtension pattern) {
		Expression[] patternMembers = pattern.getMembers();
		Expression[] formulaMembers = formula.getMembers();

		if (patternMembers.length != formulaMembers.length) {
			return null;
		}
		for (int i = 0; i != patternMembers.length; i++) {
			Expression formulaMem = formulaMembers[i];
			Expression patternMem = patternMembers[i];
			// DO NOT NEED to unify the type for this since the type must be
			// unified on the input formula and pattern. 
			if (patternMem instanceof FreeIdentifier) {
				specialization = Matcher.insert(specialization,
						(FreeIdentifier) patternMem, formulaMem);
				if (specialization == null) {
					return null;
				}
			} else {
				specialization = Matcher.match(specialization, formulaMem,
						patternMem);
				if (specialization == null) {
					return null;
				}
			}
		}
		return specialization;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see FormulaMatcher#getFormula(Formula)
	 */
	@Override
	protected SetExtension getFormula(Formula<?> formula) {
		return (SetExtension) formula;
	}

}
