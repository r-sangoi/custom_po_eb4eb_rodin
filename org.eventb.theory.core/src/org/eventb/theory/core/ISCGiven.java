/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core;

import org.eventb.theory.core.plugin.TheoryPlugin;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;

/**
 * Common protocol for statically checked givens.
 * 
 * <p> This interface is not intended to be implemented by clients.
 * 
 * @see IGiven
 * 
 * @author maamria
 *
 */
public interface ISCGiven extends ISCInferenceClause, IHypElement{

	IInternalElementType<ISCGiven> ELEMENT_TYPE = 
		RodinCore.getInternalElementType(TheoryPlugin.PLUGIN_ID + ".scGiven");
}
