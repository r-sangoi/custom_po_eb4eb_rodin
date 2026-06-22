/*******************************************************************************
 * Copyright (c) 2011,2016 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.ast.extensions.pm.assoc;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ISpecialization;

/**
 * <p>
 * Implementation for Associative-Commutative Expression problem.
 * </p>
 *
 * @author maamria
 * @author htson: Re-implements using ISpecialization
 * @version 2.0
 * @since 1.0
 */
public class ACExpressionProblem extends ACProblem<Expression> implements
		IAssociativityProblem {

	/**
	 * Constructing an Associative-Commutative Expression problem.
	 * 
	 * @param tag
	 *            the AC-Expression tag
	 * @param formulae
	 *            the array of formulae to match
	 * @param patterns
	 *            the array of the patterns to match.
	 */
	public ACExpressionProblem(int tag, Expression[] formulae,
			Expression[] patterns) {
		super(tag, formulae, patterns);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IAssociativityProblem#solve(ISpecialization)
	 */
	@Override
	public ISpecialization solve(ISpecialization specialization) {
		// TODO Implement solving AC Expression problem 
		return null;
	}
	
//	@Override
//	protected boolean mapVariables(List<IndexedFormula<Expression>> usedUpFormulae, Binding initialBinding) {
//		int sizeOfVariables = variables.size();
//		if (sizeOfVariables > 0) {
//			List<IndexedFormula<Expression>> availableFormulae = new ArrayList<IndexedFormula<Expression>>();
//			availableFormulae.addAll(indexedFormulae);
//			availableFormulae.removeAll(usedUpFormulae);
//			// we cannot solve if not enough formulae to draw from
//			if (availableFormulae.size() < sizeOfVariables) {
//				return false;
//			}
//			List<IndexedFormula<Expression>> remainingVars = new ArrayList<IndexedFormula<Expression>>();
//			for (IndexedFormula<Expression> indexedVariable : variables){
//				FreeIdentifier freeIdentifier = (FreeIdentifier) indexedVariable.getFormula();
//				Expression currentMapping = initialBinding.getCurrentMapping(freeIdentifier);
//				if(currentMapping != null){
//					IndexedFormula<Expression> indexedFormula = null;
//					if((indexedFormula=getMatch(availableFormulae, currentMapping)) == null){
//						return false;
//					}
//					usedUpFormulae.add(indexedFormula);
//				}
//				else {
//					remainingVars.add(indexedVariable);
//				}
//			}
//			// remove used up formulae again
//			availableFormulae.removeAll(usedUpFormulae);
//			if(remainingVars.size() > availableFormulae.size()){
//				return false;
//			}
//			if(remainingVars.isEmpty()){
//				return true;
//			}
//			int sizeOfRemainingVars = remainingVars.size();
//			for (int i = 0; i < sizeOfRemainingVars - 1; i++) {
//				IndexedFormula<Expression> var = remainingVars.get(i);
//				Expression formula = availableFormulae.get(i).getFormula();
//				// TODO fix bug
//				if (!initialBinding.putExpressionMapping((FreeIdentifier) var.getFormula(), 
//						formula)){
//					return false;
//				}
//				usedUpFormulae.add(availableFormulae.get(i));
//			}
//			// remove used up formulae again
//			availableFormulae.removeAll(usedUpFormulae);
//			IndexedFormula<Expression> lastVar = remainingVars.get(sizeOfRemainingVars-1);
//			List<Expression> remainingExprs = getFormulae(availableFormulae);
//			if(!initialBinding.putExpressionMapping((FreeIdentifier) lastVar.getFormula(), 
//					AstUtilities.makeAppropriateAssociativeExpression(
//							tag, existingBinding.getFormulaFactory(), remainingExprs.toArray(new Expression[remainingExprs.size()])))){
//				return false;
//			}
//			usedUpFormulae.addAll(availableFormulae);
//		}
//		return true;
//	}
//
//	@Override
//	protected void addAssociativeComplement(List<IndexedFormula<Expression>> formulae, Binding binding) {
//		List<Expression> list = new ArrayList<Expression>();
//		for (IndexedFormula<Expression> formula : formulae) {
//			list.add(formula.getFormula());
//		}
//		Expression comp = AstUtilities.makeAppropriateAssociativeExpression(tag, binding.getFormulaFactory(), list.toArray(new Expression[list.size()]));
//		binding.setAssociativeExpressionComplement(new AssociativeExpressionComplement(tag, null, comp));
//	}
}
