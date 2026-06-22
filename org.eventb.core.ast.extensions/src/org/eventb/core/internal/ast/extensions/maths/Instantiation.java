/*******************************************************************************
 * Copyright (c) 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.internal.ast.extensions.maths;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.BooleanType;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.ISpecialization;
import org.eventb.core.ast.IntegerType;
import org.eventb.core.ast.ParametricType;
import org.eventb.core.ast.PowerSetType;
import org.eventb.core.ast.ProductType;
import org.eventb.core.ast.Type;

/**
 * Represents an instantiation of a polymorphic operator. Instances record the
 * mapping of formal parameter types and formal operator arguments to actual
 * types and expressions. Once populated an instance can be used to instantiate
 * a formula.
 * 
 * @author Laurent Voisin
 */
public class Instantiation {

	private final OperatorArgument[] operatorArguments;
	private final ISpecialization specialization;

	// TODO should be available from ISpecialization API
	private final Set<GivenType> knownFormalTypes;

	public Instantiation(List<OperatorArgument> operatorArguments,
			FormulaFactory ff) {
		final int size = operatorArguments.size();
		final OperatorArgument[] model = new OperatorArgument[size];
		this.operatorArguments = operatorArguments.toArray(model);
		this.specialization = ff.makeSpecialization();
		this.knownFormalTypes = new HashSet<GivenType>();
	}

	/**
	 * Attempts to match the given child expressions with the operator
	 * arguments. Returns <code>true</code> iff matching succeeds. Populates
	 * this instantiation as a side-effect.
	 * <p>
	 * It is expected that there are as many child expressions as operator
	 * arguments.
	 * </p>
	 * 
	 * @param childExprs
	 *            child expression of an operator instance
	 * @return <code>true</code> iff matching succeeds
	 */
	public boolean matchArguments(Expression[] childExprs) {
		boolean result = true;
		assert operatorArguments.length == childExprs.length;
		for (int i = 0; i < childExprs.length; i++) {
			result &= matchArgument(operatorArguments[i], childExprs[i]);
		}
		return result;
	}

	private boolean matchArgument(OperatorArgument operatorArgument,
			Expression expression) {
		final Type formalType = operatorArgument.getArgumentType();
		final Type actualType = expression.getType();
		final boolean result = matchType(formalType, actualType);
		if (result) {
			specialization.put(operatorArgument.asFreeIdentifier(), expression);
		}
		return result;
	}

	/**
	 * Attempts to match the given actual type with the given formal type.
	 * Returns <code>true</code> iff matching succeeds. Populates this
	 * instantiation as a side-effect.
	 * 
	 * @param formalType
	 *            type to match with
	 * @param actualType
	 *            type to match
	 * @return <code>true</code> iff matching succeeds
	 */
	public boolean matchType(Type formalType, Type actualType) {
		if (formalType instanceof GivenType) {
			if (knownFormalTypes.contains(formalType)) {
				return formalType.specialize(specialization).equals(actualType);
			}
			final GivenType givenType = (GivenType) formalType;
			specialization.put(givenType, actualType);
			knownFormalTypes.add(givenType);
			return true;
		}

		if (formalType.getClass() != actualType.getClass()) {
			// Both types must be of the same kind
			return false;
		}
		if (formalType instanceof BooleanType) {
			return true;
		}
		if (formalType instanceof IntegerType) {
			return true;
		}
		if (formalType instanceof PowerSetType) {
			return matchType(formalType.getBaseType(), actualType.getBaseType());
		}
		if (formalType instanceof ProductType) {
			final ProductType formalProductType = (ProductType) formalType;
			final ProductType actualProductType = (ProductType) actualType;
			return matchType(formalProductType.getLeft(),
					actualProductType.getLeft())
					&& matchType(formalProductType.getRight(),
							actualProductType.getRight());
		}
		if (formalType instanceof ParametricType) {
			final ParametricType formalParType = (ParametricType) formalType;
			final ParametricType actualParType = (ParametricType) actualType;
			if (formalParType.getExprExtension() != actualParType
					.getExprExtension()) {
				return false;
			}
			final Type[] formalChildTypes = formalParType.getTypeParameters();
			final Type[] actualChildTypes = actualParType.getTypeParameters();
			if (formalChildTypes.length != actualChildTypes.length) {
				return false;
			}
			for (int i = 0; i < formalChildTypes.length; i++) {
				if (!matchType(formalChildTypes[i], actualChildTypes[i])) {
					return false;
				}
			}
			return true;
		}
		// Unknown class
		return false;
	}

	public <T extends Formula<T>> T instantiate(T formula) {
		return formula.specialize(specialization);
	}

}
