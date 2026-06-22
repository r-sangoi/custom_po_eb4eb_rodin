/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.rulebase.basis;

import static java.util.Collections.unmodifiableList;

import java.util.List;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.theory.rbp.utils.ProverUtilities;

/**
 * @author maamria
 *
 */
public final class DeployedRewriteRule extends AbstractDeployedRule implements IDeployedRewriteRule{

	private boolean isDefinitional;
	private boolean isComplete;
	private boolean isConditional;
	private Formula<?> lhs;
	private List<IDeployedRuleRHS> ruleRHSs;
	
	public DeployedRewriteRule(String projectName,String ruleName, String theoryName,
			Formula<?> lhs, List<IDeployedRuleRHS> ruleRHSs,
			boolean isAutomatic, boolean isInteractive, boolean isComplete, 
			boolean isDefinitional, boolean isSound, String toolTip, 
			String description, ITypeEnvironment typeEnv){
		super(ruleName, theoryName, projectName, isAutomatic, isInteractive, isSound, toolTip, description, typeEnv);
		this.lhs = lhs;
		this.ruleRHSs = unmodifiableList(ruleRHSs);
		this.isComplete = isComplete;
		this.isConditional = computeConditionality();
		this.isDefinitional = isDefinitional;
	}
	
	public Formula<?> getLeftHandSide() {
		return lhs;
	}

	
	public List<IDeployedRuleRHS> getRightHandSides() {
		return ruleRHSs;
	}

	public boolean isComplete() {
		return isComplete;
	}

	
	public boolean isConditional() {
		return isConditional;
	}
	
	
	public boolean isExpression() {
		return lhs instanceof Expression;
	}
	
	public boolean isDefinitional() {
		return isDefinitional;
	}

	private boolean computeConditionality(){
		boolean isCond = true;
		if(ruleRHSs.size() == 1){
			IDeployedRuleRHS rhs0 = ruleRHSs.get(0);
			Predicate cond = rhs0.getCondition();
			FormulaFactory factory = cond.getFactory();
			if (cond.equals(factory.makeLiteralPredicate(Predicate.BTRUE, null))) {
				isCond = false;
			}
		}
		return isCond;
	}
	
	public boolean equals(Object o){
		if(o==null || !(o instanceof DeployedRewriteRule)){
			return false;
		}
		if(this == o){
			return true;
		}
		DeployedRewriteRule deployeRule = (DeployedRewriteRule)o;
		return ruleRHSs.equals(deployeRule.ruleRHSs) && ruleName.equals(deployeRule.ruleName) &&
			theoryName.equals(deployeRule.theoryName) && toolTip.equals(deployeRule.toolTip) &&
			description.equals(deployeRule.description) && lhs.equals(deployeRule.lhs) &&
			isComplete == deployeRule.isComplete && isAutomatic == deployeRule.isAutomatic &&
			isInteractive == deployeRule.isInteractive && isDefinitional == deployeRule.isDefinitional;
	}
	
	public int hashCode(){
		return ProverUtilities.
		combineHashCode(ruleName, theoryName, lhs, ruleRHSs, toolTip, description, 
				new Boolean(isAutomatic && isInteractive && isInteractive && isDefinitional));
	}
	
	public String toString(){
		return theoryName +"."+ruleName+" : "+ lhs +"\n |->"+ProverUtilities.toString(ruleRHSs)+"\n";
	}
}
