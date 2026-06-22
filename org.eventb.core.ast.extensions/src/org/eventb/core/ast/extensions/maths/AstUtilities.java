/*******************************************************************************
 * Copyright (c) 2010, 2020 University of Southampton and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University of Southampton - initial API and implementation
 *     Systerel - add infix expression operator group
 *     Systerel - adapt datatypes to Rodin 3.0 API
 *******************************************************************************/
package org.eventb.core.ast.extensions.maths;

import static org.eventb.core.ast.Formula.BTRUE;
import static org.eventb.core.ast.Formula.LAND;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.ExtendedPredicate;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.ParametricType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.datatype.IDatatype;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.core.ast.extension.IExtendedFormula;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.ast.extension.IOperator;
import org.eventb.core.ast.extension.IOperatorGroup;
import org.eventb.core.ast.extension.IOperatorProperties.FormulaType;
import org.eventb.core.ast.extension.IOperatorProperties.Notation;
import org.eventb.core.ast.extension.IPredicateExtension;
import org.eventb.core.ast.extension.IPriorityMediator;
import org.eventb.core.ast.extension.StandardGroup;
import org.eventb.core.internal.ast.extensions.AstExtensionsPlugin;
import org.eventb.core.internal.ast.extensions.maths.ExpressionOperatorExtension;

/**
 * Utilities from the Theory Core that are mostly useful for the Rule-based
 * Prover.
 * 
 * @since 1.0
 * @author maamria
 * 
 */
public class AstUtilities {

	/**
	 * Dummy theory group
	 */
	protected static final String DUMMY_OPERATOR_GROUP = "NEW THEORY GROUP";

	/**
	 * Operator group for infix expression extensions.
	 * 
	 * <p>
	 * Group setup is performed in
	 * {@link ExpressionOperatorExtension#addPriorities(IPriorityMediator)}.
	 * </p>
	 */
	public static final String INFIX_EXTENDED_EXPRESSION_OPERATOR_GROUP = "infix extended expression group";	
	
	// the currently supported notations
	public static final String[] POSSIBLE_NOTATION_TYPES = new String[] { Notation.PREFIX.toString(),
			Notation.INFIX.toString() };

	/**
	 * Converts a string (e.g., "POSTFIX") to the corresponding notation.
	 * 
	 * @param type
	 *            in string format
	 * @return the corresponding notation
	 */
	public static Notation getNotation(String type) {
		if (type.equalsIgnoreCase(Notation.POSTFIX.toString())) {
			return Notation.POSTFIX;
		} else if (type.equalsIgnoreCase(Notation.INFIX.toString())) {
			return Notation.INFIX;
		}// default to prefix
		else {
			return Notation.PREFIX;
		}
	}

	/**
	 * Returns whether the given extended expression is associative.
	 * 
	 * @param expression
	 *            the extended expression
	 * @return whether the given extended expression is associative
	 */
	public static boolean isAssociative(ExtendedExpression expression) {
		return expression != null 
				&& expression.getExtension() instanceof IOperatorExtension				 	//cfs:difficult to test this?
				&& ((IOperatorExtension) expression.getExtension()).isAssociative();
	}

	/**
	 * Returns whether the given extended expression is associative commutative.
	 * 
	 * @param expression
	 *            the extended expression
	 * @return whether the given extended expression is associative commutative
	 */
	public static boolean isAC(ExtendedExpression expression) {
		return isAssociative(expression) && ((IOperatorExtension) expression.getExtension()).isCommutative();
	}

	/**
	 * Return the unflattened version of the given formula if it comes from a theory extensions (i.e., 
	 * is an instance of {@link IOperatorExtension}).
	 * @param formula the formula to unflatten
	 * @param factory the formula factory that knows about the extension of the formula
	 * @return the unflattened formula
	 */
	public static Formula<?> unflatten(IExtendedFormula formula, FormulaFactory factory) {
		IFormulaExtension extension = formula.getExtension();
		if (extension instanceof IOperatorExtension){											//cfs:difficult to test this?
			IOperatorExtension operatorExtension = (IOperatorExtension) extension;
			if (operatorExtension.isAssociative())
				return unflattenExpression(operatorExtension,formula.getChildExpressions(), factory);
		}
		return (Formula<?>) formula;
	}
	
