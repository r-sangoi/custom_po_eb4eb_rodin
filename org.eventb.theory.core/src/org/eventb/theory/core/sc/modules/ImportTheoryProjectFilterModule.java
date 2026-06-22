/**
 * 
 */
package org.eventb.theory.core.sc.modules;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.SCFilterModule;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.theory.core.IImportTheoryProject;
import org.eventb.theory.core.TheoryAttributes;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.TheoryGraphProblem;
import org.eventb.theory.core.sc.states.IImportProjectTable;
import org.eventb.theory.core.sc.states.TheoryAccuracyInfo;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author asiehsalehi
 *
 */
public class ImportTheoryProjectFilterModule extends SCFilterModule {
	
	public static final IModuleType<ImportTheoryProjectFilterModule> MODULE_TYPE = SCCore
			.getModuleType(TheoryPlugin.PLUGIN_ID + ".importTheoryProjectFilterModule"); //$NON-NLS-1$
	
	private IImportProjectTable projectTable;
	private TheoryAccuracyInfo accuracyInfo;

	/**
	 * 
	 */
	public ImportTheoryProjectFilterModule() {
	}
	
	@Override
	public void initModule(ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		super.initModule(repository, monitor);
		accuracyInfo = (TheoryAccuracyInfo) repository.getState(TheoryAccuracyInfo.STATE_TYPE);
		projectTable = (IImportProjectTable) repository.getState(IImportProjectTable.STATE_TYPE);
	}
	
	@Override
	public void endModule(ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		projectTable = null;
		accuracyInfo = null;
		super.endModule(repository, monitor);
	}

	/* (non-Javadoc)
	 * @see org.eventb.internal.core.tool.types.ISCFilterModule#accept(org.rodinp.core.IRodinElement, org.eventb.core.sc.state.ISCStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public boolean accept(IRodinElement element, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		IImportTheoryProject importTheoryProjectClause = (IImportTheoryProject) element;
		return validateTheoryProject(importTheoryProjectClause);
	}

	private boolean validateTheoryProject(
			IImportTheoryProject importTheoryProjectClause) throws RodinDBException {
		boolean valid = true;
// removed becuase the local theories can be imported in the theorypath
/*		if (availableTheoryProjectClause.getTheoryProject().equals(availableTheoryProjectClause.getRodinProject())){
			valid = false;
			createProblemMarker(availableTheoryProjectClause, TheoryAttributes.THEORY_PROJECT_ATTRIBUTE,
					TheoryGraphProblem.TheoryPathProjectIsThisProject, availableTheoryProjectClause.getTheoryProject().getElementName());
		}
		
		else*/ if(!importTheoryProjectClause.getTheoryProject().exists()){
			valid = false;
			//theory project does not exist 
			createProblemMarker(importTheoryProjectClause,
					TheoryAttributes.THEORY_PROJECT_ATTRIBUTE,
					TheoryGraphProblem.TheoryProjectDoesNotExistError,
					importTheoryProjectClause.getTheoryProject());
		}
		else if(projectTable.containsTheoryProject(importTheoryProjectClause.getTheoryProject().getElementName())){
			valid = false;
			//duplicated theory project 
			createProblemMarker(importTheoryProjectClause,
					TheoryAttributes.THEORY_PROJECT_ATTRIBUTE,
					TheoryGraphProblem.DuplicatedTheoryProjectError,
					importTheoryProjectClause.getTheoryProject().getElementName());
		}else if(importTheoryProjectClause.getImportTheories().length==0){
			valid = false;
			//theory project does not have any theory 
			createProblemMarker(importTheoryProjectClause,
					TheoryAttributes.THEORY_PROJECT_ATTRIBUTE,
					TheoryGraphProblem.NoSelectedTheoriesError,
					importTheoryProjectClause.getTheoryProject().getElementName());
		}
		if(!valid){
			accuracyInfo.setNotAccurate();
		}
		
		return valid;
	}

	/* (non-Javadoc)
	 * @see org.eventb.internal.core.tool.types.IModule#getModuleType()
	 */
	@Override
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

}
