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
import org.eventb.theory.core.DatabaseUtilitiesTheoryPath;
import org.eventb.theory.core.IAvailableTheory;
import org.eventb.theory.core.IAvailableTheoryProject;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.ISCAvailableTheory;
import org.eventb.theory.core.ISCAvailableTheoryProject;
import org.eventb.theory.core.TheoryAttributes;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.Messages;
import org.eventb.theory.core.sc.TheoryGraphProblem;
import org.eventb.theory.core.sc.states.ITheoryPathTable;
import org.eventb.theory.core.sc.states.TheoryPathAccuracyInfo;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author renatosilva
 *
 */
public class TheoryPathAvailableTheoryModule extends SCProcessorModule {
	
	private final IModuleType<TheoryPathAvailableTheoryModule> MODULE_TYPE = SCCore.getModuleType(TheoryPlugin.PLUGIN_ID + ".availableTheoryModule");
	
	private IAvailableTheoryProject theoryProj;
	private ITheoryPathTable theoryTable;
	private static final String THEORY_NAME = "THYPH";
	private TheoryPathAccuracyInfo accuracyInfo;

	/**
	 * 
	 */
	public TheoryPathAvailableTheoryModule() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void initModule(IRodinElement element,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		super.initModule(element, repository, monitor);
		theoryTable = (ITheoryPathTable) repository.getState(ITheoryPathTable.STATE_TYPE);
		accuracyInfo = (TheoryPathAccuracyInfo) repository.getState(TheoryPathAccuracyInfo.STATE_TYPE);
		repository.setState(theoryTable);
	}
	
	
	@Override
	public void endModule(IRodinElement element, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		accuracyInfo = null;
		theoryTable = null;
		super.endModule(element, repository, monitor);
	}

	/* (non-Javadoc)
	 * @see org.eventb.internal.core.tool.types.ISCProcessorModule#process(org.rodinp.core.IRodinElement, org.rodinp.core.IInternalElement, org.eventb.core.sc.state.ISCStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void process(IRodinElement element, IInternalElement target,
			ISCStateRepository repository, IProgressMonitor monitor)
					throws CoreException {
		theoryProj = (IAvailableTheoryProject) element;
		IAvailableTheory[] theories = theoryProj.getTheories();
		boolean valid = false;

		monitor.subTask(Messages.bind(Messages.progress_TheoryPathTheories));
		initFilterModules(repository, monitor);

		int index = 0;
		for(IAvailableTheory theory: theories){
			if(!theory.hasAvailableTheory()){
				createProblemMarker(theory,
						TheoryAttributes.AVAILABLE_THEORY_ATTRIBUTE,
						TheoryGraphProblem.NoTheoryClausesError);
			} else if(filterModules(theory, repository, monitor)){
				IDeployedTheoryRoot conflictingTheory = theoryTable.addTheory(theory);
				if(conflictingTheory==null){
					valid = true;
					saveSCTheory((ISCAvailableTheoryProject) target, theory, index++, monitor);
				}
				else {
					createProblemMarker(theory,
							TheoryAttributes.AVAILABLE_THEORY_ATTRIBUTE,
							TheoryGraphProblem.TheoriesConflictError,
							theory.getDeployedTheory().getComponentName(), DatabaseUtilitiesTheoryPath.getFullDescriptionAvailableTheory(conflictingTheory.getRodinProject(), conflictingTheory));
				}
			}
			monitor.worked(1);
		}
		
		if(!valid){
			accuracyInfo.setNotAccurate();
		}

		monitor.done();

	}
	
	private void saveSCTheory(ISCAvailableTheoryProject target,
			IAvailableTheory theory, int index,
			IProgressMonitor monitor) throws RodinDBException {
		ISCAvailableTheory scTheory = target.getSCAvailableTheory(THEORY_NAME+"_"+index);
		scTheory.create(null, monitor);
		scTheory.setSCTheory(theory, monitor); 
		scTheory.setSource(theory, monitor);
	}

	/* (non-Javadoc)
	 * @see org.eventb.internal.core.tool.types.IModule#getModuleType()
	 */
	@Override
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

}
