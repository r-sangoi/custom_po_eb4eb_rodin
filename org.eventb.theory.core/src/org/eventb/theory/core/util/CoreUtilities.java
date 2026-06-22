/*******************************************************************************
 * Copyright (c) 2010, 2020 University of Southampton and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University of Southampton - initial API and implementation
 *     Systerel - adapt datatypes to Rodin 3.0 API
 *******************************************************************************/
package org.eventb.theory.core.util;

import java.util.Set;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eventb.core.EventBAttributes;
import org.eventb.core.IExpressionElement;
import org.eventb.core.IPredicateElement;
import org.eventb.core.ast.ASTProblem;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.IResult;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.ProblemKind;
import org.eventb.core.ast.SourceLocation;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extensions.maths.AstUtilities;
import org.eventb.core.sc.GraphProblem;
import org.eventb.core.sc.IMarkerDisplay;
import org.eventb.core.sc.ParseProblem;
import org.eventb.theory.core.IFormulaElement;
import org.eventb.theory.core.ITypeElement;
import org.eventb.theory.core.TheoryAttributes;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.Messages;
import org.eventb.theory.core.sc.TheoryGraphProblem;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinProblem;
import org.rodinp.core.RodinDBException;

/**
 * Utilities used by this plug-in.
 * <p>
 * TODO double check <code>getDeployedSyntaxSymbolsOfOtherHierarchies(ISCTheoryRoot)</code>
 * 
 * @author maamria
 * 
 */
public class CoreUtilities {

	private static final Object[] NO_OBJECT = new Object[0];

	/**
	 * <p>
	 * Facility to log the given exception alongside the given message.
	 * </p>
	 * 
	 * @param exc
	 *            the exception
	 * @param message
	 *            the error message
	 */
	public static void log(Throwable exc, String message) {
		if (exc instanceof RodinDBException) {
			final Throwable nestedExc = ((RodinDBException) exc).getException();
			if (nestedExc != null) {
				exc = nestedExc;
			}
		}
		if (message == null) {
			message = "Unknown context"; //$NON-NLS-1$
		}
		IStatus status = makeErrorStatus(message, exc);
		TheoryPlugin.getDefault().getLog().log(status);
	}

	/**
	 * Creates a new core exception for this plug-in with the given message and
	 * message arguments.
	 * 
	 * @param message
	 *            a human-readable message, localized to the current locale.
	 *            Should be one of the messages defined in the {@link Messages}
	 *            class
	 * 
	 * @param args
	 *            parameters to bind with the message
	 * 
	 * @see #newCoreException(String)
	 */
	public static CoreException newCoreException(String message, Object... args) {
		return newCoreException(Messages.bind(message, args));
	}

	/**
	 * Creates a new core exception for this plug-in, with the given message.
	 * <p>
	 * The severity of the status associated to this exception is
	 * <code>ERROR</code>. The plug-in specific code is <code>OK</code>. No
	 * nested exception is stored in the status.
	 * </p>
	 * 
	 * @param message
	 *            a human-readable message, localized to the current locale.
	 */
	public static CoreException newCoreException(String message) {
		return new CoreException(makeErrorStatus(message, null));
	}

	private static Status makeErrorStatus(String message, Throwable exc) {
		return new Status(IStatus.ERROR, TheoryPlugin.PLUGIN_ID, IStatus.OK,
				message, exc);
	}

	/**
	 * Checks whether the given predicate refers only to the given types in
	 * <code>typeEnvironment</code>.
	 * 
	 * @param element
	 *            the predicate element
	 * @param pred
	 *            the predicate
	 * @param typeEnvironment
	 *            the type environment
	 * @param display
	 *            the marker display
	 * @return whether given predicates does not reference illegal identifiers
	 * @throws CoreException
	 */
	public static boolean checkAgainstTypeParameters(IPredicateElement element, Predicate pred, ITypeEnvironment typeEnvironment, IMarkerDisplay display) throws CoreException {
		return checkAgainstTypeParameters(element, pred, EventBAttributes.PREDICATE_ATTRIBUTE, typeEnvironment, display);
	}

