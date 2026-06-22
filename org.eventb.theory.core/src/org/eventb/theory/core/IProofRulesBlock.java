/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core;

import org.eventb.core.ICommentedElement;
import org.eventb.core.ILabeledElement;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for an internal element that can be used to group together a collection of proof rules.
 * <p> A proof block may define metavariable, rewrite rules and inference rules.
 * 
 * @see IMetavariable
 * @see IRewriteRule
 * @see IInferenceRule
 * 
 * @author maamria
 *
 */
public interface IProofRulesBlock extends ILabeledElement, ICommentedElement{
	
	public IInternalElementType<IProofRulesBlock> ELEMENT_TYPE = 
		RodinCore.getInternalElementType(TheoryPlugin.PLUGIN_ID + ".proofRulesBlock");

	/**
	 * Returns a handle to the metavariable with the given name.
	 * @param name of the metavariable
	 * @return the metavariable
	 */
	IMetavariable getMetavariable(String name);
	
	/**
	 * Returns all metavariables of this block.
	 * @return all metavariables
	 * @throws RodinDBException
	 */
	IMetavariable[] getMetavariables() throws RodinDBException;
	
	/**
	 * Returns a handle to the rewrite rule with the given name.
	 * @param name of the rule
	 * @return the rule
	 */
	IRewriteRule getRewriteRule(String name);
	
	/**
	 * Returns all rewrites of this block.
	 * @return all rewrites
	 * @throws RodinDBException
	 */
	IRewriteRule[] getRewriteRules() throws RodinDBException;
	
	/**
	 * Returns a handle to the inference rule with the given name.
	 * @param name of the rule
	 * @return the rule
	 */
	IInferenceRule getInferenceRule(String name);
	
	/**
	 * Returns all inferences of this block.
	 * @return all inferences
	 * @throws RodinDBException
	 */
	IInferenceRule[] getInferenceRules() throws RodinDBException;
	
}
