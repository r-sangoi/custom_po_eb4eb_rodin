/*******************************************************************************
 * Copyright (c) 2016 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University of Southampton - initial API and implementation
 *******************************************************************************/

package org.eventb.core.internal.ast.extensions.maths;

import org.eventb.core.ast.extensions.maths.IAxiomaticTypeOrigin;

/**
 * <p>
 * An implementation for axiomatic type origins. The origin contains the name of
 * the axiomatic type.
 * </p>
 *
 * @author htson
 * @version 0.1
 * @since 4.0.0
 */
public class AxiomaticTypeOrigin implements IAxiomaticTypeOrigin {

	private String name;

	/**
	 * Constructor to create an axiomatic type origin for a type with the given
	 * name.
	 * 
	 * @param name
	 *            the name of the type.
	 */
	public AxiomaticTypeOrigin(String name) {
		this.name = name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IAxiomaticTypeOrigin#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * Equality between axiomatic type origin. Two origins are the same if they
	 * contains the same type name.
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || this.getClass() != o.getClass())
			return false;
		IAxiomaticTypeOrigin other = (IAxiomaticTypeOrigin) o;

		String mineName = this.getName();
		String otherName = other.getName();
		if (mineName == null) {
			return otherName == null;
		}

		return mineName.equals(otherName);
	}

	/**
	 * The hash code is compute from the type name stored in this origin.
	 */
	@Override
	public int hashCode() {
		final int prime = 19;
		int result = prime * (name == null ? 0 : name.hashCode());
		return result;
	}

}
