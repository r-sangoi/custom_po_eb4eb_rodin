/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core;

import org.eventb.core.ILabeledElement;
import org.eventb.core.ITraceableElement;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for statically checked proof rules block.
 * 
 * <p> This interface is not intended to be implemented by clients.
 * 
 * @see IProofRulesBlock
 * 
 * @author maamria
 *
 */
public interface ISCProofRulesBlock extends ILabeledElement, ITraceableElement {

	public IInternalElementType<ISCProofRulesBlock> ELEMENT_TYPE = 
		RodinCore.getInternalElementType(TheoryPlugin.PLUGIN_ID + ".scProofRulesBlock");

	
	/**
	 * Returns a handle to the metavariable with the given name.
	 * @param name of the metavariable
	 * @return the metavariable
	 */
	ISCMetavariable getMetavariable(String name);
	
	/**
	 * Returns all metavariables of this block.
	 * @return all metavariables
	 * @throws RodinDBException
	 */
	ISCMetavariable[] getMetavariables() throws RodinDBException;
	
	/**
	 * Returns a handle to the rewrite rule with the given name.
	 * @param name of the rule
	 * @return the rule
	 */
	ISCRewriteRule getRewriteRule(String name);
	
	/**
	 * Returns all rewrites of this block.
	 * @return all rewrites
	 * @throws RodinDBException
	 */
	ISCRewriteRule[] getRewriteRules() throws RodinDBException;
	
	/**
	 * Returns a handle to the inference rule with the given name.
	 * @param name of the rule
	 * @return the rule
	 */
	ISCInferenceRule getInferenceRule(String name);
	
	/**
	 * Returns all inferences of this block.
	 * @return all inferences
	 * @throws RodinDBException
	 */
	ISCInferenceRule[] getInferenceRules() throws RodinDBException;
}
