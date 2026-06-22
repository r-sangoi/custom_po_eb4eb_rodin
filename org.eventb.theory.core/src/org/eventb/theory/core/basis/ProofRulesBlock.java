/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.basis;

import org.eventb.theory.core.IInferenceRule;
import org.eventb.theory.core.IMetavariable;
import org.eventb.theory.core.IProofRulesBlock;
import org.eventb.theory.core.IRewriteRule;
import org.eventb.theory.core.TheoryElement;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author maamria
 *
 */
public class ProofRulesBlock extends TheoryElement implements IProofRulesBlock{

	public ProofRulesBlock(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public IInternalElementType<? extends IInternalElement> getElementType() {
		return ELEMENT_TYPE;
	}

	@Override
	public IMetavariable getMetavariable(String name) {
		return getInternalElement(IMetavariable.ELEMENT_TYPE, name);
	}

	@Override
	public IMetavariable[] getMetavariables() throws RodinDBException {
		return getChildrenOfType(IMetavariable.ELEMENT_TYPE);
	}
	
	@Override
	public IRewriteRule getRewriteRule(String name) {
		return getInternalElement(IRewriteRule.ELEMENT_TYPE, name);
	}

	@Override
	public IRewriteRule[] getRewriteRules() throws RodinDBException {
		return getChildrenOfType(IRewriteRule.ELEMENT_TYPE);
	}

	@Override
	public IInferenceRule getInferenceRule(String name) {
		return getInternalElement(IInferenceRule.ELEMENT_TYPE, name);
	}

	@Override
	public IInferenceRule[] getInferenceRules() throws RodinDBException {
		return getChildrenOfType(IInferenceRule.ELEMENT_TYPE);
	}

}
