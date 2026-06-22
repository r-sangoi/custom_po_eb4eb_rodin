/*******************************************************************************
 * Copyright (c) 2011, 2022 University of Southampton and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.sc.modules;

import static org.eventb.core.ast.extensions.maths.AstUtilities.makeBTRUE;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.core.ast.extensions.maths.AstUtilities;
import org.eventb.core.ast.extensions.maths.RecursiveDefinition;
import org.eventb.core.sc.GraphProblem;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.SCProcessorModule;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.theory.core.INewOperatorDefinition;
import org.eventb.theory.core.IRecursiveDefinitionCase;
import org.eventb.theory.core.IRecursiveOperatorDefinition;
import org.eventb.theory.core.ISCNewOperatorDefinition;
import org.eventb.theory.core.ISCRecursiveDefinitionCase;
import org.eventb.theory.core.ISCRecursiveOperatorDefinition;
import org.eventb.theory.core.TheoryAttributes;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.TheoryGraphProblem;
import org.eventb.theory.core.sc.states.OperatorInformation;
import org.eventb.theory.core.sc.states.RecursiveDefinitionInfo;
import org.eventb.theory.core.sc.states.TheoryAccuracyInfo;
import org.eventb.theory.core.util.CoreUtilities;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * 
 * @author maamria
 * 
 */
public class OperatorRecursiveCaseModule extends SCProcessorModule {

	private static final IModuleType<OperatorRecursiveCaseModule> MODULE_TYPE = SCCore
			.getModuleType(TheoryPlugin.PLUGIN_ID + ".operatorRecursiveCaseModule");

	private TheoryAccuracyInfo theoryAccuracyInfo;
	private FormulaFactory factory;
	private RecursiveDefinitionInfo recursiveDefinitionInfo;
	private ITypeEnvironment typeEnvironment;
	private OperatorInformation operatorInformation;

	@Override
	public void process(IRodinElement element, IInternalElement target, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		IRecursiveOperatorDefinition definition = (IRecursiveOperatorDefinition) element;
		ISCRecursiveOperatorDefinition scDefinition = (ISCRecursiveOperatorDefinition) target;
		IRecursiveDefinitionCase[] recursiveDefinitionCases = definition.getRecursiveDefinitionCases();
		int numberOfCases = recursiveDefinitionCases.length;
		if (numberOfCases < 1) {
			createProblemMarker(definition, TheoryAttributes.INDUCTIVE_ARGUMENT_ATTRIBUTE,
					TheoryGraphProblem.NoRecCasesError);
			operatorInformation.setHasError();
			recursiveDefinitionInfo.makeImmutable();
		}
		if (numberOfCases > 0 && !operatorInformation.hasError()) {
			processCases(recursiveDefinitionCases, scDefinition, repository, monitor);
			recursiveDefinitionInfo.makeImmutable();
			if (!recursiveDefinitionInfo.isAccurate()) {
				operatorInformation.setHasError();
			} else {
				processCasesFormulae(definition, scDefinition, repository, monitor);
			}
		}
	}

