package org.eventb.theory.ui.wizard.deploy;

import java.util.Set;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.internal.ui.Messages;
import org.rodinp.core.IRodinProject;

public class SimpleDeployWizard extends AbstractDeployWizard{

	private IRodinProject project;
	private Set<ISCTheoryRoot> theoryRoots;
	
	private DeployWizardPageTwo page;
	
	public SimpleDeployWizard(IRodinProject project, Set<ISCTheoryRoot> theoryRoots) {
		setWindowTitle(Messages.wizard_deployTitle);
		this.project = project;
		this.theoryRoots = theoryRoots;
	}
	
	@Override
	public Set<ISCTheoryRoot> selectedTheories() {
		return theoryRoots;
	}

/*	@Override
	public boolean rebuildProject() {
		return page.rebuildProject();
	}*/

	@Override
	public IRodinProject getRodinProject() {
		return project;
	}

	@Override
	public void addPages() {
		page = new DeployWizardPageTwo(theoryRoots);
		addPage(page);
	}

	

}
