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
 * Common protocol for an infer clause of an inference rule.
 * 
 * @author maamria
 *
 */
public interface IInfer extends IInferenceClause{
	
	IInternalElementType<IInfer> ELEMENT_TYPE =
		RodinCore.getInternalElementType(TheoryPlugin.PLUGIN_ID + ".infer");
		

}
