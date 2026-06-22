/*******************************************************************************
 * Copyright (c) 2011, 2020 University of Southampton and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University of Southampton - initial API and implementation
 *     Systerel - adapt datatypes to Rodin 3.0 API
 *******************************************************************************/
package org.eventb.theory.core.sc.modules;

import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IIdentifierElement;
import org.eventb.core.ISCIdentifierElement;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.sc.GraphProblem;
import org.eventb.core.sc.IMarkerDisplay;
import org.eventb.theory.core.IFormulaElement;
import org.eventb.theory.core.TheoryAttributes;
import org.eventb.theory.core.util.CoreUtilities;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinDBException;

/**
 * This class contains static methods that can be used by theory static checker
 * modules.
 * 
 * @author maamria
 * 
 */
public class ModulesUtils {

	public static final int IDENT_SYMTAB_SIZE = 2047;

	public static final int LABEL_SYMTAB_SIZE = 2047;

	public static final String AXM_NAME_PREFIX = "Axm";
	
	public static final String THM_NAME_PREFIX = "Thm";

	public static final String PRB_NAME_PREFIX = "PRB";
	
	public static final String ADB_NAME_PREFIX = "ADB";

	/**
	 * Makes a free identifier from the given name.
	 * <p>
	 * Returns <code>null</code> if one of the following happens:
	 * <li>the given name cannot be parsed as an expression</li>
	 * <li>the given name is not a valid free identifier name</li>
	 * <li>the given name is primed and prime is not allowed</li>
	 * <li>the given name contains leading or trailing spaces</li>
	 * For the first problem encountered, if any, a problem marker is added and
	 * <code>null</code> is returned immediately.
	 * </p>
	 * 
	 * @param name
	 *            a name to parse
	 * @param element
	 *            the element associated the the name
	 * @param attrType
	 *            the attribute type of the element where the name is located
	 * @param factory
	 *            a formula factory to parse the name
	 * @param markerDisplay
	 *            a marker display for problem markers
	 * @return a free identifier with the given name, or <code>null</code> if
	 *         there was a problem making the identifier
	 * @throws RodinDBException
	 *             if there is a problem accessing the Rodin database
	 */
	public static FreeIdentifier parseIdentifier(String name,
			IInternalElement element, IAttributeType.String attrType,
			FormulaFactory factory, IMarkerDisplay markerDisplay)
			throws RodinDBException {

		IParseResult pResult = factory.parseExpression(name, element);
		Expression expr = pResult.getParsedExpression();
//removed because we dont need to check the uniqueness of the identifier in the time of deploy
		if (pResult.hasProblem() || !(expr instanceof FreeIdentifier)) {
			markerDisplay.createProblemMarker(element, attrType,
					GraphProblem.InvalidIdentifierError, name);
			return null;
		}
		FreeIdentifier identifier = (FreeIdentifier) expr;
		if (identifier.isPrimed()) {
			markerDisplay.createProblemMarker(element, attrType,
					GraphProblem.InvalidIdentifierError, name);
			return null;
		}
		if (!name.equals(identifier.getName())) {
			markerDisplay.createProblemMarker(element, attrType,
					GraphProblem.InvalidIdentifierSpacesError, name);
			return null;
		}
		return identifier;
	}

	/**
	 * Creates a statically checked identifier element of the type
	 * <code>T</code>.
	 * 
	 * @param <T>
	 *            the type of the element
	 * @param type
	 *            internal element type
	 * @param source
	 *            the source element
	 * @param parent
	 *            the parent rodin element
	 * @param monitor
	 *            the progress monitor
	 * @return the statically checked identifier element
	 * @throws CoreException
	 */
	public static <T extends ISCIdentifierElement> T createSCIdentifierElement(
			IInternalElementType<T> type, IIdentifierElement source,
			IInternalElement parent, IProgressMonitor monitor)
			throws CoreException {
		T scElement = parent.getInternalElement(type,
				source.getIdentifierString());
		scElement.create(null, monitor);
		return scElement;
	}

