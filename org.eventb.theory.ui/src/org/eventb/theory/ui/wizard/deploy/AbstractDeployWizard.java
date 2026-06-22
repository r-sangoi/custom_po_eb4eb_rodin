package org.eventb.theory.ui.wizard.deploy;

import java.util.Set;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.Wizard;
import org.eventb.theory.core.IDeploymentResult;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.ITheoryDeployer;
import org.eventb.theory.core.TheoryHierarchyHelper;
import org.eventb.theory.internal.ui.Messages;
import org.eventb.theory.internal.ui.TheoryUIUtils;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;

public abstract class AbstractDeployWizard extends Wizard{

	@Override
	public boolean performFinish() {
		IRodinProject rodinProject = getRodinProject();
		ITheoryDeployer deployer = TheoryHierarchyHelper.getDeployer(rodinProject, selectedTheories());
		//TheoryUIUtils.runWithProgress(deployer, rodinProject.getSchedulingRule());
		TheoryUIUtils.runWithProgress(deployer, RodinCore.getRodinDB().getSchedulingRule());
		IDeploymentResult deploymentResult = deployer.getDeploymentResult();
		// if successful
		if (deploymentResult.succeeded()){
			// we only rebuild if successful
/*			if (rebuildProject()){
				try {
					DatabaseUtilities.rebuild(rodinProject, null);
				} catch (CoreException e) {
					TheoryUIUtils.log(e, "when rebuilding project "+rodinProject);
				}
			}*/
			MessageDialog.openInformation(getShell(), getWindowTitle(), Messages.wizard_deploySuccess);
			return true;
		}
		// else issue an appropriate error
		MessageDialog.openError(getShell(), getWindowTitle(), deploymentResult.getErrorMessage());
		return false;
	}
	
	public abstract void addPages();
	
	public abstract Set<ISCTheoryRoot> selectedTheories();
	
	//public abstract boolean rebuildProject();
	
	public abstract IRodinProject getRodinProject();

}
