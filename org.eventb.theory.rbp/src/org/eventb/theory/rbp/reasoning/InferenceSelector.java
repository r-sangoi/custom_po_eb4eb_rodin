/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.reasoning;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ISpecialization;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.extensions.pm.Matcher;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.theory.core.IGeneralRule;
import org.eventb.theory.core.IReasoningTypeElement.ReasoningType;
import org.eventb.theory.internal.rbp.reasoners.input.IPRMetadata;
import org.eventb.theory.internal.rbp.reasoners.input.InferenceInput;
import org.eventb.theory.internal.rbp.reasoners.input.PRMetadata;
import org.eventb.theory.rbp.rulebase.BaseManager;
import org.eventb.theory.rbp.rulebase.IPOContext;
import org.eventb.theory.rbp.rulebase.basis.IDeployedGiven;
import org.eventb.theory.rbp.rulebase.basis.IDeployedInferenceRule;
import org.eventb.theory.rbp.tactics.applications.InferenceTacticApplication;
import org.eventb.ui.prover.ITacticApplication;

/**
 * @author maamria, asiehsalehi
 * 
 * the case when inf rule is tyoe of ISCInferenceRule is commented; 
 * for now the (SC) inf rules defined in a theory are not applicable in proving within the theory
 * TODO: like application of theorems, in POs of each inf rule, the inf rules above that should be available
 * 
 */
public class InferenceSelector {

	protected BaseManager ruleBaseManager;
	protected IPOContext context;

	public InferenceSelector(IPOContext context) {
		ruleBaseManager = BaseManager.getDefault();
		this.context = context;
	}

	public List<ITacticApplication> select(Predicate predicate,
			IProverSequent sequent) {
		if (predicate == null) {
			return backwardApplications(sequent);
		}
		// forward
		else {
			return forwardApplications(sequent, predicate);
		}
	}

	/**
	 * @param sequent
	 * @param predicate
	 * @return
	 */
	private List<ITacticApplication> forwardApplications(
			IProverSequent sequent, Predicate predicate) {
		List<IGeneralRule> rules = ruleBaseManager.getInferenceRules(false,
				ReasoningType.FORWARD, context);
		List<ITacticApplication> apps = new ArrayList<ITacticApplication>();
		for (IGeneralRule rule : rules) {
			if (rule instanceof IDeployedInferenceRule) {
				ITacticApplication app = forwardApplication((IDeployedInferenceRule) rule, sequent, predicate);
				if (app != null)
					apps.add(app);
			} else {
				throw new UnsupportedOperationException(
						"Only deployed inference rules are supported");
			}
		}
		return apps;
	}

	/**
	 * @param rule
	 * @param sequent
	 * @param predicate
	 * @return
	 */
	private ITacticApplication forwardApplication(IDeployedInferenceRule rule,
			IProverSequent sequent, Predicate predicate) {
		List<IDeployedGiven> hypGivens = rule.getHypGivens();
		if (hypGivens.isEmpty())
			return null;
		Predicate firstGivenClause = hypGivens.get(0).getGivenClause();
		// @htson: Translate the given clause to the formula factory of the input predicate.
		firstGivenClause = firstGivenClause.translate(predicate.getFactory());
		ISpecialization specialization = Matcher.match(predicate,
				firstGivenClause);

		if (specialization == null)
			return null;
		
		String projectName = rule.getProjectName();
		String theoryName = rule.getTheoryName();
		String ruleName = rule.getRuleName();
		IPRMetadata prMetadata = new PRMetadata(projectName, theoryName,
				ruleName);
		InferenceInput input = new InferenceInput(prMetadata,
				predicate);

		return new InferenceTacticApplication(input, context);
	}

	/**
	 * @param predicate
	 * @param sequent
	 * @return
	 */
	private List<ITacticApplication> backwardApplications(IProverSequent sequent) {
		// backward
		List<IGeneralRule> rules = ruleBaseManager.getInferenceRules(false,
				ReasoningType.BACKWARD, context);
		List<ITacticApplication> apps = new ArrayList<ITacticApplication>();
		for (IGeneralRule rule : rules) {
			if (rule instanceof IDeployedInferenceRule) {
				ITacticApplication app = backwardApplication((IDeployedInferenceRule) rule, sequent);
				if (app != null)
					apps.add(app);
			} else {
				throw new UnsupportedOperationException(
						"Only deployed inference rules are supported");
			}
		}
		return apps;
	}

	/**
	 * @param rule
	 * @param predicate
	 * @param sequent
	 * @return
	 */
	private ITacticApplication backwardApplication(
			IDeployedInferenceRule rule, IProverSequent sequent) {
		Predicate goal = sequent.goal();
		Predicate inferClause = rule.getInfer()
				.getInferClause();
		FormulaFactory factory = goal.getFactory();
		inferClause = inferClause.translate(factory);
		ISpecialization specialization = Matcher.match(goal,
				inferClause);
		// if goal does not match infer clause then return the empty list.
		if (specialization == null) {
			return null;
		}	
		String projectName = rule.getProjectName();
		String theoryName = rule.getTheoryName();
		String ruleName = rule.getRuleName();
		IPRMetadata prMetadata = new PRMetadata(projectName, theoryName,
				ruleName);
		InferenceInput input = new InferenceInput(prMetadata, null);

		return new InferenceTacticApplication(input, context);
	}

}
