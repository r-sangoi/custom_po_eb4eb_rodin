/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.basis;

import static org.eventb.theory.core.TheoryHierarchyHelper.getImportedTheories;
import static org.eventb.theory.internal.core.util.DeployUtilities.copyDeployedElements;

import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.theory.core.DatabaseUtilities;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.IDeploymentResult;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.ITheoryDeployer;
import org.eventb.theory.core.IUseTheory;
import org.eventb.theory.core.TheoryHierarchyHelper;
import org.eventb.theory.core.sc.modules.ModulesUtils;
import org.eventb.theory.internal.core.util.DeployUtilities;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;

/**
 * @author maamria
 * 
 */
public final class TheoryDeployer implements ITheoryDeployer {

	protected Set<ISCTheoryRoot> theoryRoots;
	protected IRodinProject project;

	protected IRodinFile targetFile;
	protected IDeploymentResult deploymentResult;

	public TheoryDeployer(IRodinProject project, Set<ISCTheoryRoot> theoryRoots) {
		this.project = project;
		this.theoryRoots = theoryRoots;
	}

	@Override
	public void run(IProgressMonitor monitor) throws CoreException {
		checkCancellationRequest(monitor);
		try {
			monitor.beginTask("Deploying theories", 10 * theoryRoots.size());
			deploy(monitor);
		} catch (CoreException exception) {
			deploymentResult = new DeploymentResult(false, exception.getMessage());
		}
	}

	@Override
	public synchronized boolean deploy(IProgressMonitor monitor) throws CoreException {
		// need to check if the target project is null
		monitor.subTask("checking project");
		{
			if (project == null) {
				deploymentResult = new DeploymentResult(false, "Deployment project does not exist.");
				return false;
			}
		}
//remove because we do not need to check conflicts in the time of deploy, since a theory is not accessible just by deploying
//conflict is checked when a theory is imported in a theorypath and thus available to be used
		
/*		monitor.subTask("checking conflicts between chosen theories");
		// check for conflicts between theories to be deployed and deployed
		// theories

		// 1- need to check between the supplied theories
		ISCTheoryRoot[] rootsArray = theoryRoots.toArray(new ISCTheoryRoot[theoryRoots.size()]);
		int length = rootsArray.length;
		if (length > 1) {
			Map<ISCTheoryRoot, Set<ISCTheoryRoot>> scConflictMap = new LinkedHashMap<ISCTheoryRoot, Set<ISCTheoryRoot>>();
			for (int i = 0; i < length; i++) {
				ISCTheoryRoot root1 = rootsArray[i];
				SCTheoryDecorator hierarchy1 = new SCTheoryDecorator(root1);
				for (int k = i + 1; k < length; k++) {
					ISCTheoryRoot root2 = rootsArray[k];
					if (hierarchy1.isConflicting(root2)) {
						Set<ISCTheoryRoot> conflictSet = scConflictMap.get(root1);
						if (conflictSet == null) {
							conflictSet = new LinkedHashSet<ISCTheoryRoot>();
							scConflictMap.put(root1, conflictSet);
						}
						conflictSet.add(root2);
					}
				}
			}
			if (!scConflictMap.isEmpty()) {
				StringBuilder sb = new StringBuilder();
				sb.append("The following mathematical extensions conflicts have been found:\n\n");
				for (ISCTheoryRoot root : scConflictMap.keySet()) {
					sb.append("\tTheory " + root.getComponentName() + " conflicts with statically checked theories:");
					for (ISCTheoryRoot cRoot : scConflictMap.get(root)) {
						sb.append(" " + cRoot.getComponentName() + " ");
					}
				}
				deploymentResult = new DeploymentResult(false, sb.toString());
				return false;
			}
		}
		// 2- need to check against deployed theories
		monitor.subTask("checking conflicts with deployed theories");
		Map<ISCTheoryRoot, Set<IDeployedTheoryRoot>> depConflictMap = new LinkedHashMap<ISCTheoryRoot, Set<IDeployedTheoryRoot>>();
		IDeployedTheoryRoot[] deployedTheories = DatabaseUtilities.getDeployedTheories(project);
		if (deployedTheories.length > 0) {
			for (ISCTheoryRoot root : rootsArray) {
				SCTheoryDecorator hierarchy = new SCTheoryDecorator(root);
				for (IDeployedTheoryRoot deployedRoot : deployedTheories) {
					ISCTheoryRoot scVersion = 
							DatabaseUtilities.getSCTheory(deployedRoot.getComponentName(), project);
					if (theoryRoots.contains(scVersion)){
						continue;
					}
					if (hierarchy.isConflicting(deployedRoot)){
						Set<IDeployedTheoryRoot> conflictSet = depConflictMap.get(root);
						if(conflictSet == null){
							conflictSet = new LinkedHashSet<IDeployedTheoryRoot>();
							depConflictMap.put(root, conflictSet);
						}
						conflictSet.add(deployedRoot);
					}
				}
			}
			if (!depConflictMap.isEmpty()){
				StringBuilder sb = new StringBuilder();
				sb.append("The following mathematical extensions conflicts have been found:\n\n");
				for (ISCTheoryRoot root : depConflictMap.keySet()) {
					sb.append("\tTheory " + root.getComponentName() + " conflicts with deployed theories:");
					for (IDeployedTheoryRoot cRoot : depConflictMap.get(root)) {
						sb.append(" " + cRoot.getComponentName() + " ");
					}
				}
				deploymentResult = new DeploymentResult(false, sb.toString());
				return false;
			}
		}*/
		// sort theories according to dependency
		SortedSet<ISCTheoryRoot> sortedTheories = 
				new TreeSet<ISCTheoryRoot>(TheoryHierarchyHelper.getSCTheoryDependencyComparator());
		sortedTheories.addAll(theoryRoots);
		// for each theory
		monitor.subTask("deploying each theory");
		for (ISCTheoryRoot theoryRoot : sortedTheories) {
			monitor.worked(2);
			String theoryName = theoryRoot.getComponentName();
			// create the target file
			monitor.subTask("creating target file " + theoryName);
			targetFile = project.getRodinFile(DatabaseUtilities.getDeployedTheoryFullName(theoryName));
			IDeployedTheoryRoot deployedTheoryRoot = (IDeployedTheoryRoot) targetFile.getRoot();
			if (targetFile.exists()) {
				targetFile.delete(true, monitor);
			}
			targetFile.create(true, monitor);
			if (!deployedTheoryRoot.exists()) {
				deployedTheoryRoot.create(null, monitor);
			}
			// set dependencies
			monitor.worked(3);
			monitor.subTask("setting dependencies for " + targetFile.getElementName());
			setDeployedTheoryDependencies(theoryRoot, deployedTheoryRoot);
			// copy the elements across
			monitor.worked(3);
			boolean accurate = copyDeployedElements(deployedTheoryRoot, theoryRoot, monitor);
			// miscellaneous information (accuracy, comments)
			deployedTheoryRoot.setAccuracy(accurate, monitor);
			deployedTheoryRoot.setComment("GENERATED THEORY FILE: !DO NOT CHANGE!", monitor);
			deployedTheoryRoot.setModificationHashValue(
					ModulesUtils.ComputeHashValue(theoryRoot.getResource()),
					monitor);
			// the default false is needed to make the enableness of the deploy pop up menu works @theoryRootActionProvider.getDeployTheoryAction.isEnabled
			deployedTheoryRoot.setOutdated(false, monitor);
			// save
			targetFile.save(monitor, true);
			deploymentResult = new DeploymentResult(true, null);
			monitor.subTask("finising with " + targetFile.getElementName());
			monitor.worked(2);
			/* populate (from Rodin2.8):
			 * traversing the workspace, each project containing a theorypath/theory which imports the deploying theory will be rebuild
			 */
			monitor.subTask("populating");
			DeployUtilities.TraversAndPopulateDeploy(monitor, deployedTheoryRoot);
		}
		return true;
	}

