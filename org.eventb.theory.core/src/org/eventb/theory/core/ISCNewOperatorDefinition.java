/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core;

import org.eventb.core.ILabeledElement;
import org.eventb.core.ISCPredicateElement;
import org.eventb.core.ITraceableElement;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for statically checked new operator definition.
 * 
 * <p>
 * This interface is not intended to be implemented by clients.
 * 
 * @see INewOperatorDefinition
 * 
 * @author maamria
 * 
 */
public interface ISCNewOperatorDefinition extends ILabeledElement,
		IFormulaTypeElement, INotationTypeElement,
		IAssociativeElement, ICommutativeElement, ITraceableElement,
		IHasErrorElement, ISCPredicateElement, ISCTypeElement, IOperatorGroupElement, IWDElement {

	IInternalElementType<ISCNewOperatorDefinition> ELEMENT_TYPE = RodinCore
			.getInternalElementType(TheoryPlugin.PLUGIN_ID
					+ ".scNewOperatorDefinition");

	/**
	 * Returns a handle to the operator argument of the given name.
	 * 
	 * @param name
	 *            the argument name
	 * @return the operator argument
	 */
	ISCOperatorArgument getOperatorArgument(String name);

	/**
	 * Returns all operator arguments of this operator.
	 * 
	 * @return all operator arguments
	 * @throws RodinDBException
	 */
	ISCOperatorArgument[] getOperatorArguments() throws RodinDBException;

	/**
	 * Returns a handle to the direct definition of the given name.
	 * 
	 * @param name
	 *            the name of the definition
	 * @return the direct definition
	 */
	ISCDirectOperatorDefinition getDirectOperatorDefinition(String name);

	/**
	 * Returns all direct definitions that are the children of this operator
	 * definition.
	 * 
	 * @return all direct definitions
	 * @throws RodinDBException
	 */
	ISCDirectOperatorDefinition[] getDirectOperatorDefinitions()
			throws RodinDBException;

	ISCRecursiveOperatorDefinition getRecursiveOperatorDefinition(String name);

	ISCRecursiveOperatorDefinition[] getRecursiveOperatorDefinitions()
			throws RodinDBException;

}
