/*******************************************************************************
 * Copyright (c) 2011, 2020 University of Southampton and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     University of Southampton - initial API and implementation
 *     Systerel - implemented findTheoryProjects
 *******************************************************************************/
package org.eventb.theory.rbp.rulebase;

import static org.eventb.theory.core.DatabaseUtilities.getNonTempSCTheoryPaths;
import static org.eventb.theory.core.TheoryHierarchyHelper.getImportedTheories;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IEventBRoot;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.IExtensionRulesSource;
import org.eventb.theory.core.IGeneralRule;
import org.eventb.theory.core.IReasoningTypeElement.ReasoningType;
import org.eventb.theory.core.util.CoreUtilities;
import org.eventb.theory.core.ISCAvailableTheory;
import org.eventb.theory.core.ISCAvailableTheoryProject;
import org.eventb.theory.core.ISCTheorem;
import org.eventb.theory.core.ISCTheoryPathRoot;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.rbp.rulebase.basis.ProjectBaseEntry;
import org.eventb.theory.rbp.utils.ProverUtilities;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IElementChangedListener;
import org.rodinp.core.IRodinDB;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * An implementation of the rule base manager.
 * 
 * @author maamria
 * @author nicolas
 * @author asiehsalehi
 * @author htson - Changed
 *         {@link #getRewriteRule(boolean, String, String, String, Class, IPOContext)}
 *         allowing to get automatic rules.
 * @version 1.1
 * @see IProjectBaseEntry
 * @see ITheoryBaseEntry
 * @since 1.0
 */
public class BaseManager implements IElementChangedListener {

	private static BaseManager manager;

	private Map<IRodinProject, IProjectBaseEntry> projectEntries;

	private BaseManager() {
		projectEntries = new LinkedHashMap<IRodinProject, IProjectBaseEntry>();
		RodinCore.addElementChangedListener(this);
	}

	public Map<IRodinProject, Map<IExtensionRulesSource, List<ISCTheorem>>> getTheorems(
			IPOContext context, FormulaFactory factory) {
		IEventBRoot parentRoot = context.getParentRoot();
		IRodinProject rodinProject = parentRoot.getRodinProject();
		check(rodinProject);
		Map<IRodinProject, Map<IExtensionRulesSource, List<ISCTheorem>>> map = new LinkedHashMap<IRodinProject, Map<IExtensionRulesSource, List<ISCTheorem>>>();
		// case when POcontext is a theory
		if (context.isTheoryRelated()) {
			map.putAll(projectEntries.get(rodinProject).getTheorems(context, factory));
		}
		// case when POcontext is a context/machine
		else {
			// add theory path stuff
			for (IDeployedTheoryRoot theory : getTheoriesFromPath(rodinProject)) {
				final IRodinProject thyProject = theory.getRodinProject();
				check(thyProject);
				IProjectBaseEntry projBaseEntry = projectEntries
						.get(thyProject);
				if (projBaseEntry != null) {
					if (!map.containsKey(thyProject)) {
						map.put(thyProject,
								new LinkedHashMap<IExtensionRulesSource, List<ISCTheorem>>());
					}
					final Map<IExtensionRulesSource, List<ISCTheorem>> thms = map
							.get(thyProject);
					ITheoryBaseEntry<IDeployedTheoryRoot> theoryBaseEntry = projBaseEntry
							.getTheoryBaseEntry(theory);
					thms.put(theory,
							theoryBaseEntry.getSCTheorems(factory));
				}
			}
		}

		return map;
	}

	/**
	 * Returns the list of definitional rewrite rules for the given formula
	 * class under the given context.
	 * 
	 * @param clazz
	 *            the formula runtime class
	 * @param context
	 *            the proof obligation context
	 * @return the list of rewrite rules
	 */
	public List<IGeneralRule> getDefinitionalRules(Class<?> clazz,
			IPOContext context) {
		IEventBRoot parentRoot = context.getParentRoot();
		IRodinProject rodinProject = parentRoot.getRodinProject();
		check(rodinProject);
		List<IGeneralRule> rules = new ArrayList<IGeneralRule>();
		//case when POcontext is a theory
		if (context.isTheoryRelated()) {
			rules.addAll(projectEntries.get(rodinProject).getDefinitionalRules(clazz, parentRoot, context.getFormulaFactory()));
		}
		//case when POcontext is a context/machine
		else {
			// add theory path stuff
			for (ITheoryBaseEntry<IDeployedTheoryRoot> theoryBaseEntry : getTheoryPathBaseEntries(rodinProject)) {
				rules.addAll(theoryBaseEntry.getDefinitionalRules(context
						.getFormulaFactory()));
			}
		}
		return rules;
	}

	private List<ITheoryBaseEntry<IDeployedTheoryRoot>> getTheoryPathBaseEntries(
			IRodinProject project) {
		final List<ITheoryBaseEntry<IDeployedTheoryRoot>> entries = new ArrayList<ITheoryBaseEntry<IDeployedTheoryRoot>>();
		for (IDeployedTheoryRoot theory : getTheoriesFromPath(project)) {
			check(theory.getRodinProject());
			IProjectBaseEntry projBaseEntry = projectEntries.get(theory
					.getRodinProject());
			if (projBaseEntry != null) {
				ITheoryBaseEntry<IDeployedTheoryRoot> theoryBaseEntry = projBaseEntry
						.getTheoryBaseEntry(theory);
				if (theoryBaseEntry != null
						&& !entries.contains(theoryBaseEntry)) {
					entries.add(theoryBaseEntry);
				}
			}
		}
		return entries;
	}

	public List<IGeneralRule> getRewriteRules(boolean automatic, Class<?> clazz, IPOContext context) {
		IEventBRoot parentRoot = context.getParentRoot();
		IRodinProject rodinProject = parentRoot.getRodinProject();
		check(rodinProject);
		List<IGeneralRule> rules = new ArrayList<IGeneralRule>();
		//case when POcontext is a theory
		if (context.isTheoryRelated()) {
			rules.addAll(projectEntries.get(rodinProject).getRewriteRules(automatic, clazz, parentRoot, context.getFormulaFactory()));
		}
		//case when POcontext is a context/machine
		else {
			// add theory path stuff
			for (ITheoryBaseEntry<IDeployedTheoryRoot> theoryBaseEntry : getTheoryPathBaseEntries(rodinProject)) {
				rules.addAll(theoryBaseEntry.getRewriteRules(automatic, clazz, context.getFormulaFactory()));
			}
		}
		return rules;
	}

	/**
	 * Returns the deployed expression rewrite rule with the given parameters.
	 * 
	 * @param automatic
	 *            flag indicating if the rule is automatic or interactive.
	 * @param ruleName
	 *            the name of the rule
	 * @param theoryName
	 *            the name of the parent theory
	 * @param clazz
	 *            the runtime class of the formula in its lhs
	 * @param context
	 *            the obligation context
	 * @return the deployed rule, or <code>null</code> if not found
	 */
	public IGeneralRule getRewriteRule(boolean automatic, String projectName, String ruleName, String theoryName, IPOContext context) {
		IEventBRoot parentRoot = context.getParentRoot();
		IRodinProject rodinProject = RodinCore.getRodinDB().getRodinProject(projectName);
		check(rodinProject);
		FormulaFactory formulaFactory = context.getFormulaFactory();
		return projectEntries.get(rodinProject).getRewriteRule(automatic, theoryName, ruleName, parentRoot,
				formulaFactory);

	}

	public List<IGeneralRule> getInferenceRules(boolean automatic,
			ReasoningType type, IPOContext context) {
		IEventBRoot parentRoot = context.getParentRoot();
		IRodinProject rodinProject = parentRoot.getRodinProject();
		check(rodinProject);
		List<IGeneralRule> rules = new ArrayList<IGeneralRule>();
		// case when POcontext is a theory
		if (context.isTheoryRelated()) {
			rules.addAll(projectEntries.get(rodinProject).getInferenceRules(automatic, type, parentRoot, context.getFormulaFactory()));
		}
		// case when POcontext is a context/machine
		else {
			// add theory path stuff
			for (ITheoryBaseEntry<IDeployedTheoryRoot> theoryBaseEntry : getTheoryPathBaseEntries(rodinProject)) {
				rules.addAll(theoryBaseEntry.getInferenceRules(automatic, type, context.getFormulaFactory()));
			}
		}
		return rules;
	}

	/**
	 * Returns the deployed inference rule that matches the given details.
	 * 
	 * @param theoryName
	 *            the theory name
	 * @param ruleName
	 *            the rule name
	 * @param context
	 *            the obligation context
	 * @return the deployed inference rule, or <code>null</code> if not found
	 */
	public IGeneralRule getInferenceRule(String projectName, String theoryName, String ruleName, IPOContext context) {
		IEventBRoot parentRoot = context.getParentRoot();
		IRodinProject rodinProject = RodinCore.getRodinDB().getRodinProject(projectName);
		check(rodinProject);
		return projectEntries.get(rodinProject).getInferenceRule(theoryName, ruleName, parentRoot,
				context.getFormulaFactory());

	}

	/**
	 * Returns the singleton instance.
	 * 
	 * @return the singleton instance
	 */
	public static BaseManager getDefault() {
		if (manager == null)
			manager = new BaseManager();
		return manager;
	}

	@Override
	public void elementChanged(ElementChangedEvent event) {
		try {
			processDelta(event.getDelta());
		} catch (CoreException e) {
			ProverUtilities.log(e, "error while processing change in db affecting the rule base manager");
		}
	}

	private void processDelta(IRodinElementDelta delta) throws CoreException {
		IRodinElement element = delta.getElement();
		IRodinElementDelta[] affected = delta.getAffectedChildren();
		if (element instanceof IRodinDB) {
			for (IRodinElementDelta d : affected) {
				processDelta(d);
			}
		}
		if (element instanceof IRodinProject) {
			for (IRodinElementDelta d : affected) {
				processDelta(d);
			}

		}
		if (element instanceof IRodinFile) {
			IRodinFile file = (IRodinFile) element;
			IRodinProject rodinProject = file.getRodinProject();
			check(rodinProject);
			if (file.getRoot() instanceof IDeployedTheoryRoot) {
				projectEntries.get(rodinProject).setHasChanged((IDeployedTheoryRoot) file.getRoot());
			}
			if (file.getRoot() instanceof ISCTheoryRoot) {
				projectEntries.get(rodinProject).setHasChanged((ISCTheoryRoot) file.getRoot());

			}
		}
	}

	private void check(IRodinProject project) {
		if (!projectEntries.containsKey(project)) {
			ProjectBaseEntry entry = new ProjectBaseEntry(project);
			projectEntries.put(project, entry);
		}
	}

	private static void addImportedTheories(IDeployedTheoryRoot theory,
			List<IDeployedTheoryRoot> theories) throws CoreException {
		for (IDeployedTheoryRoot imported : getImportedTheories(theory)) {
			if (!theories.contains(imported)) {
				addImportedTheories(imported, theories);
				theories.add(imported);
			}
		}
	}
	
	private static List<IDeployedTheoryRoot> getTheoriesFromPath(IRodinProject project) {
		final List<IDeployedTheoryRoot> pathTheories = new ArrayList<IDeployedTheoryRoot>();
		try {
			ISCTheoryPathRoot[] paths = getNonTempSCTheoryPaths(project);
			if (paths.length != 1) {
				// ignore paths
				return Collections.emptyList();
			}
			for (ISCAvailableTheoryProject availProj : paths[0]
					.getSCAvailableTheoryProjects()) {
				final ISCAvailableTheory[] scAvailableTheories = availProj.getSCAvailableTheories();
				for (ISCAvailableTheory availableTheory : scAvailableTheories) {
					final IDeployedTheoryRoot deployedTheoryRoot = availableTheory.getSCDeployedTheoryRoot();
					addImportedTheories(deployedTheoryRoot, pathTheories);
					pathTheories.add(deployedTheoryRoot);
				}
			}
		} catch (CoreException e) {
			CoreUtilities
					.log(e, "Error while processing theory path for project "
							+ project);
		}
		return pathTheories;
	}
	
	/**
	 * Finds projects defining a theory with the given name, accessible
	 * from the given context.
	 * <p>
	 * Theories are searched in context project theory path and, if context is theory related:
	 * <ul>
	 * <li>deployed theories in the context project</li>
	 * <li>the context theory itself</li>
	 * </ul>
	 * </p>
	 * @param context
	 *            a PO context
	 * @param theoryName
	 *            searched theory name
	 * @return a set of Rodin projects, potentially empty
	 */
	public Set<IRodinProject> findTheoryProjects(IPOContext context,
			String theoryName) {
		final Set<IRodinProject> found = new HashSet<IRodinProject>();
		final IEventBRoot contextRoot = context.getParentRoot();
		final IRodinProject contextProject = contextRoot.getRodinProject();
		final List<IDeployedTheoryRoot> theoriesFromPath = getTheoriesFromPath(contextProject);
		for (IDeployedTheoryRoot thy : theoriesFromPath) {
			final String bareName = thy.getRodinFile().getBareName();
			if (bareName.equals(theoryName)) {
				found.add(thy.getRodinProject());
			}
		}
		if (context.isTheoryRelated()) {
			if (contextRoot.getRodinFile().getBareName().equals(theoryName)) {
				found.add(contextProject);
			}
			try {
				for (IDeployedTheoryRoot thy : contextProject
						.getRootElementsOfType(IDeployedTheoryRoot.ELEMENT_TYPE)) {
					final String bareName = thy.getRodinFile().getBareName();
					if (bareName.equals(theoryName)) {
						found.add(contextProject);
					}
				}
			} catch (RodinDBException e) {
				ProverUtilities.log(e, "while searching project with theory "
						+ theoryName);
			}
		}
		return found;
	}
}
