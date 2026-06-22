/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.rulebase.basis;

import java.util.List;

import org.eventb.core.ast.Formula;

/**
 * <p>Common protocol for a deployed rewrite rule in a deployed theory file.</p>
 * <p>Objects of this type correspond directly to an element in a deployed theory file.</p>
 * <p>Objects of this type should be immutable (up to the containing theory).</p>
 * @author maamria
 *
 */
public interface IDeployedRewriteRule extends IDeployedRule{

	/**
	 * <p>Returns the left hand side of the rule.</p>
	 * @return the lhs
	 */
	public Formula<?> getLeftHandSide();
	
	/**
	 * <p>Returns a list of this rule right hand sides.</p>
	 * @return all rhs's
	 */
	public List<IDeployedRuleRHS> getRightHandSides();
	
	/**
	 * <p>Returns whether the right hand sides of the rule are complete.</p>
	 * @return whether the rule is complete
	 */
	public boolean isComplete();
	
	/**
	 * <p>Returns whether this rule is conditional or unconditional.</p>
	 * @return whether the rule is conditional
	 */
	public boolean isConditional();
	
	/**
	 * <p>Returns whether the left hand side of this rule is an expression.</p>
	 * <p> Obviously, this method is redundant since that can be found out by checking the rule lhs. Nonetheless, this is provided as a facility.</p>
	 * @return whether lhs of rule is an expression
	 */
	public boolean isExpression();
	
	/**
	 * Returns whether this rule is definitional.
	 * @return whether this rule is definitional
	 */
	public boolean  isDefinitional();
}
