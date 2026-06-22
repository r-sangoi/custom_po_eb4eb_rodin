/*******************************************************************************
 * Copyright (c) 2020 CentraleSupélec.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     CentraleSupélec - initial implementation
 *******************************************************************************/
package org.eventb.theory.internal.core.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eventb.core.IEventBRoot;
import org.eventb.core.ast.extension.IFormulaExtension;

/**
 * Cache associating a set of formula extensions to an Event-B root.
 *
 * This cache ignores the snapshot attribute of the underlying RodinFile: it
 * will associate the same set of formula extensions, whether the file is a
 * snapshot or not.
 *
 * All methods are thread-safe.
 *
 * @author Guillaume Verdier
 */
public class FormulaExtensionCache {

	private final Map<IEventBRoot, Set<IFormulaExtension>> cache = Collections.synchronizedMap(new HashMap<IEventBRoot, Set<IFormulaExtension>>());

	/**
	 * Checks if there is a set of formula extensions cached for a given root
	 * element.
	 *
	 * This method is thread-safe.
	 *
	 * @param root the root element to look up
	 * @return {@code true} if there is a cached set of formula extensions for the
	 *         parameter
	 */
	public boolean containsKey(IEventBRoot root) {
		IEventBRoot snapshotRoot = (IEventBRoot) root.getSnapshot();
		return cache.containsKey(snapshotRoot);
	}

	/**
	 * Gets the set of formula extensions cached for the given root element, or
	 * {@code null} if there are none.
	 *
	 * This method is thread-safe.
	 *
	 * @param root the root element to look up
	 * @return the cached set of formula extensions or {@code null} if there is none
	 */
	public Set<IFormulaExtension> get(IEventBRoot root) {
		IEventBRoot snapshotRoot = (IEventBRoot) root.getSnapshot();
		return cache.get(snapshotRoot);
	}

	/**
	 * Sets the set of formula extensions associated to the given root element.
	 *
	 * This method is thread-safe.
	 *
	 * @param root       the root element associated with the set of formula
	 *                   extensions
	 * @param extensions the set of extension to put in the cache
	 */
	public void put(IEventBRoot root, Set<IFormulaExtension> extensions) {
		IEventBRoot snapshotRoot = (IEventBRoot) root.getSnapshot();
		cache.put(snapshotRoot, extensions);
	}

	/**
	 * Removes the set of formula extensions cached for the given root element.
	 *
	 * This method is thread-safe.
	 *
	 * @param root the root element for which the cached set of formula extensions
	 *             should be removed
	 */
	public void remove(IEventBRoot root) {
		IEventBRoot snapshotRoot = (IEventBRoot) root.getSnapshot();
		cache.remove(snapshotRoot);
	}

}