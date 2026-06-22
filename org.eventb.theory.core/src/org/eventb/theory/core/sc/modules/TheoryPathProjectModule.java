/**
 * 
 */
package org.eventb.theory.core.sc.modules;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.SCProcessorModule;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.theory.core.IAvailableTheoryProject;
import org.eventb.theory.core.ISCAvailableTheoryProject;
import org.eventb.theory.core.ISCTheoryPathRoot;
import org.eventb.theory.core.ITheoryPathRoot;
import org.eventb.theory.core.TheoryAttributes;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.Messages;
import org.eventb.theory.core.sc.TheoryGraphProblem;
import org.eventb.theory.core.sc.states.ITheoryPathProjectTable;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

/**
 * @author renatosilva
 *
 */
public class TheoryPathProjectModule extends SCProcessorModule {
	
	private final IModuleType<TheoryPathProjectModule> MODULE_TYPE = SCCore
			.getModuleType(TheoryPlugin.PLUGIN_ID + ".availableTheoryProjectModule"); //$NON-NLS-1$
	
	private ITheoryPathRoot root;
	private ITheoryPathProjectTable projectTable;
//	private static final String THEORY_PROJECT_NAME = "THPRJ";
	
	@Override
	public void initModule(IRodinElement element,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		IRodinFile file = (IRodinFile) element;
		root = (ITheoryPathRoot) file.getRoot();
		projectTable = (ITheoryPathProjectTable) repository.getState(ITheoryPathProjectTable.STATE_TYPE);
		repository.setState(projectTable);
	}
	
	@Override
	public void endModule(IRodinElement element, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		projectTable = null;
		root = null;
		super.endModule(element, repository, monitor);
	}
	
	/**
	 * 
	 */
	public TheoryPathProjectModule() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.eventb.internal.core.tool.types.ISCProcessorModule#process(org.rodinp.core.IRodinElement, org.rodinp.core.IInternalElement, org.eventb.core.sc.state.ISCStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void process(IRodinElement element, IInternalElement target,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		
		IAvailableTheoryProject[] theoryProjects = root.getAvailableTheoryProjects();
		
		if (theoryProjects.length == 0) {
			// commented this out as this should be ok
			//createProblemMarker(root,
					//TheoryGraphProblem.NoTheoryProjectClausesError);
		}
		else {
			monitor.subTask(Messages.bind(Messages.progress_TheoryProjects));
			initFilterModules(repository, monitor);

			int index = 0;
			for(IAvailableTheoryProject theoryProject: theoryProjects){
				if(!theoryProject.hasTheoryProject()){
					createProblemMarker(theoryProject,
							TheoryAttributes.THEORY_PROJECT_ATTRIBUTE,
							TheoryGraphProblem.NoTheoryProjectClausesError);
				}
				else if(filterModules(theoryProject, repository, monitor)){
					ISCAvailableTheoryProject scTheoryProject = saveSCTheoryProject((ISCTheoryPathRoot) target, theoryProject, index++, monitor);
					projectTable.addTheoryProject(theoryProject);
					
					//process Theories for this project
					processTheories(theoryProject, scTheoryProject, repository, monitor);
				}
				monitor.worked(1);
			}
			endFilterModules(repository, monitor);
		}
		
		projectTable.makeImmutable();
		monitor.done();
	}
	
	private void processTheories(IAvailableTheoryProject theoryProject, IInternalElement target, ISCStateRepository repository, IProgressMonitor monitor) throws CoreException{
		initProcessorModules(theoryProject, repository, monitor);
		processModules(theoryProject, target, repository, monitor);
		endProcessorModules(theoryProject, repository, monitor);
	}

	private ISCAvailableTheoryProject saveSCTheoryProject(ISCTheoryPathRoot target,
			IAvailableTheoryProject theoryProject, int index,
			IProgressMonitor monitor) throws RodinDBException {
		ISCAvailableTheoryProject scTheoryProject = target.createChild(ISCAvailableTheoryProject.ELEMENT_TYPE, null, monitor);
//		ISCAvailableTheoryProject scTheoryProject = target.getSCAvailableTheoryProject(THEORY_PROJECT_NAME+"_"+index);
//		scTheoryProject.create(null, monitor);
		scTheoryProject.setSCTheoryProject(theoryProject.getTheoryProject(), monitor); 
		scTheoryProject.setSource(theoryProject, monitor);
		
		return scTheoryProject;
	}

	/* (non-Javadoc)
	 * @see org.eventb.internal.core.tool.types.IModule#getModuleType()
	 */
	@Override
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

}
