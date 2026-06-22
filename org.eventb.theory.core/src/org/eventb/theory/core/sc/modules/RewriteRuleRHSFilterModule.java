/*******************************************************************************
 * Copyright (c) 2011, 2020 University of Southampton and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.sc.modules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.PredicateVariable;
import org.eventb.core.ast.Type;
import org.eventb.core.sc.GraphProblem;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.SCFilterModule;
import org.eventb.core.sc.state.IIdentifierSymbolInfo;
import org.eventb.core.sc.state.IIdentifierSymbolTable;
import org.eventb.core.sc.state.ILabelSymbolInfo;
import org.eventb.core.sc.state.ILabelSymbolTable;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.internal.core.sc.ParsedFormula;
import org.eventb.theory.core.IRewriteRuleRightHandSide;
import org.eventb.theory.core.TheoryAttributes;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.TheoryGraphProblem;
import org.eventb.theory.core.sc.states.RewriteRuleLabelSymbolTable;
import org.eventb.theory.core.util.CoreUtilities;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;

/**
 * 
 * @author maamria
 * 
 */
@SuppressWarnings("restriction")
public class RewriteRuleRHSFilterModule extends SCFilterModule {

	public static final IModuleType<RewriteRuleRHSFilterModule> MODULE_TYPE = SCCore
			.getModuleType(TheoryPlugin.PLUGIN_ID
					+ ".rewriteRuleRHSFilterModule");

	private ParsedFormula lhsParsedFormula;
	private ILabelSymbolTable ruleLabelSymbolTable;
	private FormulaFactory factory;
	private ITypeEnvironment typeEnvironment;

	private IIdentifierSymbolTable identifierSymbolTable;

	public void initModule(ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		super.initModule(repository, monitor);
		lhsParsedFormula = (ParsedFormula) repository
				.getState(ParsedFormula.STATE_TYPE);
		ruleLabelSymbolTable = (ILabelSymbolTable) repository
				.getState(RewriteRuleLabelSymbolTable.STATE_TYPE);
		factory = repository.getFormulaFactory();
		typeEnvironment = repository.getTypeEnvironment();
		identifierSymbolTable = (IIdentifierSymbolTable) repository
				.getState(IIdentifierSymbolTable.STATE_TYPE);
	}

	@Override
	public boolean accept(IRodinElement element, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		IRewriteRuleRightHandSide ruleRHS = (IRewriteRuleRightHandSide) element;
		ILabelSymbolInfo symbolInfo = ruleLabelSymbolTable
				.getSymbolInfo(ruleRHS.getLabel());
		// Check condition
		Predicate condition = checkCondition(ruleRHS, repository);
		if (condition != null) {
			symbolInfo.setAttributeValue(EventBAttributes.PREDICATE_ATTRIBUTE,
					condition.toStringWithTypes());
		} else {
			return false;
		}
		// Check rhs
		Formula<?> rhs = checkRHSFormula(ruleRHS, repository);
		if (rhs != null) {
			symbolInfo.setAttributeValue(TheoryAttributes.FORMULA_ATTRIBUTE,
					rhs.toStringWithTypes());
		} else {
			return false;
		}
		return true;
	}

	protected Formula<?> checkRHSFormula(IRewriteRuleRightHandSide ruleRHS,
			ISCStateRepository repository) throws CoreException {
		if (!ruleRHS.hasFormula()) {
			createProblemMarker(ruleRHS, TheoryAttributes.FORMULA_ATTRIBUTE,
					TheoryGraphProblem.RHSFormulaMissingError);
			return null;
		}
		Formula<?> lhsFormula = lhsParsedFormula.getFormula();
		IParseResult parseResult = null;
		Formula<?> rhsFormula = null;
		String rhsStr = ruleRHS.getFormula();
		if (lhsFormula instanceof Expression) {
			parseResult = factory.parseExpression(rhsStr, ruleRHS);
			if (CoreUtilities.issueASTProblemMarkers(ruleRHS,
					TheoryAttributes.FORMULA_ATTRIBUTE, parseResult, this)) {
				return null;
			}
			rhsFormula = parseResult.getParsedExpression();
		} else if (lhsFormula instanceof Predicate) {
			parseResult = factory.parsePredicate(rhsStr, ruleRHS);
			if (CoreUtilities.issueASTProblemMarkers(ruleRHS,
					TheoryAttributes.FORMULA_ATTRIBUTE, parseResult, this)) {
				return null;
			}
			rhsFormula = parseResult.getParsedPredicate();
		}
		if (rhsFormula == null) {
			return null;
		}
		// 1- need to check idents
		if (!checkIdentsAreDefined(ruleRHS, rhsFormula,
				TheoryAttributes.FORMULA_ATTRIBUTE, repository, null)) {
			return null;
		}
		ITypeCheckResult tcResult = rhsFormula.typeCheck(typeEnvironment);
		if (CoreUtilities.issueASTProblemMarkers(ruleRHS,
				TheoryAttributes.FORMULA_ATTRIBUTE, tcResult, this)) {
			return null;
		}
		// 2- need to check all idents are in lhs as well
		if (!checkIdentsOccurInLhs(ruleRHS, rhsFormula, lhsFormula,
				TheoryAttributes.FORMULA_ATTRIBUTE,
				TheoryGraphProblem.RHSIdentsNotSubsetOfLHSIdents,
				TheoryGraphProblem.RHSTypesNotSubsetOfLHSTypes, repository,
				null)) {
			return null;
		}
		if (lhsFormula instanceof Expression) {
			Type lhsType = ((Expression) lhsFormula).getType();
			Type rhsType = ((Expression) rhsFormula).getType();
			if (!lhsType.equals(rhsType)) {
				createProblemMarker(ruleRHS,
						TheoryAttributes.FORMULA_ATTRIBUTE,
						TheoryGraphProblem.RuleTypeMismatchError, lhsType,
						rhsType);
				// Bug FIXED : needed to return null here
				return null;
			}
		}
		return rhsFormula;
	}

