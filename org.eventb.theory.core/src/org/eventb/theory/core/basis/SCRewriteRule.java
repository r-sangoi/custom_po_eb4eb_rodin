/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.basis;

import org.eventb.theory.core.ISCRewriteRule;
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
public class SCRewriteRule extends TheoryElement implements ISCRewriteRule {

	public SCRewriteRule(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public ISCRewriteRuleRightHandSide getRuleRHS(String name) {
		// TODO Auto-generated method stub
		return getInternalElement(ISCRewriteRuleRightHandSide.ELEMENT_TYPE, name);
	}

	@Override
	public ISCRewriteRuleRightHandSide[] getRuleRHSs() throws RodinDBException {
		// TODO Auto-generated method stub
		return getChildrenOfType(ISCRewriteRuleRightHandSide.ELEMENT_TYPE);
	}

	@Override
	public IInternalElementType<? extends IInternalElement> getElementType() {
		// TODO Auto-generated method stub
		return ELEMENT_TYPE;
	}

}
