package org.eventb.theory.core;

import org.eventb.core.ILabeledElement;
import org.eventb.core.ISCPredicateElement;
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
public interface ISCAxiomaticOperatorDefinition extends  ILabeledElement,
	IFormulaTypeElement, INotationTypeElement, IAssociativeElement, ICommutativeElement, 
	ITraceableElement, IHasErrorElement, ISCPredicateElement, ISCTypeElement, IOperatorGroupElement, 
	IWDElement{

	IInternalElementType<ISCAxiomaticOperatorDefinition> ELEMENT_TYPE = 
			RodinCore.getInternalElementType(TheoryPlugin.PLUGIN_ID + ".scAxiomaticOperatorDefinition");
	
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
	
}
