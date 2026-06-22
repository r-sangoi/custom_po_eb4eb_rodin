/*******************************************************************************
 * Copyright (c) 2011,2016 University of Southampton and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.rulebase.basis;
import static org.eventb.theory.core.DatabaseUtilities.originatedFromTheory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IEventBRoot;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.theory.core.DatabaseUtilities;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.IExtensionRulesSource;
import org.eventb.theory.core.IGeneralRule;
import org.eventb.theory.core.IReasoningTypeElement.ReasoningType;
import org.eventb.theory.core.ISCTheorem;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.maths.extensions.WorkspaceExtensionsManager;
import org.eventb.theory.rbp.rulebase.IPOContext;
import org.eventb.theory.rbp.rulebase.IProjectBaseEntry;
import org.eventb.theory.rbp.rulebase.ITheoryBaseEntry;
import org.eventb.theory.rbp.utils.ProverUtilities;
import org.rodinp.core.IRodinProject;

/**
 * 
 * @author maamria
 * @author asiehsalehi
 * @author htson - Changed
 *         {@link #getRewriteRule(boolean, String, String, Class, IEventBRoot, FormulaFactory)}
 *         allowing to get automatic rule.
 * @version 1.1
 * @since 1.0
 */
public class ProjectBaseEntry implements IProjectBaseEntry{

	private IRodinProject project;
	private Map<IDeployedTheoryRoot, ITheoryBaseEntry<IDeployedTheoryRoot>> deployedRoots;
	private Map<ISCTheoryRoot, ITheoryBaseEntry<ISCTheoryRoot>> scRoots;

	public ProjectBaseEntry(IRodinProject project) {
		this.project = project;
		this.deployedRoots = new LinkedHashMap<IDeployedTheoryRoot, ITheoryBaseEntry<IDeployedTheoryRoot>>();
		this.scRoots = new LinkedHashMap<ISCTheoryRoot, ITheoryBaseEntry<ISCTheoryRoot>>();
	}
	
	public void setHasChanged(ISCTheoryRoot scRoot){
		//if (!scRoots.containsKey(scRoot)) {
			TheoryBaseEntry<ISCTheoryRoot> entry = new TheoryBaseEntry<ISCTheoryRoot>(
					scRoot);
			scRoots.put(scRoot, entry);
		//}
		scRoots.get(scRoot).setHasChanged(true);
	}
	
	public void setHasChanged(IDeployedTheoryRoot depRoot){
		//if(!deployedRoots.containsKey(depRoot)){
			TheoryBaseEntry<IDeployedTheoryRoot> entry = new TheoryBaseEntry<IDeployedTheoryRoot>(depRoot);
			deployedRoots.put(depRoot, entry);
		//}
		deployedRoots.get(depRoot).setHasChanged(true);
	}

	@Override
	public List<IGeneralRule> getRewriteRules(boolean automatic, Class<?> clazz, IEventBRoot root, FormulaFactory factory) {
		List<IGeneralRule> toReturn = new ArrayList<IGeneralRule>();
			List<IDeployedTheoryRoot> reqRoots = getRequiredTheories(root);
			String componentName = root.getComponentName();
			//add imported theories (deployed)
			for (IDeployedTheoryRoot deployedRoot : reqRoots) {
				if (!scRoots.containsKey(deployedRoot)) {
					TheoryBaseEntry<ISCTheoryRoot> entry = new TheoryBaseEntry<ISCTheoryRoot>(
							deployedRoot);
					scRoots.put(deployedRoot, entry);
				}
				toReturn.addAll(scRoots.get(deployedRoot).getRewriteRules(automatic, clazz, factory));	
			}
			//add the current theory (sc)
			ISCTheoryRoot SCRoot = DatabaseUtilities.getSCTheory(componentName, project);
			if (!scRoots.containsKey(SCRoot)) {
				TheoryBaseEntry<ISCTheoryRoot> entry = new TheoryBaseEntry<ISCTheoryRoot>(
						SCRoot);
				scRoots.put(SCRoot, entry);
			}
			if (!automatic)
				toReturn.addAll(scRoots.get(SCRoot).getDefinitionalRules(clazz, factory));
		return toReturn;
	}

