/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.reasoning.structures;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;


import org.eventb.core.seqprover.IProofRule.IAntecedent;


/**
 * A simple implementation of a reduced proof tree.
 * 
 * @author maamria
 *
 */
public class InferenceDerivationTree extends TreeNode<IAntecedent>{

	/**
	 * A variable to indicate whether more derivations can be carried out on this tree.
	 */
	protected boolean deriveFurther;
	protected boolean hasBeenDerived;
	
	public InferenceDerivationTree(IAntecedent value,
			TreeNode<IAntecedent> parent) {
		super(value, parent);
		deriveFurther = true;
		hasBeenDerived = false;
	}
	
	/**
	 * Sets the children antecedents of this tree node.
	 * @param col the children antecedents
	 */
	public void setAntecedents(Collection<IAntecedent> col){
		if(col.size()==0){
			deriveFurther = false;
		}
		hasBeenDerived = true;
		setChildren(col);
	}
	
	/**
	 * Returns the antecedents that are the leafs of this reduced proof tree node.
	 * This corresponds to undischarged sequents at the bottom of the tree.
	 * @return the leaf antecedents
	 */
	public Set<IAntecedent> getLeafAntecedents(){
		Collection<IAntecedent> leafs = getLeafValues();
		if(leafs == null)
			return null;
		Set<IAntecedent> set = new LinkedHashSet<IAntecedent>(getLeafValues());
		return set;
	}
	
	/**
	 * Returns the antecedent that is stored in this node.
	 * @return the antecedent
	 */
	public IAntecedent getAntecedent(){
		return getValue();
	}
	
	/**
	 * Returns the inference trees that are the children of this tree.
	 * @return children inference trees
	 */
	public Set<InferenceDerivationTree> getInferenceTrees(){
		Set<InferenceDerivationTree> set = new LinkedHashSet<InferenceDerivationTree>();
		for (TreeNode<IAntecedent> node : getLeafs()){
			set.add((InferenceDerivationTree) node);
		}
		return set;
	}

	@Override
	protected TreeNode<IAntecedent> getNode(IAntecedent node) {
		return new InferenceDerivationTree(node, this);
	}
	
	/**
	 * Returns whether to continue derivations or the tree has been finalised. This corresponds to discharging a sequent in
	 * a proof tree.
	 * @return whether to continue derivations
	 */
	public boolean continueDeriving(){
		return deriveFurther;
	}
	
	public boolean hasBeenDerived(){
		return hasBeenDerived;
	}
	
	public String toString(){
		return value.getGoal().toString() + "\n - "+ children;
	}
	
}
