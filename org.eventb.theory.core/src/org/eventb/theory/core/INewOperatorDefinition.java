/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core;

import org.eventb.core.ICommentedElement;
import org.eventb.core.ILabeledElement;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for a new operator definition.
 * 
 * <p> A new operator definition provides the syntax, the notation, the formula type of the new operator being defined.
 * Associativity and commutativity properties are also provided as part of an operator definition.
 * 
 * <p> Each operator definitions may have a number of operator arguments. An overriding (stronger) well-definedness condition can also
 * be provided. Finally, a direct definition is also part of an operator definition.
 * 
 * <p> This interface is not intended to be implemented by clients.
 * 
 * @see IDatatypeDefinition
 * 
 * @author maamria
 *
 */
public interface INewOperatorDefinition extends ICommentedElement, ILabeledElement,
	IFormulaTypeElement, INotationTypeElement,
	IAssociativeElement, ICommutativeElement{

	IInternalElementType<INewOperatorDefinition> ELEMENT_TYPE = 
		RodinCore.getInternalElementType(TheoryPlugin.PLUGIN_ID + ".newOperatorDefinition");
	
	/**
	 * Returns a handle to the operator argument of the given name.
	 * @param name the argument name
	 * @return the operator argument
	 */
	IOperatorArgument getOperatorArgument(String name);
	
	/**
	 * Returns all operator arguments of this operator.
	 * @return all operator arguments
	 * @throws RodinDBException
	 */
	IOperatorArgument[] getOperatorArguments() throws RodinDBException;
	
	/**
	 * Returns a handle to the well-definedness condition of the given name.
	 * @param name the name of the condition
	 * @return the WD-condition element
	 */
	IOperatorWDCondition getOperatorWDCondition(String name);
	
	/**
	 * Returns all WD-conditions children of this operator.
	 * @return all WD-condition elements
	 * @throws RodinDBException
	 */
	IOperatorWDCondition[] getOperatorWDConditions() throws RodinDBException;
	
	/**
	 * Returns a handle to the direct definition of the given name.
	 * @param name the name of the definition
	 * @return the direct definition
	 */
	IDirectOperatorDefinition getDirectOperatorDefinition(String name);
	
	/**
	 * Returns all direct definitions that are the children of this operator definition.
	 * @return all direct definitions
	 * @throws RodinDBException
	 */
	IDirectOperatorDefinition[] getDirectOperatorDefinitions() throws RodinDBException;
	
	IRecursiveOperatorDefinition getRecursiveOperatorDefinition(String name);
	
	IRecursiveOperatorDefinition[] getRecursiveOperatorDefinitions() throws RodinDBException;
	
}
