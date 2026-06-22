package org.eventb.theory.core.sc.modules;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ILabeledElement;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.state.ILabelSymbolInfo;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.internal.core.sc.modules.LabeledElementModule;
import org.eventb.theory.core.IAxiomaticDefinitionsBlock;
import org.eventb.theory.core.ISCAxiomaticDefinitionsBlock;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.ITheoryRoot;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.Messages;
import org.eventb.theory.core.sc.states.AxiomaticDefinitionsLabelSymbolTable;
import org.eventb.theory.core.sc.states.AxiomsLabelSymbolTable;
import org.eventb.theory.core.sc.states.TheoryAccuracyInfo;
import org.eventb.theory.core.sc.states.TheorySymbolFactory;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;

/**
 * 
 * @author maamria
 * 
 */
@SuppressWarnings("restriction")
public class AxiomaticDefinitionsBlockModule extends LabeledElementModule {

	private final IModuleType<AxiomaticDefinitionsBlockModule> MODULE_TYPE = SCCore.getModuleType(TheoryPlugin.PLUGIN_ID
			+ ".axiomaticDefinitionsBlockModule");

	private TheoryAccuracyInfo theoryAccuracyInfo;
	private IAxiomaticDefinitionsBlock[] axmDefsBlocks;

	@Override
	public void process(IRodinElement element, IInternalElement target, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		IRodinFile file = (IRodinFile) element;
		ITheoryRoot root = (ITheoryRoot) file.getRoot();
		ISCTheoryRoot targetRoot = (ISCTheoryRoot) target;
		monitor.subTask(Messages.progress_TheoryAxiomaticBlocks);
		monitor.worked(1);

		axmDefsBlocks = root.getAxiomaticDefinitionsBlocks();
		ILabelSymbolInfo[] blocks = fetchBlocks(file, repository, monitor);
		ISCAxiomaticDefinitionsBlock[] scAxmDefsBlocks = new ISCAxiomaticDefinitionsBlock[blocks.length];
		commitBlocks(root, targetRoot, scAxmDefsBlocks, blocks, monitor);
		processBlocks(scAxmDefsBlocks, repository, blocks, monitor);
	}

	private void processBlocks(ISCAxiomaticDefinitionsBlock[] scAxmBlocks, ISCStateRepository repository,
			ILabelSymbolInfo[] blocks, IProgressMonitor monitor) throws CoreException {
		for (int i = 0; i < axmDefsBlocks.length; i++) {
			repository.setState(new AxiomsLabelSymbolTable());
			if (blocks[i] != null && !blocks[i].hasError()) {
				initProcessorModules(axmDefsBlocks[i], repository, null);
				processModules(axmDefsBlocks[i], scAxmBlocks[i], repository, monitor);
				endProcessorModules(axmDefsBlocks[i], repository, null);

			} else {
				theoryAccuracyInfo.setNotAccurate();
			}
			monitor.worked(1);
		}

	}

	private void commitBlocks(ITheoryRoot root, ISCTheoryRoot targetRoot, ISCAxiomaticDefinitionsBlock[] scAxmBlocks,
			ILabelSymbolInfo[] labelSymbolInfos, IProgressMonitor monitor) throws CoreException {
		int index = 0;

		for (int i = 0; i < axmDefsBlocks.length; i++) {
			if (labelSymbolInfos[i] != null && !labelSymbolInfos[i].hasError()) {
				scAxmBlocks[i] = createSCBlock(targetRoot, index++, labelSymbolInfos[i], axmDefsBlocks[i], monitor);
				scAxmBlocks[i].setSource(axmDefsBlocks[i], monitor);
			}
		}
	}

	private ISCAxiomaticDefinitionsBlock createSCBlock(ISCTheoryRoot targetRoot, int index, ILabelSymbolInfo symbolInfo,
			IAxiomaticDefinitionsBlock block, IProgressMonitor monitor) throws CoreException {

		ILabeledElement scBlock = symbolInfo.createSCElement(targetRoot, ModulesUtils.ADB_NAME_PREFIX + index, monitor);
		return (ISCAxiomaticDefinitionsBlock) scBlock;
	}

	protected ILabelSymbolInfo[] fetchBlocks(IRodinFile theoryFile, ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		boolean accurate = true;
		String theoryName = theoryFile.getElementName();
		initFilterModules(repository, monitor);
		ILabelSymbolInfo[] labelSymbolInfos = new ILabelSymbolInfo[axmDefsBlocks.length];
		for (int i = 0; i < axmDefsBlocks.length; i++) {
			IAxiomaticDefinitionsBlock block = axmDefsBlocks[i];
			labelSymbolInfos[i] = fetchLabel(block, theoryName, monitor);
			if (labelSymbolInfos[i] == null) {
				accurate = false;
				continue;
			}
			if (!filterModules(block, repository, null)) {
				labelSymbolInfos[i].setError();
				accurate = false;
			}
		}
		endFilterModules(repository, null);
		if (!accurate) {
			theoryAccuracyInfo.setNotAccurate();
		}
		return labelSymbolInfos;
	}

	@Override
	public void initModule(IRodinElement element, ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		super.initModule(element, repository, monitor);
		theoryAccuracyInfo = (TheoryAccuracyInfo) repository.getState(TheoryAccuracyInfo.STATE_TYPE);

	}

	@Override
	public void endModule(IRodinElement element, ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		theoryAccuracyInfo = null;
		super.endModule(element, repository, monitor);
	}

	@Override
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	@Override
	protected AxiomaticDefinitionsLabelSymbolTable getLabelSymbolTableFromRepository(ISCStateRepository repository)
			throws CoreException {
		return (AxiomaticDefinitionsLabelSymbolTable) repository.getState(AxiomaticDefinitionsLabelSymbolTable.STATE_TYPE);
	}

	@Override
	protected ILabelSymbolInfo createLabelSymbolInfo(String symbol, ILabeledElement element, String component)
			throws CoreException {
		return TheorySymbolFactory.getInstance().makeLocalAxiomaticBlock(symbol, true, element, component);
	}

}