	/**
	 * Checks whether the given formula refers only to the given types in
	 * <code>typeEnvironment</code>.
	 * 
	 * @param element
	 *            the formula element
	 * @param formula
	 *            the formula
	 * @param typeEnvironment
	 *            the type environment
	 * @param display
	 *            the marker display
	 * @return whether given formula does not reference illegal identifiers
	 * @throws CoreException
	 */
	public static boolean checkAgainstTypeParameters(IFormulaElement element, Formula<?> formula, ITypeEnvironment typeEnvironment, IMarkerDisplay display) throws CoreException {
		return checkAgainstTypeParameters(element, formula, TheoryAttributes.FORMULA_ATTRIBUTE, typeEnvironment, display);
	}

	/**
	 * Checks whether the given formula refers only to the given types in
	 * <code>typeEnvironment</code>.
	 * 
	 * @param element
	 *            the internal element
	 * @param formula
	 *            the formula
	 * @param attrType
	 *            the attribute type
	 * @param typeEnvironment
	 *            the type environment
	 * @param display
	 *            the marker display for error reporting
	 * @return whether formula refers only to type parameters
	 * @throws RodinDBException
	 */
	protected static boolean checkAgainstTypeParameters(IInternalElement element, Formula<?> formula, IAttributeType attrType, ITypeEnvironment typeEnvironment,
			IMarkerDisplay display) throws RodinDBException {
		Set<GivenType> types = formula.getGivenTypes();
		boolean ok = true;
		for (GivenType type : types) {
			if (!AstUtilities.isGivenSet(typeEnvironment, type.getName())) {
				display.createProblemMarker(element, attrType, TheoryGraphProblem.NonTypeParOccurError, type.getName());
				ok = false;
			}
		}
		return ok;
	}

	/**
	 * Parses the typing string that is an attribute of the given internal
	 * element.
	 * 
	 * @param typingElmnt
	 *            the rodin element
	 * @param factory
	 *            the formula factory
	 * @param display
	 *            the marker display for error reporting
	 * @return the type
	 * @throws CoreException
	 */
	public static Type parseTypeExpression(ITypeElement typingElmnt, FormulaFactory factory, IMarkerDisplay display) throws CoreException {
		IAttributeType.String attributeType = TheoryAttributes.TYPE_ATTRIBUTE;
		String expString = typingElmnt.getType();
		IParseResult parseResult = factory.parseType(expString);
		if (issueASTProblemMarkers(typingElmnt, attributeType, parseResult, display)) {
			return null;
		}
		Type type = parseResult.getParsedType();
		return type;
	}

	/**
	 * Parses and type checks the predicate occurring as an attribute to the
	 * given element
	 * 
	 * @param element
	 *            the rodin element
	 * @param ff
	 *            the formula factory
	 * @param typeEnvironment
	 *            the type environment
	 * @param display
	 *            the marker display for error reporting
	 * @return the parsed predicate
	 * @throws CoreException
	 */
	public static Predicate parseAndCheckPredicate(IPredicateElement element, FormulaFactory ff, ITypeEnvironment typeEnvironment, IMarkerDisplay display) throws CoreException {
		IAttributeType.String attributeType = EventBAttributes.PREDICATE_ATTRIBUTE;
		String pred = element.getPredicateString();
		IParseResult result = ff.parsePredicate(pred, null);
		if (issueASTProblemMarkers(element, attributeType, result, display)) {
			return null;
		}
		Predicate predicate = result.getParsedPredicate();
		FreeIdentifier[] idents = predicate.getFreeIdentifiers();
		for (FreeIdentifier ident : idents) {
			if (!typeEnvironment.contains(ident.getName())) {
				display.createProblemMarker(element, attributeType, GraphProblem.UndeclaredFreeIdentifierError, ident.getName());
				return null;
			}
		}
		
		ITypeCheckResult tcResult = predicate.typeCheck(typeEnvironment);
		if (issueASTProblemMarkers(element, attributeType, tcResult, display)) {
			return null;
		}
		Set<GivenType> givenTypes = predicate.getGivenTypes();
		for (GivenType type : givenTypes){
			if (!typeEnvironment.contains(type.getName())) {
				display.createProblemMarker(element, attributeType, GraphProblem.UndeclaredFreeIdentifierError, type.getName());
				return null;
			}
		}
		return predicate;
	}

