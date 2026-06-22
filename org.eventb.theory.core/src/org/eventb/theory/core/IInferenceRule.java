/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core;

import org.eventb.theory.core.plugin.TheoryPlugin;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for an inference rule.
 * 
 * <p> An inference rule has a number of givens and an infer clause.
 * 
 * <p> An inference rule with givens <code>G1, G2</code> and infer <code>I</code> is a representation of the following
 * inference rule (Sequent Calculus):
 * <p> <code>|-G1    |-G2 <br>
 * ______________.<br>
 *        |-I
 * </code>
 * 
 * <p> This interface is not intended to be implemented by clients.
 * 
 * @see IRewriteRule
 * 
 * @author maamria
 *
 */
public interface IInferenceRule extends IRule{

	IInternalElementType<IInferenceRule> ELEMENT_TYPE = 
		RodinCore.getInternalElementType(TheoryPlugin.PLUGIN_ID + ".inferenceRule");
	
	/**
	 * Returns a handle the given clause of the supplied name.
	 * @param name of the clause
	 * @return the given clause
	 */
	IGiven getGiven(String name);
	
	/**
	 * Returns the given clauses of this inference rule.
	 * @return all given clauses
	 * @throws RodinDBException
	 */
	IGiven[] getGivens() throws RodinDBException;
	
	/**
	 * Returns a handle the infer clause of the supplied name.
	 * @param name of the clause
	 * @return the infer clause
	 */
	IInfer getInfer(String name);
	
	/**
	 * Returns the infer clauses of this inference rule.
	 * @return all infer clauses
	 * @throws RodinDBException
	 */
	IInfer[] getInfers() throws RodinDBException;
	
}