	/**
	 * Parses and type checks the non-pattern formula occurring as an attribute
	 * to the given formula element.
	 * 
	 * @param element
	 *            the rodin element
	 * @param isExpression
	 *            whether to parse an expression or a predicate
	 * @param ff
	 *            the formula factory
	 * @param typeEnvironment
	 *            the type environment
	 * @param display
	 *            the marker display for error reporting
	 * @return the parsed formula
	 * @throws CoreException
	 */
	public static Formula<?> parseAndCheckFormula(IFormulaElement element,
			boolean isExpression, boolean issueErrors, FormulaFactory ff,
			ITypeEnvironment typeEnvironment, IMarkerDisplay markerDisplay)
			throws CoreException {
		final ITypeEnvironment tEnv = typeEnvironment.translate(ff);
		IAttributeType.String attributeType = TheoryAttributes.FORMULA_ATTRIBUTE;
		String form = element.getFormula();
		Formula<?> formula = null;
		if (isExpression) {
			IParseResult result = ff.parseExpression(form, null);
			if (issueErrors) {
				if (CoreUtilities.issueASTProblemMarkers(element,
						attributeType, result, markerDisplay)) {
					return null;
				}
			} else {
				if (result.hasProblem()) {
					return null;
				}
			}
			formula = result.getParsedExpression();

		} else {
			IParseResult result = ff.parsePredicate(form, null);
			if (issueErrors) {
				if (CoreUtilities.issueASTProblemMarkers(element,
						attributeType, result, markerDisplay)) {
					return null;
				}
			} else {
				if (result.hasProblem()) {
					return null;
				}
			}
			formula = result.getParsedPredicate();
		}
		FreeIdentifier[] idents = formula.getFreeIdentifiers();
		for (FreeIdentifier ident : idents) {
			if (!tEnv.contains(ident.getName())) {
				markerDisplay.createProblemMarker(element, attributeType,
						GraphProblem.UndeclaredFreeIdentifierError,
						ident.getName());
				return null;
			}
		}
		ITypeCheckResult tcResult = formula.typeCheck(tEnv);
		if (issueErrors) {
			if (CoreUtilities.issueASTProblemMarkers(element, attributeType,
					tcResult, markerDisplay)) {
				return null;
			}
		} else {
			if (tcResult.hasProblem()) {
				return null;
			}
		}
		return formula;
	}
	
	/**
	 * Parses the formula stored in the given element.
	 * @param element the formula element
	 * @param ff the formula factory
	 * @param typeEnvironment 
	 * @param markerDisplay the marker display
	 * @return the parsed formula, or <code>null</code>
	 * @throws CoreException
	 */
	public static Formula<?> parseFormula(IFormulaElement element,
			FormulaFactory ff, IMarkerDisplay markerDisplay)throws CoreException {
		IAttributeType.String attributeType = TheoryAttributes.FORMULA_ATTRIBUTE;
		String form = element.getFormula();
		IParseResult result = ff.parseExpression(form, null);
		if(result.hasProblem()){
			result = ff.parsePredicate(form, null);
			if (CoreUtilities.issueASTProblemMarkers(element,
					attributeType, result, markerDisplay)){
				return null;
			}
			return result.getParsedPredicate();
		}
		return result.getParsedExpression();
	}

	/**
	 * Parses the formula pattern stored in the given element.
	 * 
	 * @param element
	 *            the formula element
	 * @param ff
	 *            the formula factory
	 * @param markerDisplay
	 *            the marker display
	 * @return the parsed formula, or <code>null</code>
	 * @throws CoreException
	 */
	public static Formula<?> parseFormulaPattern(IFormulaElement element,
			FormulaFactory ff, IMarkerDisplay markerDisplay) throws CoreException {
		IAttributeType.String attributeType = TheoryAttributes.FORMULA_ATTRIBUTE;
		String form = element.getFormula();
		IParseResult result = ff.parseExpressionPattern(form, null);
		if(result.hasProblem()){
			result = ff.parsePredicatePattern(form, null);
			if (CoreUtilities.issueASTProblemMarkers(element,
					attributeType, result, markerDisplay)){
				return null;
			}
			return result.getParsedPredicate();
		}
		return result.getParsedExpression();
	}

