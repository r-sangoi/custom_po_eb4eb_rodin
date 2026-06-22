/*******************************************************************************
 * Copyright (c) 2011,2016 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.rulebase;

import java.util.List;

import org.eventb.core.IEventBRoot;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.theory.core.IExtensionRulesSource;
import org.eventb.theory.core.IFormulaExtensionsSource;
import org.eventb.theory.core.IGeneralRule;
import org.eventb.theory.core.IReasoningTypeElement.ReasoningType;
import org.eventb.theory.core.ISCTheorem;

/**
 * Common protocol for a theory entry that can be queried for various rules it
 * holds.
 * 
 * <p>
 * Each query must supply a <code>FormulaFactory</code> so that parsing is
 * carried out if necessary.
 * 
 * <p>
 * It might be useful to just call <code>IEventbRoot.getFormulaFactory()</code>
 * instead of passing a formula factory.
 * 
 * @author maamria
 * @author htson - Changed
 *         {@link #getRewriteRule(boolean, String, Class, FormulaFactory)}
 *         allowing to get automatic rule.
 * @version 1.0
 * @see BaseManager
 * @see ITheoryBaseEntry
 * @since 1.0
 */
public interface ITheoryBaseEntry<R extends IEventBRoot & IFormulaExtensionsSource & IExtensionRulesSource> {

	/**
	 * Returns the list of rewrite rules satisfying the given criteria.
	 * 
	 * @param automatic whether the expected rules must be automatic
	 * @param clazz the class of the left hand side of the expected rules
	 * @param factory the formula factory in case a reload is necessary
	 * @return rewrite rules the list of rules
	 */
	public List<IGeneralRule> getRewriteRules(boolean automatic, Class<?> clazz, FormulaFactory factory);
	
	/**
	 * Returns the list of inference rules satisfying the given criteria.
	 * 
	 * @param automatic whether the expected rules must be automatic
	 * @param type reasoning type the type of reasoning expected of the rules
	 * @param factory the formula factory in case a reload is necessary
	 * @return inference rules the list of inference rules
	 */
	public List<IGeneralRule> getInferenceRules(boolean automatic, ReasoningType type, FormulaFactory factory);
	
	/**
	 * Returns the interactive deployed inference rule by the given name.
	 * 
	 * @param ruleName the name of the rule
	 * @param factory the formula factory in case a reload is necessary
	 * @return the deployed rule
	 */
	public IGeneralRule getInferenceRule(String ruleName, FormulaFactory factory);
	
	/**
	 * Returns the interactive rule specified by its name.
	 * 
	 * @param ruleName name of the rule
	 * @param factory the formula factory in case a reload is necessary
	 * @return the rule or <code>null</code> if not found
	 */
	public IGeneralRule getRewriteRule(boolean automatic, String ruleName, FormulaFactory factory);
	
	/**
	 * Returns all the definitional rules in the theory.
	 * @param factory the formula factory in case a reload is necessary
	 * @return list of definitional rules
	 */
	public List<IGeneralRule> getDefinitionalRules(FormulaFactory factory);
	
	/**
	 * Returns the list of definitional rules satisfying the given criteria.
	 * @param clazz the class of the left hand side of the expected rules
	 * @param factory the formula factory in case a reload is necessary
	 * @return list of definitional rules
	 */
	public List<IGeneralRule> getDefinitionalRules(Class<?> clazz, FormulaFactory factory);
	
	/**
	 * Returns the SC theorems in this entry that has an order less than the passed order.
	 * @param order the upper bound order of expected theorems
	 * @param factory the formula factory in case a reload is necessary
	 * @return the list of deployed theorems
	 */
	public List<ISCTheorem> getSCTheorems(boolean axm, int order, FormulaFactory factory);
	
	/**
	 * Returns the list of SC theorems.
	 * @param factory the formula factory in case a reload is necessary
	 * @return the list of deployed theorems
	 */
	public List<ISCTheorem> getSCTheorems(FormulaFactory factory);
	
	/**
	 * Returns whether the theory backing this entry has changed.
	 * @return whether the theory backing this entry has changed
	 */
	public boolean hasChanged();
	
	/**
	 * Sets this entry change flag to the given boolean value.
	 * @param hasChanged whether the theory backing this entry has changed
	 */
	public void setHasChanged(boolean hasChanged) ;

}
