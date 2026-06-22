/*******************************************************************************
 * Copyright (c) 2010, 2014 University of Southampton and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University of Southampton - initial API and implementation
 *     Systerel - use Specialization
 *******************************************************************************/
package org.eventb.core.internal.ast.extensions.maths;

import java.util.HashMap;
import java.util.List;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedPredicate;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.IExtendedFormula;
import org.eventb.core.ast.extension.ITypeCheckMediator;

/**
 * Implementation for a predicate typing rule.
 * <p> The typing rule must be able to type check an extended predicate.
 * @author maamria
 *
 */
public class PredicateOperatorTypingRule extends OperatorTypingRule{

	/**
	 * @param extension
	 */
	public PredicateOperatorTypingRule(List<OperatorArgument> operatorArguments, Predicate wdPredicate, Predicate dWDPredicate) {
		super(operatorArguments, wdPredicate, dWDPredicate);
	}

	public void typeCheck(ExtendedPredicate predicate,
			ITypeCheckMediator mediator) {
		Expression[] childExpressions = predicate.getChildExpressions();
		Type[] argumentTypesAsVars = new Type[arity];
		HashMap<GivenType, Type> parameterToTypeVarMap = new HashMap<GivenType, Type>();
		for (GivenType givenType : typeParameters) {
			parameterToTypeVarMap.put(givenType,mediator.newTypeVariable());
		}
		for (int i = 0; i < argumentTypesAsVars.length; i++) {
			argumentTypesAsVars[i] = 
					constructPatternType(operatorArguments.get(i).getArgumentType(),
							parameterToTypeVarMap, mediator);
		}
		for (int i = 0; i < childExpressions.length; i++) {
			Type currentType = childExpressions[i].getType();
			mediator.sameType(argumentTypesAsVars[i], currentType);
		}
		
	}

	@Override
	protected boolean completeInstantiation(IExtendedFormula formula,
			Instantiation inst) {
		// Nothing to do
		return true;
	}

}
