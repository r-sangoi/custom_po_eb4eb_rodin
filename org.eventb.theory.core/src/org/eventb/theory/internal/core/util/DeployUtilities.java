/*******************************************************************************
 * Copyright (c) 2010, 2020 University of Southampton and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.internal.core.util;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPSRoot;
import org.eventb.core.IPSStatus;
import org.eventb.theory.core.DatabaseUtilities;
import org.eventb.theory.core.IAvailableTheory;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.IImportTheory;
import org.eventb.theory.core.IImportTheoryProject;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.ITheoryPathRoot;
import org.eventb.theory.core.ITheoryRoot;
import org.eventb.theory.core.util.CoreUtilities;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IAttributeValue;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * @since 1.0
 * @author maamria
 * 
 */
public class DeployUtilities {

	/**
	 * Duplicates the source element as a child of the new parent element. It
	 * copies all of the source details ignoring the given attributes.
	 * 
	 * @param <E>
	 *            the type of the source
	 * @param source
	 *            the source element
	 * @param type
	 *            the type
	 * @param newParent
	 *            the new parent element
	 * @param monitor
	 *            the progress monitor
	 * @param toIgnore
	 *            the attribute types to ignore when copying
	 * @return the new element that is a copy of the source and has the new
	 *         parent
	 * @throws CoreException
	 */
	public static final <E extends IInternalElement> E duplicate(E source,
			IInternalElementType<E> type, IInternalElement newParent,
			IProgressMonitor monitor, IAttributeType... toIgnore)
			throws CoreException {
		assert source.exists();
		assert newParent.exists();
		List<IAttributeType> toIgnoreList = Arrays.asList(toIgnore);
		IAttributeType[] attrTypes = source.getAttributeTypes();
		E newElement = newParent.getInternalElement(type,
				source.getElementName());
		newElement.create(null, monitor);
		for (IAttributeType attr : attrTypes) {
			if (toIgnoreList.contains(attr)) {
				continue;
			}
			IAttributeValue value = source.getAttributeValue(attr);
			newElement.setAttributeValue(value, monitor);
		}

		return newElement;
	}

	/**
	 * Calculates the soundness of the given labelled element.
	 * 
	 * TODO not used at the moment
	 * 
	 * @param root
	 *            the parent SC theory
	 * @param element
	 *            the labelled element
	 * @throws RodinDBException
	 */
	public static boolean calculateSoundness(ISCTheoryRoot root,
			IInternalElement element) throws RodinDBException {
		IPSRoot psRoot = root.getPSRoot();
		if (psRoot == null || !psRoot.exists()) {
			return false;
		}
		IPSStatus[] sts = psRoot.getStatuses();
		boolean isSound = true;
		for (IPSStatus s : sts) {
			if (s.getElementName().startsWith(element.getElementName())) {
				if (!DatabaseUtilities.isDischarged(s)
						&& !DatabaseUtilities.isReviewed(s)) {
					isSound = false;
				}
			}
		}
		return isSound;

	}
	
	public static boolean copyDeployedElements(IDeployedTheoryRoot target,
			ISCTheoryRoot source, IProgressMonitor monitor) throws CoreException{
		DeployElementRegistry registry = DeployElementRegistry.getDeployedElementsRegistry();

		for(IInternalElementType<IInternalElement> elementType: registry.getRootDeployedElements()){
			for(IInternalElement element: source.getChildrenOfType(elementType)){
				IInternalElement duplicatedElement = duplicate(element, elementType, target, monitor);
				copyDeployedElementChildren(element, duplicatedElement, elementType, registry, monitor);
			}
		}
		return true;
	}
	
	private static boolean copyDeployedElementChildren(IInternalElement source, IInternalElement parent, IInternalElementType<IInternalElement> parentElementType,
			DeployElementRegistry registry,  IProgressMonitor monitor) throws CoreException{

		for(IInternalElementType<IInternalElement> internalElementChildType: registry.getDeployedElementChildren(parentElementType)){
			for(IInternalElement childElement: source.getChildrenOfType(internalElementChildType)){
				IInternalElement childDuplicate = duplicate(childElement, internalElementChildType, parent, monitor);
				if(registry.hasDeployedElementChildren(internalElementChildType))
					copyDeployedElementChildren(childElement, childDuplicate, internalElementChildType, registry, monitor);
			}
		}
		return true;
	}
	

	/**
	 * Travers the projects in the workspace and rebuild the project containing a theorypath/theory which imports deployTheoryRoot (deploying theory)
	 * 
	 * @param monitor
	 * @param deployedTheoryRoot
	 * @throws RodinDBException
	 */
	public static void TraversAndPopulateDeploy(IProgressMonitor monitor,
			IDeployedTheoryRoot deployedTheoryRoot) throws RodinDBException {
		for (IRodinProject project : RodinCore.getRodinDB().getRodinProjects()){
			monitor.worked(2);
			boolean breakFlag = true;
			
			ITheoryPathRoot[] theoryPath = project.getRootElementsOfType(ITheoryPathRoot.ELEMENT_TYPE);
			if (theoryPath.length != 0 && theoryPath[0].getRodinFile().exists()) {
				for (IAvailableTheory availThy : theoryPath[0].getAvailableTheories()){
					if (availThy.hasAvailableTheory() && availThy.getDeployedTheory().equals(deployedTheoryRoot) && theoryPath[0].getSCTheoryPathRoot().getRodinFile().exists()) {
						//theoryPath[0].getSCTheoryPathRoot().getRodinFile().delete(true, monitor);
						buildProject(monitor, project);
						breakFlag = false;
						break;
					}		
				}
			}
			//to avoid building a single project more than one time
			if (breakFlag) {
				ITheoryRoot[] theoryRoots = project
						.getRootElementsOfType(ITheoryRoot.ELEMENT_TYPE);
				if (theoryRoots.length != 0) {
					for (ITheoryRoot thy : theoryRoots) {
						if (thy.getRodinFile().exists()) {
							for (IImportTheoryProject importThyPrj : thy
									.getImportTheoryProjects()) {
								for (IImportTheory importThy : importThyPrj
										.getImportTheories()) {
									if (importThy.getImportTheory().equals(
											deployedTheoryRoot)) {
										buildProject(monitor, project);
									}

								}
							}
						}
					}
				}
			}
			
		}
	}

	/**
	 * celan and build the project
	 * 
	 * @param monitor
	 * @param project
	 */
	private static void buildProject(IProgressMonitor monitor,
			IRodinProject project) {
		try {
			project.getProject()
					.build(IncrementalProjectBuilder.CLEAN_BUILD,
							monitor);
		} catch (CoreException e) {
			CoreUtilities
					.log(e,
							"when trying to clean/build project "
									+ project
											.getProject()
											.getName()
									+ "after deploying theory ");
		}
	}
	
}
