/*******************************************************************************
 * Copyright (c) 2011, 2022 University of Southampton and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.ISCExpressionElement;
import org.eventb.core.ITraceableElement;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;

/**
 * 
 * @author maamria
 *
 */
public interface ISCRecursiveDefinitionCase extends ISCExpressionElement,
		ISCFormulaElement, ITraceableElement {

	IInternalElementType<ISCRecursiveDefinitionCase> ELEMENT_TYPE = 
		RodinCore.getInternalElementType(TheoryPlugin.PLUGIN_ID + ".scRecursiveDefinitionCase");

	/**
	 * Get the static checked case expression of this case.
	 *
	 * Type-checking the case expression will infer the type of free variables of
	 * the case expression from the type of the inductive argument. They will be
	 * added to the type environment.
	 *
	 * @param typeEnv           type environment to use (may be updated with new
	 *                          identifiers)
	 * @param inductiveArgument typed identifier of the inductive argument
	 * @return static-checked case expression
	 * @throws CoreException if the case expression fails to parse or type-check
	 */
	Expression getSCCaseExpression(ITypeEnvironmentBuilder typeEnv, FreeIdentifier inductiveArgument)
			throws CoreException;

}
