/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.reasoning.structures;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * A basic implementation of a tree node.
 * 
 * @author maamria
 * 
 * @param E the type of the value stored in the node
 *
 */
public abstract class TreeNode<E> implements ITreeNode<E>{
	
	protected E value;
	protected TreeNode<E> parent;
	protected Map<E, TreeNode<E>> childrenMap;
	protected Set<TreeNode<E>> children;
	
	protected TreeNode(E value, TreeNode<E> parent){
		this.value = value;
		this.parent = parent;
	}
	
	public E getValue(){
		return value;
	}
	
	public void setChildren(Collection<E> col){
		if (col == null)
			throw new IllegalArgumentException("cannot accept null collection for children");
		this.childrenMap = new LinkedHashMap<E, TreeNode<E>>();
		this.children = new LinkedHashSet<TreeNode<E>>();
		for (E node : col){
			addChild(node);
		}
	}
	
	public Collection<E> getLeafValues(){
		Collection<TreeNode<E>> nodeLeafs = getLeafs();
		// no children , this node is a leaf itself
		if(nodeLeafs == null){
			return null;
		}
		Set<E> leafs = new LinkedHashSet<E>();
		for (TreeNode<E> node : getLeafs()){
			leafs.add(node.getValue());
		}
		return leafs;
	}
	
	/**
	 * Add the given child to the set of the children of this tree node.
	 * @param child the child to add
	 */
	protected void addChild(E child){
		TreeNode<E> newNode = getNode(child);
		childrenMap.put(child, newNode);
		children.add(newNode);
	}
	
	/**
	 * Returns the leaf nodes of this tree node.
	 * @return the leaf nodes
	 */
	public Collection<TreeNode<E>> getLeafs(){
		// this node is a leaf
		if(children == null){
			return null;
		}
		Set<TreeNode<E>> leafs = new LinkedHashSet<TreeNode<E>>();
		for (TreeNode<E> node : children){
			// this child is not a leaf, add its leafs
			if (node.getLeafs() != null)
				leafs.addAll(node.getLeafs());
			// this child is a leaf
			else 
				leafs.add(node);
		}
		return leafs;
	}
	
	/**
	 * Returns a fresh tree node.
	 * @return a tree node
	 */
	protected abstract TreeNode<E> getNode(E node);
}