	protected void processCases(IRecursiveDefinitionCase[] origins, ISCRecursiveOperatorDefinition target,
			ISCStateRepository repository, IProgressMonitor monitor) throws CoreException {
		for (IRecursiveDefinitionCase definitionCase : origins) {
			if (!definitionCase.hasExpressionString() || "".equals(definitionCase.getExpressionString())) {
				createProblemMarker(definitionCase, EventBAttributes.EXPRESSION_ATTRIBUTE,
						GraphProblem.ExpressionUndefError);
				recursiveDefinitionInfo.setNotAccurate();
				theoryAccuracyInfo.setNotAccurate();
				continue;
			}			
			String caseString = definitionCase.getExpressionString();
			IParseResult parseResult = factory.parseExpression(caseString, null);
			if (CoreUtilities.issueASTProblemMarkers(definitionCase, EventBAttributes.EXPRESSION_ATTRIBUTE,
					parseResult, this)) {
				recursiveDefinitionInfo.setNotAccurate();
				continue;
			}
			Expression caseExpression = parseResult.getParsedExpression();
			if (!(caseExpression instanceof ExtendedExpression)
					|| !(recursiveDefinitionInfo.isConstructor(((ExtendedExpression) caseExpression).getExtension()))) {
				createProblemMarker(definitionCase, EventBAttributes.EXPRESSION_ATTRIBUTE,
						TheoryGraphProblem.InductiveCaseNotAppropriateExp, caseExpression.toString());
				recursiveDefinitionInfo.setNotAccurate();
				theoryAccuracyInfo.setNotAccurate();
				continue;
			}
			ExtendedExpression constructorExp = (ExtendedExpression) caseExpression;
			Expression[] childExpressions = constructorExp.getChildExpressions();
			boolean illegalConsArg = false;
			Set<String> usedIdentifiers = new HashSet<>();
			for (Expression childExpression : childExpressions) {
				if (!(childExpression instanceof FreeIdentifier)) {
					illegalConsArg = true;
					recursiveDefinitionInfo.setNotAccurate();
					theoryAccuracyInfo.setNotAccurate();
					createProblemMarker(definitionCase, EventBAttributes.EXPRESSION_ATTRIBUTE,
							TheoryGraphProblem.ConsArgNotIdentInCase, childExpression.toString());
				} else if (typeEnvironment.contains(((FreeIdentifier) childExpression).getName())) {
					illegalConsArg = true;
					recursiveDefinitionInfo.setNotAccurate();
					theoryAccuracyInfo.setNotAccurate();
					createProblemMarker(definitionCase, EventBAttributes.EXPRESSION_ATTRIBUTE,
							TheoryGraphProblem.IdentCannotBeUsedAsConsArg,
							((FreeIdentifier) childExpression).toString());
				} else if (!usedIdentifiers.add(((FreeIdentifier) childExpression).getName())) {
					illegalConsArg = true;
					recursiveDefinitionInfo.setNotAccurate();
					theoryAccuracyInfo.setNotAccurate();
					createProblemMarker(definitionCase, EventBAttributes.EXPRESSION_ATTRIBUTE,
							TheoryGraphProblem.IdentAlreadyUsedInCase,
							((FreeIdentifier) childExpression).toString());
				}
			}
			if (illegalConsArg) {
				continue;
			}
			IExpressionExtension extension = ((ExtendedExpression) caseExpression).getExtension();
			if (recursiveDefinitionInfo.isCoveredConstuctor(extension)) {
				recursiveDefinitionInfo.setNotAccurate();
				theoryAccuracyInfo.setNotAccurate();
				createProblemMarker(definitionCase, EventBAttributes.EXPRESSION_ATTRIBUTE,
						TheoryGraphProblem.RecCaseAlreadyCovered);
				continue;
			}
			FreeIdentifier inductiveArg = recursiveDefinitionInfo.getInductiveArgument();
			Predicate predicate = factory.makeRelationalPredicate(Formula.EQUAL, inductiveArg, constructorExp, null);
			ITypeCheckResult tcResult = predicate.typeCheck(typeEnvironment);
			// TODO this passage may not be tested
			if (tcResult.hasProblem()) {
				createProblemMarker(definitionCase, EventBAttributes.EXPRESSION_ATTRIBUTE,
						TheoryGraphProblem.UnableToTypeCase);
				theoryAccuracyInfo.setNotAccurate();
				continue;
			} else {
				ITypeEnvironmentBuilder localTypeEnvironment = typeEnvironment.makeBuilder();
				localTypeEnvironment.addAll(tcResult.getInferredEnvironment());
				// we do this because it's type checked
				constructorExp = (ExtendedExpression) ((RelationalPredicate) predicate).getRight();
				recursiveDefinitionInfo.addEntry(definitionCase, constructorExp, localTypeEnvironment);
			}
		}
	}

