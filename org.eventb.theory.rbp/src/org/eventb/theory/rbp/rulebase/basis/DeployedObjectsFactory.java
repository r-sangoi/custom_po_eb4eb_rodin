/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.rulebase.basis;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.Predicate;
import org.eventb.theory.core.IExtensionRulesSource;
import org.eventb.theory.core.ISCAxiomaticDefinitionAxiom;
import org.eventb.theory.core.ISCGiven;
import org.eventb.theory.core.ISCInfer;
import org.eventb.theory.core.ISCInferenceRule;
import org.eventb.theory.core.ISCMetavariable;
import org.eventb.theory.core.ISCProofRulesBlock;
import org.eventb.theory.core.ISCRewriteRule;
import org.eventb.theory.core.ISCRewriteRuleRightHandSide;
import org.eventb.theory.core.ISCTheorem;
import org.eventb.theory.rbp.utils.ProverUtilities;


/**
 * @author maamria
 * 
 */
public class DeployedObjectsFactory {

	public static List<IDeployedTheorem> getDeployedTheorems(IExtensionRulesSource source, ITypeEnvironment typeEnvironment){
		try {
			List<IDeployedTheorem> result = new ArrayList<IDeployedTheorem>();
			ISCTheorem[] scTheorems = source.getTheorems();
			for (ISCTheorem scTheorem : scTheorems){
				if (!scTheorem.hasLabel() || !scTheorem.hasPredicateString() ||
						!scTheorem.hasOrderAttribute()){
					continue;
				}
				if (scTheorem.getSource() instanceof ISCAxiomaticDefinitionAxiom) {
					IDeployedTheorem deployedTheorem = 
						new DeployedTheorem(scTheorem.getLabel(), 
								scTheorem.getPredicate(typeEnvironment), scTheorem.getOrder(), true);
					result.add(deployedTheorem);
				}
				else {
					IDeployedTheorem deployedTheorem = 
					new DeployedTheorem(scTheorem.getLabel(), 
							scTheorem.getPredicate(typeEnvironment), scTheorem.getOrder(), false);
					result.add(deployedTheorem);
				}
			}
			return result;
		} catch(CoreException e){
			ProverUtilities.log(e, "error creating deployed theorems from "+ source);
		}
		return new ArrayList<IDeployedTheorem>();
	}
	
	public static List<IDeployedRewriteRule> getDeployedRewriteRules(
			ISCProofRulesBlock block, FormulaFactory factory,
			ITypeEnvironment typeEnvironment) {
		try {
			List<IDeployedRewriteRule> result = new ArrayList<IDeployedRewriteRule>();
			ITypeEnvironmentBuilder augTypeEnvironment = typeEnvironment.makeBuilder();
			for (ISCMetavariable var : block.getMetavariables()) {
				augTypeEnvironment.add(var.getIdentifier(factory));
			}
			ISCRewriteRule[] rules = block.getRewriteRules();
			for (ISCRewriteRule rule : rules) {
				IDeployedRewriteRule deployedRewriteRule = getDeployedRewriteRule(
						rule, factory, augTypeEnvironment);
				if (deployedRewriteRule != null)
					result.add(deployedRewriteRule);
			}

			return result;
		} catch (CoreException e) {
			ProverUtilities.log(e, "error creating deployed inference rules from "+ block);
		}
		return new ArrayList<IDeployedRewriteRule>();
	}

	public static List<IDeployedInferenceRule> getDeployedInferenceRules(
			ISCProofRulesBlock block, FormulaFactory factory,
			ITypeEnvironment typeEnvironment) {
		try {
			List<IDeployedInferenceRule> result = new ArrayList<IDeployedInferenceRule>();
			ITypeEnvironmentBuilder augTypeEnvironment = typeEnvironment.makeBuilder();
			ISCMetavariable[] vars = block.getMetavariables();
			for (ISCMetavariable var : vars) {
				augTypeEnvironment.add(var.getIdentifier(factory));
			}
			ISCInferenceRule[] rules = block.getInferenceRules();
			for (ISCInferenceRule rule : rules) {
				IDeployedInferenceRule deployedInferenceRule = getDeployedInferenceRule(
						rule, augTypeEnvironment);
				if (deployedInferenceRule != null)
					result.add(deployedInferenceRule);
			}

			return result;
		} catch (CoreException e) {
			ProverUtilities.log(e, "error creating deployed inference rules from "+ block);
		}
		return new ArrayList<IDeployedInferenceRule>();
	}

