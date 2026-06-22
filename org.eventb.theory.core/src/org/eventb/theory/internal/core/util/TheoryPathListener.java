package org.eventb.theory.internal.core.util;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.jobs.Job;
import org.eventb.theory.core.IAvailableTheory;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.IImportTheory;
import org.eventb.theory.core.IImportTheoryProject;
import org.eventb.theory.core.ITheoryPathRoot;
import org.eventb.theory.core.ITheoryRoot;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IElementChangedListener;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

public class TheoryPathListener implements IElementChangedListener {
	
	@Override
	public void elementChanged(ElementChangedEvent event) {
		final IRodinElementDelta delta = event.getDelta();
		final IRodinElementDelta[] affectedProjects = delta
				.getAffectedChildren();
		for (IRodinElementDelta project : affectedProjects) {
			final Set<IProject> toBuild = new HashSet<IProject>();
			for (IRodinElementDelta addedChild : project.getAddedChildren()) {
				addTheoryPathProjects(addedChild.getElement(), toBuild);
				addTheoryProject(addedChild.getElement(), toBuild);
			}
			for (IRodinElementDelta removedChild : project
					.getRemovedChildren()) {
				addTheoryPathProjects(removedChild.getElement(), toBuild);
				addTheoryProject(removedChild.getElement(), toBuild);
			}
			if (toBuild.isEmpty()) {
				return;
			}

			final Job cleanBuildJob = new TheoryPathCleanBuilder(toBuild);
			cleanBuildJob.setRule(RodinCore.getRodinDB().getSchedulingRule());
			cleanBuildJob.schedule();
		}
	}

	/*
	 * when the added/deleted file is a theorypath
	 */
	private static void addTheoryPathProjects(IRodinElement element,
			Set<IProject> toBuild) {
		if (element instanceof IRodinFile) {
			final IRodinFile file = (IRodinFile) element;
			if (file.getRootElementType().equals(
					ITheoryPathRoot.ELEMENT_TYPE)) {
				toBuild.add(file.getRodinProject().getProject());
			}
		}
	}

	/*
	 * 
	 * when the added/deleted file is a theory
	 * Traverse the projects in the workspace and rebuild the project containing a theorypath/theory which imports added/deleted theory
	 * 
	 */
	private void addTheoryProject(IRodinElement element, Set<IProject> toBuild) {
		if (element instanceof IRodinFile) {
			final IRodinFile file = (IRodinFile) element;
			if (file.getRootElementType().equals(
					IDeployedTheoryRoot.ELEMENT_TYPE)) {
				
				try {
					for (IRodinProject project : RodinCore.getRodinDB().getRodinProjects()){
						boolean breakFlag = true;
						
						ITheoryPathRoot[] theoryPath = project.getRootElementsOfType(ITheoryPathRoot.ELEMENT_TYPE);
						if (theoryPath.length != 0 && theoryPath[0].getRodinFile().exists()) {
							for (IAvailableTheory availThy : theoryPath[0].getAvailableTheories()){
								if (availThy.hasAvailableTheory() && availThy.getDeployedTheory().equals(((IRodinFile) element).getRoot()) && theoryPath[0].getSCTheoryPathRoot().getRodinFile().exists()) {
									//theoryPath[0].getSCTheoryPathRoot().getRodinFile().delete(true, monitor);
									toBuild.add(project.getProject());
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
												if (importThy.getImportTheory().equals(((IRodinFile) element).getRoot())) {
													toBuild.add(project.getProject());
												}

											}
										}
									}
								}
							}
						}
						
					}
				} catch (RodinDBException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}
		}
	}
}