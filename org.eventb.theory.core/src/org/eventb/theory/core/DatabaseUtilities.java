/*******************************************************************************
 * Copyright (c) 2010, 2020 University of Southampton and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University of Southampton - initial API and implementation
 *******************************************************************************/
package org.eventb.theory.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eventb.core.IPSStatus;
import org.eventb.core.seqprover.IConfidence;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.util.CoreUtilities;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * Accessibility class for some fields and methods for other plug-ins. Includes
 * mostly Rodin database related facilities.
 * 
 * @since 1.0
 * 
 * @author maamria
 * 
 */
public class DatabaseUtilities {
	
	// As in "theory unchecked file"
	public static final String THEORY_FILE_EXTENSION = "tuf";
	// As in "theory checked file"
	public static final String SC_THEORY_FILE_EXTENSION = "tcf";
	// As in "deployed theory file"
	public static final String DEPLOYED_THEORY_FILE_EXTENSION = "dtf";

	// The theory configuration for the SC and POG
	public static final String THEORY_CONFIGURATION = TheoryPlugin.PLUGIN_ID + ".thy";

	/**
	 * Returns the SC theories that are the children of the given project.
	 * 
	 * @param project
	 *            the rodin project
	 * @param filter
	 *            theories filter to apply
	 * @return SC theories
	 * @throws CoreException
	 */
	public static ISCTheoryRoot[] getSCTheoryRoots(IRodinProject project, ITheoryFilter<ISCTheoryRoot> filter)
			throws CoreException {
		ISCTheoryRoot[] roots = project.getRootElementsOfType(ISCTheoryRoot.ELEMENT_TYPE);
		List<ISCTheoryRoot> okRoots = new ArrayList<ISCTheoryRoot>();
		for (ISCTheoryRoot root : roots) {
			if (filter.filter(root)) {
				okRoots.add(root);
			}
		}
		return okRoots.toArray(new ISCTheoryRoot[okRoots.size()]);
	}

	/**
	 * Returns the SC theory parent of the given element if any.
	 * 
	 * @param element
	 *            the rodin element
	 * @return the parent theory
	 */
	public static ISCTheoryRoot getSCTheoryParent(IRodinElement element) {
		return element.getAncestor(ISCTheoryRoot.ELEMENT_TYPE);
	}

	/**
	 * Returns a set of string representations of the list of the given
	 * elements.
	 * 
	 * @param <E>
	 *            the type of elements
	 * @param elements
	 *            the actual elements
	 * @return the set
	 */
	public static <E extends IInternalElement> Set<String> getElementNames(Collection<E> elements) {
		Set<String> set = new LinkedHashSet<String>();
		for (E e : elements) {
			set.add(e.getElementName());
		}
		return set;
	}

	/**
	 * Returns whether the theory has errors.
	 * 
	 * @param root
	 *            the SC theory root
	 * @return whether the theory is not accurate
	 */
	public static boolean doesTheoryHaveErrors(ISCTheoryRoot root) {
		try {
			if (!root.exists() || (root.exists() && !root.isAccurate())) {
				return true;
			}
		} catch (RodinDBException e) {
			CoreUtilities.log(e, "failed to retrieve details from theory.");
			return true;
		}
		return false;
	}

	/**
	 * Returns whether a theory is deployable i.e., that it is not empty and is
	 * accurate.
	 * 
	 * @param theoryRoot
	 *            the theory
	 * @return whether a theory is deployable
	 */
	public static boolean isTheoryDeployable(ITheoryRoot theoryRoot) {
		ISCTheoryRoot scRoot = theoryRoot.getSCTheoryRoot();
		return isTheoryDeployable(scRoot);
	}

	/**
	 * Returns whether a theory is deployable i.e., that it is not empty and is
	 * accurate.
	 * 
	 * @param scRoot
	 *            the theory
	 * @return whether a theory is deployable
	 */
	public static boolean isTheoryDeployable(ISCTheoryRoot scRoot) {
		return scRoot.exists() && !doesTheoryHaveErrors(scRoot);
	}

	/**
	 * Returns a handle to the SC theory with the given name.
	 * 
	 * @param name
	 * @param project
	 * @return a handle to the SC theory
	 */
	public static ISCTheoryRoot getSCTheory(String name, IRodinProject project) {
		IRodinFile file = project.getRodinFile(getSCTheoryFullName(name));
		return (ISCTheoryRoot) file.getRoot();
	}