	@Override
	public IGeneralRule getRewriteRule(boolean automatic, String theoryName, String ruleName, IEventBRoot root, FormulaFactory factory) {
		if (originatedFromTheory(root.getRodinFile(), project) && theoryName.equals(root.getElementName()) ){
			ISCTheoryRoot scRoot = DatabaseUtilities.getSCTheory(theoryName, project);
			if (!scRoots.containsKey(scRoot)){
				TheoryBaseEntry<ISCTheoryRoot> entry = new TheoryBaseEntry<ISCTheoryRoot>(scRoot);
				scRoots.put(scRoot, entry);
			}
			return scRoots.get(scRoot).getRewriteRule(automatic, ruleName, factory);
		}
		else {
			IDeployedTheoryRoot depRoot = DatabaseUtilities.getDeployedTheory(theoryName, project);
			if (depRoot == null || !depRoot.exists()){
				return null;
			}
			if (!deployedRoots.containsKey(depRoot)){
				TheoryBaseEntry<IDeployedTheoryRoot> entry = new TheoryBaseEntry<IDeployedTheoryRoot>(depRoot);
				deployedRoots.put(depRoot, entry);
			}
			return deployedRoots.get(depRoot).getRewriteRule(automatic, ruleName, factory);
		}
	}
	
	public List<IGeneralRule> getInferenceRules(boolean automatic, ReasoningType type, 
			IEventBRoot root,FormulaFactory factory){
		List<IGeneralRule> toReturn = new ArrayList<IGeneralRule>();
		// case when POContext is a theory
		if (originatedFromTheory(root.getRodinFile(), project)){
			List<IDeployedTheoryRoot> reqRoots = getRequiredTheories(root);
			//String componentName = root.getComponentName();
			//add imported theories (deployed)
			for (IDeployedTheoryRoot deployedRoot : reqRoots) {
				if (!scRoots.containsKey(deployedRoot)) {
					TheoryBaseEntry<ISCTheoryRoot> entry = new TheoryBaseEntry<ISCTheoryRoot>(
							deployedRoot);
					scRoots.put(deployedRoot, entry);
				}
				if (!root.getComponentName().equals(deployedRoot.getComponentName())){
					toReturn.addAll(scRoots.get(deployedRoot).getInferenceRules(automatic, type, factory));
				}
			}
			
			//add the current theory (sc)
//			ISCTheoryRoot SCRoot = DatabaseUtilities.getSCTheory(componentName, project);
//			if (!scRoots.containsKey(SCRoot)) {
//				TheoryBaseEntry<ISCTheoryRoot> entry = new TheoryBaseEntry<ISCTheoryRoot>(SCRoot);
//				scRoots.put(SCRoot, entry);
//			}
//			toReturn.addAll(scRoots.get(SCRoot).getInferenceRules(automatic, type, factory));
			
		}
		return toReturn;
	}
	
	public IGeneralRule getInferenceRule(String theoryName, String ruleName, 
			IEventBRoot root, FormulaFactory factory){
//		if (originatedFromTheory(root.getRodinFile(), project)){
//			ISCTheoryRoot scRoot = DatabaseUtilities.getSCTheory(theoryName, project);
//			if (!scRoots.containsKey(scRoot)){
//				TheoryBaseEntry<ISCTheoryRoot> entry = new TheoryBaseEntry<ISCTheoryRoot>(scRoot);
//				scRoots.put(scRoot, entry);
//			}
//			return scRoots.get(scRoot).getInferenceRule(ruleName, factory);
//		}
//		else {
			IDeployedTheoryRoot depRoot = DatabaseUtilities.getDeployedTheory(theoryName, project);
			if (depRoot == null || !depRoot.exists()){
				return null;
			}
			if (!deployedRoots.containsKey(depRoot)){
				TheoryBaseEntry<IDeployedTheoryRoot> entry = new TheoryBaseEntry<IDeployedTheoryRoot>(depRoot);
				deployedRoots.put(depRoot, entry);
			}
			return deployedRoots.get(depRoot).getInferenceRule(ruleName, factory);
//		}
	}
	

