package org.eventb.theory.core;

import org.eventb.core.ICommentedElement;
import org.eventb.core.ILabeledElement;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * 
 * @author maamria
 * 
 */
public interface IAxiomaticOperatorDefinition extends ICommentedElement, ILabeledElement, IFormulaTypeElement,
		INotationTypeElement, ITypeElement, IAssociativeElement, ICommutativeElement {

	IInternalElementType<IAxiomaticOperatorDefinition> ELEMENT_TYPE = RodinCore
			.getInternalElementType(TheoryPlugin.PLUGIN_ID + ".axiomaticOperatorDefinition");
	
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
}
