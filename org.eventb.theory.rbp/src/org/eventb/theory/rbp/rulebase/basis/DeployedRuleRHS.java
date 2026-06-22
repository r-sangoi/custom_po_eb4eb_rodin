/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.rulebase.basis;

import org.eventb.core.ast.Formula;
import org.eventb.core.ast.Predicate;
import org.eventb.theory.rbp.utils.ProverUtilities;

/**
 * @author maamria
 *
 */
public final class DeployedRuleRHS implements IDeployedRuleRHS{

	private String name;
	private Formula<?> rhs;
	private Predicate cond;

	public DeployedRuleRHS(String name, Formula<?> rhs, Predicate cond){
		this.name = name;
		this.rhs = rhs;
		this.cond = cond;
	}
	
	@Override
	public Predicate getCondition() {
		return cond;
	}

	@Override
	public Formula<?> getRHSFormula() {
		return rhs;
	}

	@Override
	public String getRHSName() {
		return name;
	}
	
	public boolean equals(Object o){
		if(o==null || !(o instanceof DeployedRuleRHS)){
			return false;
		}
		if(this == o){
			return true;
		}
		DeployedRuleRHS depRhs = (DeployedRuleRHS)o;
		return name.equals(depRhs.name) && rhs.equals(depRhs.rhs) && cond.equals(depRhs.cond);
	}
	
	public String toString(){
		return "("+name + " : "+cond  +" -> "+rhs+")";
	}
	
	public int hashCode(){
		return ProverUtilities.
			combineHashCode(name, rhs, cond);
	}

}
