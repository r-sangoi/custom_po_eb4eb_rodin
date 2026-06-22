/*******************************************************************************
 * Copyright (c) 2013, 2020 University of Southampton and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.sc.modules;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.IIdentifierElement;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.PowerSetType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.IOperatorProperties.FormulaType;
import org.eventb.core.ast.extension.IOperatorProperties.Notation;
import org.eventb.core.sc.GraphProblem;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.state.IIdentifierSymbolInfo;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.internal.core.sc.modules.IdentifierModule;
import org.eventb.theory.core.IAxiomaticOperatorDefinition;
import org.eventb.theory.core.IOperatorArgument;
import org.eventb.theory.core.ISCAxiomaticOperatorDefinition;
import org.eventb.theory.core.ISCOperatorArgument;
import org.eventb.theory.core.ITheoryRoot;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.TheoryGraphProblem;
import org.eventb.theory.core.sc.states.OperatorInformation;
import org.eventb.theory.core.sc.states.TheoryAccuracyInfo;
import org.eventb.theory.core.sc.states.TheorySymbolFactory;
import org.eventb.theory.core.util.CoreUtilities;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;

@SuppressWarnings("restriction")
public class AxiomaticOperatorArgumentModule extends IdentifierModule{
	
	private final IModuleType<OperatorArgumentModule> MODULE_TYPE = SCCore.getModuleType(TheoryPlugin.PLUGIN_ID
			+ ".axiomaticOperatorArgumentModule");

	private TheoryAccuracyInfo theoryAccuracyInfo;
	private OperatorInformation operatorInformation;

	/**
	 * TODO this should not be needed. Need to request change to implementation
	 * of symbol tables.
	 */
	private List<IIdentifierSymbolInfo> insertionOrderedSymbols;

	@Override
	public void process(IRodinElement element, IInternalElement target, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		IAxiomaticOperatorDefinition operatorDefinition = (IAxiomaticOperatorDefinition) element;
		ISCAxiomaticOperatorDefinition scOperatorDefinition = (ISCAxiomaticOperatorDefinition) target;
		IOperatorArgument[] arguments = operatorDefinition.getOperatorArguments();
		Notation notation = operatorInformation.getNotation();
		int arity = arguments.length;
		FormulaType formulaType = operatorInformation.getFormulaType();
		// check syntactic properties regarding arity
		// Infix needs at least two arguments
		if (notation.equals(Notation.INFIX)) {
			if (arity < 2) {
				createProblemMarker(operatorDefinition, EventBAttributes.LABEL_ATTRIBUTE,
						TheoryGraphProblem.OperatorExpInfixNeedsAtLeastTwoArgs);
				operatorInformation.setHasError();
				theoryAccuracyInfo.setNotAccurate();
			}
		}
		// Check formula type
		// Predicate operators need at least one argument
		if (formulaType.equals(FormulaType.PREDICATE) && arity < 1) {
			createProblemMarker(operatorDefinition, EventBAttributes.LABEL_ATTRIBUTE,
					TheoryGraphProblem.OperatorPredNeedOneOrMoreArgs);
			operatorInformation.setHasError();
			theoryAccuracyInfo.setNotAccurate();
		}
		if (!operatorInformation.hasError()) {
			insertionOrderedSymbols = new ArrayList<IIdentifierSymbolInfo>();
			fetchSymbols(arguments, target, repository, monitor);
			for (IIdentifierSymbolInfo symbolInfo : insertionOrderedSymbols) {
				if (symbolInfo == null || symbolInfo.hasError()) {
					operatorInformation.setHasError();
				} else if (symbolInfo.getSymbolType() == ISCOperatorArgument.ELEMENT_TYPE && symbolInfo.isPersistent()) {
					Type type = symbolInfo.getType();
					if (type == null) { // identifier could not be typed
						symbolInfo.createUntypedErrorMarker(this);
						symbolInfo.setError();
						operatorInformation.setHasError();
					}
					if (!symbolInfo.hasError()) {
						if (scOperatorDefinition != null) {
							operatorInformation.addOperatorArgument(symbolInfo.getSymbol(), type);
							symbolInfo.createSCElement(scOperatorDefinition, null);
						} else {
							operatorInformation.setHasError();
						}
					} else {
						operatorInformation.setHasError();
					}
					symbolInfo.makeImmutable();
				}
			}
		}
	}

	@Override
	public void initModule(IRodinElement element, ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		super.initModule(element, repository, monitor);
		theoryAccuracyInfo = (TheoryAccuracyInfo) repository.getState(TheoryAccuracyInfo.STATE_TYPE);
		operatorInformation = (OperatorInformation) repository.getState(OperatorInformation.STATE_TYPE);
	}

	@Override
	public void endModule(IRodinElement element, ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		theoryAccuracyInfo = null;
		operatorInformation = null;
		super.endModule(element, repository, monitor);
	}

	@Override
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	@Override
	protected IIdentifierSymbolInfo createIdentifierSymbolInfo(String name, IIdentifierElement element) {
		IAxiomaticOperatorDefinition opDef = (IAxiomaticOperatorDefinition) element.getParent();
		return TheorySymbolFactory.getInstance().makeLocalOperatorArgument(name, true, element,
				opDef.getAncestor(ITheoryRoot.ELEMENT_TYPE).getComponentName());
	}

	// needed order of insertion because it is lost in the given table
	protected void fetchSymbols(IIdentifierElement[] elements, IInternalElement target, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		for (IIdentifierElement element : elements) {
			FreeIdentifier identifier = parseIdentifier(element, monitor);

			if (identifier == null) {
				operatorInformation.setHasError();
				continue;
			}
			String name = identifier.getName();

			IIdentifierSymbolInfo newSymbolInfo = createIdentifierSymbolInfo(name, element);
			newSymbolInfo.setAttributeValue(EventBAttributes.SOURCE_ATTRIBUTE, element);

			boolean ok = insertIdentifierSymbol(element, newSymbolInfo);
			if (!ok || !checkAndType((IOperatorArgument) element, newSymbolInfo)) {
				operatorInformation.setHasError();
				theoryAccuracyInfo.setNotAccurate();
				continue;
			}
			typeIdentifierSymbol(newSymbolInfo, typeEnvironment);
			insertionOrderedSymbols.add(newSymbolInfo);
			monitor.worked(1);
		}
	}

	protected void typeIdentifierSymbol(IIdentifierSymbolInfo newSymbolInfo, ITypeEnvironmentBuilder environment)
			throws CoreException {
		environment.addName(newSymbolInfo.getSymbol(), newSymbolInfo.getType());
	}

	/**
	 * Checks and types the identifier present in the given operator argument.
	 * 
	 * <p>
	 * As a side effect, this method sets the type of the given symbol.
	 * 
	 * @param operatorArgument
	 *            the operator argument
	 * @param identifierSymbolInfo
	 *            the symbol info
	 * @return whether the argument has successfully been typed
	 * @throws CoreException
	 */
	private boolean checkAndType(IOperatorArgument operatorArgument, IIdentifierSymbolInfo identifierSymbolInfo)
			throws CoreException {
		if (!operatorArgument.hasExpressionString() || operatorArgument.getExpressionString().equals("")) {
			createProblemMarker(operatorArgument, EventBAttributes.EXPRESSION_ATTRIBUTE,
					GraphProblem.ExpressionUndefError);
			return false;
		}
		Expression exp = CoreUtilities.parseAndCheckExpression(operatorArgument, factory, typeEnvironment, this);
		if (exp == null) {
			return false;
		}
		Type type = exp.getType();
		if (!(type instanceof PowerSetType)) {
			createProblemMarker(operatorArgument, EventBAttributes.EXPRESSION_ATTRIBUTE,
					TheoryGraphProblem.OpArgExprNotSet, exp.toString());
			return false;
		}
		identifierSymbolInfo.setType(type.getBaseType());
		if (!exp.isATypeExpression()) {
			FreeIdentifier identifier = factory.makeFreeIdentifier(identifierSymbolInfo.getSymbol(), null, type);
			Predicate wdCondition = factory.makeRelationalPredicate(Formula.IN, identifier, exp, null);
			operatorInformation.addWDCondition(wdCondition);
		}
		return true;
	}
}
