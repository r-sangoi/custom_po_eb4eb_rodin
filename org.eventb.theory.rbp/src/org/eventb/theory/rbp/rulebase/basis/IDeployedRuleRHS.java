/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.rulebase.basis;

import org.eventb.core.ast.Formula;
import org.eventb.core.ast.Predicate;

/**
 * <p>Common protocol for a rule right hand side in a rewrite rule in a theory file.</p>
 * <p>Objects of this type correspond directly to certain elements in deployed theory files.</p>
 * <p>Objects of this type are immutable.</p>
 * @author maamria
 *
 */
public interface IDeployedRuleRHS {

	/**
	 * <p>Returns the condition of this right hand side.</p>
	 * @return the condition
	 */
	public Predicate getCondition();
	
	/**
	 * <p>Returns the right hand side formula.</p>
	 * @return the formula
	 */
	public Formula<?> getRHSFormula();
	
	/**
	 * <p>Returns the name of this rhs.</p>
	 * @return the name
	 */
	public String getRHSName();
}