	/**
	 * Returns the non-flattened version of the the extended expression with the
	 * given children.
	 * 
	 * @param extension
	 *            the formula extension which should also be an expression extension
	 * @param children
	 *            the extended expression children
	 * @param factory
	 *            the formula factory
	 * @return the non-flattened extended expression, the result is
	 *         left-associative
	 */
	public static ExtendedExpression unflattenExpression(IOperatorExtension extension, Expression[] children,
			FormulaFactory factory) {
		// do not handle predicate extensions, maybe even throw an illegal arg exception
		if (!(extension instanceof IExpressionExtension)){
			return null;
		}
		// not associative ... return as is
		if (!isAnAssociativeExtension(extension)) {
			return factory.makeExtendedExpression((IExpressionExtension) extension, children, new Predicate[0], null);
		}
		// only works for associative extended expressions resulting from theory extensions
		// children length has to be 2 or more
		IExpressionExtension expreExtension = (IExpressionExtension) extension;
		int length = children.length;
		// return as is if number of children is 2
		if (length == 2) {
			return factory.makeExtendedExpression(expreExtension, children, new Predicate[0], null);
		} else {
			Expression[] toWorkWith = subChildren(length - 2, children);
			return factory.makeExtendedExpression(expreExtension,
					new Expression[] { unflattenExpression(extension, toWorkWith, factory), children[length - 1] },
					new Predicate[0], null);
		}
	}

	/**
	 * This method returns the array resulting from taking the elements of
	 * children in the same order ending at the (zero-based) given ending index
	 * (inclusive).
	 * 
	 * @param end
	 *            the ending index (zero-based)
	 * @param children
	 *            the original array
	 * @return the sub-array
	 */
	public static Expression[] subChildren(int end, Expression[] children) {
		if (end > children.length - 1) {
			return children;
		}
		if (end < 0) {
			return new Expression[0];
		} else {
			int newLength = end + 1;
			Expression[] result = new Expression[newLength];
			for (int i = 0; i < newLength; i++) {
				result[i] = children[i];
			}
			return result;
		}
	}

	/**
	 * Returns the position of the operator in the given extended expression.
	 * 
	 * @param eexp
	 *            the expression
	 * @param predStr
	 *            the string of the expression
	 * @return operator position
	 */
	public static PositionPoint getPositionOfOperator(ExtendedExpression eexp, String predStr) {
		PositionPoint point = null;
		final IExpressionExtension extension = eexp.getExtension();
		Notation notation = extension.getKind().getProperties().getNotation();

		switch (notation) {
		case INFIX:
			Expression ie1 = eexp.getChildExpressions()[0];
			Expression ie2 = eexp.getChildExpressions()[1];
			point = getOperatorPosition(predStr, ie1.getSourceLocation().getEnd() + 1, ie2.getSourceLocation()
					.getStart());
			break;

		default:
			if (eexp.getChildExpressions().length == 0) {
				point = getOperatorPosition(predStr, eexp.getSourceLocation().getStart(), eexp.getSourceLocation()
						.getEnd() + 1);
			} else {
				Expression pe1 = eexp.getChildExpressions()[0];
				point = getOperatorPosition(predStr, eexp.getSourceLocation().getStart(), pe1.getSourceLocation()
						.getStart());
			}
			break;
		}
		return point;
	}

