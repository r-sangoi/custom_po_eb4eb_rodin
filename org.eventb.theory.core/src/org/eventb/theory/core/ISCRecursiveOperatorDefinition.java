/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core;

import org.eventb.core.ITraceableElement;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * 
 * @author maamria
 *
 */
public interface ISCRecursiveOperatorDefinition extends
		IInductiveArgumentElement, ITraceableElement {

	IInternalElementType<ISCRecursiveOperatorDefinition> ELEMENT_TYPE = 
		RodinCore.getInternalElementType(TheoryPlugin.PLUGIN_ID + ".scRecursiveOperatorDefinition");
	
	public ISCRecursiveDefinitionCase getRecursiveDefinitionCase(String name);
	
	public ISCRecursiveDefinitionCase[] getRecursiveDefinitionCases() throws RodinDBException;
}