	/**
	 * Returns a handle to the theory with the given name.
	 * 
	 * @param name
	 * @param project
	 * @return a handle to the theory
	 */
	public static ITheoryRoot getTheory(String name, IRodinProject project) {
		IRodinFile file = project.getRodinFile(getTheoryFullName(name));
		return (ITheoryRoot) file.getRoot();
	}

	/**
	 * Returns the full name of a theory file.
	 * 
	 * @param bareName
	 *            the name
	 * @return the full name
	 */
	public static String getTheoryFullName(String bareName) {
		return bareName + "." + THEORY_FILE_EXTENSION;
	}

	/**
	 * Returns the full name of a SC theory file.
	 * 
	 * @param bareName
	 *            the name
	 * @return the full name
	 */
	public static String getSCTheoryFullName(String bareName) {
		return bareName + "." + SC_THEORY_FILE_EXTENSION;
	}

	/**
	 * Returns the project with the given name if it exists.
	 * 
	 * @param name
	 *            the project name
	 * @return the project or <code>null</code> if the project does not exist
	 */
	public static IRodinProject getRodinProject(String name) {
		IRodinProject project = RodinCore.getRodinDB().getRodinProject(name);
		if (project.exists())
			return project;
		else
			return null;
	}

	/**
	 * Returns whether the given rodin file originated from a theory file.
	 * 
	 * @param file
	 *            the rodin file
	 * @return whether the file originated from a theory
	 * @throws CoreException
	 */
	public static boolean originatedFromTheory(IRodinFile file) {
		IRodinProject project = file.getRodinProject();
		ITheoryRoot theoryRoot = getTheory(file.getBareName(), project);
		if (theoryRoot.exists()) {
			return true;
		}
		return false;
	}

	/**
	 * Returns whether the file originated from a theory within the given rodin
	 * project.
	 * 
	 * @param file
	 *            the rodin file
	 * @param project
	 *            the rodin project
	 * @return whether the file originated from a theory within the given rodin
	 *         project
	 */
	public static boolean originatedFromTheory(IRodinFile file, IRodinProject project) {
		return file.getRodinProject().equals(project) && originatedFromTheory(file);
	}

	/**
	 * Rebuilds the given rodin project.
	 * 
	 * @param project
	 *            the rodin project
	 * @param monitor
	 *            the progress monitor
	 * @throws CoreException
	 */
	public static void rebuild(final IRodinProject project, IProgressMonitor monitor) throws CoreException {
		if (project == null || !project.exists()) {
			throw new CoreException(new Status(IStatus.ERROR, TheoryPlugin.PLUGIN_ID,
					"attempting to rebuild a non-existent project"));
		}
		RodinCore.run(new IWorkspaceRunnable() {
			@Override
			public void run(IProgressMonitor monitor) throws CoreException {
				IProject nativeProj = ((IProject) project.getAdapter(IProject.class));
				if (nativeProj != null) {
					nativeProj.build(IncrementalProjectBuilder.CLEAN_BUILD, monitor);
				}
			}
		}, monitor);

	}

	/**
	 * Returns the deployed theories that are the children of the given project.
	 * 
	 * @param project
	 *            the rodin project
	 * @param filter
	 *            theories filter
	 * @return deployed theories
	 * @throws CoreException
	 */
	public static IDeployedTheoryRoot[] getDeployedTheories(IRodinProject project,
			ITheoryFilter<IDeployedTheoryRoot> filter) throws CoreException {
		IDeployedTheoryRoot[] roots = project.getRootElementsOfType(IDeployedTheoryRoot.ELEMENT_TYPE);
		List<IDeployedTheoryRoot> okRoots = new ArrayList<IDeployedTheoryRoot>();
		for (IDeployedTheoryRoot root : roots) {
			if (filter.filter(root)) {
				okRoots.add(root);
			}
		}
		return okRoots.toArray(new IDeployedTheoryRoot[okRoots.size()]);
	}

	/**
	 * Returns the list of SC theories that can be deployed i.e., theories that
	 * are accurate and not empty.
	 * 
	 * @return list of deployable SC theories
	 * @throws CoreException
	 */
	public static List<ISCTheoryRoot> getDeployableSCTheories(IRodinProject project) throws CoreException {
		if (!project.exists()) {
			return new ArrayList<ISCTheoryRoot>();
		}
		ISCTheoryRoot[] roots = project.getRootElementsOfType(ISCTheoryRoot.ELEMENT_TYPE);
		List<ISCTheoryRoot> list = new ArrayList<ISCTheoryRoot>();
		for (ISCTheoryRoot scRoot : roots) {
			if (isTheoryDeployable(scRoot)) {
				list.add(scRoot);
			}
		}
		return list;
	}