	protected Predicate checkCondition(IRewriteRuleRightHandSide ruleRHS,
			ISCStateRepository repository) throws CoreException {
		if (!ruleRHS.hasPredicateString()) {
			createProblemMarker(ruleRHS, EventBAttributes.PREDICATE_ATTRIBUTE,
					TheoryGraphProblem.CondUndefError);
			return null;
		}
		Formula<?> lhsFormula = lhsParsedFormula.getFormula();
		String condStr = ruleRHS.getPredicateString();
		IParseResult parseResult = factory.parsePredicatePattern(condStr, ruleRHS);
		if (CoreUtilities.issueASTProblemMarkers(ruleRHS,
				EventBAttributes.PREDICATE_ATTRIBUTE, parseResult, this)) {
			return null;
		}
		Predicate condition = parseResult.getParsedPredicate();
		// 1- need to check idents
		if (!checkIdentsAreDefined(ruleRHS, condition,
				EventBAttributes.PREDICATE_ATTRIBUTE, repository, null)) {
			return null;
		}
		ITypeCheckResult tcResult = condition.typeCheck(typeEnvironment);
		if (CoreUtilities.issueASTProblemMarkers(ruleRHS,
				EventBAttributes.PREDICATE_ATTRIBUTE, tcResult, this)) {
			return null;
		}
		// 2- need to check all idents are in lhs as well
		if (!checkIdentsOccurInLhs(ruleRHS, condition, lhsFormula,
				EventBAttributes.PREDICATE_ATTRIBUTE,
				TheoryGraphProblem.CondIdentsNotSubsetOfLHSIdents,
				TheoryGraphProblem.CondTypesNotSubsetOfLHSTypes, repository,
				null)) {
			return null;
		}
		return condition;
	}

	public void endModule(ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		lhsParsedFormula = null;
		ruleLabelSymbolTable = null;
		factory = null;
		typeEnvironment = null;
		super.endModule(repository, monitor);
	}

	@Override
	public IModuleType<?> getModuleType() {
		// TODO Auto-generated method stub
		return MODULE_TYPE;
	}
	
	protected boolean checkIdentsOccurInLhs(IRewriteRuleRightHandSide element,
			Formula<?> form, Formula<?> lhsFormula, IAttributeType.String type,
			TheoryGraphProblem identsProblem, TheoryGraphProblem typesProblem,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		List<FreeIdentifier> lhsIdents = new ArrayList<FreeIdentifier>(Arrays.asList(lhsFormula.getFreeIdentifiers()));
		Set<GivenType> lhsTypes = lhsFormula.getGivenTypes();
		List<FreeIdentifier> formIdents = new ArrayList<FreeIdentifier>(Arrays.asList(form
				.getFreeIdentifiers()));
		Set<GivenType> formTypes = form.getGivenTypes();
		if (!lhsTypes.containsAll(formTypes)) {
			createProblemMarker(element, type, typesProblem, element.getLabel());
			return false;
		}
		lhsIdents.removeAll(getFreeIdentifiers(lhsTypes));
		formIdents.removeAll(getFreeIdentifiers(formTypes));
		if (!lhsIdents.containsAll(formIdents)) {
			createProblemMarker(element, type, identsProblem,
					element.getLabel());
			return false;
		}
		
		List<PredicateVariable> formVars = Arrays.asList(form.getPredicateVariables());
		List<PredicateVariable> lhsVars = Arrays.asList(lhsFormula.getPredicateVariables());
		if (!lhsVars.containsAll(formVars)){
			createProblemMarker(element,
					TheoryGraphProblem.RHSPredVarsNOTSubsetOFLHS);
			return false;
		}
		return true;
	}

	protected boolean checkIdentsAreDefined(IRewriteRuleRightHandSide element,
			Formula<?> form, IAttributeType.String type,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		boolean ok = true;
		IInternalElement internalElement = (IInternalElement) element;
		FreeIdentifier[] condIdents = form.getFreeIdentifiers();
		for (FreeIdentifier freeIdentifier : condIdents) {
			IIdentifierSymbolInfo symbolInfo = getSymbolInfo(internalElement,
					type, freeIdentifier, monitor);
			if (symbolInfo == null) {
				ok = false;
			}
		}

		return ok;
	}

	protected IIdentifierSymbolInfo getSymbolInfo(IInternalElement element,
			IAttributeType.String type, FreeIdentifier freeIdentifier,
			IProgressMonitor monitor) throws CoreException {
		IIdentifierSymbolInfo symbolInfo = identifierSymbolTable
				.getSymbolInfo(freeIdentifier.getName());
		if (symbolInfo == null) {
			createProblemMarker(element, type, freeIdentifier
					.getSourceLocation().getStart(), freeIdentifier
					.getSourceLocation().getEnd(),
					GraphProblem.UndeclaredFreeIdentifierError,
					freeIdentifier.getName());
		} else if (symbolInfo.hasError()) {
			createProblemMarker(element, type, freeIdentifier
					.getSourceLocation().getStart(), freeIdentifier
					.getSourceLocation().getEnd(),
					GraphProblem.FreeIdentifierFaultyDeclError,
					freeIdentifier.getName());
			symbolInfo = null;
		}
		return symbolInfo;
	}

	private Set<FreeIdentifier> getFreeIdentifiers(Set<GivenType> types){
		Set<FreeIdentifier> set = new LinkedHashSet<FreeIdentifier>();
		for (GivenType givenType : types){
			set.add((FreeIdentifier)givenType.toExpression());
		}
		return set;
	}
}
