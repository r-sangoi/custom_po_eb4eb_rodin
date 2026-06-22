/*******************************************************************************
 * Copyright (c) 2011, 2020 University of Southampton and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.internal.rbp.rulebase;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.theory.core.DatabaseUtilities;
import org.eventb.theory.core.IExtensionRulesSource;
import org.eventb.theory.core.ISCTheorem;
import org.eventb.theory.rbp.rulebase.BaseManager;
import org.eventb.theory.rbp.rulebase.IPOContext;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * A utility class the help retrieve theorems suitable for a given context.
 * @author maamria, asiehsalehi
 *
 */
public class TheoremsRetriever {

	private IPOContext poContext;
	private BaseManager baseManager;
	private FormulaFactory factory;
	
	private Map<IRodinProject, Map<IExtensionRulesSource, List<ISCTheorem>>> theorems;

	public TheoremsRetriever(IPOContext poContext){
		this.poContext = poContext;
		this.factory = poContext.getFormulaFactory();
		baseManager = BaseManager.getDefault();
		getTheorems();
	}
	
	/**
	 * Populates the relevant theorems.
	 */
	protected void getTheorems(){
		this.theorems =  baseManager.getTheorems(poContext, factory);
	}
	
	/**
	 * Returns all rodin projects from which theorems can be retrieved for the context.
	 * @return suitable rodin projects
	 */
	public String[] getRodinProjects(){
		Set<IRodinProject> keySet = theorems.keySet();
		Set<String> set = new LinkedHashSet<String>();
		for (IRodinProject p : keySet) {
			set.add(p.getElementName());
		}
		return set.toArray(new String[set.size()]);
	}
	
	/**
	 * Returns the theories in the given project from which theorems can be retrieved for the context.
	 * @param project the project name
	 * @return suitable theories
	 */
	public String[] getTheories(String project){
		IRodinProject rodinProject =  RodinCore.getRodinDB().getRodinProject(project);
		Map<IExtensionRulesSource, List<ISCTheorem>> theories = theorems.get(rodinProject);
		if (theories != null){
			Set<IExtensionRulesSource> keySet = theories.keySet();
			return DatabaseUtilities.getElementNames(keySet).toArray(new String[keySet.size()]);
		}
		return new String[0];
	}
	
	/**
	 * Returns all SC theorem specified by the given modifiers.
	 * @param project the project name
	 * @param theory the theory name
	 * @return list of SC theorems
	 */
	public List<ISCTheorem> getSCTheorems(String project, String theory){
		IRodinProject rodinProject =  RodinCore.getRodinDB().getRodinProject(project);
		Map<IExtensionRulesSource, List<ISCTheorem>> theories = theorems.get(rodinProject);
		for (IExtensionRulesSource source : theories.keySet()){
			if (source.getElementName().equals(theory)){
				return theories.get(source);
			}
		}
		return new ArrayList<ISCTheorem>();
	}
	
	/**
	 * Returns the particular theorem defined by the given modifiers.
	 * @param project the project name
	 * @param theory the theory name
	 * @param theorem the theorem name
	 * @return the SC theorem, or <code>null</code> if not found
	 */
	public ISCTheorem getSCTheorem(String project, String theory, String theorem){
		try {
			for (ISCTheorem SCTheorem : getSCTheorems(project, theory)){
				if (SCTheorem.getLabel().equals(theorem)){
					return SCTheorem;
				}
			}
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Returns the theorems defined by the given modifiers.
	 * @param project the project name
	 * @param theory the theory name
	 * @param theorems the theorems names
	 * @return the SC theorems
	 */
	public List<ISCTheorem> getSCTheorems(String project, String theory, List<String> theorems){
		List<ISCTheorem> list = new ArrayList<ISCTheorem>();
		try {
			for (ISCTheorem SCTheorem : getSCTheorems(project, theory)){
				if (theorems.contains(SCTheorem.getLabel())){
					list.add(SCTheorem);
				}
			}
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 * Returns the formula factory suitable for the proof obligation context.
	 * @return the formula factory
	 */
	public FormulaFactory getFactory(){
		return factory;
	}
}
