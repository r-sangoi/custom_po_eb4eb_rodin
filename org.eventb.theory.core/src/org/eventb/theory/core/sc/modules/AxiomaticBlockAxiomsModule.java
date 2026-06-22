package org.eventb.theory.core.sc.modules;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ILabeledElement;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.state.IAccuracyInfo;
import org.eventb.core.sc.state.ILabelSymbolInfo;
import org.eventb.core.sc.state.ILabelSymbolTable;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.internal.core.sc.modules.PredicateModule;
import org.eventb.theory.core.IAxiomaticDefinitionAxiom;
import org.eventb.theory.core.IAxiomaticDefinitionsBlock;
import org.eventb.theory.core.IExtensionRulesSource;
import org.eventb.theory.core.ISCAxiomaticDefinitionAxiom;
import org.eventb.theory.core.ISCAxiomaticDefinitionsBlock;
import org.eventb.theory.core.ISCTheorem;
import org.eventb.theory.core.ITheoryRoot;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.states.AxiomsLabelSymbolTable;
import org.eventb.theory.core.sc.states.TheoryAccuracyInfo;
import org.eventb.theory.core.sc.states.TheorySymbolFactory;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;

@SuppressWarnings("restriction")
public class AxiomaticBlockAxiomsModule extends PredicateModule<IAxiomaticDefinitionAxiom>{

	private final IModuleType<AxiomaticBlockAxiomsModule> MODULE_TYPE = 
		SCCore.getModuleType(TheoryPlugin.PLUGIN_ID + ".axiomaticBlockAxiomsModule");
	
	private TheoryAccuracyInfo theoryAccuracyInfo;

	@Override
	public void process(IRodinElement element, IInternalElement target,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		IAxiomaticDefinitionsBlock axmBlock = (IAxiomaticDefinitionsBlock) element;
		ISCAxiomaticDefinitionsBlock scAxmBlock = (ISCAxiomaticDefinitionsBlock) target;
		symbolInfos = fetchAxioms(axmBlock, repository, monitor);
		ISCAxiomaticDefinitionAxiom[] scAxioms = new ISCAxiomaticDefinitionAxiom[symbolInfos.length];
		commitAxioms(axmBlock, scAxmBlock, scAxioms, symbolInfos, monitor);
	}

	private void commitAxioms(IAxiomaticDefinitionsBlock block, ISCAxiomaticDefinitionsBlock scBlock,
			ISCAxiomaticDefinitionAxiom[] scAxioms,
			ILabelSymbolInfo[] labelSymbolInfos, IProgressMonitor monitor) 
	throws CoreException{
		int index = 0;
		for (int i = 0; i < formulaElements.length; i++) {
			if (labelSymbolInfos[i] != null && !labelSymbolInfos[i].hasError()) {
				scAxioms[i] = createSCAxiom(scBlock, index++, labelSymbolInfos[i],
						formulaElements[i], monitor);
				if (scBlock.getParent() instanceof IExtensionRulesSource){
					IExtensionRulesSource extSrc = (IExtensionRulesSource) scBlock.getParent();
					ISCTheorem theorem = extSrc.getTheorem(scAxioms[i].getLabel());
					theorem.create(null, monitor);
					theorem.setLabel(scAxioms[i].getLabel(), monitor);
					theorem.setSource(scAxioms[i], monitor);
					theorem.setGenerated(true, monitor);
					theorem.setOrder(i, monitor);
					theorem.setPredicateString(scAxioms[i].getPredicateString(), monitor);
				}
			}
			else {
				theoryAccuracyInfo.setNotAccurate();
			}
		}
	}
	
	private ISCAxiomaticDefinitionAxiom createSCAxiom(ISCAxiomaticDefinitionsBlock targetBlock,
			int index, ILabelSymbolInfo symbolInfo,
			IAxiomaticDefinitionAxiom axiom,
			IProgressMonitor monitor) throws CoreException {
		
		ILabeledElement scAxioms = symbolInfo.createSCElement(targetBlock,
				ModulesUtils.AXM_NAME_PREFIX + index, monitor);
		return (ISCAxiomaticDefinitionAxiom) scAxioms;
	}
	
	protected ILabelSymbolInfo[] fetchAxioms(IAxiomaticDefinitionsBlock axmBlock,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		boolean accurate = true;
		String theoryName = axmBlock.getAncestor(ITheoryRoot.ELEMENT_TYPE).getComponentName();
		initFilterModules(repository, monitor);
		ILabelSymbolInfo[] labelSymbolInfos = new ILabelSymbolInfo[formulaElements.length];
		for(int i = 0 ; i < formulaElements.length; i++){
			IAxiomaticDefinitionAxiom thm = formulaElements[i];
			labelSymbolInfos[i] = fetchLabel(thm, theoryName, monitor);
			if(labelSymbolInfos[i] == null){
				accurate = false;
				continue;
			}
			if (!filterModules(thm, repository, null)) {
				accurate = false;
				labelSymbolInfos[i].setError();
			}
		}
		endFilterModules(repository, null);
		if(!accurate){
			theoryAccuracyInfo.setNotAccurate();
		}
		return labelSymbolInfos;
	}
	
	@Override
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	@Override
	public void initModule(IRodinElement element,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		super.initModule(element, repository, monitor);
		theoryAccuracyInfo = (TheoryAccuracyInfo) repository.getState(TheoryAccuracyInfo.STATE_TYPE);
		
	}

	@Override
	public void endModule(IRodinElement element, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		theoryAccuracyInfo = null;
		super.endModule(element, repository, monitor);
	}
	
	@Override
	protected IAccuracyInfo getAccuracyInfo(ISCStateRepository repository)
			throws CoreException {
		return (TheoryAccuracyInfo) repository.getState(TheoryAccuracyInfo.STATE_TYPE);
	}

	@Override
	protected IAxiomaticDefinitionAxiom[] getFormulaElements(IRodinElement element)
			throws CoreException {
		IAxiomaticDefinitionsBlock block = (IAxiomaticDefinitionsBlock) element;
		return block.getAxiomaticDefinitionAxioms();
	}

	@Override
	protected void makeProgress(IProgressMonitor monitor) {
		monitor.worked(1);
	}

	@Override
	protected ILabelSymbolTable getLabelSymbolTableFromRepository(
			ISCStateRepository repository) throws CoreException {
		return (AxiomsLabelSymbolTable) repository.getState(AxiomsLabelSymbolTable.STATE_TYPE);
	}

	@Override
	protected ILabelSymbolInfo createLabelSymbolInfo(String symbol,
			ILabeledElement element, String component) throws CoreException {
		return TheorySymbolFactory.getInstance().makeLocalAxiom(symbol, true, element, component);
	}


}
