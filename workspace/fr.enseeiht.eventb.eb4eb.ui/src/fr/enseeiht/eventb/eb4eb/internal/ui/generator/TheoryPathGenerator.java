package fr.enseeiht.eventb.eb4eb.internal.ui.generator;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ISCMachineRoot;
import org.eventb.theory.core.DatabaseUtilities;
import org.eventb.theory.core.IAvailableTheory;
import org.eventb.theory.core.IAvailableTheoryProject;
import org.eventb.theory.core.ISCAvailableTheory;
import org.eventb.theory.core.ISCAvailableTheoryProject;
import org.eventb.theory.core.ISCTheoryPathRoot;
import org.eventb.theory.core.ITheoryPathRoot;
import org.rodinp.core.RodinDBException;

import fr.enseeiht.eventb.eb4eb.ui.EB4EBUIPlugin;

public class TheoryPathGenerator {

	private ITheoryPathRoot root;
	
	public TheoryPathGenerator(ITheoryPathRoot root) {
		this.root = root;
	}
	
	public void generateTheoryPath(ISCMachineRoot mch, IProgressMonitor pMonitor) throws RodinDBException {
		
		// Generate the theory path containing the EB4EB Core Theory
		IAvailableTheoryProject theoryProject = this.root.createChild(IAvailableTheoryProject.ELEMENT_TYPE, null, pMonitor);
		theoryProject.setTheoryProject(DatabaseUtilities.getRodinProject(EB4EBUIPlugin.THEORY_CORE_PROJECT_NAME), pMonitor);
		
		IAvailableTheory availableTheory = theoryProject.createChild(IAvailableTheory.ELEMENT_TYPE, null, pMonitor);
		availableTheory.setAvailableTheory(DatabaseUtilities.getDeployedTheory(EB4EBUIPlugin.THEORY_CORE_FILE_NAME, DatabaseUtilities.getRodinProject(EB4EBUIPlugin.THEORY_CORE_PROJECT_NAME)), pMonitor);
		//availableTheory.setLabel("test", monitor); TODO : erreur
		
		// Copy the theory path from the target machine project
		for (ISCTheoryPathRoot mchTheoryPath : DatabaseUtilities.getNonTempSCTheoryPaths(mch.getRodinProject())) {
			for (ISCAvailableTheoryProject mchTheoryProject : mchTheoryPath.getSCAvailableTheoryProjects()) {
				IAvailableTheoryProject copyMchTheoryProject = this.root.createChild(IAvailableTheoryProject.ELEMENT_TYPE, null, pMonitor);
				copyMchTheoryProject.setTheoryProject(mchTheoryProject.getSCAvailableTheoryProject(), pMonitor);
				for (ISCAvailableTheory mchAvailableTheory : mchTheoryProject.getSCAvailableTheories()) {
					IAvailableTheory copyMchAvailableTheory = copyMchTheoryProject.createChild(IAvailableTheory.ELEMENT_TYPE, null, pMonitor);
					copyMchAvailableTheory.setAvailableTheory(mchAvailableTheory.getSCDeployedTheoryRoot(), pMonitor);
				}
			}
		}
		
	}
	
}
