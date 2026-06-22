/*******************************************************************************
 * Copyright (c) 2011, 2020 University of Southampton and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.theory.core.basis.TheoryDeployer;
import org.eventb.theory.core.util.CoreUtilities;
import org.eventb.theory.internal.core.util.DeployUtilities;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

/**
 * A helper class to manipulate theory hierarchies.
 * 
 * @author im06r
 * 
 */
public class TheoryHierarchyHelper {
	
	/**
	 * Returns the import relationship closure of the given SC theory. TODO
	 * ensure no cycles
	 * 
	 * @param scRoot
	 *            the SC theory
	 * @return the import closure
	 * @throws CoreException
	 */
	public static Set<IDeployedTheoryRoot> importClosure(ISCTheoryRoot scRoot) throws CoreException {
		//TODO use WorkspaceExtensionsManager instead
		Set<IDeployedTheoryRoot> closure = new LinkedHashSet<IDeployedTheoryRoot>();
		Set<IDeployedTheoryRoot> imported = getImportedTheories(scRoot);
		closure.addAll(imported);
		for (ISCTheoryRoot otherRoot : imported) {
			closure.addAll(importClosure(otherRoot));
		}
		return closure;
	}

	/**
	 * Returns the SC theories imported by the given SC theory.
	 * 
	 * @param importer
	 *            the SC theory
	 * @return the list of imported SC theories
	 * @throws CoreException
	 */
	public static Set<IDeployedTheoryRoot> getImportedTheories(ISCTheoryRoot importer) throws CoreException {
		Set<IDeployedTheoryRoot> result = new LinkedHashSet<IDeployedTheoryRoot>();
		if (importer == null || !importer.exists()) {
			return result;
		}
		
		for (ISCImportTheoryProject impProject : importer.getSCImportTheoryProjects()) {
			for (ISCImportTheory impTheory : impProject.getSCImportTheories()) {
				if (impTheory.hasImportTheory()) {
					result.add(impTheory.getImportTheory());
				}
			}
		}
		return result;
	}

	/**
	 * Returns the SC theories imported by the given theory.
	 * 
	 * @param importer
	 *            the theory
	 * @return the list of imported SC theories
	 * @throws CoreException
	 */
	public static Set<ISCTheoryRoot> getImportedTheories(ITheoryRoot importer) throws CoreException {
		final Set<ISCTheoryRoot> result = new LinkedHashSet<ISCTheoryRoot>();

		if (importer == null || !importer.exists()) {
			return result;
		}

		for (IImportTheoryProject impProject : importer.getImportTheoryProjects()) {
			for (IImportTheory impTheory : impProject.getImportTheories()) {
				if (impTheory.hasImportTheory()) {
					result.add(impTheory.getImportTheory());
				}
			}
		}
		return result;
	}

	/**
	 * Returns the deployed theories imported by the given theory.
	 * 
	 * @param theory
	 *            the deployed theory
	 * @return the list of used theories
	 * @throws CoreException
	 */
	public static Set<IDeployedTheoryRoot> getImportedTheories(IDeployedTheoryRoot theory) throws CoreException {
		final Set<IDeployedTheoryRoot> result = new LinkedHashSet<IDeployedTheoryRoot>();
		if (theory == null || !theory.exists()) {
			return result;
		}
		for (IUseTheory use : theory.getUsedTheories()) {
			if (use.hasUseTheory()) {
				result.add(use.getUsedTheory());
			}
		}
		return result;
	}

	/**
	 * Returns the deployed theories imported by the given theory path.
	 * 
	 * @param theoryPath
	 *            the theory path
	 * @return a list of imported theories
	 * @throws RodinDBException
	 */
	public static List<IDeployedTheoryRoot> getTheoryPathImports(
			ISCTheoryPathRoot theoryPath) throws RodinDBException {
		final List<IDeployedTheoryRoot> deployedRoots = new ArrayList<IDeployedTheoryRoot>();

		for (ISCAvailableTheoryProject availProj : theoryPath
				.getSCAvailableTheoryProjects()) {
			for (ISCAvailableTheory availThy : availProj
					.getSCAvailableTheories()) {
				IDeployedTheoryRoot deployedRoot = availThy
						.getSCDeployedTheoryRoot();
				// when availThy is undeployed then deployedTheoryRoot == null
				if (deployedRoot != null) {
					deployedRoots.add(deployedRoot);
				}
			}
		}
		return deployedRoots;
	}

