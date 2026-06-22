/*******************************************************************************
 * Copyright (c) 2010, 2020 University of Southampton and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core;

import static org.eventb.internal.core.Messages.database_SCIdentifierTypeParseFailure;
import static org.eventb.internal.core.Messages.database_SCPredicateParseFailure;
import static org.eventb.internal.core.Messages.database_SCPredicateTCFailure;
import static org.eventb.theory.core.TheoryAttributes.APPLICABILITY_ATTRIBUTE;
import static org.eventb.theory.core.TheoryAttributes.ASSOCIATIVE_ATTRIBUTE;
import static org.eventb.theory.core.TheoryAttributes.COMMUTATIVE_ATTRIBUTE;
import static org.eventb.theory.core.TheoryAttributes.COMPLETE_ATTRIBUTE;
import static org.eventb.theory.core.TheoryAttributes.DEFINITIONAL_ATTRIBUTE;
import static org.eventb.theory.core.TheoryAttributes.DESC_ATTRIBUTE;
import static org.eventb.theory.core.TheoryAttributes.FORMULA_ATTRIBUTE;
import static org.eventb.theory.core.TheoryAttributes.FORMULA_TYPE_ATTRIBUTE;
import static org.eventb.theory.core.TheoryAttributes.GIVEN_TYPE_ATTRIBUTE;
import static org.eventb.theory.core.TheoryAttributes.GROUP_ID_ATTRIBUTE;
import static org.eventb.theory.core.TheoryAttributes.HYP_ATTRIBUTE;
import static org.eventb.theory.core.TheoryAttributes.IMPORT_THEORY_ATTRIBUTE;
import static org.eventb.theory.core.TheoryAttributes.INDUCTIVE_ARGUMENT_ATTRIBUTE;
import static org.eventb.theory.core.TheoryAttributes.NOTATION_TYPE_ATTRIBUTE;
import static org.eventb.theory.core.TheoryAttributes.REASONING_TYPE_ATTRIBUTE;
import static org.eventb.theory.core.TheoryAttributes.TYPE_ATTRIBUTE;
import static org.eventb.theory.core.TheoryAttributes.WD_ATTRIBUTE;
import static org.eventb.theory.core.sc.Messages.database_SCTypeParseFailure;
import static org.eventb.theory.core.util.CoreUtilities.newCoreException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.IOperatorProperties.FormulaType;
import org.eventb.core.ast.extension.IOperatorProperties.Notation;
import org.eventb.core.ast.extensions.maths.AstUtilities;
import org.eventb.core.basis.EventBElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

/**
 * Common implementation for Event-B Theory elements.
 * 
 * @author maamria
 * 
 */
