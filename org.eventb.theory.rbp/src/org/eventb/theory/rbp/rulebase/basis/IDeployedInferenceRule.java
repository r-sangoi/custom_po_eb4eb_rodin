/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.rulebase.basis;

import java.util.List;

import org.eventb.theory.core.IReasoningTypeElement.ReasoningType;

/**
 * Common protocol for a deployed inference rule.
 * 
 * @author maamria
 *
 */
public interface IDeployedInferenceRule extends IDeployedRule{
	
	/**
	 * Return whether this inference rule is suitable for backward reasoning.
	 * @return whether this inference rule is suitable for backward reasoning
	 */
	boolean isSuitableForBackwardReasoning();
	
	/**
	 * Return whether this inference rule is suitable for forward reasoning.
	 * @return whether this inference rule is suitable for forward reasoning
	 */
	boolean isSuitableForForwardReasoning() ;
	
	/**
	 * Return whether this inference rule is suitable for backward and forward reasoning.
	 * @return whether this inference rule is suitable for backward and forward reasoning
	 */
	boolean isSuitableForAllReasoning();
	
	/**
	 * Returns the reasoning type of this rule.
	 * @return the reasoning type
	 */
	ReasoningType getReasoningType();
	
	/**
	 * Returns the non-hyp given clauses of this inference rule.
	 * @return non-hyp given clauses
	 */
	public List<IDeployedGiven> getGivens();
	
	/**
	 * Returns the hyp given clauses of this inference rule.
	 * @return hyp given clauses
	 */
	public List<IDeployedGiven> getHypGivens();
	
	/**
	 * Returns the infer clause of this inference rule.
	 * @return infer clause
	 */
	public IDeployedInfer getInfer();
	
}
