package org.eventb.theory.ui.wizard.deploy;

import java.util.Set;

import org.eclipse.jface.wizard.Wizard;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.TheoryHierarchyHelper;
import org.eventb.theory.internal.ui.Messages;
import org.eventb.theory.internal.ui.TheoryUIUtils;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;

public class UndeployWizard extends Wizard {

	private UndeployWizardPage page;
	private Set<IDeployedTheoryRoot> deployedRoots;
	private IRodinProject project;
	
	public UndeployWizard(IRodinProject project , Set<IDeployedTheoryRoot> deployedRoots) {
		setWindowTitle(Messages.wizard_undeployTitle);
		this.deployedRoots = deployedRoots;
		this.project = project;
	}

	@Override
	public void addPages() {
		page = new UndeployWizardPage(deployedRoots);
		addPage(page);
	}

	@Override
	public boolean performFinish() {
		//TheoryUIUtils.runWithProgress(TheoryHierarchyHelper.getUndeployer(project, deployedRoots), project.getSchedulingRule());
		
		TheoryUIUtils.runWithProgress(TheoryHierarchyHelper.getUndeployer(project, deployedRoots), RodinCore.getRodinDB().getSchedulingRule());
		
		//TheoryUIUtils.runWithProgress(TheoryHierarchyHelper.getUndeployer(project, deployedRoots), ResourcesPlugin.getWorkspace().getRoot().getSchedulingRule());

/*		if (page.rebuildProject()){
			try {
				DatabaseUtilities.rebuild(project, null);
			} catch (CoreException e) {
				TheoryUIUtils.log(e, "when rebuilding project "+project);
			}
		}*/
		return true;
	}

}
