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
import org.eventb.core.IIdentifierElement;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.state.IIdentifierSymbolInfo;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.internal.core.sc.modules.IdentifierModule;
import org.eventb.theory.core.IMetavariable;
import org.eventb.theory.core.IProofRulesBlock;
import org.eventb.theory.core.ISCMetavariable;
import org.eventb.theory.core.ISCProofRulesBlock;
import org.eventb.theory.core.ITheoryRoot;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.states.TheoryAccuracyInfo;
import org.eventb.theory.core.sc.states.TheorySymbolFactory;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;

/**
 * @author maamria
 * 
 */
@SuppressWarnings("restriction")
public class MetavariableModule extends IdentifierModule {

	private final IModuleType<MetavariableModule> MODULE_TYPE = SCCore
			.getModuleType(TheoryPlugin.PLUGIN_ID + ".metavariableModule");
	
	private TheoryAccuracyInfo accuracyInfo;
	
	@Override
	public void process(IRodinElement element, IInternalElement target,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		IProofRulesBlock rulesBlock = (IProofRulesBlock) element;
		ISCProofRulesBlock scRulesBlock = (ISCProofRulesBlock) target;
		IMetavariable[] metavars = rulesBlock.getMetavariables();
		fetchSymbols(metavars, scRulesBlock, repository, monitor);
		boolean accurate = true;
		// in case some metavars were not filtered in
		if(identifierSymbolTable.getSymbolInfosFromTop().size() != metavars.length){
			accurate = false;
		}
		for (IIdentifierSymbolInfo symbolInfo : identifierSymbolTable.getSymbolInfosFromTop()) {
			if (symbolInfo.getSymbolType() == ISCMetavariable.ELEMENT_TYPE
					&& symbolInfo.isPersistent() && !symbolInfo.hasError()) {
				if (target != null) {
					symbolInfo.createSCElement(target, null);
				}
			}
			else {
				accurate = false;
			}
		}
		if(!accurate)
			accuracyInfo.setNotAccurate();
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
		accuracyInfo = (TheoryAccuracyInfo) repository.getState(TheoryAccuracyInfo.STATE_TYPE);
	}

	@Override
	public void endModule(IRodinElement element, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		accuracyInfo = null;
		super.endModule(element, repository, monitor);
	}

	protected void typeIdentifierSymbol(IIdentifierSymbolInfo newSymbolInfo,
			final ITypeEnvironmentBuilder environment) throws CoreException {
		environment.addName(newSymbolInfo.getSymbol(), newSymbolInfo.getType());
	}
	
	
	@Override
	protected IIdentifierSymbolInfo createIdentifierSymbolInfo(String name,
			IIdentifierElement element) {
		return TheorySymbolFactory.getInstance().makeLocalMetavariable(
				name, true, element, element.getAncestor(ITheoryRoot.ELEMENT_TYPE).getComponentName());
	}

}
