/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.sc.modules;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.sc.SCFilterModule;
import org.eventb.core.sc.state.ILabelSymbolInfo;
import org.eventb.core.sc.state.ILabelSymbolTable;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.theory.core.IApplicabilityElement.RuleApplicability;
import org.eventb.theory.core.IRule;
import org.eventb.theory.core.ITheoryRoot;
import org.eventb.theory.core.TheoryAttributes;
import org.eventb.theory.core.sc.TheoryGraphProblem;
import org.eventb.theory.core.sc.states.ProofRulesLabelSymbolTable;
import org.rodinp.core.IRodinElement;

/**
 * 
 * @author maamria
 * 
 */
public abstract class RuleFilterModule<R extends IRule> extends SCFilterModule {

	private ILabelSymbolTable labelSymbolTable;

	public void initModule(ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		super.initModule(repository, monitor);
		labelSymbolTable = (ProofRulesLabelSymbolTable) repository
				.getState(ProofRulesLabelSymbolTable.STATE_TYPE);
	}

	@Override
	public boolean accept(IRodinElement element, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		R rule = getRule(element);
		final String label = rule.getLabel();
		final ILabelSymbolInfo symbolInfo = labelSymbolTable
				.getSymbolInfo(label);
		if (symbolInfo == null) {
			throw new IllegalStateException("No defined symbol for: " + label);
		}
		// apply further checks first
		if (!furtherCheck(rule, symbolInfo, repository, monitor)){
			return false;
		}
		// Check applicability attribute
		if (!checkApplicabilityAttribute(rule, symbolInfo, repository, monitor)) {
			return false;
		}
		// all OK
		return true;
	}

	public void endModule(ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		labelSymbolTable = null;
		super.endModule(repository, monitor);
	}
	
	protected abstract R getRule(IRodinElement element);
	
	protected abstract boolean furtherCheck(R rule,
			ILabelSymbolInfo symbolInfo, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException;


	private boolean checkApplicabilityAttribute(R rule,
			ILabelSymbolInfo symbolInfo, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		boolean isAuto = false;
		boolean isInter = true;
		if (!rule.hasApplicabilityAttribute()) {
			createProblemMarker(rule, TheoryAttributes.APPLICABILITY_ATTRIBUTE,
					TheoryGraphProblem.ApplicabilityUndefError);
			// default is manual
			isAuto = false;
			isInter = true;
		} else {
			isAuto = rule.isAutomatic();
			isInter = rule.isInteractive();
		}
		
		symbolInfo.setAttributeValue(TheoryAttributes.APPLICABILITY_ATTRIBUTE,
				RuleApplicability.getRuleApplicability(isAuto, isInter).toString());
		// use the name of theory see TestRewriteRules.testRewriteRules_013()
		String desc = rule.getAncestor(ITheoryRoot.ELEMENT_TYPE).getComponentName() + "." + rule.getLabel();
		if (!rule.hasDescription() || rule.getDescription().equals("")) {
			createProblemMarker(rule, TheoryAttributes.DESC_ATTRIBUTE,
					TheoryGraphProblem.DescNotSupplied, rule.getLabel());
		} else {
			desc = rule.getDescription();
		}
		symbolInfo.setAttributeValue(TheoryAttributes.DESC_ATTRIBUTE, desc);
		return true;
	}
}
