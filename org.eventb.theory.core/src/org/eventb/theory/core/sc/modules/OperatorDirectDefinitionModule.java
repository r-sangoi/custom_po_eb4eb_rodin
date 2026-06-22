/*******************************************************************************
 * Copyright (c) 2011, 2020 University of Southampton and others.
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
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.extensions.maths.AstUtilities;
import org.eventb.core.ast.extensions.maths.DirectDefinition;
import org.eventb.core.ast.extensions.wd.WDComputer;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.SCProcessorModule;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.theory.core.IDirectOperatorDefinition;
import org.eventb.theory.core.INewOperatorDefinition;
import org.eventb.theory.core.ISCDirectOperatorDefinition;
import org.eventb.theory.core.ISCNewOperatorDefinition;
import org.eventb.theory.core.TheoryAttributes;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.TheoryGraphProblem;
import org.eventb.theory.core.sc.states.OperatorInformation;
import org.eventb.theory.core.sc.states.TheoryAccuracyInfo;
import org.eventb.theory.internal.core.util.GeneralUtilities;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * 
 * @author maamria
 * 
 */
public class OperatorDirectDefinitionModule extends SCProcessorModule {

	private static final IModuleType<OperatorDirectDefinitionModule> MODULE_TYPE = SCCore
			.getModuleType(TheoryPlugin.PLUGIN_ID + ".operatorDirectDefinitionModule");

	private TheoryAccuracyInfo theoryAccuracyInfo;
	private OperatorInformation operatorInformation;
	private FormulaFactory factory;
	private ITypeEnvironment typeEnvironment;

	@Override
	public void process(IRodinElement element, IInternalElement target, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		INewOperatorDefinition newOperatorDefinition = (INewOperatorDefinition) element;
		ISCNewOperatorDefinition scNewOperatorDefinition = (ISCNewOperatorDefinition) target;
		IDirectOperatorDefinition[] definitions = newOperatorDefinition.getDirectOperatorDefinitions();
		if (definitions.length == 1 && !operatorInformation.hasError()) {
			processDirectDefinitions(definitions, newOperatorDefinition, scNewOperatorDefinition, repository, monitor);
		}
	}

	@Override
	public void initModule(IRodinElement element, ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		super.initModule(element, repository, monitor);
		theoryAccuracyInfo = (TheoryAccuracyInfo) repository.getState(TheoryAccuracyInfo.STATE_TYPE);
		operatorInformation = (OperatorInformation) repository.getState(OperatorInformation.STATE_TYPE);
		factory = repository.getFormulaFactory();
		typeEnvironment = repository.getTypeEnvironment();
	}

	@Override
	public void endModule(IRodinElement element, ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		theoryAccuracyInfo = null;
		operatorInformation = null;
		factory = null;
		typeEnvironment = null;
		super.endModule(element, repository, monitor);
	}

	@Override
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	private void processDirectDefinitions(IDirectOperatorDefinition[] definitions,
			INewOperatorDefinition newOperatorDefinition, ISCNewOperatorDefinition scNewOperatorDefinition,
			ISCStateRepository repository, IProgressMonitor monitor) throws CoreException, RodinDBException {
		IDirectOperatorDefinition definition = definitions[0];
		Formula<?> defFormula = processDirectDefinition(definition, repository);
		String label = newOperatorDefinition.getLabel();
		if (defFormula != null) {
			if (AstUtilities.isExpressionOperator(operatorInformation.getFormulaType())) {
				if (defFormula instanceof Expression) {
					commitDirectDefinition(defFormula, definition, scNewOperatorDefinition, repository, monitor);
				} else {
					setError();
					createProblemMarker(definition, TheoryAttributes.FORMULA_ATTRIBUTE,
							TheoryGraphProblem.OperatorDefNotExpError, label);
					theoryAccuracyInfo.setNotAccurate();
				}
			} else {
				if (defFormula instanceof Predicate) {
					commitDirectDefinition(defFormula, definition, scNewOperatorDefinition, repository, monitor);

				} else {
					setError();
					createProblemMarker(definition, TheoryAttributes.FORMULA_ATTRIBUTE,
							TheoryGraphProblem.OperatorDefNotPredError, label);
					theoryAccuracyInfo.setNotAccurate();
				}
			}
		} else {
			setError();
			theoryAccuracyInfo.setNotAccurate();
		}
	}

	private void commitDirectDefinition(Formula<?> defFormula, IDirectOperatorDefinition definition,
			ISCNewOperatorDefinition scNewOperatorDefinition, ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		createSCDirectDefinition(defFormula, scNewOperatorDefinition, definition, repository, monitor);
		if (defFormula instanceof Expression)
			operatorInformation.setResultantType(((Expression) defFormula).getType());
		operatorInformation.setDefinition(new DirectDefinition(defFormula));
		if (operatorInformation.getWdCondition() == null) {
			Predicate wdPredicate = defFormula.getWDPredicate();
			scNewOperatorDefinition.setPredicate(wdPredicate, monitor);
			operatorInformation.addWDCondition(wdPredicate);
		}
		Predicate dwdPredicate = getDWDPredicate(defFormula);
		operatorInformation.setD_WDCondition(dwdPredicate);
		scNewOperatorDefinition.setWDCondition(dwdPredicate, monitor);
	}

	private void createSCDirectDefinition(Formula<?> definitionFormula, ISCNewOperatorDefinition scOperatorDefinition,
			IDirectOperatorDefinition definition, ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		ISCDirectOperatorDefinition scDef = scOperatorDefinition.getDirectOperatorDefinition(definition
				.getElementName());
		scDef.create(null, monitor);
		scDef.setSCFormula(definitionFormula, monitor);
		scDef.setSource(definition, monitor);
	}

	private Formula<?> processDirectDefinition(IDirectOperatorDefinition definition, ISCStateRepository repository)
			throws CoreException {
		if(!definition.hasFormula() || "".equals(definition.getFormula())){
			createProblemMarker(definition, TheoryAttributes.FORMULA_ATTRIBUTE, TheoryGraphProblem.MissingFormulaError);
		}
		if (definition.hasFormula()) {
			Formula<?> formula = ModulesUtils.parseFormula(definition, factory, this);
			if (formula != null) {
				formula = ModulesUtils.checkFormula(definition, formula, typeEnvironment, this);
				if (formula != null) {
					FreeIdentifier[] idents = formula.getFreeIdentifiers();
					List<String> notAllowed = new ArrayList<String>();
					for (FreeIdentifier ident : idents) {
						if (!operatorInformation.isAllowedIdentifier(ident)) {
							notAllowed.add(ident.getName());
						}
					}
					if (notAllowed.size() != 0) {
						createProblemMarker(definition, TheoryAttributes.FORMULA_ATTRIBUTE,
								TheoryGraphProblem.OpCannotReferToTheseIdents, GeneralUtilities.toString(notAllowed));
						return null;
					}
					return formula;
				}
			}
		} 
		return null;
	}

	private void setError() throws CoreException {
		operatorInformation.setHasError();
	}

	private Predicate getDWDPredicate(Formula<?> formula) {
		return WDComputer.getYLemma(formula);
	}
}
