/*******************************************************************************
 * Copyright (c) 2011,2016 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.rulebase;

import java.util.List;
import java.util.Map;

import org.eventb.core.IEventBRoot;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.IExtensionRulesSource;
import org.eventb.theory.core.IGeneralRule;
import org.eventb.theory.core.IReasoningTypeElement.ReasoningType;
import org.eventb.theory.core.ISCTheorem;
import org.eventb.theory.core.ISCTheoryRoot;
import org.rodinp.core.IRodinProject;

/**
 * 
 * @author maamria
 * @author htson - Changed
 *         {@link #getRewriteRule(boolean, String, String, Class, IEventBRoot, FormulaFactory)}
 *         allowing to get automatic rule.
 * @version 1.1
 * @see BaseManager
 * @see ITheoryBaseEntry
 * @since 1.0
 */
public interface IProjectBaseEntry {

	/**
	 * Sets the change flag on the entry backed by the passed SC theory.
	 * @param scRoot the SC theory
	 */
	public void setHasChanged(ISCTheoryRoot scRoot);
	
	/**
	 * Sets the change flag on the entry backed by the passed deployed theory.
	 * @param depRoot the deployed theory
	 */
	public void setHasChanged(IDeployedTheoryRoot depRoot);
	
	/**
	 * Returns the list of rewrite rules satisfying the given criteria. The rules must be available to use
	 * by the Event-B root specified.
	 * 
	 * @param automatic whether the expected rules must be automatic
	 * @param clazz the class of the left hand side of the expected rules
	 * @param root the Event-B root (PO context)
	 * @param factory the formula factory in case a reload is necessary
	 * @return rewrite rules the list of rules
	 */
	public List<IGeneralRule> getRewriteRules(boolean automatic, Class<?> clazz, 
			IEventBRoot root, FormulaFactory factory);
	
	/**
	 * Returns the interactive rule specified by its name as well as the runtime class of its lhs.
	 * @param theoryName the name of the theory
	 * @param ruleName name of the rule
	 * @param root the Event-B root (PO context)
	 * @param factory the formula factory in case a reload is necessary
	 * 
	 * @return the rule or <code>null</code> if not found
	 */
	public IGeneralRule getRewriteRule(boolean automatic, String theoryName, String ruleName, 
			IEventBRoot root, FormulaFactory factory);
	/**
	 * Returns the list of inference rules satisfying the given criteria. The rules must be available to use
	 * by the Event-B root specified.
	 * 
	 * @param automatic whether the expected rules must be automatic
	 * @param type reasoning type the type of reasoning expected of the rules
	 * @param root the Event-B root (PO context)
	 * @param factory the formula factory in case a reload is necessary
	 * @return inference rules the list of inference rules
	 */
	public List<IGeneralRule> getInferenceRules(boolean automatic, ReasoningType type, IEventBRoot root,FormulaFactory factory);
	
	/**
	 * Returns the interactive deployed inference rule by the given name. The rules must be available to use
	 * by the Event-B root specified.
	 * @param theoryName the name of the theory
	 * @param ruleName the name of the rule
	 * @param factory the formula factory in case a reload is necessary
	 * @return the deployed rule
	 */
	public IGeneralRule getInferenceRule(String theoryName, String ruleName, IEventBRoot root, FormulaFactory factory);
	
	/**
	 * Returns the SC theorems available for the PO context <code>context</code>.
	 * @param context the proof obligation context
	 * @param factory the formula factory
	 * @return the deployed theorems
	 */
	public Map<IRodinProject, Map<IExtensionRulesSource, List<ISCTheorem>>> getTheorems(IPOContext context, FormulaFactory factory);
	
	/**
	 * Returns all the definitional rules available to the given root.
	 * @param root the Event-B root
	 * @param factory the formula factory in case a reload is necessary
	 * @return list of definitional rules
	 */
	public List<IGeneralRule> getDefinitionalRules(IEventBRoot root, FormulaFactory factory);
	
	/**
	 * Returns the list of definitional rules satisfying the given criteria. The rules must be available
	 * to use by the given root.
	 * @param clazz the class of the left hand side of the expected rules
	 * @param root the Event-B root
	 * @param factory the formula factory in case a reload is necessary
	 * @return list of definitional rules
	 */
	public List<IGeneralRule> getDefinitionalRules(Class<?> clazz, IEventBRoot root, FormulaFactory factory);
	
	public ITheoryBaseEntry<ISCTheoryRoot> getTheoryBaseEntry(ISCTheoryRoot root);
	
	public ITheoryBaseEntry<IDeployedTheoryRoot> getTheoryBaseEntry(IDeployedTheoryRoot root);
	
}
