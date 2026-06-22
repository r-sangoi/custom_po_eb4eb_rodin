/*******************************************************************************
 * Copyright (c) 2012, 2020 University of Southampton and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core;

import java.util.List;

import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eventb.core.EventBAttributes;
import org.eventb.core.IEventBRoot;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.Messages;
import org.eventb.theory.core.util.CoreUtilities;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IElementChangedListener;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;

/**
 * @author RenatoSilva
 *
 */
public class DatabaseUtilitiesTheoryPath{
	
	// As in "theory path unchecked language"
	public static final String THEORY_PATH_FILE_EXTENSION = "tul";
	// As in "theory path checked language"
	public static final String SC_THEORY_PATH_FILE_EXTENSION = "tcl";
	
	// The theory path configuration for the SC and POG
	public static final String THEORY_PATH_CONFIGURATION = TheoryPlugin.PLUGIN_ID + ".tul";
	
	/**
	 * Returns the full name of a theory path file.
	 * 
	 * @param name
	 *            the name
	 * @return the full name
	 */
	public static String getTheoryPathFullName(String name) {
		return name + "." + THEORY_PATH_FILE_EXTENSION;
	}
	
	/**
	 * Returns the full name of a theory path file.
	 * 
	 * @param name
	 *            the name
	 * @return the full name
	 */
	public static String getSCTheoryPathFullName(String name) {
		return name + "." + SC_THEORY_PATH_FILE_EXTENSION;
	}
	
	public static String getFullDescriptionAvailableTheory(IRodinProject rodinProject, IDeployedTheoryRoot deployedTheory){
		return "["+rodinProject.getElementName()+"]."+deployedTheory.getComponentName();
	}
	
	/**
	 * Returns a handle to the theory with the given name.
	 * 
	 * @param name
	 * @param project
	 * @return a handle to the theory
	 */
	public static ITheoryPathRoot getTheoryPath(String name, IRodinProject project) {
		IRodinFile file = project.getRodinFile(getTheoryPathFullName(name));
		return (ITheoryPathRoot) file.getRoot();
	}

	// From Rodin 2.8 when the dependency is correctly added to the builder, no listener is needed.
	/**
	 * Adds a listener to the workspace that it is triggered when any of the <code>files</code> are modified. 
	 * The listener action is applied to <code>root</code> for <code>resourceChangeKind</code>.
	 * 
	 * @param files
	 * @param root
	 * @param resourceChangeKind
	 */
	public static <T extends IEventBRoot> void addListener(final List<IRodinFile> files, final T root){
		IElementChangedListener listener = new IElementChangedListener() {
			@Override
			public void elementChanged(ElementChangedEvent event) {
				IRodinElementDelta delta = event.getDelta();
				IRodinElementDelta[] affectedProjects = delta.getAffectedChildren();
				for(IRodinElementDelta affectedProject: affectedProjects){
					//In case the project is deleted or it is closed
					if(affectedProject.getAffectedChildren().length==0){
						for(IRodinFile file: files){
							if(file.getRodinProject().equals(affectedProject.getElement())) {
								notifyDependency(root);
								break;
							}
						}
					}
					else {
						for(IRodinElementDelta affectedFile: affectedProject.getAffectedChildren()){
							if(files.contains(affectedFile.getElement())){
								notifyDependency(root);
								break;
							}
						}
					}
				}
			}
		};
		
		RodinCore.addElementChangedListener(listener, ElementChangedEvent.POST_CHANGE);
	}
	
	/**
	 * Builds the rodinProject specified according to the kind of build defined
	 * @param root
	 * @param builderKind
	 */
	private static void notifyDependency(final IEventBRoot root) {
		IJobManager jobManager = Job.getJobManager();
		ISchedulingRule currentRule = jobManager.currentRule();
		
		jobManager.beginRule(currentRule, null);
		
		Job job = new Job(Messages.progress_notifyTheoryPath) {
			
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					if (root.exists()) {
					root.setAttributeValue(EventBAttributes.COMMENT_ATTRIBUTE,root.hasAttribute(EventBAttributes.COMMENT_ATTRIBUTE) ? root.getAttributeValue(EventBAttributes.COMMENT_ATTRIBUTE): "", monitor);
					root.getRodinFile().save(monitor, true);
					root.getRodinProject().getProject().build(IncrementalProjectBuilder.INCREMENTAL_BUILD, null);
					}
				} catch (CoreException e) {
					CoreUtilities.log(e, "when trying to build project " + root.getElementName() + " after notifying file "+ root.getComponentName());
					return Status.CANCEL_STATUS;
				}
				
				return Status.OK_STATUS;

			}
		};
		
		job.setUser(false);
		job.schedule(); // start as soon as possible
		
		jobManager.endRule(currentRule);
	}

}
