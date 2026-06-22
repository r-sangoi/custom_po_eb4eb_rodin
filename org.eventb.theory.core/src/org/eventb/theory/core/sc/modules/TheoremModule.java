/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
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
import org.eventb.theory.core.ISCTheorem;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.ITheorem;
import org.eventb.theory.core.ITheoryRoot;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.states.TheoremsLabelSymbolTable;
import org.eventb.theory.core.sc.states.TheoryAccuracyInfo;
import org.eventb.theory.core.sc.states.TheorySymbolFactory;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;

/**
 * @author maamria
 *
 */
@SuppressWarnings("restriction")
public class TheoremModule extends PredicateModule<ITheorem>{

	private final IModuleType<TheoremModule> MODULE_TYPE = 
		SCCore.getModuleType(TheoryPlugin.PLUGIN_ID + ".theoremModule");
	
	private TheoryAccuracyInfo theoryAccuracyInfo;

	@Override
	public void process(IRodinElement element, IInternalElement target,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		IRodinFile file = (IRodinFile) element;
		ITheoryRoot root = (ITheoryRoot) file.getRoot();
		ISCTheoryRoot targetRoot = (ISCTheoryRoot) target;
		symbolInfos = fetchTheorems(file, repository, monitor);
		ISCTheorem[] scTheorems = new ISCTheorem[symbolInfos.length];
		commitTheorems(root, targetRoot, scTheorems, symbolInfos, monitor);
	}

	private void commitTheorems(ITheoryRoot root, ISCTheoryRoot targetRoot,
			ISCTheorem[] scTheorems,
			ILabelSymbolInfo[] labelSymbolInfos, IProgressMonitor monitor) 
	throws CoreException{
		int index = 0;
		for (int i = 0; i < formulaElements.length; i++) {
			if (labelSymbolInfos[i] != null && !labelSymbolInfos[i].hasError()) {
				scTheorems[i] = createSCTheorem(targetRoot, index++, labelSymbolInfos[i],
						formulaElements[i], monitor);
				scTheorems[i].setOrder(i, monitor);
			}
			else {
				theoryAccuracyInfo.setNotAccurate();
			}
		}
	}
	
	private ISCTheorem createSCTheorem(ISCTheoryRoot targetRoot,
			int index, ILabelSymbolInfo symbolInfo,
			ITheorem theorem,
			IProgressMonitor monitor) throws CoreException {
		
		ILabeledElement scTheorem = symbolInfo.createSCElement(targetRoot,
				ModulesUtils.THM_NAME_PREFIX + index, monitor);
		return (ISCTheorem) scTheorem;
	}
	
	protected ILabelSymbolInfo[] fetchTheorems(IRodinFile theoryFile,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		boolean accurate = true;
		String theoryName = theoryFile.getElementName();
		initFilterModules(repository, monitor);
		ILabelSymbolInfo[] labelSymbolInfos = new ILabelSymbolInfo[formulaElements.length];
		for(int i = 0 ; i < formulaElements.length; i++){
			ITheorem thm = formulaElements[i];
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
	protected ITheorem[] getFormulaElements(IRodinElement element)
			throws CoreException {
		IRodinFile file = (IRodinFile) element;
		ITheoryRoot root = (ITheoryRoot) file.getRoot();
		return root.getTheorems();
	}

	@Override
	protected void makeProgress(IProgressMonitor monitor) {
		monitor.worked(1);
	}

	@Override
	protected ILabelSymbolTable getLabelSymbolTableFromRepository(
			ISCStateRepository repository) throws CoreException {
		return (TheoremsLabelSymbolTable) repository.getState(TheoremsLabelSymbolTable.STATE_TYPE);
	}

	@Override
	protected ILabelSymbolInfo createLabelSymbolInfo(String symbol,
			ILabeledElement element, String component) throws CoreException {
		return TheorySymbolFactory.getInstance().makeLocalTheorem(symbol, true, element, component);
	}

}