	public void processCasesFormulae(IRecursiveOperatorDefinition definition,
			ISCRecursiveOperatorDefinition scDefinition, ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		Map<IRecursiveDefinitionCase, RecursiveDefinitionInfo.CaseEntry> baseEntries = recursiveDefinitionInfo
				.getBaseEntries();
		Map<IRecursiveDefinitionCase, RecursiveDefinitionInfo.CaseEntry> inductiveEntries = recursiveDefinitionInfo
				.getInductiveEntries();
		if (!baseEntries.isEmpty() || !inductiveEntries.isEmpty()) {
			ISCNewOperatorDefinition scParent = scDefinition.getAncestor(ISCNewOperatorDefinition.ELEMENT_TYPE);
			INewOperatorDefinition parent = definition.getAncestor(INewOperatorDefinition.ELEMENT_TYPE);
			if (!recursiveDefinitionInfo.coveredAllConstructors()) {
				createProblemMarker(definition, TheoryAttributes.INDUCTIVE_ARGUMENT_ATTRIBUTE,
						TheoryGraphProblem.NoCoverageAllRecCase);
				operatorInformation.setHasError();
				theoryAccuracyInfo.setNotAccurate();
			} else {
				boolean isExpression = AstUtilities.isExpressionOperator(operatorInformation.getFormulaType());
				Type resultantType = null;
				
				// process base cases
				Map<Expression, Formula<?>> recursiveCases = new LinkedHashMap<Expression, Formula<?>>();
				for (IRecursiveDefinitionCase defCase : baseEntries.keySet()) {
					RecursiveDefinitionInfo.CaseEntry caseEntry = baseEntries.get(defCase);
					if (!defCase.hasFormula()) {
						createProblemMarker(defCase, TheoryAttributes.FORMULA_ATTRIBUTE,
								TheoryGraphProblem.MissingFormulaError);
						caseEntry.setErroneous();
						operatorInformation.setHasError();
						theoryAccuracyInfo.setNotAccurate();
						continue;
					} else {
						Formula<?> formula = ModulesUtils.parseFormula(defCase, factory, this);
						if (formula == null) {
							caseEntry.setErroneous();
							operatorInformation.setHasError();
							continue;
						}

						//check undefined identifiers
						FreeIdentifier[] idents = formula.getFreeIdentifiers();
						boolean hasError = false;
						for (FreeIdentifier ident : idents) {
							ITypeEnvironment localTypeEnvironment = caseEntry.getLocalTypeEnvironment();
							if (!localTypeEnvironment.contains(ident.getName())) {
								createProblemMarker(defCase, TheoryAttributes.FORMULA_TYPE_ATTRIBUTE,
										GraphProblem.UndeclaredFreeIdentifierError,
										ident.getName());
								operatorInformation.setHasError();
								theoryAccuracyInfo.setNotAccurate();
								hasError = true;
							}
						}
						if (hasError)
							continue;
						
						if (isExpression && !(formula instanceof Expression)) {
							createProblemMarker(parent, TheoryAttributes.FORMULA_TYPE_ATTRIBUTE,
									TheoryGraphProblem.OperatorDefNotExpError, parent.getLabel());
							caseEntry.setErroneous();
							operatorInformation.setHasError();
							theoryAccuracyInfo.setNotAccurate();
							continue;
						}
						if (!isExpression && !(formula instanceof Predicate)) {
							createProblemMarker(parent, TheoryAttributes.FORMULA_TYPE_ATTRIBUTE,
									TheoryGraphProblem.OperatorDefNotPredError, parent.getLabel());
							caseEntry.setErroneous();
							operatorInformation.setHasError();
							theoryAccuracyInfo.setNotAccurate();
							continue;
						}
						formula = ModulesUtils.checkFormula(defCase, formula, caseEntry.getLocalTypeEnvironment(),
								this);
						if (formula == null) {
							caseEntry.setErroneous();
							operatorInformation.setHasError();
							continue;
						}
						if (isExpression) {
							Expression expression = (Expression) formula;
							if (resultantType == null) {
								resultantType = expression.getType();
								operatorInformation.setResultantType(resultantType);
							}
							if (!expression.getType().equals(resultantType)) {
								createProblemMarker(defCase, TheoryAttributes.FORMULA_ATTRIBUTE,
										TheoryGraphProblem.RecOpTypeNotConsistent, resultantType,
										expression.getType());
								caseEntry.setErroneous();
								operatorInformation.setHasError();
								theoryAccuracyInfo.setNotAccurate();
								continue;
							} else {
								recursiveCases.put(caseEntry.getCaseExpression(), formula);
							}
						} else {
							recursiveCases.put(caseEntry.getCaseExpression(), formula);
						}
					}
				}
				
				if (!operatorInformation.hasError()) {
					
					FormulaFactory localFactory = factory.withExtensions(Collections.singleton(operatorInformation
							.getInterimExtension()));
					// process inductive cases
					for (IRecursiveDefinitionCase defCase : inductiveEntries.keySet()) {
						RecursiveDefinitionInfo.CaseEntry caseEntry = inductiveEntries.get(defCase);
						if (!defCase.hasFormula()) {
							createProblemMarker(defCase, TheoryAttributes.FORMULA_ATTRIBUTE,
									TheoryGraphProblem.MissingFormulaError);
							caseEntry.setErroneous();
							operatorInformation.setHasError();
							theoryAccuracyInfo.setNotAccurate();
							continue;
						} else {
							Formula<?> formula = ModulesUtils.parseAndCheckFormula(defCase, resultantType != null,
									true, localFactory, caseEntry.getLocalTypeEnvironment(), this);
							if (formula != null) {
								if (resultantType != null) {
									Expression expression = (Expression) formula;
									if (!expression.getType().equals(resultantType)) {
										createProblemMarker(defCase, TheoryAttributes.FORMULA_ATTRIBUTE,
												TheoryGraphProblem.RecOpTypeNotConsistent, resultantType,
												expression.getType());
										caseEntry.setErroneous();
										operatorInformation.setHasError();
										theoryAccuracyInfo.setNotAccurate();
									} else {
										recursiveCases.put(caseEntry.getCaseExpression(), formula);
									}
								} else {
									recursiveCases.put(caseEntry.getCaseExpression(), formula);
								}
							} else {
								caseEntry.setErroneous();
								operatorInformation.setHasError();
							}
						}
					}
					if (!operatorInformation.hasError()) {
						RecursiveDefinition recursiveDefinition = new RecursiveDefinition(
								recursiveDefinitionInfo.getInductiveArgument(), recursiveCases);
						operatorInformation.setDefinition(recursiveDefinition);
						if (operatorInformation.getWdCondition() == null) {
							operatorInformation.addWDCondition(makeBTRUE(localFactory));
							scParent.setPredicate(makeBTRUE(localFactory), monitor);
						}
						for (IRecursiveDefinitionCase defCase : baseEntries.keySet()) {
							if (!baseEntries.get(defCase).isErroneous())
								createSCCase(defCase, scDefinition, recursiveDefinition, baseEntries, monitor);
						}
						for (IRecursiveDefinitionCase defCase : inductiveEntries.keySet()) {
							if (!inductiveEntries.get(defCase).isErroneous())
								createSCCase(defCase, scDefinition, recursiveDefinition, inductiveEntries, monitor);
						}
					}
				}
			}

		}
	}

