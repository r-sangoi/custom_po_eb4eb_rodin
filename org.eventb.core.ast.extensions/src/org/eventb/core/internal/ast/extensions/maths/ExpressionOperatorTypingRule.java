/*******************************************************************************
 * Copyright (c) 2010, 2016 University of Southampton and others.
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
import java.util.Map;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.IExtendedFormula;
import org.eventb.core.ast.extension.ITypeCheckMediator;
import org.eventb.core.ast.extension.ITypeMediator;
import org.eventb.core.ast.extensions.maths.AstUtilities;

/**
 * Implementation for an extended expression typing rule.
 * 
 * <p> The typing rule must be able to synthesise a type of an extended expression based on the types
 * of its children.
 * <p> The typing rule must be able to verify if a proposed type is indeed an acceptable type for a given
 * expression defined by its children.
 * <p> The typing rule must be able to type check an extended expression.
 * 
 * @author maamria
 * @version 1.1
 * @since 1.0
 */
public class ExpressionOperatorTypingRule extends OperatorTypingRule{

	protected Type resultantType;
	protected boolean isAssociative;

	public ExpressionOperatorTypingRule(List<OperatorArgument> operatorArguments, Predicate wdPredicate, Predicate dWDPredicate,Type resultantType, boolean isAssociative) {
		super(operatorArguments, wdPredicate, dWDPredicate);
		this.resultantType = resultantType;
		this.isAssociative = isAssociative;
		this.typeParameters.addAll(AstUtilities.getGivenTypes(resultantType));
	}

	public String toString() {
		return super.toString() + "=>" + resultantType.toString();
	}
	// FIXED BUG : equality of typing rules for expressions
	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (o == null || !(o instanceof ExpressionOperatorTypingRule)) {
			return false;
		}
		ExpressionOperatorTypingRule rule = (ExpressionOperatorTypingRule) o;
		return super.equals(rule) && isAssociative == rule.isAssociative &&
			resultantType.equals(rule.resultantType);
	}
	
	@Override
	public int hashCode() {
		return super.hashCode() + 7 *resultantType.hashCode() + 11 * (new Boolean(isAssociative)).hashCode();
	}

	public boolean verifyType(Type proposedType, Expression[] childExprs, Predicate[] childPreds) {
		if (childExprs.length != arity && !isAssociative)
			return false;
		Map<GivenType, Type> calculatedInstantiations = new HashMap<GivenType, Type>();
		if (!unifyTypes(resultantType, proposedType, calculatedInstantiations)) {
			return false;
		}
		if (isAssociative) {
			for (int i = 0; i < arity; i++) {
				if (!unifyTypes(resultantType, childExprs[i].getType(), calculatedInstantiations)) {
					return false;
				}
			}
			return true;
		}
		for (int i = 0; i < arity; i++) {
			if (!unifyTypes(operatorArguments.get(i).getArgumentType(), childExprs[i].getType(), calculatedInstantiations)) {
				return false;
			}
		}
		return true;
	}

	public Type typeCheck(ExtendedExpression expression, ITypeCheckMediator mediator) {
		Expression[] childExpressions = expression.getChildExpressions();
		Type[] argumentTypesAsVars = new Type[arity];
		HashMap<GivenType, Type> parameterToTypeVarMap = new HashMap<GivenType, Type>();
		for (GivenType givenType : typeParameters) {
			parameterToTypeVarMap.put(givenType, mediator.newTypeVariable());
		}
		// Revealed by testExtensions_022_TC() and testExtensions_023_TC() in TestExtensions
		Type pType = constructPatternType(resultantType, parameterToTypeVarMap, mediator);
		if (isAssociative) {
			for (Expression child : childExpressions) {
				mediator.sameType(child.getType(), pType);
			}
			return pType;
		}
		if (childExpressions.length != arity) {
			return null;
		}
		
		for (int i = 0; i < argumentTypesAsVars.length; i++) {
			argumentTypesAsVars[i] = constructPatternType(operatorArguments.get(i).getArgumentType(), parameterToTypeVarMap, mediator);
		}

		for (int i = 0; i < childExpressions.length; i++) {
			Type currentType = childExpressions[i].getType();
			mediator.sameType(argumentTypesAsVars[i], currentType);
		}
		return pType;
	}

	public Type synthesizeType(Expression[] childExprs, Predicate[] childPreds, ITypeMediator mediator) {
		Type[] types = AstUtilities.getTypes(childExprs);
		return synthesizeType(types, mediator.getFactory());
	}

	protected Type synthesizeType(Type[] childrenTypes, FormulaFactory factory) {
		if (isAssociative) {
			// associative operators always have 2 or more children
			// @htson: Make sure that all children have the same non-null type.
			Type type = childrenTypes[0];
			if (type == null)
				return null;
			for (int i = 1; i != childrenTypes.length; ++i) {
				if (!type.equals(childrenTypes[i]))
					return null;
			}
			return type;
		}

		Expression typeExpression = resultantType.toExpression();
		String rawTypeExp = typeExpression.toString();
		Expression exp = factory.parseExpression(rawTypeExp, null).getParsedExpression();
		Map<FreeIdentifier, Expression> typeSubs = getTypeSubstitutions(childrenTypes, factory);
		// FIXED Bug if type substitutions is empty that means we are dealing with a generic operator, 
		// hence need proposed type and type check
		if (typeSubs == null || typeSubs.isEmpty())
			return null;
		ITypeEnvironment typeEnvironment = generateTypeParametersTypeEnvironment(typeSubs, factory);
		exp.typeCheck(typeEnvironment);
		Expression actTypeExpression = exp.substituteFreeIdents(typeSubs);
		try {
			return actTypeExpression.toType();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
		return null;
	}

	protected Map<FreeIdentifier, Expression> getTypeSubstitutions(Type[] childrenTypes, FormulaFactory factory) {
		Map<FreeIdentifier, Expression> subs = new HashMap<FreeIdentifier, Expression>();
		Map<GivenType, Type> instantiations = new HashMap<GivenType, Type>();
		if (isAssociative) {
			if (!isValidTypeInstantiation(0, childrenTypes[0], instantiations)) {
				return null;
			}
		} else {
			for (int i = 0; i < childrenTypes.length; i++) {

				if (!isValidTypeInstantiation(i, childrenTypes[i], instantiations)) {
					return null;
				}
			}
		}
		for (GivenType gType : instantiations.keySet()) {
			final Type instType = instantiations.get(gType).translate(factory);
			subs.put(factory.makeFreeIdentifier(gType.getName(), null, instType.toExpression().getType()), instType.toExpression());
		}
		return subs;
	}

	@Override
	protected boolean completeInstantiation(IExtendedFormula formula,
			Instantiation inst) {
		final Type actualType = ((ExtendedExpression) formula).getType();
		return inst.matchType(resultantType, actualType);
	}

}
