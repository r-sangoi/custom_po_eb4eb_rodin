/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.sc.modules;

import static org.eventb.core.ast.Formula.BTRUE;

import java.util.Collection;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.theory.core.IInfer;
import org.eventb.theory.core.IInferenceRule;
import org.eventb.theory.core.ISCInfer;
import org.eventb.theory.core.ISCInferenceRule;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.TheoryGraphProblem;
import org.eventb.theory.core.sc.states.TheoryAccuracyInfo;
import org.rodinp.core.IRodinElement;

/**
 * @author maamria
 * 
 */
public class InferenceInferClauseModule extends InferenceClausesModule<IInfer, ISCInfer> {

	public static final IModuleType<InferenceInferClauseModule> MODULE_TYPE = SCCore
			.getModuleType(TheoryPlugin.PLUGIN_ID + ".inferenceInferClauseModule");
	
	private TheoryAccuracyInfo accuracyInfo;

	@Override
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	@Override
	protected IInfer[] getClauses(IInferenceRule rule) throws CoreException {
		return rule.getInfers();
	}

	@Override
	protected void addIdentifiers(Predicate predicate) throws CoreException {
		Collection<GivenType> types = predicate.getGivenTypes();
		FreeIdentifier iTypes[] = new FreeIdentifier[types.size()];
		int i = 0;
		for (GivenType type : types) {
			iTypes[i] = factory.makeFreeIdentifier(type.getName(), null, typeEnvironment.getType(type.getName()));
			i++;
		}
		inferenceIdentifiers.addInferIdentifiers(iTypes);
		inferenceIdentifiers.addInferIdentifiers(predicate.getFreeIdentifiers());

	}

	@Override
	protected ISCInfer getSCClause(ISCInferenceRule parent, String name) {
		ISCInfer scInfer = parent.getInfer(name);
		return scInfer;
	}

	@Override
	protected boolean checkPredicate(Predicate predicate, IInfer clause) throws CoreException {
		if (predicate.getTag() == BTRUE) {
			createProblemMarker(clause, EventBAttributes.PREDICATE_ATTRIBUTE,
					TheoryGraphProblem.InferenceInferBTRUEPredErr);
			accuracyInfo.setNotAccurate();
			return false;
		}
		return true;
	}

	@Override
	protected boolean checkClauses(IInfer[] clauses, IInferenceRule rule) throws CoreException {
		// Rule must have one infer clause
		if (clauses.length != 1) {
			createProblemMarker(rule, EventBAttributes.LABEL_ATTRIBUTE, TheoryGraphProblem.RuleInfersError,
					rule.getLabel());
			accuracyInfo.setNotAccurate();
			return false;
		}
		return true;
	}

	@Override
	protected void processSCClause(ISCInfer scClause, IInfer clause) throws CoreException {}
	
	@Override
	public void initModule(
			IRodinElement element,
			ISCStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		super.initModule(element, repository, monitor);
		accuracyInfo = (TheoryAccuracyInfo) repository.getState(TheoryAccuracyInfo.STATE_TYPE);
	}
	

	@Override
	public void endModule(
			IRodinElement element,
			ISCStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		accuracyInfo = null;
		super.endModule(element, repository, monitor);
	}

	@Override
	protected void addHypIdentifiers(Predicate predicate) throws CoreException {
		
	}

}
