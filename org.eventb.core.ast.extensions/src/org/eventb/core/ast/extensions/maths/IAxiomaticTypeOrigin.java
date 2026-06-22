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

package org.eventb.core.ast.extensions.maths;

import org.eventb.core.internal.ast.extensions.maths.AxiomaticTypeOrigin;

/**
 * <p>
 * A common interface for axiomatic type origin.
 * </p>
 *
 * @author htson
 * @version 0.1
 * @see AxiomaticTypeOrigin
 * @since 4.0
 */
public interface IAxiomaticTypeOrigin {

	/**
	 * Returns the name of the axiomatic type.
	 * 
	 * @return the name of the axiomatic type.
	 */
	public String getName();

}