	/**
	 * Returns the deployed theories that are the children of the given project.
	 * 
	 * @param project
	 *            the rodin project
	 * @return deployed theories
	 * @throws CoreException
	 */
	public static IDeployedTheoryRoot[] getDeployedTheories(IRodinProject project) throws CoreException {
		return getDeployedTheories(project, getExistingDeployedTheoriesFilter());
	}

	/**
	 * Returns a handle to the deployed theory with the given name.
	 * 
	 * @param name
	 * @param project
	 * @return a handle to the deployed theory
	 */
	public static IDeployedTheoryRoot getDeployedTheory(String name, IRodinProject project) {
		IRodinFile file = project.getRodinFile(getDeployedTheoryFullName(name));
		return (IDeployedTheoryRoot) file.getRoot();
	}

	/**
	 * Returns the full name of a deployed theory file.
	 * 
	 * @param name
	 *            the name
	 * @return the full name
	 */
	public static String getDeployedTheoryFullName(String name) {
		return name + "." + DEPLOYED_THEORY_FILE_EXTENSION;
	}


	/**
	 * Returns whether the given proof status is of discharged status.
	 * 
	 * @param status
	 *            proof status
	 * @return whether status PO has been discharged
	 * @throws RodinDBException
	 */
	public static boolean isDischarged(IPSStatus status) throws RodinDBException {
		return (status.getConfidence() > IConfidence.REVIEWED_MAX) && (!status.isBroken());
	}

	/**
	 * Returns whether the given proof status is of reviewed status.
	 * 
	 * @param status
	 *            proof status
	 * @return whether status PO has been reviewed
	 * @throws RodinDBException
	 */
	public static boolean isReviewed(IPSStatus status) throws RodinDBException {
		return (status.getConfidence() > IConfidence.PENDING) && (status.getConfidence() <= IConfidence.REVIEWED_MAX);
	}

	/**
	 * Returns non temporary statically checked theory path roots of the given
	 * project.
	 * 
	 * @param project
	 *            a Rodin project
	 * @return an array of theory path roots
	 * @throws RodinDBException
	 *             if there is a problem accessing the Rodin database
	 */
	public static ISCTheoryPathRoot[] getNonTempSCTheoryPaths(
			IRodinProject project) throws RodinDBException {
		final ISCTheoryPathRoot[] allRoots = project
				.getRootElementsOfType(ISCTheoryPathRoot.ELEMENT_TYPE);
		final List<ISCTheoryPathRoot> result = new ArrayList<ISCTheoryPathRoot>();
		for (ISCTheoryPathRoot root : allRoots) {
			final IPath path = root.getRodinFile().getPath();
			final String fileExtension = path.getFileExtension();
			if (fileExtension != null && !fileExtension.endsWith("_tmp")) {
				result.add(root);
			}
		}
		return result.toArray(new ISCTheoryPathRoot[result.size()]);
	}
	
	/**
	 * Returns a filter that accepts existing SC theory file with the ordinary
	 * file extension.
	 * 
	 * @return a theory filter
	 */
	public static ITheoryFilter<ISCTheoryRoot> getNonTempSCTheoriesFilter() {

		return new ITheoryFilter<ISCTheoryRoot>() {
			@Override
			public boolean filter(ISCTheoryRoot theory) {
				return theory.exists()
						&& !theory.getRodinFile().getElementName().endsWith(SC_THEORY_FILE_EXTENSION + "_tmp");
			}
		};
	}

	/**
	 * Returns a filter that accepts existing SC theories.
	 * 
	 * @return a theory filter
	 */
	public static ITheoryFilter<ISCTheoryRoot> getExistingSCTheoriesFilter() {

		return new ITheoryFilter<ISCTheoryRoot>() {
			@Override
			public boolean filter(ISCTheoryRoot theory) {
				return theory.exists();
			}
		};
	}

	/**
	 * Returns a filter that accepts existing deployed theories.
	 * 
	 * @return a theory filter
	 */
	public static ITheoryFilter<IDeployedTheoryRoot> getExistingDeployedTheoriesFilter() {

		return new ITheoryFilter<IDeployedTheoryRoot>() {
			@Override
			public boolean filter(IDeployedTheoryRoot theory) {
				return theory.exists();
			}
		};
	}

}