	/**
	 * Parses and type checks the predicate occurring as an attribute to the
	 * given element
	 * 
	 * @param element
	 *            the rodin element
	 * @param ff
	 *            the formula factory
	 * @param typeEnvironment
	 *            the type environment
	 * @param display
	 *            the marker display for error reporting
	 * @return the parsed predicate
	 * @throws CoreException
	 */
	public static Predicate parseAndCheckPredicatePattern(IPredicateElement element, FormulaFactory ff, ITypeEnvironment typeEnvironment, IMarkerDisplay display) throws CoreException {
		IAttributeType.String attributeType = EventBAttributes.PREDICATE_ATTRIBUTE;
		String pred = element.getPredicateString();
		IParseResult result = ff.parsePredicatePattern(pred, null);
		if (issueASTProblemMarkers(element, attributeType, result, display)) {
			return null;
		}
		Predicate predicate = result.getParsedPredicate();
		FreeIdentifier[] idents = predicate.getFreeIdentifiers();
		for (FreeIdentifier ident : idents) {
			if (!typeEnvironment.contains(ident.getName())) {
				display.createProblemMarker(element, attributeType, GraphProblem.UndeclaredFreeIdentifierError, ident.getName());
				return null;
			}
		}
		
		ITypeCheckResult tcResult = predicate.typeCheck(typeEnvironment);
		if (issueASTProblemMarkers(element, attributeType, tcResult, display)) {
			return null;
		}
		Set<GivenType> givenTypes = predicate.getGivenTypes();
		for (GivenType type : givenTypes){
			if (!typeEnvironment.contains(type.getName())) {
				display.createProblemMarker(element, attributeType, GraphProblem.UndeclaredFreeIdentifierError, type.getName());
				return null;
			}
		}
		return predicate;
	}

	/**
	 * Parses and type checks the expression occurring as an attribute to the
	 * given element
	 * 
	 * @param element
	 *            the rodin element
	 * @param ff
	 *            the formula factory
	 * @param typeEnvironment
	 *            the type environment
	 * @param display
	 *            the marker display for error reporting
	 * @return the parsed expression
	 * @throws CoreException
	 */
	public static Expression parseAndCheckExpression(IExpressionElement element, FormulaFactory ff, ITypeEnvironment typeEnvironment, IMarkerDisplay display) throws CoreException {
		IAttributeType.String attributeType = EventBAttributes.EXPRESSION_ATTRIBUTE;
		String exp = element.getExpressionString();
		IParseResult result = ff.parseExpression(exp, null);
		if (issueASTProblemMarkers(element, attributeType, result, display)) {
			return null;
		}
		Expression expression = result.getParsedExpression();
		FreeIdentifier[] idents = expression.getFreeIdentifiers();
		for (FreeIdentifier ident : idents) {
			if (!typeEnvironment.contains(ident.getName())) {
				display.createProblemMarker(element, attributeType, GraphProblem.UndeclaredFreeIdentifierError, ident.getName());
				return null;
			}
		}
		
		ITypeCheckResult tcResult = expression.typeCheck(typeEnvironment);
		if (issueASTProblemMarkers(element, attributeType, tcResult, display)) {
			return null;
		}
		Set<GivenType> givenTypes = expression.getGivenTypes();
		for (GivenType type : givenTypes){
			if (!typeEnvironment.contains(type.getName())) {
				display.createProblemMarker(element, attributeType, GraphProblem.UndeclaredFreeIdentifierError, type.getName());
				return null;
			}
		}
		return expression;
	}

