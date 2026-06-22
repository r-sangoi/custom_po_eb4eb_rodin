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
import org.eventb.core.EventBAttributes;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.sc.SCCore;
import org.eventb.core.tool.IModuleType;
import org.eventb.theory.core.IGiven;
import org.eventb.theory.core.IInferenceRule;
import org.eventb.theory.core.ISCGiven;
import org.eventb.theory.core.ISCInferenceRule;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.TheoryGraphProblem;

/**
 * @author maamria
 * 
 */
public class InferenceGivenClauseModule extends
		InferenceClausesModule<IGiven, ISCGiven> {

	public static final IModuleType<InferenceGivenClauseModule> MODULE_TYPE = SCCore
			.getModuleType(TheoryPlugin.PLUGIN_ID + ".inferenceGivenClauseModule");

	@Override
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	@Override
	protected IGiven[] getClauses(IInferenceRule rule) throws CoreException {
		return rule.getGivens();
	}

	@Override
	protected void addIdentifiers(Predicate predicate) throws CoreException{
		Collection<GivenType> types = predicate.getGivenTypes();
		FreeIdentifier iTypes[] = new FreeIdentifier[types.size()];
		int i = 0;
		for (GivenType type : types) {
			iTypes[i] = factory.makeFreeIdentifier(type.getName(), null,
					typeEnvironment.getType(type.getName()));
			i++;
		}
		inferenceIdentifiers.addGivenIdentifiers(iTypes);
		inferenceIdentifiers.addGivenIdentifiers(predicate.getFreeIdentifiers());
		
	}
	
	@Override
	protected void addHypIdentifiers(Predicate predicate) throws CoreException{
		Collection<GivenType> types = predicate.getGivenTypes();
		FreeIdentifier iTypes[] = new FreeIdentifier[types.size()];
		int i = 0;
		for (GivenType type : types) {
			iTypes[i] = factory.makeFreeIdentifier(type.getName(), null,
					typeEnvironment.getType(type.getName()));
			i++;
		}
		inferenceIdentifiers.addHypIdentifiers(iTypes);
		inferenceIdentifiers.addHypIdentifiers(predicate.getFreeIdentifiers());
		
	}

	@Override
	protected ISCGiven getSCClause(ISCInferenceRule parent, String name) {
		ISCGiven scGiven = parent.getGiven(name);
		return scGiven;
	}

	@Override
	protected boolean checkPredicate(Predicate predicate, IGiven clause)
			throws CoreException {
		if (predicate.getTag() == BTRUE) {
			createProblemMarker(clause, EventBAttributes.PREDICATE_ATTRIBUTE,
					TheoryGraphProblem.InferenceGivenBTRUEPredWarn);
		}
		return true;
	}

	@Override
	protected boolean checkClauses(IGiven[] clauses, IInferenceRule rule) throws CoreException {
		return true;
	}

	@Override
	protected void processSCClause(ISCGiven scClause, IGiven clause) throws CoreException{
		scClause.setHyp(clause.hasHypAttribute() && clause.isHyp(), null);
	}

}
