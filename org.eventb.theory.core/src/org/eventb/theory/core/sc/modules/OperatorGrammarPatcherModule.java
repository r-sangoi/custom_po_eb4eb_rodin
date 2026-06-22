/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.sc.modules;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.ast.extensions.maths.AstUtilities;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.SCProcessorModule;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.theory.core.INewOperatorDefinition;
import org.eventb.theory.core.ISCNewOperatorDefinition;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.TheoryGraphProblem;
import org.eventb.theory.core.sc.states.OperatorInformation;
import org.eventb.theory.internal.core.util.GeneralUtilities;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;

/**
 * @author maamria
 * 
 */
public class OperatorGrammarPatcherModule extends SCProcessorModule {

	IModuleType<OperatorGrammarPatcherModule> MODULE_TYPE = SCCore.getModuleType(TheoryPlugin.PLUGIN_ID
			+ ".operatorGrammarPatcherModule");

	private FormulaFactory factory;
	private OperatorInformation operatorInformation;

	@Override
	public void process(IRodinElement element, IInternalElement target, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		INewOperatorDefinition newOperatorDefinition = (INewOperatorDefinition) element;
		ISCNewOperatorDefinition scNewOperatorDefinition = (ISCNewOperatorDefinition) target;
		ISCTheoryRoot theoryRoot = scNewOperatorDefinition.getAncestor(ISCTheoryRoot.ELEMENT_TYPE);
		
		if (!operatorInformation.hasError()) {
			String syntax = operatorInformation.getSyntax();
//removed if do not need to check the uniqueness of the operators 
			if (AstUtilities.checkOperatorSyntaxSymbol(syntax, factory)) {
				operatorInformation.makeImmutable();
				IFormulaExtension formulaExtension = operatorInformation.getExtension(scNewOperatorDefinition);
				FormulaFactory newFactory = factory.withExtensions(GeneralUtilities.singletonSet(formulaExtension));
				repository.setFormulaFactory(newFactory);
				repository.setTypeEnvironment(AstUtilities.getTypeEnvironmentForFactory(
						repository.getTypeEnvironment(), newFactory));
				factory = repository.getFormulaFactory();
				scNewOperatorDefinition.setHasError(false, monitor);
				scNewOperatorDefinition.setOperatorGroup(formulaExtension.getGroupId(), monitor);
				if (operatorInformation.isExpressionOperator()) {
					scNewOperatorDefinition.setType(operatorInformation.getResultantType(), monitor);
				}
				operatorInformation.generateDefinitionalRule(newOperatorDefinition, theoryRoot, factory);
			} else {
				createProblemMarker((INewOperatorDefinition) element, EventBAttributes.LABEL_ATTRIBUTE,
						TheoryGraphProblem.OperatorWithSameSynJustBeenAddedError, syntax);
				operatorInformation.setHasError();
				operatorInformation.makeImmutable();
				// FIXME set accuracy to false
			}
		}
	}

	@Override
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	@Override
	public void initModule(IRodinElement element, ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		super.initModule(element, repository, monitor);
		factory = repository.getFormulaFactory();
		operatorInformation = (OperatorInformation) repository.getState(OperatorInformation.STATE_TYPE);
	}

	@Override
	public void endModule(IRodinElement element, ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		factory = null;
		operatorInformation = null;
		super.endModule(element, repository, monitor);
	}
}