	/**
	 * Type checks the formula.
	 * @param element the formula element
	 * @param formula the actual formula
	 * @param typeEnvironment the type environment
	 * @param markerDisplay the marker display
	 * @return the type checked formula, or <code>null</code>
	 * @throws CoreException
	 */
	public static Formula<?> checkFormula(IFormulaElement element, Formula<?> formula,
			ITypeEnvironment typeEnvironment,
			IMarkerDisplay markerDisplay)throws CoreException {
		ITypeCheckResult tcResult = formula.typeCheck(typeEnvironment);
		if(CoreUtilities.issueASTProblemMarkers(element,
					TheoryAttributes.FORMULA_ATTRIBUTE, tcResult, markerDisplay)){
			return null;
		}
		return formula;
	}
	
	/**
	 * Attempts to parse and check the formula string.
	 * @param form the formula string
	 * @param ff the formula factory
	 * @param env the type environment
	 * @return the parsed formula or <code>null</code> if the passed string cannot be parsed, or if parsed cannot be typechecked 
	 */
	public static Formula<?> parseAndTypeCheckFormula(String form, FormulaFactory ff, ITypeEnvironment env){
		Formula<?> formula = null;
		IParseResult result = ff.parseExpression(form, null);
		if(result.hasProblem()){
			result = ff.parsePredicate(form, null);
			if (result.hasProblem()){
				return null;
			}
			formula = result.getParsedPredicate();
		}
		else {
			formula = result.getParsedExpression();
		}
		ITypeCheckResult typeCheck = formula.typeCheck(env);
		if (typeCheck.hasProblem()){
			return null;
		}
		return formula;
	}
	
	/**
	 * Attempts to parse and check the formula string.
	 * 
	 * @param form
	 *            the formula string
	 * @param ff
	 *            the formula factory
	 * @param env
	 *            the type environment
	 * @return the parsed formula or <code>null</code> if the passed string
	 *         cannot be parsed, or if parsed cannot be typechecked
	 */
	public static Formula<?> parseAndTypeCheckFormulaPattern(String form, FormulaFactory ff, ITypeEnvironment env) {
		Formula<?> formula = null;
		IParseResult result = ff.parseExpressionPattern(form, null);
		if(result.hasProblem()){
			result = ff.parsePredicatePattern(form, null);
			if (result.hasProblem()){
				return null;
			}
			formula = result.getParsedPredicate();
		}
		else {
			formula = result.getParsedExpression();
		}
		ITypeCheckResult typeCheck = formula.typeCheck(env);
		if (typeCheck.hasProblem()){
			return null;
		}
		return formula;
	}
	
	/**
	 * Calculate the MD5 digest algorithm hash value for the given file.
	 * <p>
	 * The given file must exist.
	 * <p>
	 * 
	 * @param file
	 *            a file
	 * @return the string of the calculated hash value
	 * @throws IllegalArgumentException
	 *             if the file does not exist
	 */
	public static String ComputeHashValue(IFile file) {
		if (!file.exists()) {
			throw new IllegalArgumentException("File does not exist: " + file);
		}
		try {
			final InputStream is = file.getContents();
			try {
				final MessageDigest md = MessageDigest.getInstance("MD5");
				final DigestInputStream dis = new DigestInputStream(is, md);
				while (dis.read() != -1)
					;
				final byte[] digest = md.digest();
				final StringBuffer sb = new StringBuffer();
				for (int i = 0; i < digest.length; i++) {
					sb.append(Integer.toHexString((digest[i] & 0xFF) | 0x100)
							.substring(1, 3));
				}
				return sb.toString();
			} catch (NoSuchAlgorithmException e) {
				CoreUtilities.log(e, "while computing hash for " + file);
			} finally {
				is.close();
			}
		} catch (Exception e) {
			CoreUtilities.log(e, "while computing hash for " + file);
		}
		return null;
	}
}
