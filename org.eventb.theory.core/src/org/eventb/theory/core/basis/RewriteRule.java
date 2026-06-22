/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.basis;

import org.eventb.theory.core.IRewriteRule;
import org.eventb.theory.core.IRewriteRuleRightHandSide;
import org.eventb.theory.core.TheoryElement;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author maamria
 *
 */
public class RewriteRule extends TheoryElement implements IRewriteRule{

	public RewriteRule(String name, IRodinElement parent) {
		super(name, parent);
	}

	
	@Override
	public IInternalElementType<? extends IInternalElement> getElementType() {
		return ELEMENT_TYPE;
	}

	@Override
	public IRewriteRuleRightHandSide getRuleRHS(String name) {
		return getInternalElement(IRewriteRuleRightHandSide.ELEMENT_TYPE, name);
	}

	@Override
	public IRewriteRuleRightHandSide[] getRuleRHSs() throws RodinDBException {
		return getChildrenOfType(IRewriteRuleRightHandSide.ELEMENT_TYPE);
	}

}
