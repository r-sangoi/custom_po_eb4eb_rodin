/*******************************************************************************
 * Copyright (c) 2010, 2014 University of Southampton and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University of Southampton - initial API and implementation
 *     Systerel - adapt datatypes to Rodin 3.0 API
 *******************************************************************************/
package org.eventb.core.ast.extensions.maths;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.datatype.IDatatypeBuilder;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.core.ast.extension.IOperatorProperties.FormulaType;
import org.eventb.core.ast.extension.IOperatorProperties.Notation;
import org.eventb.core.ast.extension.IPredicateExtension;
import org.eventb.core.internal.ast.extensions.maths.AxiomaticTypeExtension;
import org.eventb.core.internal.ast.extensions.maths.AxiomaticTypeOrigin;
import org.eventb.core.internal.ast.extensions.maths.DatatypeOrigin;
import org.eventb.core.internal.ast.extensions.maths.ExpressionOperatorExtension;
import org.eventb.core.internal.ast.extensions.maths.ExpressionOperatorTypingRule;
import org.eventb.core.internal.ast.extensions.maths.OperatorArgument;
import org.eventb.core.internal.ast.extensions.maths.OperatorTypingRule;
import org.eventb.core.internal.ast.extensions.maths.PredicateOperatorExtension;
import org.eventb.core.internal.ast.extensions.maths.PredicateOperatorTypingRule;

/**
 * Factory class for the different mathematical extensions contributed by the
 * Theory plug-in.
 * 
 * @since 1.0
 * 
 * @author maamria
 * 
 */
public final class MathExtensionsFactory {

	/**
	 * Creates an operator extension properties with the passed parameters.
	 * @param operatorID the operator ID
	 * @param syntax the operator syntax
	 * @param formulaType the operator formula type
	 * @param notation the operator notation
	 * @param groupID the operator group ID
	 * @return the operator properties 
	 */
	public static OperatorExtensionProperties getOperatorExtensionProperties(String operatorID, String syntax, 
			FormulaType formulaType, Notation notation, 
			String groupID){
		return new OperatorExtensionProperties(operatorID, syntax, formulaType, notation, groupID);
	}
	
	/**
	 * Returns the expression extension with the given properties and the operator typing rule.
	 * @param properties the operator properties, must not be <code>null</code>
	 * @param isCommutative whether the operator is commutative
	 * @param isAssociative whether the operator is associative
	 * @param operatorTypingRule the operator typing rule, must not be <code>null</code>
	 * @param source the source of the extension
	 * @return the expression operator extension
	 */
	public static  IExpressionExtension getExpressionExtension(OperatorExtensionProperties properties,
			boolean isCommutative, boolean isAssociative, Map<String, Type> operatorArguments, Type resultantType, 
			Predicate wdPredicate, Predicate dWDPredicate, Definition definition, Object source) {
		List<OperatorArgument> opArgs = new ArrayList<OperatorArgument>();
		int index = 0;
		for (String name : operatorArguments.keySet()){
			opArgs.add(new OperatorArgument(index++, name, operatorArguments.get(name)));
		}
		OperatorTypingRule operatorTypingRule = new ExpressionOperatorTypingRule(opArgs, wdPredicate, 
				dWDPredicate, resultantType, isAssociative);
		return new ExpressionOperatorExtension(properties, isCommutative, isAssociative, operatorTypingRule, definition,
				source);
	}
	
	/**
	 * Returns the predicate extension with the given properties and the operator typing rule.
	 * @param properties the operator syntactic properties
	 * @param isCommutative whether the operator is commutative
	 * @param operatorArguments the map of arguments, not that order is imported here
	 * @param wdPredicate the WD predicate 
	 * @param dWDPredicate the D-based WD predicate
	 * @param source the source of this extension if any
	 * @return the predicate extension
	 */
	
	public static  IPredicateExtension getPredicateExtension(OperatorExtensionProperties properties,
			boolean isCommutative, Map<String, Type> operatorArguments, Predicate wdPredicate, 
			Predicate dWDPredicate, Definition definition,Object source) {
		List<OperatorArgument> opArgs = new ArrayList<OperatorArgument>();
		int index = 0;
		for (String name : operatorArguments.keySet()){
			opArgs.add(new OperatorArgument(index++, name, operatorArguments.get(name)));
		}
		OperatorTypingRule operatorTypingRule = new PredicateOperatorTypingRule(opArgs,
				wdPredicate, dWDPredicate);
		return new PredicateOperatorExtension(properties, isCommutative, operatorTypingRule, definition,source);
	}
	
	/**
	 * Returns a datatype builder for the given identifier and type arguments,
	 * based on the given factory.
	 * 
	 * @param identifier
	 *            the name of the datatype
	 * @param typeArguments
	 *            the type arguments of this datatype
	 * @param factory
	 *            the formula factory
	 * @param origin
	 *            the datatype definition of the datatype 
	 * @return the set of resulting extensions
	 */
	public static IDatatypeBuilder makeDatatypeBuilder(String identifier,
			List<String> typeArguments, FormulaFactory factory, IDatatypeOrigin origin) {
		final List<GivenType> givenTypes = new ArrayList<GivenType>();
		for (String typeArgument : typeArguments) {
			givenTypes.add(factory.makeGivenType(typeArgument));
		}
		return factory.makeDatatypeBuilder(identifier, givenTypes, origin);
	}
	
	/**
	 * Returns the axiomatic type extension with the name <code>typeName</code>.
	 * 
	 * @param typeName
	 *            the name of the type, e.g., REAL
	 * @param id
	 *            the id of the operator
	 * @param origin
	 *            the origin of the extension
	 * @return the axiomatic type extension
	 */
	public static IExpressionExtension getAxiomaticTypeExtension(
			String typeName, String id, IAxiomaticTypeOrigin origin) {
		return AxiomaticTypeExtension.getAxiomaticTypeExtension(typeName, id,
				origin);
	}

	/**
	 * Creates a datatype origin for a datatype of a given name
	 * 
	 * @param name
	 *            the name of the datatype.
	 * @return the newly created datatype origin.
	 */
	public static IDatatypeOrigin makeDatatypeOrigin(String name) {
		return new DatatypeOrigin(name);
	}

	/**
	 * @param name
	 * @return
	 */
	public static IAxiomaticTypeOrigin makeAxiomaticTypeOrigin(String name) {
		return new AxiomaticTypeOrigin(name);
	}
}