	/**
	 * Returns the importing relationship closure of the given deployed theory
	 * i.e., all theories importing the given theory.
	 * 
	 * @param root
	 *            the deployed theory
	 * @return the importing closure
	 * @throws CoreException
	 */
	public static Set<IDeployedTheoryRoot> importingClosure(IDeployedTheoryRoot root) throws CoreException {
		Set<IDeployedTheoryRoot> set = new LinkedHashSet<IDeployedTheoryRoot>();
		for (IDeployedTheoryRoot otherRoot : DatabaseUtilities.getDeployedTheories(root.getRodinProject())) {
			if (doesTheoryImportTheory(otherRoot, root)) {
				set.add(otherRoot);
			}
		}
		return set;
	}

	/**
	 * Returns the importing relationship closure of the given SC theory i.e.,
	 * all theories importing the given theory.
	 * 
	 * @param root
	 *            the SC theory
	 * @return the importing closure
	 * @throws CoreException
	 */
	public static Set<ISCTheoryRoot> importingClosure(ISCTheoryRoot root) throws CoreException {
		Set<ISCTheoryRoot> set = new LinkedHashSet<ISCTheoryRoot>();
		ISCTheoryRoot[] scTheoryRoots = DatabaseUtilities.getSCTheoryRoots(root.getRodinProject(),
				DatabaseUtilities.getNonTempSCTheoriesFilter());
		for (ISCTheoryRoot otherRoot : scTheoryRoots) {
			if (doesTheoryImportTheory(otherRoot, root)) {
				set.add(otherRoot);
			}
		}
		return set;
	}

