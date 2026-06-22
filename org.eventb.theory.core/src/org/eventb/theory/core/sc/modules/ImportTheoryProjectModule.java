/**
 * 
 */
package org.eventb.theory.core.sc.modules;

import static org.eventb.theory.core.TheoryHierarchyHelper.getImportedTheories;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.ast.extensions.maths.AstUtilities;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.SCProcessorModule;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.IImportTheory;
import org.eventb.theory.core.IImportTheoryProject;
import org.eventb.theory.core.ISCImportTheoryProject;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.ITheoryRoot;
import org.eventb.theory.core.TheoryAttributes;
import org.eventb.theory.core.basis.SCTheoryDecorator;
import org.eventb.theory.core.maths.extensions.WorkspaceExtensionsManager;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.Messages;
import org.eventb.theory.core.sc.TheoryGraphProblem;
import org.eventb.theory.core.sc.states.IImportProjectTable;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

/**
 * @author asiehsalehi
 *
 */
public class ImportTheoryProjectModule extends SCProcessorModule {
	
	private final IModuleType<ImportTheoryProjectModule> MODULE_TYPE = SCCore
			.getModuleType(TheoryPlugin.PLUGIN_ID + ".importTheoryProjectModule"); //$NON-NLS-1$
	
	private ITheoryRoot root;
	private IImportProjectTable projectTable;
//	private static final String THEORY_PROJECT_NAME = "THPRJ";
	
	@Override
	public void initModule(IRodinElement element,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		IRodinFile file = (IRodinFile) element;
		root = (ITheoryRoot) file.getRoot();
		projectTable = (IImportProjectTable) repository.getState(IImportProjectTable.STATE_TYPE);
		repository.setState(projectTable);
	}
	
	@Override
	public void endModule(IRodinElement element, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {

		/*
		 * FIXME This is neither the appropriate way to do this check, nor the
		 * place to do it. The check should be done while processing, not in the
		 * endModule. Moreover, the check should use the state computed by the
		 * processors, rather than reading again from the source file.
		 */
		
		//checking conflict between imported theories and the self theory (importing theory)
		IImportTheoryProject[] theoryProjects = root.getImportTheoryProjects();
		for(IImportTheoryProject theoryProject: theoryProjects){
			IImportTheory[] importedTheories = theoryProject.getImportTheories();
			for (IImportTheory importedTheory: importedTheories){
				if (!importedTheory.hasImportTheory()) {
					continue;
				}
				IDeployedTheoryRoot deployedTheoryRoot = importedTheory.getImportTheory();
				ISCTheoryRoot theory = (ISCTheoryRoot) deployedTheoryRoot;
				SCTheoryDecorator hierarchy = new SCTheoryDecorator(theory);
				
				ISCTheoryRoot selfTheory = root.getSCTheoryRoot();
				SCTheoryDecorator selfHierarchy = new SCTheoryDecorator(selfTheory);
				if (selfTheory.getRodinFile().exists() && hierarchy.isConflicting(selfHierarchy)){
					createProblemMarker(importedTheory,
							TheoryAttributes.IMPORT_THEORY_ATTRIBUTE,
							TheoryGraphProblem.TheoriesConflictError,
							selfTheory.getComponentName(), deployedTheoryRoot.getComponentName());
				}
			}	
		}
		
		projectTable = null;
		root = null;
		super.endModule(element, repository, monitor);
	}
	
	/**
	 * 
	 */
	public ImportTheoryProjectModule() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void process(IRodinElement element, IInternalElement target,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		
		IImportTheoryProject[] theoryProjects = root.getImportTheoryProjects();
		
		if (theoryProjects.length == 0) {
			// commented this out as this should be ok
			//createProblemMarker(root,
					//TheoryGraphProblem.NoTheoryProjectClausesError);
		}
		else {
			monitor.subTask(Messages.bind(Messages.progress_TheoryProjects));
			initFilterModules(repository, monitor);

			int index = 0;
			for(IImportTheoryProject theoryProject: theoryProjects){
				if(!theoryProject.hasTheoryProject()){
					createProblemMarker(theoryProject,
							TheoryAttributes.THEORY_PROJECT_ATTRIBUTE,
							TheoryGraphProblem.ImportTheoryProjectMissing);
					// FIXME set accuracy to false
				}
				else if(filterModules(theoryProject, repository, monitor)){
					ISCImportTheoryProject scTheoryProject = saveSCTheoryProject((ISCTheoryRoot) target, theoryProject, index++, monitor);
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
	
	private void processTheories(IImportTheoryProject theoryProject, IInternalElement target, ISCStateRepository repository, IProgressMonitor monitor) throws CoreException{
		initProcessorModules(theoryProject, repository, monitor);
		processModules(theoryProject, target, repository, monitor);
		endProcessorModules(theoryProject, repository, monitor);
		
		final Set<ISCTheoryRoot> importedTheories = getImportedTheories(root);
		patchFormulaFactory(importedTheories, repository);		
	}

	private ISCImportTheoryProject saveSCTheoryProject(ISCTheoryRoot target,
			IImportTheoryProject theoryProject, int index,
			IProgressMonitor monitor) throws RodinDBException {
		ISCImportTheoryProject scTheoryProject = target.createChild(ISCImportTheoryProject.ELEMENT_TYPE, null, monitor);
//		ISCImportTheoryProject scTheoryProject = target.getSCImportTheoryProject(THEORY_PROJECT_NAME+"_"+index);
//		scTheoryProject.create(null, monitor);
		scTheoryProject.setSCTheoryProject(theoryProject.getTheoryProject(), monitor); 
		scTheoryProject.setSource(theoryProject, monitor);
		
		return scTheoryProject;
	}
	
	/**
	 * Patches the formula factory to be used for the rest of the static checking process.
	 * @param importedTheories the set of imported theories to consider
	 * @param repository the state repository
	 * @throws CoreException
	 */
	protected void patchFormulaFactory(Set<ISCTheoryRoot> importedTheories,
			ISCStateRepository repository) throws CoreException {
		FormulaFactory factory = repository.getFormulaFactory();
		ITypeEnvironmentBuilder typeEnvironment = repository.getTypeEnvironment();

		final Set<IFormulaExtension> exts = new HashSet<IFormulaExtension>();
		for (ISCTheoryRoot theoryRoot : importedTheories) {
			final WorkspaceExtensionsManager mgr = WorkspaceExtensionsManager.getInstance();
			exts.addAll(mgr.getFormulaExtensions(theoryRoot));
		}
		factory = factory.withExtensions(exts);
		typeEnvironment = AstUtilities
				.getTypeEnvironmentForFactory(typeEnvironment, factory);
		
		repository.setFormulaFactory(factory);
		//repository.setTypeEnvironment(factory.makeTypeEnvironment());
		repository.setTypeEnvironment(typeEnvironment);
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.internal.core.tool.types.IModule#getModuleType()
	 */
	@Override
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

}
