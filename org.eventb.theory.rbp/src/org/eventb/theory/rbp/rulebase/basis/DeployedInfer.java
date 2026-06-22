/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.rulebase.basis;

import org.eventb.core.ast.Predicate;

/**
 * @author maamria
 *
 */
public final class DeployedInfer implements IDeployedInfer{

	private Predicate pred;

	public DeployedInfer(Predicate pred){
		this.pred = pred;
	}
	
	@Override
	public Predicate getInferClause() {
		return pred;
	}
	
	public boolean equals(Object o){
		if(o==null || !(o instanceof DeployedInfer)){
			return false;
		}
		if(this == o){
			return true;
		}
		return pred.equals(((DeployedInfer)o).pred);
	}
	
	public int hashCode(){
		return pred.hashCode();
	}
	
	public String toString(){
		return pred.toString();
	}

}
