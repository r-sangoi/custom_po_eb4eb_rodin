/*******************************************************************************
 * Copyright (c) 2010, 2020 University of Southampton and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.basis;

import static org.eventb.internal.core.Messages.database_SCPredicateParseFailure;
import static org.eventb.internal.core.Messages.database_SCPredicateTCFailure;
import static org.eventb.theory.core.util.CoreUtilities.newCoreException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.theory.core.ISCRewriteRuleRightHandSide;
import org.eventb.theory.core.TheoryElement;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author maamria
 * 
 */
@SuppressWarnings("restriction")
public class SCRewriteRuleRightHandSide extends TheoryElement implements
		ISCRewriteRuleRightHandSide {

	public SCRewriteRuleRightHandSide(String name, IRodinElement parent) {
		super(name, parent);
	}

	private Predicate getPredicate(FormulaFactory factory)
			throws CoreException {
		String contents = getPredicateString();
		final IRodinElement source;
		if (hasAttribute(EventBAttributes.SOURCE_ATTRIBUTE)) {
			source = getAttributeValue(EventBAttributes.SOURCE_ATTRIBUTE);
		} else {
			source = null;
		}
		IParseResult parserResult = factory
				.parsePredicatePattern(contents, source);
		if (parserResult.getProblems().size() != 0) {
			throw newCoreException(database_SCPredicateParseFailure, this);
		}
		Predicate result = parserResult.getParsedPredicate();
		return result;
	}

	@Override
	public Predicate getPredicate(ITypeEnvironment typenv) throws CoreException {
		Predicate result = getPredicate(typenv.getFormulaFactory());
		ITypeCheckResult tcResult = result.typeCheck(typenv);
		if (!tcResult.isSuccess()) {
			throw newCoreException(database_SCPredicateTCFailure, this);
		}
		assert result.isTypeChecked();
		return result;
	}

	@Override
	public void setPredicate(Predicate predicate, IProgressMonitor monitor)
			throws RodinDBException {
		setPredicateString(predicate.toStringWithTypes(), monitor);
	}

	@Override
	public IInternalElementType<? extends IInternalElement> getElementType() {
		// TODO Auto-generated method stub
		return ELEMENT_TYPE;
	}

}