	@Override
	public Map<IRodinProject, Map<IExtensionRulesSource, List<ISCTheorem>>> getTheorems(IPOContext poContext, FormulaFactory factory) {
		IEventBRoot root = poContext.getParentRoot();
		String componentName = root.getComponentName();
		Map<IRodinProject, Map<IExtensionRulesSource, List<ISCTheorem>>> map = new LinkedHashMap<IRodinProject, Map<IExtensionRulesSource, List<ISCTheorem>>>();
		if (originatedFromTheory(root.getRodinFile(), project)){
			int order = poContext.getOrder();
			boolean axm = poContext.isAxiom();
			ISCTheoryRoot scRoot = DatabaseUtilities.getSCTheory(componentName, project);
			List<IDeployedTheoryRoot> requiredRoots = getRequiredTheories(scRoot);
			//add imported theories (deployed)
			for (ISCTheoryRoot scTheoryRoot : requiredRoots){
				if (!scRoots.containsKey(scTheoryRoot)){
					TheoryBaseEntry<ISCTheoryRoot> entry = new TheoryBaseEntry<ISCTheoryRoot>(scTheoryRoot);
					scRoots.put(scTheoryRoot, entry);
				} 
				final IRodinProject thyProject = scTheoryRoot.getRodinProject();
				if (!map.containsKey(thyProject)) {
					map.put(thyProject,
							new LinkedHashMap<IExtensionRulesSource, List<ISCTheorem>>());
				}
				final Map<IExtensionRulesSource, List<ISCTheorem>> thms = map.get(thyProject);
				thms.put(scTheoryRoot, scRoots.get(scTheoryRoot).getSCTheorems(factory));
			}
			//add the current theory (sc)
			if (!scRoots.containsKey(scRoot)){
				TheoryBaseEntry<ISCTheoryRoot> entry = new TheoryBaseEntry<ISCTheoryRoot>(scRoot);
				scRoots.put(scRoot, entry);
			} 
			if(order != -1){
				final IRodinProject thyProject = scRoot.getRodinProject();	
				if (!map.containsKey(thyProject)) {
					map.put(thyProject,
							new LinkedHashMap<IExtensionRulesSource, List<ISCTheorem>>());
				}
				final Map<IExtensionRulesSource, List<ISCTheorem>> thms = map.get(thyProject);
				thms.put(scRoot, scRoots.get(scRoot).getSCTheorems(axm, order, factory));
			}
			else {
				final IRodinProject thyProject = scRoot.getRodinProject();
				if (!map.containsKey(thyProject)) {
					map.put(thyProject,
							new LinkedHashMap<IExtensionRulesSource, List<ISCTheorem>>());
				}
				final Map<IExtensionRulesSource, List<ISCTheorem>> thms = map.get(thyProject);
				thms.put(scRoot, scRoots.get(scRoot).getSCTheorems(factory));
			}
			
		}
		return map;
	}

	@Override
	public List<IGeneralRule> getDefinitionalRules(IEventBRoot root, FormulaFactory factory) {
		List<IGeneralRule> toReturn = new ArrayList<IGeneralRule>();
		if (DatabaseUtilities.originatedFromTheory(root.getRodinFile(), project)) {
			List<IDeployedTheoryRoot> reqRoots = getRequiredTheories(root);
			String componentName = root.getComponentName();
			ISCTheoryRoot SCRoot = DatabaseUtilities.getSCTheory(componentName, project);
			reqRoots.add(SCRoot.getDeployedTheoryRoot());
			for (ISCTheoryRoot scRoot : reqRoots) {
				if (!scRoots.containsKey(scRoot)) {
					TheoryBaseEntry<ISCTheoryRoot> entry = new TheoryBaseEntry<ISCTheoryRoot>(
							scRoot);
					scRoots.put(scRoot, entry);
				}
				toReturn.addAll(scRoots.get(scRoot).getDefinitionalRules(factory));
				
			}
		}
		else {
			IDeployedTheoryRoot[] deployedTheoryRoots = getDeployedRoots();
			for (IDeployedTheoryRoot deployedTheoryRoot : deployedTheoryRoots){
				if(!deployedRoots.containsKey(deployedTheoryRoot)){
					TheoryBaseEntry<IDeployedTheoryRoot> entry = new TheoryBaseEntry<IDeployedTheoryRoot>(deployedTheoryRoot);
					deployedRoots.put(deployedTheoryRoot, entry);
				}
				toReturn.addAll(deployedRoots.get(deployedTheoryRoot).getDefinitionalRules(factory));
			}
		}
		return toReturn;
	}

