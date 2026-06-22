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
import org.eventb.theory.core.ISCTheoryPathRoot;
import org.eventb.theory.core.ITheoryPathRoot;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.TheoryGraphProblem;
import org.eventb.theory.core.sc.states.TheoryPathAccuracyInfo;
import org.eventb.theory.core.sc.states.TheoryPathProjectTable;
import org.eventb.theory.core.sc.states.TheoryPathTable;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

/**
 * @author renatosilva
 *
 */
public class TheoryPathModule extends SCProcessorModule {
	
	private final IModuleType<TheoryPathModule> MODULE_TYPE = SCCore.getModuleType(TheoryPlugin.PLUGIN_ID + ".theoryPathModule");

	private final static int SYMTAB_SIZE = 2047;

	private TheoryPathAccuracyInfo accuracyInfo;
	private ISCTheoryPathRoot theoryLanguageRoot;
	private IRodinElement source;

	/**
	 * 
	 */
	public TheoryPathModule() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.eventb.internal.core.tool.types.ISCProcessorModule#process(org.rodinp.core.IRodinElement, org.rodinp.core.IInternalElement, org.eventb.core.sc.state.ISCStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void process(IRodinElement element, IInternalElement target,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		theoryLanguageRoot = (ISCTheoryPathRoot) target;
		source = element;
		multipleTheoryPathValidation();
		
		processModules(element, target, repository, monitor);
	}
	
	/**
	 * Validates if the project has more than one theoryPath file
	 * @throws RodinDBException 
	 */
	private void multipleTheoryPathValidation() throws RodinDBException{
		IRodinProject rodinProject = theoryLanguageRoot.getRodinProject();
		
		ITheoryPathRoot[] theoryPathRoots = rodinProject.getRootElementsOfType(ITheoryPathRoot.ELEMENT_TYPE);
		if(theoryPathRoots.length>1){
			accuracyInfo.setNotAccurate();
			for(int i=1;i<theoryPathRoots.length; i++){
				ITheoryPathRoot root = theoryPathRoots[i];
				createProblemMarker(root,
						TheoryGraphProblem.MultipleTheoryPathProjectError,rodinProject.getElementName());
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.eventb.internal.core.tool.types.IModule#getModuleType()
	 */
	@Override
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}
	
	@Override
	public void initModule(IRodinElement element, ISCStateRepository repository, IProgressMonitor monitor) throws CoreException {
		accuracyInfo = new TheoryPathAccuracyInfo();
		final TheoryPathProjectTable projectSymbolTable = new TheoryPathProjectTable(SYMTAB_SIZE);
		final TheoryPathTable theorySymbolTable = new TheoryPathTable(SYMTAB_SIZE);
		repository.setState(projectSymbolTable);
		repository.setState(theorySymbolTable);
		repository.setState(accuracyInfo);
		initProcessorModules(element, repository, monitor);
	};
	
	@Override
	public void endModule(IRodinElement element, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		theoryLanguageRoot.setAccuracy(accuracyInfo.isAccurate(), monitor);
		theoryLanguageRoot.setSource(source, monitor);
		endProcessorModules(element, repository, monitor);
		accuracyInfo = null;
		super.endModule(element, repository, monitor);
	}
}
