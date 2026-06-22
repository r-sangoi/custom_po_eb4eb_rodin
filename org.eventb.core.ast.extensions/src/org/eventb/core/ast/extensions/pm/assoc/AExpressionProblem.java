/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.ast.extensions.pm.assoc;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ISpecialization;
import org.eventb.core.ast.extensions.pm.Matcher;

/**
 * <p>
 * Implementation for Associative Expression problem.
 * </p>
 *
 * @author maamria
 * @author htson: Re-implements using ISpecialization
 * @version 2.0
 * @since 1.0
 */
public class AExpressionProblem extends AssociativityProblem<Expression> {

	public AExpressionProblem(int tag, Expression[] formulae, Expression[] patterns) {
		super(tag, formulae, patterns);
	}

	/**
	 * Recursively solve the problem by traversing the arrays of formulae and
	 * patterns.
	 * 
	 * @see IAssociativityProblem#solve(ISpecialization)
	 * @see #solve(ISpecialization, int, int, int)
	 */
	@Override
	public ISpecialization solve(ISpecialization specialization) {
		if (formulae.length < patterns.length)
			return null;
		return solve(specialization, 0, 0, formulae.length - patterns.length);
	}

	/**
	 * Method for solving the matching problem for associative expression by
	 * traversing the array of formulae and patterns.
	 * 
	 * @param specialization
	 *            The initial specialization.
	 * @param fIndex
	 *            the current index in the array of formulae.
	 * @param pIndex
	 *            the current index in the array of patterns.
	 * @param buffer
	 *            the allowed buffer for matching, i.e., the maximum number of
	 *            additional formulae that can be matched to a single pattern.
	 * @return the resulting specialization if matching is successful. Return
	 *         <code>null</code> otherwise.
	 * @precondition the pIndex is always within bound. The buffer together with
	 *               the difference of the formulae and pattern indices is the
	 *               same as the difference between the lenght of the formula
	 *               and the pattern arrays. This buffer cannot be negative.
	 *               Since this method is recursive, the preconditions are also
	 *               the invariants.
	 */
	private ISpecialization solve(ISpecialization specialization, int fIndex,
			int pIndex, int buffer) {
		// TODO The method can be rewritten so that different termination cases
		// can be considered once and for all.
		
		// ASSERT preconditions.
		assert pIndex < patterns.length;
		assert ((fIndex - pIndex) + buffer == formulae.length - patterns.length); 
		assert buffer >= 0;
		
		Expression pattern = patterns[pIndex];
		if (pattern instanceof FreeIdentifier) {
			// Try to match the pattern with the current formula, but first
			// clone the specialization
			ISpecialization clone = specialization.clone();
			clone = Matcher.unifyTypes(clone, formulae[fIndex].getType(),
					pattern.getType());
			if (clone != null) {
				clone = Matcher.insert(clone, (FreeIdentifier) pattern,
						formulae[fIndex]);
				if (clone != null) {
					if (pIndex + 1 == patterns.length) { // We have finished matching the patterns.
						if (buffer == 0) { // We have exhausted the formulae arrays.
							return clone;
						}
					} else { // We have not yet finished matching all the patterns, recursively solve the problem.
						clone = solve(clone, fIndex+1, pIndex+1, buffer);
						if (clone != null) {
							return clone;
						}
					}
				}
			}

			if (buffer == 0) // If there is no buffer space
				return null;
			
			// Try to match the pattern with a number of formulae (up to a number of allowed buffer)
			FormulaFactory factory = specialization.getFactory();
			int i = 1;
			AssociativeExpression testFormula = factory
					.makeAssociativeExpression(tag, new Expression[] {
							formulae[fIndex], formulae[fIndex + 1] }, null);
			while (i <= buffer) {
				// Try to match the pattern with the current formula, but first clone the specialization
				clone = specialization.clone();
				clone = Matcher.unifyTypes(clone, testFormula.getType(), pattern.getType());
				if (clone != null) {
					clone = Matcher.insert(clone, (FreeIdentifier) pattern, testFormula);
					if (clone != null) {
						if (pIndex + 1 == patterns.length) {
							if (buffer == i)
								return clone;
							else
								return null;
						}
						clone = solve(clone, fIndex+i+1, pIndex+1, buffer-i);
						if (clone != null) {
							return clone;
						}
					}
				}
				i++;
				testFormula = factory
						.makeAssociativeExpression(tag, new Expression[] {
								testFormula, formulae[fIndex + i] }, null);
			}
			return null;
		} else {
			// Try to match current "pattern" with the current "formula"
			specialization = Matcher.match(specialization, formulae[fIndex],
					pattern);
			if (specialization != null) {
				// If match then continue to with the next formula and next
				// pattern.
				if (pIndex + 1 == patterns.length) {
					if (buffer == 0)
						return specialization;
					else
						return null;
				}
				return solve(specialization, fIndex + 1, pIndex + 1, buffer);
			} else {
				return null;
			}
		}
	}

//	protected Match<Expression> getMatchWithRank(List<Match<Expression>> list, boolean highest) {
//		Collections.sort(list, getMatchComparator());
//		if (highest)
//			return list.get(list.size() - 1);
//		else
//			return list.get(0);
//	}
//
//	protected Match<Expression> getSubsequentMatch(List<Match<Expression>> available, int index) {
//		for (Match<Expression> form : available) {
//			if (form.getIndexedFormula().getIndex() == index + 1) {
//				return form;
//			}
//		}
//		return null;
//	}
//
//	protected List<IndexedFormula<Expression>> getSublist(List<IndexedFormula<Expression>> list, int index, boolean before) {
//		List<IndexedFormula<Expression>> newList = new ArrayList<IndexedFormula<Expression>>();
//		for (IndexedFormula<Expression> indexedFormula : list) {
//			if (before) {
//				if (indexedFormula.getIndex() < index) {
//					newList.add(indexedFormula);
//				}
//			} else {
//				if (indexedFormula.getIndex() > index) {
//					newList.add(indexedFormula);
//				}
//			}
//		}
//		return newList;
//	}
//
//	protected Comparator<IndexedFormula<Expression>> getIndexedFormulaComparator() {
//		return new Comparator<IndexedFormula<Expression>>() {
//			@Override
//			public int compare(IndexedFormula<Expression> o1, IndexedFormula<Expression> o2) {
//				if (o1.getIndex() > o2.getIndex()) {
//					return 1;
//				}
//				if (o1.getIndex() < o2.getIndex()) {
//					return -1;
//				}
//				if (o1.equals(o2)) {
//					return 0;
//				}
//				return 1;
//			}
//		};
//	}
//	
//	protected Comparator<Match<Expression>> getMatchComparator() {
//		return new Comparator<Match<Expression>>() {
//			@Override
//			public int compare(Match<Expression> o1, Match<Expression> o2) {
//				if (o1.getIndexedFormula().getIndex() > o2.getIndexedFormula().getIndex()) {
//					return 1;
//				}
//				if (o1.getIndexedFormula().getIndex() < o2.getIndexedFormula().getIndex()) {
//					return -1;
//				}
//				if (o1.equals(o2)) {
//					return 0;
//				}
//				return 1;
//			}
//		};
//	}

//	protected Expression getAssociativeExpression(List<Expression> list) {
//		if (list.size() == 0) {
//			return null;
//		}
//		return AstUtilities.makeAppropriateAssociativeExpression(tag, existingBinding.getFormulaFactory(), list.toArray(new Expression[list.size()]));
//	}

//	private Binding oneVariableOneFormula(List<IndexedFormula<Expression>> availableFormulae, boolean acceptPartialMatch) {
//		if (variables.size() == 1 && searchSpace.size() == 1) {
//			IndexedFormula<Expression> var = variables.get(0);
//			FreeIdentifier identifier = (FreeIdentifier) var.getFormula();
//			Binding initialBinding = existingBinding.clone();
//			MatchEntry<Expression> entry = searchSpace.get(0);
//			Expression varMapping = initialBinding.getCurrentMapping(identifier);
//			if (varMapping != null) {
//				IndexedFormula<Expression> match = getMatch(availableFormulae, varMapping);
//				if (match == null) {
//					return null;
//				}
//				int firstIndex = match.getIndex();
//				Match<Expression> groundMatch = getSubsequentMatch(entry.getMatches(), firstIndex);
//				if (groundMatch == null) {
//					return null;
//				} else {
//					initialBinding.insertBinding(groundMatch.getBinding());
//					List<Expression> beforeSublist = getFormulae(getSublist(availableFormulae, firstIndex, true));
//					List<Expression> afterSublist = getFormulae(getSublist(availableFormulae, firstIndex + 1, false));
//					if (beforeSublist.size() != 0 || afterSublist.size() != 0) {
//						if (!acceptPartialMatch) {
//							return null;
//						} else {
//							initialBinding.setAssociativeExpressionComplement(new AssociativeExpressionComplement(tag, getAssociativeExpression(beforeSublist), getAssociativeExpression(afterSublist)));
//						}
//					}
//				}
//			} else {
//				boolean varIsBefore = var.getIndex() < entry.getIndexedPattern().getIndex();
//				if (varIsBefore) {
//					Match<Expression> match = getMatchWithRank(entry.getMatches(), true);
//					int axisIndex = match.getIndexedFormula().getIndex();
//					List<IndexedFormula<Expression>> beforeIndexedSublist = getSublist(availableFormulae, axisIndex, true);
//					List<Expression> beforeSublist = getFormulae(beforeIndexedSublist);
//					if (beforeSublist.size() == 0) {
//						return null;
//					} else {
//						Expression varMatch = getAssociativeExpression(beforeSublist);
//						if(!initialBinding.putExpressionMapping(identifier, varMatch)){
//							return null;
//						}
//						List<Expression> afterSublist = getFormulae(getSublist(availableFormulae, axisIndex, false));
//						if (afterSublist.size() > 0) {
//							if (!acceptPartialMatch) {
//								return null;
//							} else {
//								initialBinding.setAssociativeExpressionComplement(new AssociativeExpressionComplement(tag, null, getAssociativeExpression(afterSublist)));
//							}
//						}
//					}
//				} else {
//					Match<Expression> match = getMatchWithRank(entry.getMatches(), false);
//					int axisIndex = match.getIndexedFormula().getIndex();
//					List<IndexedFormula<Expression>> afterIndexedSublist = getSublist(availableFormulae, axisIndex, false);
//					List<Expression> afterSublist = getFormulae(afterIndexedSublist);
//					if (afterSublist.size() == 0) {
//						return null;
//					} else {
//						Expression varMatch = getAssociativeExpression(afterSublist);
//						if(!initialBinding.putExpressionMapping(identifier, varMatch)){
//							return null;
//						}
//						List<Expression> beforeSublist = getFormulae(getSublist(availableFormulae, axisIndex, true));
//						if (beforeSublist.size() > 0) {
//							if (!acceptPartialMatch) {
//								return null;
//							} else {
//								initialBinding.setAssociativeExpressionComplement(new AssociativeExpressionComplement(tag, getAssociativeExpression(beforeSublist), null));
//							}
//						}
//					}
//				}
//			}
//			return initialBinding;
//		}
//		return null;
//	}
//
//	private Binding twoVariables(List<IndexedFormula<Expression>> availableFormulae, boolean acceptPartialMatch) {
//		if (variables.size() == 2) {
//			Binding initialBinding = existingBinding.clone();
//			IndexedFormula<Expression> var1 = variables.get(0);
//			FreeIdentifier identifier1 = (FreeIdentifier) var1.getFormula();
//			IndexedFormula<Expression> var2 = variables.get(1);
//			FreeIdentifier identifier2 = (FreeIdentifier) var2.getFormula();
//			Expression varMapping1 = initialBinding.getCurrentMapping(identifier1);
//			Expression varMapping2 = initialBinding.getCurrentMapping(identifier2);
//			if (varMapping1 != null && varMapping2 != null) {
//				if (availableFormulae.size() != 2) {
//					return null;
//				} else {
//					IndexedFormula<Expression> if1 = getMatch(availableFormulae, varMapping1);
//					IndexedFormula<Expression> if2 = getMatch(availableFormulae, varMapping2);
//					if (if1 == null || if2 == null || if1.equals(if2))
//						return null;
//
//				}
//			} else if (varMapping1 != null) {
//				IndexedFormula<Expression> if1 = getMatch(availableFormulae, varMapping1);
//				if (if1 == null) {
//					return null;
//				}
//				List<IndexedFormula<Expression>> afterIndexedSublist = getSublist(availableFormulae, if1.getIndex(), false);
//				List<Expression> afterSublist = getFormulae(afterIndexedSublist);
//				if (afterSublist.size() == 0) {
//					return null;
//				} else {
//					Expression varMatch = getAssociativeExpression(afterSublist);
//					if(!initialBinding.putExpressionMapping(identifier2, varMatch)){
//						return null;
//					}
//					List<Expression> beforeSublist = getFormulae(getSublist(availableFormulae, if1.getIndex(), true));
//					if (beforeSublist.size() > 0) {
//						if (!acceptPartialMatch) {
//							return null;
//						} else {
//							initialBinding.setAssociativeExpressionComplement(new AssociativeExpressionComplement(tag, getAssociativeExpression(beforeSublist), null));
//						}
//					}
//				}
//			} else if (varMapping2 != null) {
//				IndexedFormula<Expression> if2 = getMatch(availableFormulae, varMapping2);
//				if (if2 == null) {
//					return null;
//				}
//				List<IndexedFormula<Expression>> beforeIndexedSublist = getSublist(availableFormulae, if2.getIndex(), true);
//				List<Expression> beforeSublist = getFormulae(beforeIndexedSublist);
//				if (beforeSublist.size() == 0) {
//					return null;
//				} else {
//					Expression varMatch = getAssociativeExpression(beforeSublist);
//					if(!initialBinding.putExpressionMapping(identifier1, varMatch)){
//						return null;
//					}
//					List<Expression> afterSublist = getFormulae(getSublist(availableFormulae, if2.getIndex(), false));
//					if (afterSublist.size() > 0) {
//						if (!acceptPartialMatch) {
//							return null;
//						} else {
//							initialBinding.setAssociativeExpressionComplement(new AssociativeExpressionComplement(tag, null, getAssociativeExpression(afterSublist)));
//						}
//					}
//				}
//			} else {
//				IndexedFormula<Expression> var2Match = availableFormulae.get(availableFormulae.size() - 1);
//				if(!initialBinding.putExpressionMapping(identifier2, var2Match.getFormula())){
//					return null;
//				}
//				List<Expression> beforeFormulae = getFormulae(getSublist(availableFormulae, availableFormulae.size() - 1, true));
//				if(!initialBinding.putExpressionMapping(identifier1, getAssociativeExpression(beforeFormulae))){
//					return null;
//				}
//			}
//			return initialBinding;
//		}
//		return null;
//	}
//	
//	private Binding twoFormulae(List<IndexedFormula<Expression>> availableFormulae, boolean acceptPartialMatch){
//		if (searchSpace.size() == 2) {
//			Binding initialBinding = existingBinding.clone();
//			MatchEntry<Expression> matchEntry1 = searchSpace.get(0);
//			MatchEntry<Expression> matchEntry2 = searchSpace.get(1);
//			IndexedFormula<Expression> indexedFormula1 = matchEntry1.getIndexedPattern();
//			IndexedFormula<Expression> indexedFormula2 = matchEntry2.getIndexedPattern();
//			if (comesBefore(indexedFormula1, indexedFormula2)){
//				for (Match<Expression> match : matchEntry1.getMatches()){
//					for (Match<Expression> otherMatch : matchEntry2.getMatches()){
//						if (otherMatch.equals(match)){
//							continue;
//						}
//						int matchIndex = match.getIndexedFormula().getIndex();
//						if (matchIndex == otherMatch.getIndexedFormula().getIndex() - 1){
//							initialBinding.insertBinding(match.getBinding());
//							initialBinding.insertBinding(otherMatch.getBinding());
//							List<Expression> beforeSublist = getFormulae(getSublist(availableFormulae, matchIndex, true));
//							List<Expression> afterSublist = getFormulae(getSublist(availableFormulae, matchIndex+1, false));
//							if (beforeSublist.size() != 0 || afterSublist.size() != 0) {
//								if(!acceptPartialMatch){
//									continue;
//								}
//								else {
//									initialBinding.setAssociativeExpressionComplement(
//											new AssociativeExpressionComplement(tag, 
//													getAssociativeExpression(beforeSublist), getAssociativeExpression(afterSublist)));
//								}
//							}
//							return initialBinding;
//						}
//					}
//				}
//			}
//			else {
//				for (Match<Expression> match : matchEntry2.getMatches()){
//					for (Match<Expression> otherMatch : matchEntry1.getMatches()){
//						if (otherMatch.equals(match)){
//							continue;
//						}
//						int matchIndex = match.getIndexedFormula().getIndex();
//						if (match.getIndexedFormula().getIndex() == otherMatch.getIndexedFormula().getIndex() - 1){
//							initialBinding.insertBinding(match.getBinding());
//							initialBinding.insertBinding(otherMatch.getBinding());
//							List<Expression> beforeSublist = getFormulae(getSublist(availableFormulae, matchIndex, true));
//							List<Expression> afterSublist = getFormulae(getSublist(availableFormulae, matchIndex+1, false));
//							if (beforeSublist.size() != 0 || afterSublist.size() != 0) {
//								if(!acceptPartialMatch){
//									continue;
//								}
//								else {
//									initialBinding.setAssociativeExpressionComplement(
//											new AssociativeExpressionComplement(tag, 
//													getAssociativeExpression(beforeSublist), getAssociativeExpression(afterSublist)));
//								}
//							}
//							return initialBinding;
//						}
//					}
//				}
//			}
//			
//		}
//		return null;
//	}
	
//	private boolean comesBefore(IndexedFormula<Expression> indexedFormula1, IndexedFormula<Expression> indexedFormula2){
//		if (indexedFormula1.getIndex() > indexedFormula2.getIndex()){
//			return false;
//		}
//		return true;
//	}
}