@SuppressWarnings("restriction")
public abstract class TheoryElement extends EventBElement implements
		IAssociativeElement, ICommutativeElement, IFormulaElement,
		IFormulaTypeElement, INotationTypeElement, 
		ITypeElement, ICompleteElement, IDescriptionElement,
		IDefinitionalElement, ISCTypeElement,
		IGivenTypeElement, ISCGivenTypeElement, ISCFormulaElement,
		IReasoningTypeElement, IOperatorGroupElement,
		IImportTheoryElement, IInductiveArgumentElement, IWDElement, IApplicabilityElement, 
		IHypElement{

	public TheoryElement(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public boolean hasOperatorGroup() throws RodinDBException {
		return hasAttribute(GROUP_ID_ATTRIBUTE);
	}

	@Override
	public String getOperatorGroup() throws RodinDBException {
		return getAttributeValue(GROUP_ID_ATTRIBUTE);
	}

	@Override
	public void setOperatorGroup(String newGroup, IProgressMonitor monitor)
			throws RodinDBException {
		setAttributeValue(GROUP_ID_ATTRIBUTE, newGroup, monitor);
	}
	
	@Override
	public Type getType(FormulaFactory factory) throws CoreException {
		final String type = getType();
		final IParseResult result = factory.parseType(type);
		if (result.hasProblem()){
			throw newCoreException(database_SCTypeParseFailure, this);
		}
		return result.getParsedType();
	}

	@Override
	public void setType(Type type, IProgressMonitor monitor)
			throws RodinDBException {
		setType(type.toString(), monitor);
	}

	@Override
	public boolean hasAssociativeAttribute() throws RodinDBException {
		return hasAttribute(ASSOCIATIVE_ATTRIBUTE);
	}

	@Override
	public boolean isAssociative() throws RodinDBException {
		return getAttributeValue(ASSOCIATIVE_ATTRIBUTE);
	}

	public void setAssociative(boolean isAssociative, IProgressMonitor monitor)
			throws RodinDBException {
		setAttributeValue(ASSOCIATIVE_ATTRIBUTE, isAssociative, monitor);
	}

	@Override
	public boolean hasCommutativeAttribute() throws RodinDBException {
		return hasAttribute(COMMUTATIVE_ATTRIBUTE);
	}

	@Override
	public boolean isCommutative() throws RodinDBException {
		return getAttributeValue(COMMUTATIVE_ATTRIBUTE);
	}

	@Override
	public void setCommutative(boolean isCommutative, IProgressMonitor monitor)
			throws RodinDBException {
		setAttributeValue(COMMUTATIVE_ATTRIBUTE, isCommutative, monitor);
	}

	@Override
	public String getFormula() throws RodinDBException {
		return getAttributeValue(FORMULA_ATTRIBUTE);
	}

	@Override
	public boolean hasFormula() throws RodinDBException {
		return hasAttribute(FORMULA_ATTRIBUTE);
	}

	@Override
	public void setFormula(String formula, IProgressMonitor monitor)
			throws RodinDBException {
		setAttributeValue(FORMULA_ATTRIBUTE, formula, monitor);
	}

	@Override
	public FormulaType getFormulaType() throws RodinDBException {
		return (getAttributeValue(FORMULA_TYPE_ATTRIBUTE) ? FormulaType.EXPRESSION
				: FormulaType.PREDICATE);
	}

	@Override
	public boolean hasFormulaType() throws RodinDBException {
		return hasAttribute(FORMULA_TYPE_ATTRIBUTE);
	}

	@Override
	public void setFormulaType(FormulaType type, IProgressMonitor monitor)
			throws RodinDBException {
		setAttributeValue(FORMULA_TYPE_ATTRIBUTE,
				type.equals(FormulaType.EXPRESSION) ? true : false, monitor);
	}

	@Override
	public Notation getNotationType() throws RodinDBException {
		return AstUtilities
				.getNotation(getAttributeValue(NOTATION_TYPE_ATTRIBUTE));
	}

	@Override
	public boolean hasNotationType() throws RodinDBException {
		return hasAttribute(NOTATION_TYPE_ATTRIBUTE);
	}

	@Override
	public void setNotationType(String notation, IProgressMonitor monitor)
			throws RodinDBException {
		setAttributeValue(NOTATION_TYPE_ATTRIBUTE, notation, monitor);
	}

	@Override
	public String getType() throws RodinDBException {
		return getAttributeValue(TYPE_ATTRIBUTE);
	}

	@Override
	public boolean hasType() throws RodinDBException {
		return hasAttribute(TYPE_ATTRIBUTE);
	}

	@Override
	public void setType(String type, IProgressMonitor monitor)
			throws RodinDBException {
		setAttributeValue(TYPE_ATTRIBUTE, type, monitor);
	}

	public boolean hasComplete() throws RodinDBException {
		return hasAttribute(COMPLETE_ATTRIBUTE);
	}

	public boolean isComplete() throws RodinDBException {
		return getAttributeValue(COMPLETE_ATTRIBUTE);
	}

	public void setComplete(boolean isComplete, IProgressMonitor pm)
			throws RodinDBException {
		setAttributeValue(COMPLETE_ATTRIBUTE, isComplete, pm);

	}

	public boolean hasDescription() throws RodinDBException {
		return hasAttribute(DESC_ATTRIBUTE);
	}

	public String getDescription() throws RodinDBException {
		return getAttributeValue(DESC_ATTRIBUTE);
	}

	public void setDescription(String newDescription, IProgressMonitor monitor)
			throws RodinDBException {
		setAttributeValue(DESC_ATTRIBUTE, newDescription, monitor);
	}

	@Override
	public boolean hasDefinitionalAttribute() throws RodinDBException {
		return hasAttribute(DEFINITIONAL_ATTRIBUTE);
	}

	@Override
	public boolean isDefinitional() throws RodinDBException {
		return getAttributeValue(DEFINITIONAL_ATTRIBUTE);
	}

	@Override
	public void setDefinitional(boolean isDefinitional, IProgressMonitor monitor)
			throws RodinDBException {
		setAttributeValue(DEFINITIONAL_ATTRIBUTE, isDefinitional, monitor);
	}

	@Override
	public boolean hasGivenType() throws RodinDBException {
		return hasAttribute(GIVEN_TYPE_ATTRIBUTE);
	}

	@Override
	public String getGivenType() throws RodinDBException {
		return getAttributeValue(GIVEN_TYPE_ATTRIBUTE);
	}

	@Override
	public void setGivenType(String type, IProgressMonitor monitor)
			throws RodinDBException {
		setAttributeValue(GIVEN_TYPE_ATTRIBUTE, type, monitor);

	}

	@Override
	public Type getSCGivenType(FormulaFactory factory) throws CoreException {
		String typeStr = getAttributeValue(GIVEN_TYPE_ATTRIBUTE);
		IParseResult parserResult = factory.parseType(typeStr);
		if (parserResult.getProblems().size() != 0) {
			throw newCoreException(database_SCIdentifierTypeParseFailure, this);
		}
		return parserResult.getParsedType();
	}

	@Override
	public void setSCGivenType(Type type, IProgressMonitor monitor)
			throws RodinDBException {
		setAttributeValue(GIVEN_TYPE_ATTRIBUTE, type.toString(), monitor);

	}

	@Override
	public boolean hasSCFormula() throws RodinDBException {
		return hasFormula();
	}

	@Override
	public Formula<?> getSCFormula(FormulaFactory ff,
			ITypeEnvironment typeEnvironment) throws CoreException {
		String form = getFormula();
		Formula<?> formula = parseFormula(form, ff, true);
		if (formula == null) {
			throw newCoreException("Error parsing formula: " + form
					+ "\nwith factory: " + ff.getExtensions());
		}
		ITypeCheckResult result = formula.typeCheck(typeEnvironment);
		if (result.hasProblem()) {
			throw newCoreException("Error typechecking formula: " + formula
					+ "\nwith factory: " + ff.getExtensions()
					+ "\nwith type env: " + typeEnvironment + "\nresult: "
					+ result);
		}
		return formula;
	}

	@Override
	public void setSCFormula(Formula<?> formula, IProgressMonitor monitor)
			throws RodinDBException {
		setFormula(formula.toStringWithTypes(), monitor);
	}

	@Override
	public boolean hasReasoningAttribute() throws RodinDBException {
		return hasAttribute(REASONING_TYPE_ATTRIBUTE);
	}

	@Override
	public boolean isSuitableForBackwardReasoning() throws RodinDBException {
		String type = getAttributeValue(REASONING_TYPE_ATTRIBUTE);
		return isSuitableForAllReasoning()
				|| ReasoningType.getReasoningType(type).equals(ReasoningType.BACKWARD);
	}

	@Override
	public boolean isSuitableForForwardReasoning() throws RodinDBException {
		String type = getAttributeValue(REASONING_TYPE_ATTRIBUTE);
		return isSuitableForAllReasoning()
				|| ReasoningType.getReasoningType(type).equals(ReasoningType.FORWARD);
	}

	@Override
	public boolean isSuitableForAllReasoning() throws RodinDBException {
		String type = getAttributeValue(REASONING_TYPE_ATTRIBUTE);
		return ReasoningType.getReasoningType(type).equals(
				ReasoningType.BACKWARD_AND_FORWARD);
	}

	@Override
	public void setReasoningType(ReasoningType type, IProgressMonitor monitor)
			throws RodinDBException {
		setAttributeValue(REASONING_TYPE_ATTRIBUTE,
				type.toString(), monitor);

	}

	@Override
	public boolean hasImportTheory() throws RodinDBException {
		return hasAttribute(IMPORT_THEORY_ATTRIBUTE);
	}
	
	@Override
	public IDeployedTheoryRoot getImportTheory() throws RodinDBException {
		return (IDeployedTheoryRoot) getAttributeValue(IMPORT_THEORY_ATTRIBUTE);
	}
	
	@Override
	public IRodinProject getImportTheoryProject() throws RodinDBException {
		//return (IRodinProject) getAttributeValue(IMPORT_THEORY_PROJECT_ATTRIBUTE);
		if(parent!=null){
			IImportTheoryProject theoryProject = (IImportTheoryProject) parent;
			return theoryProject.getTheoryProject();
		}
		
		return null;
	}

	@Override
	public void setImportTheory(IDeployedTheoryRoot root, IProgressMonitor monitor)
			throws RodinDBException {
		setAttributeValue(IMPORT_THEORY_ATTRIBUTE, root, monitor);
	}

	@Override
	public boolean hasHypAttribute() throws RodinDBException {
		return hasAttribute(HYP_ATTRIBUTE);
	}
	
	@Override
	public boolean isHyp() throws RodinDBException {
		return getAttributeValue(HYP_ATTRIBUTE);
	}
	
	@Override
	public void setHyp(boolean isHyp, IProgressMonitor monitor) throws RodinDBException {
		setAttributeValue(HYP_ATTRIBUTE, isHyp, monitor);
	}
	
	public boolean hasInductiveArgument() throws RodinDBException{
		return hasAttribute(INDUCTIVE_ARGUMENT_ATTRIBUTE);
	}

	public String getInductiveArgument() throws RodinDBException{
		return getAttributeValue(INDUCTIVE_ARGUMENT_ATTRIBUTE);
	}

	public void setInductiveArgument(String inductiveArgument, IProgressMonitor monitor)
			throws RodinDBException{
		setAttributeValue(INDUCTIVE_ARGUMENT_ATTRIBUTE, inductiveArgument, monitor);
	}
	
	@Override
	public boolean hasWDAttribute() throws RodinDBException {
		return hasAttribute(WD_ATTRIBUTE);
	}

	@Override
	public Predicate getWDCondition(FormulaFactory factory, ITypeEnvironment typeEnvironment) throws CoreException {
		String wdStr = getAttributeValue(WD_ATTRIBUTE);
		IParseResult result = factory.parsePredicate(wdStr, null);
		if(result.hasProblem()){
			throw newCoreException(database_SCPredicateParseFailure);
		}
		Predicate pred = result.getParsedPredicate();
		ITypeCheckResult tcResult = pred.typeCheck(typeEnvironment);
		if(tcResult.hasProblem()){
			throw newCoreException(database_SCPredicateTCFailure);
		}
		return pred;
	}

	@Override
	public void setWDCondition(Predicate newWD, IProgressMonitor monitor) throws RodinDBException {
		setAttributeValue(WD_ATTRIBUTE, newWD.toStringWithTypes(), monitor);
	}
	
	@Override
	public boolean hasApplicabilityAttribute() throws RodinDBException {
		return hasAttribute(APPLICABILITY_ATTRIBUTE);
	}
	
	@Override
	public RuleApplicability getApplicability() throws RodinDBException {
		String value = getAttributeValue(APPLICABILITY_ATTRIBUTE);
		return RuleApplicability.getRuleApplicability(value);
	}
	
	@Override
	public void setApplicability(RuleApplicability applicability, IProgressMonitor monitor) throws RodinDBException {
		setAttributeValue(APPLICABILITY_ATTRIBUTE, applicability.toString(), monitor);
	}
	
	@Override
	public boolean isAutomatic() throws RodinDBException {
		String value = getAttributeValue(APPLICABILITY_ATTRIBUTE);
		return RuleApplicability.getRuleApplicability(value).isAutomatic();
	}
	
	@Override
	public boolean isInteractive() throws RodinDBException {
		String value = getAttributeValue(APPLICABILITY_ATTRIBUTE);
		return RuleApplicability.getRuleApplicability(value).isInteractive();
	}

	/**
	 * Parses the formula string provided using the given formula factory. The
	 * formula string may contain predicate variables.
	 * 
	 * @param formStr
	 *            the formula string
	 * @param ff
	 *            the formula factory
	 * @param isPattern
	 *            whether the formula is expected to have predicate variables
	 * @return the parsed formula
	 */
	public static Formula<?> parseFormula(String formStr, FormulaFactory ff,
			boolean isPattern) {
		Formula<?> formula = null;
		if (isPattern) {
			IParseResult res = ff.parseExpressionPattern(formStr, null);
			if (!res.hasProblem()) {
				formula = res.getParsedExpression();
			} else {
				res = ff.parsePredicatePattern(formStr, null);
				if (!res.hasProblem()) {
					formula = res.getParsedPredicate();
				}
			}
		} else {
			IParseResult res = ff.parseExpression(formStr, null);
			if (!res.hasProblem()) {
				formula = res.getParsedExpression();
			} else {
				res = ff.parsePredicate(formStr, null);
				if (!res.hasProblem()) {
					formula = res.getParsedPredicate();
				}
			}
		}

		return formula;
	}
}
