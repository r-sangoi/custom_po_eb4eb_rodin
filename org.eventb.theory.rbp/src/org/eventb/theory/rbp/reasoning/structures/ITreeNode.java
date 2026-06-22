/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.reasoning.structures;

import java.util.Collection;

/**
 * Common interface for trees holding elements of the same type.
 * 
 * @author maamria
 * 
 * @param E the type of the values stored in the tree nodes
 *
 */
interface ITreeNode<E> {
	
	/**
	 * Returns the value stored in this node.
	 * @return the value
	 */
	E getValue();
	
	/**
	 *	Adds the given values as children of this node.
	 *
	 * @param col the children, must not be <code>null</code>
	 */
	void setChildren(Collection<E> col);
	
	/**
	 * Returns the leaf values of this tree node.
	 * @return the leaf values or <code>null</code> if this node is a leaf itself
	 */
	Collection<E> getLeafValues();

}
