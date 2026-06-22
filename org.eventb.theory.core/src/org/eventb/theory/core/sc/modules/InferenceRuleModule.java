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
import org.eventb.core.EventBAttributes;
import org.eventb.core.ILabeledElement;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.state.ILabelSymbolInfo;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.theory.core.IInferenceRule;
import org.eventb.theory.core.IProofRulesBlock;
import org.eventb.theory.core.IReasoningTypeElement.ReasoningType;
import org.eventb.theory.core.ISCInferenceRule;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.Messages;
import org.eventb.theory.core.sc.TheoryGraphProblem;
import org.eventb.theory.core.sc.states.InferenceIdentifiers;
import org.eventb.theory.core.sc.states.RuleAccuracyInfo;
import org.eventb.theory.core.sc.states.TheorySymbolFactory;
import org.rodinp.core.IRodinElement;

/**
 * @author maamria
 * 
 */
@SuppressWarnings("restriction")
public class InferenceRuleModule extends RuleModule<IInferenceRule, ISCInferenceRule> {

	public static final IModuleType<InferenceRuleModule> MODULE_TYPE = SCCore.getModuleType(TheoryPlugin.PLUGIN_ID + ".inferenceRuleModule");

	@Override
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	@Override
	protected String getMessage() {
		return Messages.progress_TheoryInferenceRules;
	}

	@Override
	protected ISCInferenceRule[] createSCRulesArray(int length) {
		return new ISCInferenceRule[length];
	}

	@Override
	protected ILabelSymbolInfo makeLocalRule(String symbol, ILabeledElement element, String component) throws CoreException {
		return TheorySymbolFactory.getInstance().makeLocalInferenceRule(symbol, true, element, component);
	}

	@Override
	protected IInferenceRule[] getRuleElements(IRodinElement element) throws CoreException {
		IProofRulesBlock rulesBlock = (IProofRulesBlock) element;
		return rulesBlock.getInferenceRules();
	}

	@Override
	protected ILabelSymbolInfo[] fetchRules(IInferenceRule[] rules, String theoryName, 
			ISCStateRepository repository, IProgressMonitor monitor) throws CoreException {
		boolean accurate = true;
		ILabelSymbolInfo[] symbolInfos = new ILabelSymbolInfo[rules.length];
		initFilterModules(repository, monitor);
		for (int i = 0; i < rules.length; i++) {
			symbolInfos[i] = fetchLabel(rules[i], theoryName, monitor);
			if (symbolInfos[i] == null) {
				accurate = false;
				continue;
			}
			if (!filterModules(rules[i], repository, monitor)) {
				symbolInfos[i].setError();
				accurate = false;
			}
		}
		endFilterModules(repository, monitor);
		if (!accurate)
			accuracyInfo.setNotAccurate();
		return symbolInfos;
	}

	@Override
	protected ISCInferenceRule getSCRule(ILabeledElement scRule) {
		return (ISCInferenceRule) scRule;
	}

	@Override
	protected void processRules(IInferenceRule[] rules, ISCInferenceRule[] scRules, ISCStateRepository repository, ILabelSymbolInfo[] infos, IProgressMonitor monitor)
			throws CoreException {
		for (int i = 0; i < rules.length; i++) {
			if (infos[i] != null && !infos[i].hasError()) {
				IInferenceRule rule = rules[i];
				// Needed states
				RuleAccuracyInfo ruleAccuracyInfo = new RuleAccuracyInfo();
				repository.setState(ruleAccuracyInfo);
				InferenceIdentifiers inferenceIdentifiers = new InferenceIdentifiers();
				repository.setState(inferenceIdentifiers);
				// call the children processor module
				{
					initProcessorModules(rule, repository, null);
					processModules(rule, scRules[i], repository, monitor);
					endProcessorModules(rule, repository, null);
				}
				inferenceIdentifiers.makeImmutable();
				if (!inferenceIdentifiers.isRuleApplicable()) {
					createProblemMarker(rule, EventBAttributes.LABEL_ATTRIBUTE, TheoryGraphProblem.InferenceRuleNotApplicableError);
					ruleAccuracyInfo.setNotAccurate();
					accuracyInfo.setNotAccurate();
				} else if (ruleAccuracyInfo.isAccurate()) {
					ReasoningType reasoningType = null;
					if (inferenceIdentifiers.isRuleApplicableInBothDirections()) {
						reasoningType = ReasoningType.BACKWARD_AND_FORWARD;
					} else if (inferenceIdentifiers.isRuleBackwardApplicable()) {
						reasoningType = ReasoningType.BACKWARD;
					} else if (inferenceIdentifiers.isRuleForwardApplicable()) {
						reasoningType = ReasoningType.FORWARD;
					}
					if (reasoningType != null) {
						scRules[i].setReasoningType(reasoningType, monitor);
					}
				}
				if (scRules[i] != null) {
					scRules[i].setAccuracy(ruleAccuracyInfo.isAccurate(), monitor);
				}
			} else {
				if (scRules[i] != null)
					scRules[i].setAccuracy(false, monitor);
				accuracyInfo.setNotAccurate();
			}
		}
		monitor.worked(1);

	}

}
