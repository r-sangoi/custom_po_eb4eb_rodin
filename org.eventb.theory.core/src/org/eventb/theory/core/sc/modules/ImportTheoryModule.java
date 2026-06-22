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
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.IImportTheory;
import org.eventb.theory.core.IImportTheoryProject;
import org.eventb.theory.core.ISCImportTheory;
import org.eventb.theory.core.ISCImportTheoryProject;
import org.eventb.theory.core.TheoryAttributes;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.Messages;
import org.eventb.theory.core.sc.TheoryGraphProblem;
import org.eventb.theory.core.sc.states.IImportTheoryTable;
import org.eventb.theory.core.sc.states.TheoryAccuracyInfo;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author asiehsalehi
 *
 */
public class ImportTheoryModule extends SCProcessorModule {
	
	private final IModuleType<ImportTheoryModule> MODULE_TYPE = SCCore.getModuleType(TheoryPlugin.PLUGIN_ID + ".importTheoryModule");
	
	private IImportTheoryTable theoryTable;
	private TheoryAccuracyInfo accuracyInfo;

	/**
	 * 
	 */
	public ImportTheoryModule() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void initModule(IRodinElement element,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		super.initModule(element, repository, monitor);
		theoryTable = (IImportTheoryTable) repository.getState(IImportTheoryTable.STATE_TYPE);
		accuracyInfo = (TheoryAccuracyInfo) repository.getState(TheoryAccuracyInfo.STATE_TYPE);
		repository.setState(theoryTable);
	}
	
	
	@Override
	public void endModule(IRodinElement element, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		
		//early to check this, it is moved to the ImportTheoryProjectModule.endModule
		//checking conflict between imported theories and the self theory (importing theory)
/*		Collection<IDeployedTheoryRoot> importedTheories = theoryTable.getAllTheories();
		IDeployedTheoryRoot[] importTheoriesArray = importedTheories.toArray(new IDeployedTheoryRoot[importedTheories.size()]);
		for (int i = 0 ; i < importTheoriesArray.length ; i++){
			ISCTheoryRoot theory = (ISCTheoryRoot) importTheoriesArray[i];
			SCTheoryDecorator hierarchy = new SCTheoryDecorator(theory);
			
			ISCTheoryRoot selfTheory = ((ITheoryRoot) element.getParent()).getSCTheoryRoot();
			SCTheoryDecorator selfHierarchy = new SCTheoryDecorator(selfTheory);
			if (hierarchy.isConflicting(selfHierarchy)){
				createProblemMarker(getImportTheory(theoryProj.getImportTheories(), importTheoriesArray[i]),
				//createProblemMarker((IImportTheory) importTheoriesArray[i],
						TheoryAttributes.IMPORT_THEORY_ATTRIBUTE,
						TheoryGraphProblem.TheoriesConflictError,
						selfTheory.getComponentName(), importTheoriesArray[i].getComponentName());
			}
		}*/
		
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
		final IImportTheoryProject theoryProj = (IImportTheoryProject) element;
		boolean valid = false;

		monitor.subTask(Messages.bind(Messages.progress_TheoryPathTheories));
		initFilterModules(repository, monitor);

		int index = 0;
		for(IImportTheory theory: theoryProj.getImportTheories()){
			if(!theory.hasImportTheory()){
				createProblemMarker(theory,
						TheoryAttributes.IMPORT_THEORY_ATTRIBUTE,
						TheoryGraphProblem.NoTheoryClausesError);
			} else if(filterModules(theory, repository, monitor)){
				IDeployedTheoryRoot conflictingTheory = theoryTable.addTheory(theory);
				if(conflictingTheory==null){
					valid = true;
					saveSCTheory((ISCImportTheoryProject) target, theory, index++, monitor);
				}
				else {
					createProblemMarker(theory,
							TheoryAttributes.IMPORT_THEORY_ATTRIBUTE,
							TheoryGraphProblem.TheoriesConflictError,
							theory.getImportTheory().getComponentName(), DatabaseUtilitiesTheoryPath.getFullDescriptionAvailableTheory(conflictingTheory.getRodinProject(), conflictingTheory));
				}
			}
			monitor.worked(1);
		}
		
		if(!valid){
			accuracyInfo.setNotAccurate();
		}

		monitor.done();

	}
	
/*	private IImportTheory getImportTheory(IImportTheory[] theories, IDeployedTheoryRoot deployedTheory) throws RodinDBException {
		
		for(IImportTheory theory: theories){
			if (theory.getImportTheory().getComponentName().equals(deployedTheory.getComponentName()))
				return theory;
		}
		return null;
	}*/

	/**
	 * Patches the formula factory to be used for the rest of the static checking process.
	 * @param importedTheories the set of imported theories to consider
	 * @param repository the state repository
	 * @throws CoreException
	 */
/*	protected void patchFormulaFactory(Set<IDeployedTheoryRoot> importedTheories,
			ISCStateRepository repository) throws CoreException {
		// need to patch up formula factory
		//SCTheoriesGraph graph = new SCTheoriesGraph();
		DeployedTheoriesGraph graph = new DeployedTheoriesGraph();
		graph.setElements(importedTheories);
		FormulaFactory factory = repository.getFormulaFactory();
		ITypeEnvironment typeEnvironment = repository.getTypeEnvironment();

		for (ISCTheoryRoot root : graph.getElements()) {
			FormulaExtensionsLoader loader = new FormulaExtensionsLoader(root,
					factory);
			Set<IFormulaExtension> exts = loader.load();
			factory = factory.withExtensions(exts);
			typeEnvironment = AstUtilities
					.getTypeEnvironmentForFactory(typeEnvironment, factory);
		}
		repository.setFormulaFactory(factory);
		repository.setTypeEnvironment(factory.makeTypeEnvironment());
	}*/
	
	private void saveSCTheory(ISCImportTheoryProject target,
			IImportTheory theory, int index, IProgressMonitor monitor)
			throws RodinDBException {
		final ISCImportTheory scTheory = target.createChild(
				ISCImportTheory.ELEMENT_TYPE, null, monitor);
		scTheory.setImportTheory(theory.getImportTheory(), monitor);
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
