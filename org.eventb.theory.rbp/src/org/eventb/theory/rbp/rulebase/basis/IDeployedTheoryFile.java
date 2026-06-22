/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.rulebase.basis;

import java.util.List;

import org.eventb.core.ast.ITypeEnvironment;

/**
 * @author maamria
 *
 */
public interface IDeployedTheoryFile {
	/**
	 * <p>Returns a list of rewrite rules.</p>
	 * @return list of rules
	 */
	public List<IDeployedRewriteRule> getRewriteRules();
	/**
	 * <p>Returns the type environment of this theory.</p>
	 * @return the type environment
	 */
	public ITypeEnvironment getGloablTypeEnvironment();
	
	/**
	 * Returns the list of inference rules in this theory.
	 * @return inference rules
	 */
	public List<IDeployedInferenceRule> getInferenceRules();
	
	public List<IDeployedTheorem> getTheorems();
	
	/**
	 * <p>Returns the theory name.</p>
	 * @return the name with extension
	 */
	public String getTheoryName();
	
	/**
	 * Returns whether the theory has any rules in it.
	 * @return whether the theory has any rules in it
	 */
	public boolean isEmpty();
}
