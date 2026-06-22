/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.rulebase.basis;

import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.theory.core.IGeneralRule;

/**
 * @author maamria
 *
 */
public interface IDeployedRule extends IGeneralRule{
	
	/**
	 * <p>Returns the description of this rule.</p>
	 * @return the description
	 */
	public String getDescription();
	
	/**
	 * <p>Returns the name of the rule.</p>
	 * @return the rule name
	 */
	public String getRuleName();
	
	/**
	 * <p>Returns the name of the parent theory.</p>
	 * @return parent theory name without extension
	 */
	public String getTheoryName();
	
	public String getProjectName();
	
	/**
	 * <p>Returns the tool tip associated with this rule.</p>
	 * @return the tool tip
	 */
	public String getToolTip();
	
	/**
	 * <p>Returns the type environment under which the sides of the rule are typecheck.</p>
	 * @return the rule type environment
	 */
	public ITypeEnvironment getTypeEnvironment();
	
	/**
	 * <p>Returns whether the rule can be used by the automatic prover.</p>
	 * @return whether the rule is automatic
	 */
	public boolean isAutomatic();
	
	/**
	 * <p>Returns whether the rule can be used interactively.</p>
	 * @return whether the rule is interactive
	 */
	public boolean isInteracive();
	
	/**
	 * <p>Returns whether this rule is sound i.e., its proof obligations have all been either discharged or reviewed.</p>
	 * @return whether the rule is sound
	 */
	public boolean isSound();

}