	/**
	 * Returns whether deployed theory <code>importer</code> imports deployed
	 * theory <code>importee</code>.
	 * 
	 * @param importer
	 * @param importee
	 * @return whether use relationship exists
	 * @throws CoreException
	 */
	public static boolean doesTheoryImportTheory(IDeployedTheoryRoot importer, IDeployedTheoryRoot importee)
			throws CoreException {
		if (importer == null || !importer.exists()) {
			return false;
		}
		String usedName = importee.getComponentName();
		Set<IDeployedTheoryRoot> theories = getImportedTheories(importer);
		for (IDeployedTheoryRoot theory : theories) {
			if (theory.getComponentName().equals(usedName) || doesTheoryImportTheory(theory, importee)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns whether theory <code>importer</code> imports SC theory
	 * <code>importee</code>.
	 * 
	 * @param importer
	 * @param importee
	 * @return whether import relationship exists
	 * @throws CoreException
	 */
	public static boolean doesTheoryImportTheory(ITheoryRoot importer, ISCTheoryRoot importee) throws CoreException {
		if (importer == null || !importer.exists()) {
			return false;
		}
		String importeeName = importee.getComponentName();
		Set<ISCTheoryRoot> theories = getImportedTheories(importer);
		for (ISCTheoryRoot theory : theories) {
			if (theory.getComponentName().equals(importeeName) || doesTheoryImportTheory(theory, importee)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns whether SC theory <code>importer</code> imports SC theory
	 * <code>importee</code>.
	 * 
	 * @param importer
	 * @param importee
	 * @return whether import relationship exists
	 * @throws CoreException
	 */
	public static boolean doesTheoryImportTheory(ISCTheoryRoot importer, ISCTheoryRoot importee) throws CoreException {
		if (importer == null || !importer.exists()) {
			return false;
		}
		String importeeName = importee.getComponentName();
		Set<IDeployedTheoryRoot> theories = getImportedTheories(importer);
		for (IDeployedTheoryRoot theory : theories) {
			if (theory.getComponentName().equals(importeeName) || doesTheoryImportTheory(theory, importee)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the list of SC theories that can be imported by the given
	 * unchecked theory.
	 * 
	 * <p>
	 * This method essentially avoids redundancy and circularity.
	 * 
	 * @param theory
	 *            the unchecked theory
	 * @return the list of potential imports
	 * @throws CoreException
	 */
	public static ISCTheoryRoot[] getPotentialTheoryImports(ITheoryRoot theory) throws CoreException {
		IRodinProject project = theory.getRodinProject();
		ISCTheoryRoot[] allSCRoots = DatabaseUtilities.getSCTheoryRoots(project,
				DatabaseUtilities.getExistingSCTheoriesFilter());
		Set<ISCTheoryRoot> potentialImports = new LinkedHashSet<ISCTheoryRoot>();
		ISCTheoryRoot theorySC = theory.getSCTheoryRoot();
		for (ISCTheoryRoot root : allSCRoots) {
			// not import oneself and no circularity
			if (!root.getComponentName().equals(theory.getComponentName()) && !doesTheoryImportTheory(root, theorySC)) {
				potentialImports.add(root);
			}
		}
		return potentialImports.toArray(new ISCTheoryRoot[potentialImports.size()]);
	}

	/**
	 * Returns the set of SC theories to deploy based on the given array of
	 * theories.
	 * 
	 * @param roots
	 *            the initial roots
	 * @return the set of SC theories to deploy, this is the initial roots plus
	 *         any required dependencies
	 * @throws CoreException
	 */
	public static Set<ISCTheoryRoot> getAllTheoriesToDeploy(ISCTheoryRoot... roots) throws CoreException {
		Set<ISCTheoryRoot> set = new LinkedHashSet<ISCTheoryRoot>();
		for (ISCTheoryRoot root : roots) {
			Set<IDeployedTheoryRoot> importedTheories = importClosure(root);
			for (IDeployedTheoryRoot importedRoot : importedTheories) {
				// FIXED BUG check if the outdated attribute exists
				if (!importedRoot.exists() || (importedRoot.exists() && 
						importedRoot.hasOutdatedAttribute() && importedRoot.isOutdated())) {
					set.add(importedRoot);
				}
			}
			set.add(root);
		}
		return set;
	}

	/**
	 * Returns the set of theories to undeploy based on the given array.
	 * 
	 * @param roots
	 *            the initial array of theories to undeploy
	 * @return the complete set of theories to undeploy
	 */
	public static Set<IDeployedTheoryRoot> getAllTheoriesToUndeploy(IDeployedTheoryRoot... roots) throws CoreException {
		Set<IDeployedTheoryRoot> set = new LinkedHashSet<IDeployedTheoryRoot>();
		for (IDeployedTheoryRoot root : roots) {
			set.addAll(importingClosure(root));
			set.add(root);
		}
		return set;
	}

	/**
	 * Returns a comparator of SC theories based on the import relationship that
	 * exists between theories.
	 * 
	 * <p>
	 * A theory A is greater than theory B iff A imports B.
	 * 
	 * @return a theories dependency comparator
	 */
	public static Comparator<ISCTheoryRoot> getSCTheoryDependencyComparator() {
		return new Comparator<ISCTheoryRoot>() {

			@Override
			public int compare(ISCTheoryRoot root1, ISCTheoryRoot root2) {
				try {
					if (doesTheoryImportTheory(root1, root2)) {
						return 1;
					} else if (doesTheoryImportTheory(root2, root1)) {
						return -1;
					} else if (root1.getComponentName().equals(root2.getComponentName())) {
						return 0;
					}
				} catch (CoreException e) {
					CoreUtilities
							.log(e,
									"Error comparing theories " + root1.getComponentName() + " and "
											+ root2.getComponentName());
				}
				// FIXME wrong !!! Do not use this comparator anymore
				// check what is safer to return
				return 1;
			}
		};
	}

	public static ITheoryDeployer getDeployer(IRodinProject project, Set<ISCTheoryRoot> theories){
		return new TheoryDeployer(project, theories);
	}
	
	public static IWorkspaceRunnable getUndeployer(final IRodinProject project, final Set<IDeployedTheoryRoot> theories){
		return new IWorkspaceRunnable() {
			
			@Override
			public void run(IProgressMonitor monitor) throws CoreException {
				monitor.beginTask("undeploying theories", 3*theories.size());
				for (IDeployedTheoryRoot root : theories){
					if (root.exists()) {
						root.getRodinFile().delete(true, monitor);
						/* populate (from Rodin2.8):
						 * traversing the workspace, each project containing a theorypath/theory which imports the deploying theory will be rebuild
						 */
						monitor.subTask("populating");
						DeployUtilities.TraversAndPopulateDeploy(monitor, root);
					}
					monitor.worked(3);
				}	
			}
		};
	}
}