	@Override
	public void initModule(IRodinElement element, ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		super.initModule(element, repository, monitor);
		theoryAccuracyInfo = (TheoryAccuracyInfo) repository.getState(TheoryAccuracyInfo.STATE_TYPE);
		factory = repository.getFormulaFactory();
		typeEnvironment = repository.getTypeEnvironment();
		recursiveDefinitionInfo = (RecursiveDefinitionInfo) repository.getState(RecursiveDefinitionInfo.STATE_TYPE);
		operatorInformation = (OperatorInformation) repository.getState(OperatorInformation.STATE_TYPE);
	}

	@Override
	public void endModule(IRodinElement element, ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		theoryAccuracyInfo = null;
		operatorInformation = null;
		factory = null;
		typeEnvironment = null;
		recursiveDefinitionInfo = null;
		super.endModule(element, repository, monitor);
	}

	@Override
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	/**
	 * Creates the statically checked recursive definition case.
	 * 
	 * @param origin
	 *            the recursive case origin
	 * @param parent
	 *            the SC recursive definition parent
	 * @param recursiveDefinition
	 *            the recursive definition
	 * @param entries
	 *            the entries
	 * @param monitor
	 *            the progress monitor
	 * @throws RodinDBException
	 */
	private void createSCCase(IRecursiveDefinitionCase origin, ISCRecursiveOperatorDefinition parent,
			RecursiveDefinition recursiveDefinition,
			Map<IRecursiveDefinitionCase, RecursiveDefinitionInfo.CaseEntry> entries, IProgressMonitor monitor)
			throws RodinDBException {
		ISCRecursiveDefinitionCase scDefCase = parent.getRecursiveDefinitionCase(origin.getElementName());
		scDefCase.create(null, monitor);
		RecursiveDefinitionInfo.CaseEntry entry = entries.get(origin);
		scDefCase.setExpression(entry.getCaseExpression(), monitor);
		scDefCase.setSCFormula(recursiveDefinition.getRecursiveCases().get(entry.getCaseExpression()), monitor);
		scDefCase.setSource(origin, monitor);
	}
}
