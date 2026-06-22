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
import org.eventb.theory.core.IAvailableTheory;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.IUseTheory;
import org.eventb.theory.core.TheoryAttributes;
import org.eventb.theory.core.plugin.TheoryPlugin;
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
public class TheoryPathAvailableTheoryFilterModule extends SCFilterModule {
	
	private final IModuleType<TheoryPathAvailableTheoryFilterModule> MODULE_TYPE = SCCore.getModuleType(TheoryPlugin.PLUGIN_ID + ".availableTheoryFilterModule");
	
	private ITheoryPathTable theoryTable;

	@SuppressWarnings("unused")
	private TheoryPathAccuracyInfo accuracyInfo;

	/**
	 * 
	 */
	public TheoryPathAvailableTheoryFilterModule() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void initModule(ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		super.initModule(repository, monitor);
		accuracyInfo = (TheoryPathAccuracyInfo) repository.getState(TheoryPathAccuracyInfo.STATE_TYPE);
		theoryTable = (ITheoryPathTable) repository.getState(ITheoryPathTable.STATE_TYPE);
	}
	
	@Override
	public void endModule(ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		theoryTable = null;
		accuracyInfo = null;
		super.endModule(repository, monitor);
	}

	/* (non-Javadoc)
	 * @see org.eventb.internal.core.tool.types.IModule#getModuleType()
	 */
	@Override
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	@Override
	public boolean accept(IRodinElement element, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		IAvailableTheory availableTheoryClause = (IAvailableTheory) element;
		return validateTheory(availableTheoryClause);
	}

	private boolean validateTheory(IAvailableTheory availableTheoryClause) throws RodinDBException {
		boolean valid = true;
		IDeployedTheoryRoot newDeployedTheory = availableTheoryClause.getDeployedTheory();
		
		if(theoryTable.containsTheory(availableTheoryClause)){
			valid = false;
			//duplicated theory project 
			createProblemMarker(availableTheoryClause,
					TheoryAttributes.AVAILABLE_THEORY_ATTRIBUTE,
					TheoryGraphProblem.DuplicatedTheoryError,
					newDeployedTheory.getElementName());
		}
		else if(!newDeployedTheory.exists()){
			//deployed theory does not exist 
			createProblemMarker(availableTheoryClause,
					TheoryAttributes.AVAILABLE_THEORY_ATTRIBUTE,
					TheoryGraphProblem.DeployedTheoryNotExistError,
					newDeployedTheory.getElementName());
			return false;
		} 
		
		for(IDeployedTheoryRoot deployedTheory: theoryTable.getAllTheories()){
			//Check if the newDeployedTheories imports do not clash with existing ones
/*			for(IUseTheory usedTheory: newDeployedTheory.getUsedTheories()){
				if(isDependentOf(usedTheory.getUsedTheory(),deployedTheory)){
					valid = false;
					//redundant deployed theory dependency
					createRedundanctProblem(availableTheoryClause, usedTheory.getUsedTheory(), newDeployedTheory);
				}
			}*/
			
			if(isDependentOf(newDeployedTheory, deployedTheory)){
				valid = false;
				//redundant deployed theory dependency 
				createRedundanctProblem(availableTheoryClause, newDeployedTheory, deployedTheory);
			}
		}
		
//		if(!valid){
//			accuracyInfo.setNotAccurate();
//		}
		
		return valid;
	}
	
	private void createRedundanctProblem(IInternalElement availableTheoryClause, IRodinElement usedDeployedTheory, IRodinElement newDeployedTheory) throws RodinDBException{
		//redundant deployed theory dependency 
		createProblemMarker(availableTheoryClause,
				TheoryAttributes.AVAILABLE_THEORY_ATTRIBUTE,
				TheoryGraphProblem.RedundantDeployedTheoryWarning,
				usedDeployedTheory.getElementName(),
				newDeployedTheory!=null ? newDeployedTheory.getElementName(): "");
	}
	
	private boolean isDependentOf(IDeployedTheoryRoot newDeployedTheory, IDeployedTheoryRoot existingDeployedTheory) throws RodinDBException{
		boolean isDependent = false;

		if(newDeployedTheory.equals(existingDeployedTheory)){
			return true;
		}

		for(IUseTheory usedTheory: existingDeployedTheory.getUsedTheories()){
			IDeployedTheoryRoot importTheory = usedTheory.getUsedTheory();
			if(importTheory.equals(newDeployedTheory) || isDependentOf(newDeployedTheory, importTheory)){
				isDependent = true;
				break;
			}
		}

		return isDependent;
	}

}
