/*******************************************************************************
 * Copyright (c) 2010, 2013 University of Southampton and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University of Southampton - initial API and implementation
 *     Systerel -  refactored after imports only concern deployed theories
 *******************************************************************************/
package org.eventb.theory.core.maths.extensions.dependencies;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IEventBRoot;

/**
 * Common protocol for an Event-B roots dependencies graph.
 * 
 * @author maamria
 * 
 * @param E
 *            the type of the Event-B root
 * 
 */
public interface IDependenciesGraph<E extends IEventBRoot> {

	/**
	 * Adds the given element to this graph.
	 * 
	 * @param element
	 *            the element to add
	 * @return whether the element was added correctly
	 * @throws CoreException
	 *             if something goes wrong
	 */
	public void addElement(E element) throws CoreException;

	/**
	 * Removes the given element from this graph.
	 * 
	 * @param element
	 *            the element to remove
	 * @return whether the element was removed correctly
	 */
	public void removeElement(E element);

	/**
	 * Returns the elements that are reachable from this element.
	 * <p>
	 * Returned element list contains no duplicate. Elements are sorted least
	 * dependency first.
	 * 
	 * @param element
	 *            the element
	 * @return reachable elements
	 */
	public List<E> getUpperSet(E element);

	/**
	 * Returns whether the graph contains a node for the given element.
	 * 
	 * @param element
	 *            the element for which to check whether a node exists
	 * @return whether a node for the given element exists in the graph
	 */
	public boolean contains(E element);
	
}
