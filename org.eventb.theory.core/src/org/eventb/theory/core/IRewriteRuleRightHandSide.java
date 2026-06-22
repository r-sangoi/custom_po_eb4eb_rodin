package org.eventb.theory.core;

import org.eventb.core.ICommentedElement;
import org.eventb.core.ILabeledElement;
import org.eventb.core.IPredicateElement;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;

/**
 * <p>Common protocol for a right hand side of a rewrite rule.</p>
 * <p>A rewrite rule right hand side has a label and a comment. 
 * It also has a right hand side formula and a condition (predicate).</p>
 * 
 * <p> This interface is not intended to be implemented by clients.
 * 
 * @author maamria
 *
 */
public interface IRewriteRuleRightHandSide extends 
	ICommentedElement, IPredicateElement, IFormulaElement, ILabeledElement
{

	IInternalElementType<IRewriteRuleRightHandSide> ELEMENT_TYPE = RodinCore
		.getInternalElementType(TheoryPlugin.PLUGIN_ID + ".rewriteRuleRHS");
}