	@Override
	public synchronized IDeploymentResult getDeploymentResult() {
		return deploymentResult;
	}

	protected void checkCancellationRequest(IProgressMonitor monitor) {
		if (monitor.isCanceled()) {
			// nothing to do at the moment
		}
	}

	protected void setDeployedTheoryDependencies(ISCTheoryRoot source, IDeployedTheoryRoot target) throws CoreException {
		for (ISCTheoryRoot importedRoot : getImportedTheories(source)) {
			IDeployedTheoryRoot deployedCounterpart = importedRoot.getDeployedTheoryRoot();
			if (deployedCounterpart.exists()) {
				IUseTheory use = target.getUsedTheory(deployedCounterpart.getComponentName());
				use.create(null, null);
				use.setUsedTheory(deployedCounterpart, null);
			}
		}
	}

	protected boolean checkDeployedTheoryDependencies(ISCTheoryRoot source) throws CoreException {
		for (ISCTheoryRoot importedRoot : getImportedTheories(source)) {
			if (!checkDeployedTheoryDependencies(importedRoot)) {
				return false;
			}
			IDeployedTheoryRoot deployedCounterpart = importedRoot.getDeployedTheoryRoot();
			if (!deployedCounterpart.exists()) {
				deploymentResult = new DeploymentResult(false, "Failed dependencies : deployed theory '"
						+ deployedCounterpart.getComponentName() + "' does not exist in the project '"
						+ importedRoot.getRodinProject().getElementName() + "'.");
				return false;
			}
			if (deployedCounterpart.hasOutdatedAttribute() && deployedCounterpart.isOutdated()) {
				deploymentResult = new DeploymentResult(false, "Failed dependencies : deployed theory '"
						+ deployedCounterpart.getComponentName() + "' is outdated in the project '"
						+ importedRoot.getRodinProject().getElementName() + "'.");
				return false;
			}
		}
		return true;
	}
}
