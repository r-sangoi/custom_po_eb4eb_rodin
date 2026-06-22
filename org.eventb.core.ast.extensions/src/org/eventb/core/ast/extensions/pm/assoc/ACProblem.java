/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.ast.extensions.pm.assoc;

import org.eventb.core.ast.Formula;

/**
 * A basic implementation of an AC matching problem.
 * 
 * @author maamria
 * 
 */
/**
 * <p>
 * Abstract implementation for Associative-Commutative Formula problem.
 * </p>
 *
 * @author maamria
 * @author htson: Re-implements using ISpecialization
 * @version 2.0
 * @since 1.0
 */
public abstract class ACProblem<F extends Formula<F>> extends
		AssociativityProblem<F> implements IAssociativityProblem {

	public ACProblem(int tag, F[] formulae, F[] patterns) {
		super(tag, formulae, patterns);
	}
//
//	public Binding solve(boolean acceptPartialMatch) {
//		if (!isSolvable) {
//			return null;
//		}
//		Binding initialBinding = existingBinding.clone();
//		boolean searchSpaceEmpty = searchSpace.size() == 0;
//		// trace consumed formulae
//		List<IndexedFormula<F>> usedUpFormulae = new ArrayList<IndexedFormula<F>>();
//		if (!searchSpaceEmpty) {
//			MatchEntry<F> matchEntry = searchSpace.get(0);
//			List<Match<F>> matchesList = matchEntry.getMatches();
//			boolean solved = false;
//			for (Match<F> match : matchesList) {
//				ACMatchStack<F> matchStack = new ACMatchStack<F>(matcher, match);
//				if (explore(1, matchStack)) {
//					Binding matchBinding = matchStack.getFinalBinding();
//					matchBinding.makeImmutable();
//					// if we cannot insert the match binding in the original binding
//					if(!initialBinding.isBindingInsertable(matchBinding)){
//						continue;
//					}
//					solved = initialBinding.insertBinding(matchBinding);
//					usedUpFormulae = matchStack.getUsedUpFormulae();
//					break;
//				}
//			}
//			if (!solved) {
//				return null;
//			}
//		}
//		if(!mapVariables(usedUpFormulae, initialBinding)){
//			return null;
//		}
//		List<IndexedFormula<F>> leftFormulae = new ArrayList<IndexedFormula<F>>(indexedFormulae);
//		leftFormulae.removeAll(usedUpFormulae);
//		if (leftFormulae.size() > 0){
//			if(!acceptPartialMatch){
//				return null;
//			}
//			addAssociativeComplement(leftFormulae, initialBinding);
//		}
//		return initialBinding;
//	}
//
//	protected boolean explore(int patternIndex, ACMatchStack<F> matchStack) {
////		if (patternIndex < 1) {
////			// we backtracked too much
////			return false;
////		}
////		if (patternIndex == searchSpace.size()) {
////			// finished all of them
////			return true;
////		}
////		MatchEntry<F> matchEntry = searchSpace.get(patternIndex);
////		List<Match<F>> matchesList = matchEntry.getMatches();
////		for (Match<F> match : matchesList) {
////			if (matchStack.push(match)) {
////				return explore(patternIndex + 1, matchStack);
////			}
////		}
////		matchStack.pop();
////		return explore(patternIndex - 1, matchStack);
//
//		if (patternIndex == searchSpace.size()) {
//			// finished all of them
//			return true;
//		}
//		MatchEntry<F> matchEntry = searchSpace.get(patternIndex);
//		List<Match<F>> matchesList = matchEntry.getMatches();
//		boolean flag = false;
//		for (Match<F> match : matchesList) {
//			if (matchStack.push(match)) {
//				if (explore(patternIndex + 1, matchStack)) {
//					flag = true; 
//					break;
//				}
//			}
//		}
//		if (!flag) {
//			matchStack.pop();
//			return false;
//		}
//		else	
//			return true;
//	}
//	
//	/**
//	 * Maps the rest of the variables to finish the mapping process.
//	 * @param usedUpFormulae the formulae not available to draw matches from
//	 * @param initialBinding the binding to fill
//	 * @return whether all variables have been mapped successfully
//	 */
//	protected abstract boolean mapVariables(List<IndexedFormula<F>> usedUpFormulae, Binding initialBinding);
//	
//	/**
//	 * Sets the associative complement consisting of the given formulae.
//	 * @param formulae the formulae
//	 * @param binding the target binding
//	 */
//	protected abstract void addAssociativeComplement(List<IndexedFormula<F>> formulae, Binding binding);
}
