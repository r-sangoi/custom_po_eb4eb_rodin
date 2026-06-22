/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.rulebase.basis;

import org.eventb.core.ast.Predicate;

/**
 * Common protocol for an infer clause.
 * 
 * @author maamria
 *
 */
public interface IDeployedInfer {

	/**
	 * Returns the infer clause.
	 * @return the infer clause
	 */
	public Predicate getInferClause();
	
}
