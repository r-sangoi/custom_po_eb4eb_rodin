package org.eventb.theory.core.sc.modules;


import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.SCProcessorModule;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.internal.core.sc.symbolTable.IdentifierSymbolTable;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.maths.extensions.WorkspaceExtensionsManager;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.states.AxiomaticDefinitionsLabelSymbolTable;
import org.eventb.theory.core.sc.states.ImportProjectTable;
import org.eventb.theory.core.sc.states.ImportTheoryTable;
import org.eventb.theory.core.sc.states.OperatorsLabelSymbolTable;
import org.eventb.theory.core.sc.states.ProofRulesLabelSymbolTable;
import org.eventb.theory.core.sc.states.RulesBlocksLabelSymbolTable;
import org.eventb.theory.core.sc.states.TheoremsLabelSymbolTable;
import org.eventb.theory.core.sc.states.TheoryAccuracyInfo;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;

/**
 * 
 * @author maamria
 * 
 */
@SuppressWarnings("restriction")
public class TheoryModule extends SCProcessorModule {

	private final IModuleType<TheoryModule> MODULE_TYPE = SCCore.getModuleType(TheoryPlugin.PLUGIN_ID + ".theoryModule");

	public final static int LABEL_SYMTAB_SIZE = 2047;
	public final static int IDENT_SYMTAB_SIZE = 2047;

	private TheoryAccuracyInfo accuracyInfo;
	private ISCTheoryRoot theoryRoot;
	private IRodinElement source;

	@Override
	public void endModule(IRodinElement element, ISCStateRepository repository, IProgressMonitor monitor) throws CoreException {
		theoryRoot.setAccuracy(accuracyInfo.isAccurate(), monitor);
		theoryRoot.setSource(source, monitor);
		endProcessorModules(element, repository, monitor);
		
		fireSCChange();
	}

	private void fireSCChange() throws CoreException {
		final String tmpName = theoryRoot.getRodinFile().getElementName();
		final int last = tmpName.lastIndexOf("_tmp");
		final String scName = last < 0 ? tmpName : tmpName.substring(0, last);
		final ISCTheoryRoot scRoot = (ISCTheoryRoot) theoryRoot
				.getRodinProject().getRodinFile(scName).getRoot();

		WorkspaceExtensionsManager.getInstance().scTheoryChanged(scRoot);
	}

	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	@Override
	public void initModule(IRodinElement element, ISCStateRepository repository, IProgressMonitor monitor) throws CoreException {
		accuracyInfo = new TheoryAccuracyInfo();
		
		final ImportProjectTable projectSymbolTable = new ImportProjectTable(TheoryModule.LABEL_SYMTAB_SIZE);
		final ImportTheoryTable theorySymbolTable = new ImportTheoryTable(TheoryModule.LABEL_SYMTAB_SIZE);
		final TheoremsLabelSymbolTable thmSymbolTable = new TheoremsLabelSymbolTable();
		final RulesBlocksLabelSymbolTable blocksSymbolTable = new RulesBlocksLabelSymbolTable();
		final ProofRulesLabelSymbolTable rulesSymbolTable = new ProofRulesLabelSymbolTable();
		final OperatorsLabelSymbolTable opLabelSymbolTable = new OperatorsLabelSymbolTable();
		final IdentifierSymbolTable identSymbolTable = new IdentifierSymbolTable(IDENT_SYMTAB_SIZE, repository.getFormulaFactory());
		final AxiomaticDefinitionsLabelSymbolTable axmBlocksSymbolTable = new AxiomaticDefinitionsLabelSymbolTable();
		repository.setState(projectSymbolTable);
		repository.setState(theorySymbolTable);
		repository.setState(identSymbolTable);
		repository.setState(thmSymbolTable);
		repository.setState(blocksSymbolTable);
		repository.setState(rulesSymbolTable);
		repository.setState(opLabelSymbolTable);
		repository.setState(axmBlocksSymbolTable);
		
		repository.setState(accuracyInfo);
		initProcessorModules(element, repository, monitor);
	}

	@Override
	public void process(IRodinElement element, IInternalElement target, ISCStateRepository repository, IProgressMonitor monitor) 
	throws CoreException {
		theoryRoot = (ISCTheoryRoot) target;
		source = element;
		processModules(element, target, repository, monitor);
	}

}
