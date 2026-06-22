/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core;

/**
 * 
 * A simple protocol for formula extensions sources filter.
 * 
 * @author maamria
 * 
 * @param <T>
 *            the type of the source
 */
public interface ITheoryFilter<T extends IFormulaExtensionsSource> {

	/**
	 * Returns whether the given theory satisfies the criteria of this
	 * filter.
	 * 
	 * @param theory
	 *            the theory
	 * @return whether <code>theory</code> satisfies the criteria of this
	 *         filter
	 */
	public boolean filter(T theory);
	
}
