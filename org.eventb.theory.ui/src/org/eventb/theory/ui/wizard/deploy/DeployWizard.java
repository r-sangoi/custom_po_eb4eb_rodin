package org.eventb.theory.ui.wizard.deploy;

import java.util.Set;

import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.internal.ui.Messages;
import org.rodinp.core.IRodinProject;

public class DeployWizard extends AbstractDeployWizard {

	private DeployWizardPageOne pageOne;
	private DeployWizardPageTwo pageTwo;

	public DeployWizard() {
		setWindowTitle(Messages.wizard_deployTitle);
	}

	@Override
	public void addPages() {
		pageOne = new DeployWizardPageOne();
		addPage(pageOne);
		pageTwo = new DeployWizardPageTwo();
		addPage(pageTwo);
	}

	public Set<ISCTheoryRoot> selectedTheories() {
		return pageOne.getSelectedTheories();
	}

/*	public boolean rebuildProject() {
		return pageTwo.rebuildProject();
	}*/

	@Override
	public boolean canFinish() {
		// only finish on second page
		if (getContainer().getCurrentPage() == pageOne)
			return false;
		else
			return true;
	}

	@Override
	public IRodinProject getRodinProject() {
		return pageOne.getRodinProject();
	}

}