	/**
	 * Returns the position of the operator in the given extended predicate.
	 * 
	 * @param epred
	 *            the predicate
	 * @param predStr
	 *            the string of the predicate
	 * @return operator position
	 */
	public static PositionPoint getPositionOfOperator(ExtendedPredicate epred, String predStr) {
		PositionPoint point = null;
		final IPredicateExtension extension = epred.getExtension();
		Notation notation = extension.getKind().getProperties().getNotation();

		switch (notation) {
		case INFIX:
			Expression pe1 = epred.getChildExpressions()[0];
			Expression pe2 = epred.getChildExpressions()[1];
			point = getOperatorPosition(predStr, pe1.getSourceLocation().getEnd() + 1, pe2.getSourceLocation()
					.getStart());
			break;
		default:
			if (epred.getChildExpressions().length == 0) {
				point = getOperatorPosition(predStr, epred.getSourceLocation().getStart(), epred.getSourceLocation()
						.getEnd() + 1);
			} else {
				Expression ie1 = epred.getChildExpressions()[0];
				point = getOperatorPosition(predStr, epred.getSourceLocation().getStart(), ie1.getSourceLocation()
						.getStart());
			}
			break;
		}
		return point;
	}

	/**
	 * Returns whether the given extension is a theory extension.
	 * 
	 * @param extension
	 * @return whether the given extension is a theory extension
	 */
	public static boolean isATheoryExtension(IFormulaExtension extension) {
		return extension instanceof IOperatorExtension;
	}

	/**
	 * Returns whether the given extension is an associative theory extension.
	 * 
	 * @param extension
	 *            the theory extension
	 * @return whether the extension declares an associative operator
	 */
	public static boolean isAnAssociativeExtension(IFormulaExtension extension) {
		return isATheoryExtension(extension) 
				&& ((IOperatorExtension) extension).isAssociative();
	}

	/**
	 * Returns whether the given extension is a commutative theory extension.
	 * 
	 * @param extension
	 *            the theory extension
	 * @return whether the extension declares a commutative operator
	 */
	public static boolean isACommutativeExtension(IFormulaExtension extension) {
		return isATheoryExtension(extension) 
				&& ((IOperatorExtension) extension).isCommutative();
	}

	/**
	 * An utility method to return the operator source location within the range
	 * (start, end).
	 * <p>
	 * 
	 * @param predStr
	 *            the actual predicate string.
	 * @param start
	 *            the starting index for searching.
	 * @param end
	 *            the last index for searching
	 * @return the location in the predicate string ignore the empty spaces or
	 *         brackets in the beginning and in the end.
	 */
	protected static PositionPoint getOperatorPosition(String predStr, int start, int end) {
		int i = start;
		int x = start;
		int y;
		boolean letter = false;
		while (i < end) {
			char c = predStr.charAt(i);
			if (letter == false 
					&& !isSpaceOrBracket(c)) {
				x = i;
				letter = true;
			} else if (letter == true 
					&& isSpaceOrBracket(c)) {
				y = i;
				return new PositionPoint(x, y);
			}
			++i;
		}
		if (letter == true)
			return new PositionPoint(x, end);
		else
			return new PositionPoint(start, end);		//cfs: may not be reachable?
	}

	/**
	 * A private utility method to check if a character is either a space or a
	 * bracket.
	 * <p>
	 * 
	 * @param c
	 *            the character to check.
	 * @return <code>true</code> if the character is a space or bracket,
	 *         otherwise return <code>false</code>.
	 */
	private static boolean isSpaceOrBracket(char c) {
		return (c == '\t' 
				|| c == '\n' 
				|| c == ' ' 
				|| c == '(' 
				|| c == ')');
	}

	/**
	 * An implementation of a point.
	 * 
	 * <p>
	 * This is a hook to avoid using the SWT point implementation.
	 * 
	 * @author maamria
	 * 
	 */
	public static class PositionPoint {
		int x;
		int y;

		public PositionPoint(int x, int y) {
			this.x = x;
			this.y = y;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}
	}

	/**
	 * Returns whether the formula type is an expression type.
	 * 
	 * @param type
	 *            the formula type
	 * @return whether the type is an expression
	 */
	public static final boolean isExpressionOperator(FormulaType type) {
		return type.equals(FormulaType.EXPRESSION);
	}