	/**
	 * Returns whether <code>result</code> has any problems. Issues AST related
	 * problems.
	 * 
	 * @param element
	 *            the rodin element
	 * @param attributeType
	 *            the attribute
	 * @param result
	 *            the parse result
	 * @param display
	 *            the marker display
	 * @return whether an error is encountered
	 * @throws RodinDBException
	 */
	public static boolean issueASTProblemMarkers(IInternalElement element, IAttributeType.String attributeType, IResult result, IMarkerDisplay display) throws RodinDBException {

		boolean errorIssued = false;
		for (ASTProblem parserProblem : result.getProblems()) {
			final SourceLocation location = parserProblem.getSourceLocation();
			final ProblemKind problemKind = parserProblem.getMessage();
			final Object[] args = parserProblem.getArgs();

			final IRodinProblem problem;
			final Object[] objects; // parameters for the marker

			switch (problemKind) {

			case FreeIdentifierHasBoundOccurences:
				problem = ParseProblem.FreeIdentifierHasBoundOccurencesWarning;
				objects = new Object[] { args[0] };
				break;

			case BoundIdentifierHasFreeOccurences:
				// ignore
				// this is just the symmetric message to
				// FreeIdentifierHasBoundOccurences
				continue;

			case BoundIdentifierIsAlreadyBound:
				problem = ParseProblem.BoundIdentifierIsAlreadyBoundWarning;
				objects = new Object[] { args[0] };
				break;

			case Circularity:
				problem = ParseProblem.CircularityError;
				objects = NO_OBJECT;
				break;

			case InvalidTypeExpression:
				problem = ParseProblem.InvalidTypeExpressionError;
				objects = NO_OBJECT;
				break;

			case LexerError:
				problem = ParseProblem.LexerError;
				objects = new Object[] { args[0] };
				break;

			case TypeCheckFailure:
				problem = ParseProblem.TypeCheckError;
				objects = NO_OBJECT;
				break;

			case TypesDoNotMatch:
				problem = ParseProblem.TypesDoNotMatchError;
				objects = new Object[] { args[0], args[1] };
				break;

			case TypeUnknown:
				problem = ParseProblem.TypeUnknownError;
				objects = NO_OBJECT;
				break;

			case MinusAppliedToSet:
				problem = ParseProblem.MinusAppliedToSetError;
				objects = NO_OBJECT;
				break;

			case MulAppliedToSet:
				problem = ParseProblem.MulAppliedToSetError;
				objects = NO_OBJECT;
				break;

			// syntax errors
			case BECMOAppliesToOneIdent:
			case DuplicateIdentifierInPattern:
			case ExtensionPreconditionError:
			case FreeIdentifierExpected:
			case IncompatibleIdentExprNumbers:
			case IncompatibleOperators:
			case IntegerLiteralExpected:
			case InvalidAssignmentToImage:
			case InvalidGenericType:
			case MisplacedLedOperator:
			case MisplacedNudOperator:
			case NotUpgradableError:
			case PredicateVariableNotAllowed:
			case PrematureEOF:
			case UnexpectedOftype:
			case UnexpectedSubFormulaKind:
			case UnexpectedSymbol:
			case UnknownOperator:
			case UnmatchedTokens:
			case VariousPossibleErrors:
			case DatatypeParsingError:

				problem = ParseProblem.SyntaxError;
				
				objects = new Object[] { parserProblem.toString() };
				break;
			default:

				problem = ParseProblem.InternalError;
				objects = NO_OBJECT;

				break;
			}

			if (location == null) {
				display.createProblemMarker(element, attributeType, problem, objects);
			} else {
				display.createProblemMarker(element, attributeType, location.getStart(), location.getEnd(), problem, objects);
			}

			errorIssued |= problem.getSeverity() == IMarker.SEVERITY_ERROR;
		}

		return errorIssued;
	}
}
