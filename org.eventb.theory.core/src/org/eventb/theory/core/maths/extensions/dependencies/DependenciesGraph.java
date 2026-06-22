/*******************************************************************************
 * Copyright (c) 2010, 2020 University of Southampton and others.
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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IEventBRoot;

/**
 * @author maamria
 * 
 */
public abstract class DependenciesGraph<E extends IEventBRoot> implements
		IDependenciesGraph<E> {

	private final Map<E, DependencyNode<E>> verticesMap = new HashMap<E, DependencyNode<E>>();

	/**
	 * Returns the elements which are reachable from the given elements.
	 * 
	 * @param element
	 *            the element
	 * @return all reachable elements
	 * @throws CoreException
	 *             if something goes wrong
	 */
	protected abstract Collection<E> getEdgesOut(E element)
			throws CoreException;

	@Override
	public void addElement(E added) throws CoreException {
		final DependencyNode<E> depNode = new DependencyNode<E>(added);
		verticesMap.put(added, depNode);
		for (E depend : getEdgesOut(added)) {
			if (!verticesMap.containsKey(depend)) {
				addElement(depend);
			}
			depNode.addConnectedNode(verticesMap.get(depend));
		}
	}

	@Override
	public void removeElement(E removed) {
		final DependencyNode<E> removedNode = verticesMap.remove(removed);
		if (removedNode == null) {
			return;
		}
		for (DependencyNode<E> depNode : verticesMap.values()) {
			depNode.removeConnectedNode(removedNode);
		}
	}

	@Override
	public List<E> getUpperSet(E element) {
		final List<E> list = new ArrayList<E>();
		final DependencyNode<E> correspNode = verticesMap.get(element);
		if (correspNode == null) {
			throw new IllegalArgumentException("Element not in graph: "
					+ element);
		}
		final List<DependencyNode<E>> upperSet = getUpperSet(correspNode);
		for (DependencyNode<E> node : upperSet) {
			list.add(node.element);
		}
		return list;
	}

	private List<DependencyNode<E>> getUpperSet(DependencyNode<E> node) {

		List<DependencyNode<E>> upper = new ArrayList<DependencyNode<E>>();
		final Deque<DependencyNode<E>> toProcess = new ArrayDeque<DependencyNode<E>>(
				node.connected);
		while (!toProcess.isEmpty()) {
			// FIXME cycles, although not allowed by UI
			final DependencyNode<E> n = toProcess.pop();
			if (upper.contains(n)) {
				continue;
			}
			if (upper.containsAll(n.connected)) {
				upper.add(n);
			} else {
				toProcess.addAll(n.connected);
				toProcess.add(n);
			}
		}
		return upper;
	}

	@Override
	public boolean contains(E element) {
		return verticesMap.containsKey(element);
	}

}
