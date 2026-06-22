/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.ast.extensions.maths;

import org.eventb.core.ast.extension.IFormulaExtension;

/**
 * Common protocol for an operator extension. Additional methods are added for convenience when
 * checking the operator properties.
 * 
 * <p> Note that despite the semantic nature of associativity and commutativity, they will be used to facilitate
 * pattern matching. 
 * 
 * <p> This interface can be implemented by clients.
 * <p> This interface can be extended by clients.
 * 
 * @since 1.0
 * 
 * @author maamria
 *
 */

public interface IOperatorExtension extends IFormulaExtension{
	
	/**
	 * Returns whether this operator is associative.
	 * @return operator associativity
	 */
	public boolean isAssociative();
	
	/**
	 * Returns whether this operator is commutative.
	 * @return operator commutativity
	 */
	public boolean isCommutative();
	
	/**
	 * Returns the definition of this operator. 
	 * 
	 * This could be <code>null</code> if the extension is temporary (i.e., from a SC theory).
	 * @return the definition of the operator
	 */
	public Definition getDefinition();
	
	/**
	 * Sets the definition of this operator.
	 * 
	 * @throws IllegalStateException if a definition is already set.
	 * @param definition the definition to assign
	 */
	public void setDefinition(Definition definition);
}
