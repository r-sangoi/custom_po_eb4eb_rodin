/*******************************************************************************
 * Copyright (c) 2006, 2020 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - Mathematical Language V2
 *     Systerel - added origin of predicates in proof
 *******************************************************************************/
package org.eventb.theory.core.basis;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.basis.SCPredicateElement;
import org.eventb.theory.core.ISCPredicatePatternElement;
import org.eventb.theory.core.sc.Messages;
import org.eventb.theory.core.util.CoreUtilities;
import org.rodinp.core.IRodinElement;

/**
 * Common implementation of Event-B SC elements that contain a predicate
 * pattern, as an extension of the Rodin database.
 * <p>
 * This class is intended to be subclassed by clients that want to extend this
 * internal element type.
 * </p>
 * <p>
 * This class should not be used in any other way than subclassing it in a
 * database extension. In particular, clients should not use it, but rather use
 * its associated interface <code>ISCPredicatePatternElement</code>.
 * </p>
 * <p>
 * Adapted from {@link SCPredicateElement}.
 * </p>
 *
 * @author htson
 * @version
 * @see
 * @since 4.0
 */
public abstract class SCPredicatePatternElement extends SCPredicateElement implements
		ISCPredicatePatternElement {

	/**
	 * Constructor used by the Rodin database.
	 */
	public SCPredicatePatternElement(String name, IRodinElement parent) {
		super(name, parent);
	}

	/**
	 * @since 4.0
	 */
	@Override
	public Predicate getPredicate(ITypeEnvironment typenv) throws CoreException {
		final String contents = getPredicateString();
		final FormulaFactory factory = typenv.getFormulaFactory();
		final IRodinElement source = getSourceIfExists();
		final IParseResult pResult = factory.parsePredicatePattern(contents,
				source);
		if (pResult.hasProblem()) {
			throw CoreUtilities.newCoreException(
					Messages.database_SCPredicatePatternParseFailure, this);
		}
		final Predicate result = pResult.getParsedPredicate();
		final ITypeCheckResult tcResult = result.typeCheck(typenv);
		if (!tcResult.isSuccess()) {
			throw CoreUtilities.newCoreException(
					Messages.database_SCPredicatePatternTCFailure, this);
		}
		assert result.isTypeChecked();
		return result;
	}

}