	/**
	 * Checks whether the name <code>name</code> is a given set in the given
	 * type environment.
	 * 
	 * @param typeEnvironment
	 *            the type environment
	 * @param name
	 *            the name
	 * @return whether <code>name</code> is a given set
	 */
	public static boolean isGivenSet(ITypeEnvironment typeEnvironment, String name) {
		Type type = typeEnvironment.getType(name);
		if (type == null) {
			return false;
		}
		final Type baseType = type.getBaseType();
		if (baseType instanceof GivenType) {
			GivenType givenType = (GivenType) baseType;
			return givenType.getName().equals(name);
		}
		return false;
	}

	/**
	 * Returns a predicate resulting from conjuncting the given predicates.
	 * 
	 * <p>
	 * Note that simplifications are performed before the resulting predicate is
	 * produced.
	 * 
	 * @param preds
	 *            some predicates (at least one)
	 * @return the predicate
	 */
	public static Predicate conjunctPredicates(Predicate... preds) {
		final FormulaFactory ff = preds[0].getFactory();
		return conjunctPredicates(Arrays.asList(preds), ff);
	}

	/**
	 * Returns a predicate resulting from conjuncting the given predicates.
	 * 
	 * <p>
	 * Note that simplifications are performed before the resulting predicate is
	 * produced.
	 * 
	 * @param preds
	 *            the list of predicates, should be modifiable
	 * @param ff
	 *            the formula factor
	 * @return the predicate
	 */
	public static Predicate conjunctPredicates(List<Predicate> preds, FormulaFactory ff) {
		final List<Predicate> conjuncts = new ArrayList<Predicate>(preds.size());
		for (final Predicate pred : preds) {
			if (pred.getTag() != BTRUE)
				conjuncts.add(pred);
		}
		switch (conjuncts.size()) {
		case 0:
			return makeBTRUE(ff);
		case 1:
			return conjuncts.get(0);
		default:
			return ff.makeAssociativePredicate(LAND, conjuncts, null);
		}
	}

	/**
	 * Returns an appropriate group for the operator with the supplied
	 * properties.
	 * <p>
	 * The group is guessed depending on the information passed to this method.
	 * 
	 * @param formulaType
	 *            the formula type of the operator
	 * @param notation
	 *            the notation of the operator
	 * @param arity
	 *            the airty of the operator
	 * @return the appropriate group
	 */
	public static String getGroupFor(FormulaType formulaType, Notation notation, int arity) {
		String group = DUMMY_OPERATOR_GROUP;
		switch (formulaType) {
		case EXPRESSION: {
			switch (notation) {
			case INFIX: {
				group = INFIX_EXTENDED_EXPRESSION_OPERATOR_GROUP;
				break;
			}
			case PREFIX: {
				if (arity > 0) {
					group = StandardGroup.CLOSED.getId();
				} else {
					group = StandardGroup.ATOMIC_EXPR.getId();
				}
				break;
			}
			case POSTFIX: {
				// leave as part of the dummy group TODO check this
			}
			}
			break;
		}
		case PREDICATE: {
			switch (notation) {
			case INFIX: {
				if (arity == 0) {
					group = StandardGroup.ATOMIC_PRED.getId();
				}
				// infix makes sense for ops with more than two args
				if (arity > 1) {
					group = StandardGroup.RELOP_PRED.getId();
				}
				break;
			}
			case PREFIX: {
				if (arity > 0) {
					group = StandardGroup.CLOSED.getId();
				} else {
					group = StandardGroup.ATOMIC_PRED.getId();
				}
				break;
			}
			case POSTFIX: {
				// leave as part of the dummy group TODO check this
			}
			}
		}
		}
		return group;
	}

	/**
	 * Checks whether an operator with the given ID is already in the given
	 * factory.
	 * 
	 * @param id
	 *            the ID of the new operator
	 * @param ff
	 *            the formula factory
	 * @return <code>true</code> if the operator ID does not exist in the
	 *         formula factory
	 */
	public static boolean checkOperatorID(String id, FormulaFactory ff) {
		return !AstUtilities.getOperatorIDs(ff).contains(id);
	}

