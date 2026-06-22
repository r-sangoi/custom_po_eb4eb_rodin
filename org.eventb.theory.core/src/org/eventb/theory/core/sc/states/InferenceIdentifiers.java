/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.sc.states;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.state.ISCState;
import org.eventb.core.tool.IStateType;
import org.eventb.internal.core.tool.state.State;
import org.eventb.theory.core.plugin.TheoryPlugin;

/**
 * An implementation of a repository state that keeps track of free identifiers (including given types) occurring in both 
 * infer and given clauses of an inference rule.
 * 
 * <p> This enables to identify the type of reasoning (backward/forward) which the currently processed inference rule is suitable for.
 * 
 * <p> Note that given types are treated as free identifiers.
 * 
 * @author maamria
 *
 */
@SuppressWarnings("restriction")
public class InferenceIdentifiers extends State implements ISCState{

	public final static IStateType<InferenceIdentifiers> STATE_TYPE = SCCore.getToolStateType(
			TheoryPlugin.PLUGIN_ID + ".inferenceIdentifiers");
	
	private List<FreeIdentifier> inferIdents;
	private List<FreeIdentifier> givenIdents;
	private List<FreeIdentifier> hypIdents;
	
	public InferenceIdentifiers(){
		inferIdents = new ArrayList<FreeIdentifier>();
		givenIdents = new ArrayList<FreeIdentifier>();
		hypIdents = new ArrayList<FreeIdentifier>();
	}
	
	@Override
	public void makeImmutable() {
		inferIdents = Collections.unmodifiableList(inferIdents);
		givenIdents = Collections.unmodifiableList(givenIdents);
		hypIdents = Collections.unmodifiableList(hypIdents);
		super.makeImmutable();
	}
	
	public void addInferIdentifiers(FreeIdentifier[] idents) throws CoreException{
		assertMutable();
		for(FreeIdentifier ident : idents){
			if(!inferIdents.contains(ident)){
				inferIdents.add(ident);
			}
		}
	}

	public void addGivenIdentifiers(FreeIdentifier[] idents) throws CoreException{
		assertMutable();
		for(FreeIdentifier ident : idents){
			if(!givenIdents.contains(ident)){
				givenIdents.add(ident);
			}
		}
	}
	
	public void addHypIdentifiers(FreeIdentifier[] idents) throws CoreException{
		assertMutable();
		for(FreeIdentifier ident : idents){
			if(!hypIdents.contains(ident)){
				hypIdents.add(ident);
			}
		}
	}
	
	public boolean isRuleApplicable() throws CoreException{
		assertImmutable();
		return givenIdents.containsAll(inferIdents) ||
			inferIdents.containsAll(givenIdents);
	}
	
	public boolean isRuleBackwardApplicable() throws CoreException{
		assertImmutable();
		List<FreeIdentifier> inferHypIdents = new ArrayList<FreeIdentifier>();
		inferHypIdents.addAll(inferIdents);
		for(FreeIdentifier ident : hypIdents){
			if(!inferHypIdents.contains(ident)){
				inferHypIdents.add(ident);
			}
		}
		List<FreeIdentifier> givenNotHypIdents = new ArrayList<FreeIdentifier>();
		givenNotHypIdents.addAll(givenIdents);
		givenNotHypIdents.removeAll(hypIdents);
		return inferIdents.containsAll(givenIdents) ||
				inferHypIdents.containsAll(givenNotHypIdents);
	}
	
	public boolean isRuleForwardApplicable() throws CoreException{
		assertImmutable();
		return givenIdents.containsAll(inferIdents);
	}
	
	public boolean isRuleApplicableInBothDirections() throws CoreException{
		assertImmutable();
		return isRuleBackwardApplicable() &&
			isRuleForwardApplicable();
	}
	
	@Override
	public IStateType<?> getStateType() {
		return STATE_TYPE;
	}

}
