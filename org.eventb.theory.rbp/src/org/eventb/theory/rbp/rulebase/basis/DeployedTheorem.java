/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.rulebase.basis;

import org.eventb.core.ast.Predicate;

/**
 * 
 * @author maamria
 *
 */
public class DeployedTheorem implements IDeployedTheorem{

	private String name;
	private Predicate theorem;
	private int order;
	private boolean axm;

	public DeployedTheorem(String name, Predicate theorem, int order, boolean axm){
		this.name = name;
		this.theorem = theorem;
		this.order = order;
		this.axm = axm;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public Predicate getTheorem() {
		return theorem;
	}

	@Override
	public boolean hasTypeParameters() {
		return theorem.getGivenTypes().size() != 0;
	}

	@Override
	public int getOrder() {
		return order;
	}
	
	@Override
	public boolean isAxm() {
		return axm;
	}

	@Override
	public boolean equals(Object o) {
		if(o == null || !(o instanceof DeployedTheorem)){
			return false;
		}
		if(o == this){
			return true;
		}
		DeployedTheorem other = (DeployedTheorem) o;
		return name.equals(other.name) && theorem.equals(other.theorem) &&
			order == other.order && axm == other.axm;
	}
	
	@Override
	public int hashCode() {
		return 7*name.hashCode() + 11* theorem.hashCode() + order;
	}

	@Override
	public int compareTo(IDeployedTheorem thm) {
		int diff = order - thm.getOrder();
		if (diff < 0){
			return -1;
		}
		if(diff > 0){
			return 1;
		}
		if (diff == 0){
			if(equals(thm)){
				return 0;
			}
		}
		return 1;
	}
}