	public static IDeployedInferenceRule getDeployedInferenceRule(
			ISCInferenceRule rule, ITypeEnvironment typeEnvironment) {
		try {
			// BUG fix, if not accurate don't load
			if (!rule.isAccurate()){
				return null;
			}
			String ruleName = rule.getLabel();
			String theoryName = rule.getRoot().getElementName();
			boolean isAutomatic = rule.isAutomatic();
			boolean isInteractive = rule.isInteractive();
			String toolTip = rule.getDescription();
			String description = rule.getDescription();

			List<IDeployedGiven> givens = new ArrayList<IDeployedGiven>();
			List<IDeployedGiven> hypGivens = new ArrayList<IDeployedGiven>();
			for (ISCGiven given : rule.getGivens()) {
				IDeployedGiven deployedGiven = getDeployedGiven(given, typeEnvironment);
				if (deployedGiven == null) {
					return null;
				}
				if (given.hasHypAttribute() && given.isHyp()){
					hypGivens.add(deployedGiven);
				}
				else{
					givens.add(deployedGiven);
				}
				
			}
			IDeployedInfer infer = getDeployedInfer(rule.getInfers()[0],
					typeEnvironment);
			if(infer == null){
				return null;
			}

			IDeployedInferenceRule infRule = new DeployedInferenceRule(rule.getRodinProject().getElementName(),
					ruleName, theoryName, isAutomatic, isInteractive, true,
					toolTip, description,
					rule.isSuitableForBackwardReasoning(),
					rule.isSuitableForForwardReasoning(), givens, hypGivens, infer,
					typeEnvironment);
			return infRule;
		} catch (CoreException e) {
			ProverUtilities.log(e, "error creating deployed inference rule from "+ rule);
		}
		return null;
	}

	public static IDeployedRewriteRule getDeployedRewriteRule(
			ISCRewriteRule rule, FormulaFactory factory,
			ITypeEnvironment typeEnvironment) {
		try {
			if (!rule.isAccurate()){
				return null;
			}
			String ruleName = rule.getLabel();
			String theoryName = rule.getRoot().getElementName();
			Formula<?> lhs = rule.getSCFormula(factory, typeEnvironment);
			if (lhs == null){
				return null;
			}
			List<IDeployedRuleRHS> ruleRHSs = new ArrayList<IDeployedRuleRHS>();
			for (ISCRewriteRuleRightHandSide rhs : rule.getRuleRHSs()) {
				IDeployedRuleRHS deployedRuleRHS = getDeployedRuleRHS(rhs,
						factory, typeEnvironment);
				if (deployedRuleRHS == null) {
					return null;
				}
				ruleRHSs.add(deployedRuleRHS);
			}
			boolean isAutomatic = rule.isAutomatic();
			boolean isInteractive = rule.isInteractive();
			boolean isComplete = rule.isComplete();
			boolean isDefinitional = rule.hasDefinitionalAttribute() && rule.isDefinitional();
			String toolTip = rule.getDescription();
			String description = rule.getDescription();

			IDeployedRewriteRule depRule = new DeployedRewriteRule(rule.getRodinProject().getElementName(), ruleName,
					theoryName, lhs, ruleRHSs, isAutomatic, isInteractive,
					isComplete, isDefinitional,true, toolTip, description, typeEnvironment);
			return depRule;
		} catch (CoreException e) {
			ProverUtilities.log(e, "error creating deployed rewrite rule from "+ rule);
		}
		return null;
	}

	public static IDeployedRuleRHS getDeployedRuleRHS(
			ISCRewriteRuleRightHandSide rhs, FormulaFactory factory,
			ITypeEnvironment typeEnvironment) {
		try {
			String name = rhs.getLabel();
			Formula<?> rhsForm = rhs.getSCFormula(factory, typeEnvironment);
			Predicate cond = rhs.getPredicate(typeEnvironment);
			IDeployedRuleRHS depRHS = new DeployedRuleRHS(name, rhsForm, cond);
			return depRHS;
		} catch (CoreException e) {
			ProverUtilities.log(e, "error creating deployed rule rhs from "+ rhs);
		}
		return null;
	}

	public static IDeployedGiven getDeployedGiven(ISCGiven given,
			ITypeEnvironment typeEnvironment) {
		try {
			IDeployedGiven dep = new DeployedGiven(given.getPredicate(typeEnvironment), given.isHyp());
			return dep;
		} catch (CoreException e) {
			ProverUtilities.log(e, "error creating deployed given clause from "+ given);
		}
		return null;
	}

	public static IDeployedInfer getDeployedInfer(ISCInfer infer,
			ITypeEnvironment typeEnvironment) {
		try {
			IDeployedInfer dep = new DeployedInfer(infer.getPredicate(typeEnvironment));
			return dep;
		} catch (CoreException e) {
			ProverUtilities.log(e, "error creating deployed infer clause from "+ infer);
		}
		return null;
	}

}
