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
import org.eventb.core.sc.state.ILabelSymbolInfo;
import org.eventb.core.sc.state.ILabelSymbolTable;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.internal.core.sc.modules.LabeledElementModule;
import org.eventb.theory.core.IProofRulesBlock;
import org.eventb.theory.core.IRule;
import org.eventb.theory.core.ISCProofRulesBlock;
import org.eventb.theory.core.ISCRule;
import org.eventb.theory.core.sc.Messages;
import org.eventb.theory.core.sc.states.ProofRulesLabelSymbolTable;
import org.eventb.theory.core.sc.states.TheoryAccuracyInfo;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;

/**
 * @author maamria
 * 
 */
@SuppressWarnings("restriction")
public abstract class RuleModule<R extends IRule, S extends ISCRule>
		extends LabeledElementModule {

	protected TheoryAccuracyInfo accuracyInfo;

	@Override
	public void initModule(IRodinElement element,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		super.initModule(element, repository, monitor);
		accuracyInfo = (TheoryAccuracyInfo) repository
				.getState(TheoryAccuracyInfo.STATE_TYPE);

	}

	public void process(IRodinElement element, IInternalElement target,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		IProofRulesBlock block = (IProofRulesBlock) element;
		R [] rules = getRuleElements(block);
		monitor.subTask(Messages.bind(getMessage()));
		ILabelSymbolInfo[] symbolInfos = fetchRules(rules, block.getParent()
				.getElementName(), repository, monitor);
		S[] scRules = createSCRulesArray(rules.length);
		commitRules(rules, (ISCProofRulesBlock) target, scRules, symbolInfos, monitor);
		processRules(rules, scRules, repository, symbolInfos, monitor);
	}

	@Override
	public void endModule(IRodinElement element, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		accuracyInfo = null;
		super.endModule(element, repository, monitor);
	}
	
	@Override
	protected ILabelSymbolInfo createLabelSymbolInfo(String symbol,
			ILabeledElement element, String component) throws CoreException {
		return makeLocalRule(symbol, element, component);
	}

	@Override
	protected ILabelSymbolTable getLabelSymbolTableFromRepository(
			ISCStateRepository repository) throws CoreException {
		return (ILabelSymbolTable) repository
				.getState(ProofRulesLabelSymbolTable.STATE_TYPE);
	}
	
	/**
	 * Returns the message to display as a progress message.
	 * @return the progress message
	 */
	protected abstract String getMessage();
	
	/**
	 * Returns the statically checked rules array.
	 * @param length array length
	 * @return the statically checked rules array
	 */
	protected abstract S[] createSCRulesArray(int length);

	/**
	 * Returns a symbol for a rule.
	 * @param symbol the symbol
	 * @param element the labelled element
	 * @param component the component 
	 * @return the symbol
	 * @throws CoreException
	 */
	protected abstract ILabelSymbolInfo makeLocalRule(String symbol,
			ILabeledElement element, String component) throws CoreException;
	
	/**
	 * Returns the rule children of the given element.
	 * @param element the rodin element
	 * @return the rule children
	 * @throws CoreException
	 */
	protected abstract R[] getRuleElements(IRodinElement element) throws CoreException ;

	/**
	 * Fetches the symbols for the rules elements occurring in the given theory.
	 * @param rules 
	 * @param theoryName the name of the theory
	 * @param repository the state repository
	 * @param monitor the progress monitor
	 * @return the fetched symbols
	 * @throws CoreException
	 */
	protected abstract ILabelSymbolInfo[] fetchRules(R[] rules, String theoryName,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException;
	
	/**
	 * Casts the labelled element to return the statically checked rule.
	 * @param scRule the labelled element
	 * @return the statically checked rule
	 */
	protected abstract S getSCRule(ILabeledElement scRule);
	
	/**
	 * Performs the actual processing of rules.
	 * @param rules the unchecked rules
	 * @param scRules the statically checked rules
	 * @param repository the state repository
	 * @param infos the symbol infos
	 * @param monitor the progress monitor
	 * @throws CoreException
	 */
	protected abstract void processRules(R[] rules, S[] scRules,
			ISCStateRepository repository, ILabelSymbolInfo[] infos,
			IProgressMonitor monitor) throws CoreException ;

	// Commits the SC rules
	private void commitRules(R[] rules, ISCProofRulesBlock target,
			S[] scRules, ILabelSymbolInfo[] symbolInfos,
			IProgressMonitor monitor) throws CoreException {
		for (int i = 0; i < rules.length; i++) {
			if (symbolInfos[i] != null && !symbolInfos[i].hasError()) {
				scRules[i] = createSCRule(target, symbolInfos[i],
						monitor);
			}
		}
	}

	// create an empty SC rule element
	private S createSCRule(ISCProofRulesBlock target,
			ILabelSymbolInfo symbolInfo, IProgressMonitor monitor)
			throws CoreException {
		ILabeledElement scRule = symbolInfo.createSCElement(target,
				symbolInfo.getSymbol(), monitor);
		S rule = getSCRule(scRule);
		return rule;
	}
	
}
