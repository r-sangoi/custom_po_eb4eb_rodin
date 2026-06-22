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
import org.eventb.theory.core.IAvailableTheoryProject;
import org.eventb.theory.core.TheoryAttributes;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.TheoryGraphProblem;
import org.eventb.theory.core.sc.states.ITheoryPathProjectTable;
import org.eventb.theory.core.sc.states.TheoryPathAccuracyInfo;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author renatosilva
 *
 */
public class TheoryPathProjectFilterModule extends SCFilterModule {
	
	public static final IModuleType<TheoryPathProjectFilterModule> MODULE_TYPE = SCCore
			.getModuleType(TheoryPlugin.PLUGIN_ID + ".availableTheoryProjectFilterModule"); //$NON-NLS-1$
	
	private ITheoryPathProjectTable projectTable;
	private TheoryPathAccuracyInfo accuracyInfo;

	/**
	 * 
	 */
	public TheoryPathProjectFilterModule() {
	}
	
	@Override
	public void initModule(ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		super.initModule(repository, monitor);
		accuracyInfo = (TheoryPathAccuracyInfo) repository.getState(TheoryPathAccuracyInfo.STATE_TYPE);
		projectTable = (ITheoryPathProjectTable) repository.getState(ITheoryPathProjectTable.STATE_TYPE);
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
		IAvailableTheoryProject availableTheoryProjectClause = (IAvailableTheoryProject) element;
		return validateTheoryProject(availableTheoryProjectClause);
	}

	private boolean validateTheoryProject(
			IAvailableTheoryProject availableTheoryProjectClause) throws RodinDBException {
		boolean valid = true;
// removed becuase the local theories can be imported in the theorypath
/*		if (availableTheoryProjectClause.getTheoryProject().equals(availableTheoryProjectClause.getRodinProject())){
			valid = false;
			createProblemMarker(availableTheoryProjectClause, TheoryAttributes.THEORY_PROJECT_ATTRIBUTE,
					TheoryGraphProblem.TheoryPathProjectIsThisProject, availableTheoryProjectClause.getTheoryProject().getElementName());
		}
		
		else*/ if(!availableTheoryProjectClause.getTheoryProject().exists()){
			valid = false;
			//theory project does not exist 
			createProblemMarker(availableTheoryProjectClause,
					TheoryAttributes.THEORY_PROJECT_ATTRIBUTE,
					TheoryGraphProblem.TheoryProjectDoesNotExistError,
					availableTheoryProjectClause.getTheoryProject());
		}
		else if(projectTable.containsTheoryProject(availableTheoryProjectClause.getTheoryProject().getElementName())){
			valid = false;
			//duplicated theory project 
			createProblemMarker(availableTheoryProjectClause,
					TheoryAttributes.THEORY_PROJECT_ATTRIBUTE,
					TheoryGraphProblem.DuplicatedTheoryProjectError,
					availableTheoryProjectClause.getTheoryProject().getElementName());
		}else if(availableTheoryProjectClause.getTheories().length==0){
			valid = false;
			//theory project does not have any theory 
			createProblemMarker(availableTheoryProjectClause,
					TheoryAttributes.THEORY_PROJECT_ATTRIBUTE,
					TheoryGraphProblem.NoSelectedTheoriesError,
					availableTheoryProjectClause.getTheoryProject().getElementName());
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
