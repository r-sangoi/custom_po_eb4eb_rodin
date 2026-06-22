/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.basis;

import org.eventb.theory.core.ISCInferenceRule;
import org.eventb.theory.core.ISCMetavariable;
import org.eventb.theory.core.ISCProofRulesBlock;
import org.eventb.theory.core.ISCRewriteRule;
import org.eventb.theory.core.TheoryElement;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author maamria
 *
 */
public class SCProofRulesBlock extends TheoryElement implements ISCProofRulesBlock{

	public SCProofRulesBlock(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public ISCMetavariable getMetavariable(String name) {
		// TODO Auto-generated method stub
		return getInternalElement(ISCMetavariable.ELEMENT_TYPE, name);
	}

	@Override
	public ISCMetavariable[] getMetavariables() throws RodinDBException {
		// TODO Auto-generated method stub
		return getChildrenOfType(ISCMetavariable.ELEMENT_TYPE);
	}

	@Override
	public ISCRewriteRule getRewriteRule(String name) {
		// TODO Auto-generated method stub
		return getInternalElement(ISCRewriteRule.ELEMENT_TYPE, name);
	}

	@Override
	public ISCRewriteRule[] getRewriteRules() throws RodinDBException {
		// TODO Auto-generated method stub
		return getChildrenOfType(ISCRewriteRule.ELEMENT_TYPE);
	}

	@Override
	public ISCInferenceRule getInferenceRule(String name) {
		// TODO Auto-generated method stub
		return getInternalElement(ISCInferenceRule.ELEMENT_TYPE, name);
	}

	@Override
	public ISCInferenceRule[] getInferenceRules() throws RodinDBException {
		// TODO Auto-generated method stub
		return getChildrenOfType(ISCInferenceRule.ELEMENT_TYPE);
	}

	@Override
	public IInternalElementType<? extends IInternalElement> getElementType() {
		// TODO Auto-generated method stub
		return ELEMENT_TYPE;
	}

}