	/**
	 * Checks whether an operator with the given syntax symbol is already in the
	 * given factory.
	 * 
	 * @param symbol
	 *            the syntax symbol of the new operator
	 * @param ff
	 *            the formula factory
	 * @return <code>true</code> if the symbol does not exist in the formula
	 *         factory
	 */
	public static boolean checkOperatorSyntaxSymbol(String symbol, FormulaFactory ff) {
		return !AstUtilities.getOperatorSyntaxSymbols(ff).contains(symbol);
	}

	/**
	 * Checks whether a group with the given ID is already in the given factory.
	 * 
	 * @param id
	 *            the ID of the new group
	 * @param ff
	 *            the formula factory
	 * @return <code>true</code> if the group ID does not exists in the formula
	 *         factory
	 */
	public static boolean checkGroupID(String id, FormulaFactory ff) {
		return !AstUtilities.getOperatorGroups(ff).contains(id);
	}

	/**
	 * Populates all syntax symbols of all operators recognised by the given
	 * formula factory.
	 * 
	 * @param ff
	 *            the formula factory
	 * @return the list of all syntax symbols
	 */
	static List<String> getOperatorSyntaxSymbols(FormulaFactory ff) {
		List<String> result = new ArrayList<String>();
		Set<IOperatorGroup> groups = ff.getGrammarView().getGroups();
		for (IOperatorGroup g : groups) {
			for (IOperator op : g.getOperators()) {
				result.add(op.getSyntaxSymbol());
			}
		}
		return result;
	}

	/**
	 * Populates all IDs of all operators recognised by the given formula
	 * factory.
	 * 
	 * @param ff
	 *            the formula factory
	 * @return the list of all existing IDs
	 */
	static List<String> getOperatorIDs(FormulaFactory ff) {
		List<String> result = new ArrayList<String>();
		Set<IOperatorGroup> groups = ff.getGrammarView().getGroups();
		for (IOperatorGroup g : groups) {
			for (IOperator op : g.getOperators()) {
				result.add(op.getId());
			}
		}
		return result;
	}

	/**
	 * Populates all groups of all operators recognised by the given formula
	 * factory.
	 * 
	 * @param ff
	 *            the formula factory
	 * @return the list of all existing group IDs
	 */
	static List<String> getOperatorGroups(FormulaFactory ff) {
		List<String> result = new ArrayList<String>();
		Set<IOperatorGroup> groups = ff.getGrammarView().getGroups();
		for (IOperatorGroup g : groups) {
			result.add(g.getId());
		}
		return result;
	}

	/**
	 * Returns the array of types of the given expressions.
	 * 
	 * @param exps
	 *            the expressions, each of which should not be <code>null</code>
	 * @return the corresponding array of types
	 */
	public static Type[] getTypes(Expression[] exps) {
		Type[] types = new Type[exps.length];
		for (int i = 0; i < types.length; i++) {
			types[i] = exps[i].getType();
		}
		return types;
	}

	/**
	 * Returns an operator ID with the given details.
	 * 
	 * @param theoryName
	 *            the parent theory
	 * @param syntax
	 *            the syntax of the operator
	 * @return a suitable operator ID
	 */
	public static String makeOperatorID(String theoryName, String syntax) {
		return theoryName + "." + syntax;
	}

	/**
	 * Creates a type environment using the given factory with all the names
	 * occurring in the given type environment.
	 * 
	 * <p>
	 * Each type environment has a reference to a particular formula factory. As
	 * such, it is always important to work with compatible type environment and
	 * factory objects.
	 * 
	 * @param typeEnvironment
	 *            the old type environment
	 * @param factory
	 *            the formula factory
	 * @return the new type environment
	 */
	public static ITypeEnvironmentBuilder getTypeEnvironmentForFactory(ITypeEnvironment typeEnvironment, FormulaFactory factory) {
		if (typeEnvironment.isTranslatable(factory))
			return typeEnvironment.translate(factory).makeBuilder();
		else
			throw new IllegalArgumentException("tupeEnvironment " + typeEnvironment + " is not translatable");
	}

	/**
	 * Returns the given types occurring in <code>type</code>.
	 * 
	 * @param type
	 *            the type
	 * @return all given types
	 */
	public static List<GivenType> getGivenTypes(Type type) {
		return new ArrayList<GivenType>(type.getGivenTypes());
	}