	@Override
	public List<IGeneralRule> getDefinitionalRules(Class<?> clazz, IEventBRoot root, FormulaFactory factory) {
		List<IGeneralRule> toReturn = new ArrayList<IGeneralRule>();
		if (DatabaseUtilities.originatedFromTheory(root.getRodinFile(), project)) {
			List<IDeployedTheoryRoot> reqRoots = getRequiredTheories(root);
			String componentName = root.getComponentName();
			ISCTheoryRoot SCRoot = DatabaseUtilities.getSCTheory(componentName, project);
			reqRoots.add(SCRoot.getDeployedTheoryRoot());
			for (ISCTheoryRoot scRoot : reqRoots) {
				if (!scRoots.containsKey(scRoot)) {
					TheoryBaseEntry<ISCTheoryRoot> entry = new TheoryBaseEntry<ISCTheoryRoot>(
							scRoot);
					scRoots.put(scRoot, entry);
				}
				toReturn.addAll(scRoots.get(scRoot).getDefinitionalRules(clazz, factory));
				
			}
		}
		else {
			IDeployedTheoryRoot[] deployedTheoryRoots = getDeployedRoots();
			for (IDeployedTheoryRoot deployedTheoryRoot : deployedTheoryRoots){
				if(!deployedRoots.containsKey(deployedTheoryRoot)){
					TheoryBaseEntry<IDeployedTheoryRoot> entry = new TheoryBaseEntry<IDeployedTheoryRoot>(deployedTheoryRoot);
					deployedRoots.put(deployedTheoryRoot, entry);
				}
				toReturn.addAll(deployedRoots.get(deployedTheoryRoot).getDefinitionalRules(clazz, factory));
			}
		}
		return toReturn;
	}
	
	private IDeployedTheoryRoot[] getDeployedRoots (){
		try {
			return DatabaseUtilities.getDeployedTheories(project);
		} catch (CoreException e) {
			ProverUtilities.log(e, "error while getting deployed roots of "+ project.getElementName());
		}
		return new IDeployedTheoryRoot[0];
	}

	private List<IDeployedTheoryRoot> getRequiredTheories(IEventBRoot root) {
		if (!originatedFromTheory(root.getRodinFile())) {
			return Collections.emptyList();
		}
		final ISCTheoryRoot scRoot = DatabaseUtilities.getSCTheory(root.getComponentName(), project);
		if (!scRoot.exists()) {
			return Collections.emptyList();
		}
		try {
			return WorkspaceExtensionsManager.getInstance().getTheoryImportClosure(scRoot);
		} catch (CoreException e) {
			ProverUtilities.log(e, "error while getting import closure of "
					+ root.getPath());
			return Collections.emptyList();
		}
	}

	@Override
	public ITheoryBaseEntry<ISCTheoryRoot> getTheoryBaseEntry(ISCTheoryRoot root) {
		if (!root.getRodinProject().equals(project)){
			return null;
		}
		ITheoryBaseEntry<ISCTheoryRoot> entry = scRoots.get(root);
		if (entry == null){
			entry = new TheoryBaseEntry<ISCTheoryRoot>(root);
		}
		scRoots.put(root, entry);
		return entry;
	}

	@Override
	public ITheoryBaseEntry<IDeployedTheoryRoot> getTheoryBaseEntry(IDeployedTheoryRoot root) {
		if (!root.getRodinProject().equals(project)){
			return null;
		}
		ITheoryBaseEntry<IDeployedTheoryRoot> entry = deployedRoots.get(root);
		if (entry == null){
			entry = new TheoryBaseEntry<IDeployedTheoryRoot>(root);
		}
		deployedRoots.put(root, entry);
		return entry;
	}
}
