/*******************************************************************************
 * Copyright (c) 2010, 2020 University of Southampton and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.sc.modules;

import static org.eventb.core.ast.Formula.BTRUE;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.extensions.maths.AstUtilities;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.SCProcessorModule;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.theory.core.INewOperatorDefinition;
import org.eventb.theory.core.IOperatorWDCondition;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.TheoryGraphProblem;
import org.eventb.theory.core.sc.states.OperatorInformation;
import org.eventb.theory.core.sc.states.TheoryAccuracyInfo;
import org.eventb.theory.core.util.CoreUtilities;
import org.eventb.theory.internal.core.util.GeneralUtilities;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;

/**
 * @author maamria
 * 
 */
public class OperatorWDConditionModule extends SCProcessorModule {

	private final IModuleType<OperatorWDConditionModule> MODULE_TYPE = SCCore.getModuleType(TheoryPlugin.PLUGIN_ID
			+ ".operatorWDConditionModule");

	private TheoryAccuracyInfo theoryAccuracyInfo;
	private OperatorInformation operatorInformation;

	@Override
	public void process(IRodinElement element, IInternalElement target, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		INewOperatorDefinition newOpDef = (INewOperatorDefinition) element;
		IOperatorWDCondition[] wdConds = newOpDef.getOperatorWDConditions();
		// check for error
		if (!operatorInformation.hasError() && wdConds != null && wdConds.length > 0) {
			Predicate wdPred = processWdConditions(wdConds, repository, monitor);
			if (wdPred != null && wdPred.getTag() != BTRUE) {
				if (target != null) {
					Predicate wdPredWD = wdPred.getWDPredicate();
					wdPred = AstUtilities.conjunctPredicates(wdPredWD, wdPred);
					operatorInformation.addWDCondition(wdPred);
				} else {
					operatorInformation.setHasError();
				}
			}
		}
	}

	private Predicate processWdConditions(IOperatorWDCondition[] wds, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException{
		List<Predicate> wdPredicates = new ArrayList<Predicate>();
		for(IOperatorWDCondition wd : wds){
			if(!wd.hasPredicateString() || wd.getPredicateString().equals("")){
				createProblemMarker(wd, EventBAttributes.PREDICATE_ATTRIBUTE, TheoryGraphProblem.WDPredMissingError);
				operatorInformation.setHasError();
				theoryAccuracyInfo.setNotAccurate();
				continue;
			}
			Predicate pred = CoreUtilities.parseAndCheckPredicate(wd, repository.getFormulaFactory(), 
					repository.getTypeEnvironment(), this);
			if(pred == null || !checkAgainstReferencedIdentifiers(pred, wd)){
				operatorInformation.setHasError();
				theoryAccuracyInfo.setNotAccurate();
				continue;
			}
			else {
				if(pred.getTag() != BTRUE)
					wdPredicates.add(pred);
			}
		}
		return AstUtilities.conjunctPredicates(wdPredicates, repository.getFormulaFactory());
	}

	private boolean checkAgainstReferencedIdentifiers(Predicate wdPredicate, IOperatorWDCondition wdConditionElement)
			throws CoreException {
		FreeIdentifier[] idents = wdPredicate.getFreeIdentifiers();
		List<String> notAllowed = new ArrayList<String>();
		for (FreeIdentifier ident : idents) {
			if (!operatorInformation.isAllowedIdentifier(ident)) {
				notAllowed.add(ident.getName());
			}
		}
		if (notAllowed.size() != 0) {
			createProblemMarker(wdConditionElement, EventBAttributes.PREDICATE_ATTRIBUTE,
					TheoryGraphProblem.OpCannotReferToTheseIdents, GeneralUtilities.toString(notAllowed));
			return false;
		}
		return true;
	}

	@Override
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	@Override
	public void initModule(IRodinElement element, ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		super.initModule(element, repository, monitor);
		theoryAccuracyInfo = (TheoryAccuracyInfo) repository.getState(TheoryAccuracyInfo.STATE_TYPE);
		operatorInformation = (OperatorInformation) repository.getState(OperatorInformation.STATE_TYPE);

	}

	@Override
	public void endModule(IRodinElement element, ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		theoryAccuracyInfo = null;
		operatorInformation = null;
		super.endModule(element, repository, monitor);
	}

}