	/**
	 * <p>
	 * Utility method to parse a string as a formula knowing beforehand whether
	 * it is a an expression or predicate.
	 * </p>
	 * <p>
	 * Use only for theory formulas. The resulting formula is not necessarily
	 * type-checked.
	 * </p>
	 * 
	 * @param formStr
	 *            the formula string
	 * @param isExpression
	 *            whether to parse an expression or a predicate
	 * @return the parsed formula or <code>null</code> if there was an error
	 */
	public static Formula<?> parseFormula(String formStr, boolean isExpression, FormulaFactory factory) {
		Formula<?> form = null;
		if (isExpression) {
			IParseResult r = factory.parseExpressionPattern(formStr, null);
			form = r.getParsedExpression();
		} else {
			IParseResult r = factory.parsePredicatePattern(formStr, null);
			form = r.getParsedPredicate();
		}
		return form;
	}

	/**
	 * Returns the truth predicate built with the given factory.
	 * 
	 * @param factory
	 *            some formula factory
	 * @return the truth predicate built with the given factory
	 */
	public static Predicate makeBTRUE(FormulaFactory factory) {
		return factory.makeLiteralPredicate(BTRUE, null);
	}

	/**
	 * Returns the associative (potentially extended) expression that fit the
	 * given details.
	 * 
	 * @param tag
	 *            the tag
	 * @param factory
	 *            the formula factory
	 * @param exps
	 *            the expressions
	 * @return the resultant expression
	 */
	public static Expression makeAppropriateAssociativeExpression(int tag, FormulaFactory factory, Expression... exps) {
		List<Expression> es = getListWithoutNulls(exps);
		if (es.size() == 1)
			return es.get(0);
		IFormulaExtension extension = factory.getExtension(tag);
		if (extension != null) {
			return factory.makeExtendedExpression((IExpressionExtension) extension,
					es, Collections.<Predicate>emptyList(), null);
		} else {
			return factory.makeAssociativeExpression(tag, es, null);
		}
	}

	/**
	 * Returns the associative predicate that fit the given details.
	 * 
	 * @param tag
	 *            the tag
	 * @param factory
	 *            the formula factory
	 * @param preds
	 *            the predicates
	 * @return the resultant predicate
	 */
	public static Predicate makeAssociativePredicate(int tag, FormulaFactory factory, Predicate... preds) {
		List<Predicate> es = getListWithoutNulls(preds);
		if (es.size() == 1)
			return es.get(0);
		else {
			return factory.makeAssociativePredicate(tag, es, null);
		}
	}
	
	/**
	 * Returns a list of the non-null elements in the passed array.
	 * @param <E> the type of the objects
	 * @param es the elements
	 * @return the list of non-null elements
	 */
	@SafeVarargs
	public static <E> List<E> getListWithoutNulls(E... es){
		List<E> list = new ArrayList<E>();
		for (E e : es){
			if (e != null){
				list.add(e);
			}
		}
		return list;
	}
	
	/**
	 * Throws an exception if any of the passed objects is <code>null</code>.
	 * @param os the array of objects
	 * @throws IllegalArgumentException if any of the passed object is <code>null</code>
	 */
	public static void ensureNotNull(Object... os){
		for (Object o : os)
			if (o == null){
				IllegalArgumentException exc = new IllegalArgumentException("null objects not allowed, but passed as argument");
				AstExtensionsPlugin.log(exc, "null objects not allowed, but passed as argument");
				throw exc;
			}
	}
	
	/**
	 * Returns whether the given type is a datatype type  (as opposed to for example an axiomatic type).
	 * @param type the type to check
	 * @return whether <code>type</code> is a datatype type
	 */
	public static boolean isDatatypeType(Type type){
		if (!(type instanceof ParametricType)){
			return false;
		}
		ParametricType parametricType = (ParametricType) type;
		return parametricType.getExprExtension().getOrigin() instanceof IDatatype;
	}
}
