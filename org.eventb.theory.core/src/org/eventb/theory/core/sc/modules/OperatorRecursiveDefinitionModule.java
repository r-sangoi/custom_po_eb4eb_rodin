/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.sc.modules;

import static org.eventb.core.ast.extensions.maths.AstUtilities.makeBTRUE;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.extensions.maths.AstUtilities;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.SCProcessorModule;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.theory.core.INewOperatorDefinition;
import org.eventb.theory.core.IRecursiveOperatorDefinition;
import org.eventb.theory.core.ISCNewOperatorDefinition;
import org.eventb.theory.core.ISCRecursiveOperatorDefinition;
import org.eventb.theory.core.TheoryAttributes;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.TheoryGraphProblem;
import org.eventb.theory.core.sc.states.OperatorInformation;
import org.eventb.theory.core.sc.states.RecursiveDefinitionInfo;
import org.eventb.theory.core.sc.states.TheoryAccuracyInfo;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;

/**
 * 
 * @author maamria
 * 
 */
public class OperatorRecursiveDefinitionModule extends SCProcessorModule {

	private static final IModuleType<OperatorRecursiveDefinitionModule> MODULE_TYPE = SCCore
			.getModuleType(TheoryPlugin.PLUGIN_ID + ".operatorRecursiveDefinitionModule");

	private TheoryAccuracyInfo theoryAccuracyInfo;
	private OperatorInformation operatorInformation;
	private ITypeEnvironment typeEnvironment;
	private FormulaFactory factory;

	@Override
	public void process(IRodinElement element, IInternalElement target, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		INewOperatorDefinition newOperatorDefinition = (INewOperatorDefinition) element;
		ISCNewOperatorDefinition scNewOperatorDefinition = (ISCNewOperatorDefinition) target;
		IRecursiveOperatorDefinition[] definitions = newOperatorDefinition.getRecursiveOperatorDefinitions();
		if (definitions.length == 1 && !operatorInformation.hasError()) {
			RecursiveDefinitionInfo recursiveDefinitionInfo = new RecursiveDefinitionInfo();
			repository.setState(recursiveDefinitionInfo);
			IRecursiveOperatorDefinition recursiveOperatorDefinition = definitions[0];
			process(recursiveOperatorDefinition, newOperatorDefinition, scNewOperatorDefinition,
					recursiveDefinitionInfo, repository, monitor);
			repository.removeState(RecursiveDefinitionInfo.STATE_TYPE);
		}
	}

	private void process(IRecursiveOperatorDefinition recursiveOperatorDefinition, INewOperatorDefinition parent,
			ISCNewOperatorDefinition target, RecursiveDefinitionInfo recursiveDefinitionInfo,
			ISCStateRepository repository, IProgressMonitor monitor) throws CoreException {
		boolean error = false;

		if (!recursiveOperatorDefinition.hasInductiveArgument() ||
				recursiveOperatorDefinition.getInductiveArgument().equals("")) {
			createProblemMarker(recursiveOperatorDefinition, TheoryAttributes.INDUCTIVE_ARGUMENT_ATTRIBUTE,
					TheoryGraphProblem.InductiveArgMissing);
			error = true;
			operatorInformation.setHasError();
			theoryAccuracyInfo.setNotAccurate();
		} else {
			String inductiveArgument = recursiveOperatorDefinition.getInductiveArgument();
			if (!typeEnvironment.contains(inductiveArgument)
					|| !AstUtilities.isDatatypeType(typeEnvironment.getType(inductiveArgument))) {
				createProblemMarker(recursiveOperatorDefinition, TheoryAttributes.INDUCTIVE_ARGUMENT_ATTRIBUTE,
						TheoryGraphProblem.ArgumentNotExistOrNotParametric, inductiveArgument);
				error = true;
				operatorInformation.setHasError();
				theoryAccuracyInfo.setNotAccurate();
			}
			else {
				FreeIdentifier ident = factory.makeFreeIdentifier(inductiveArgument, null,
						typeEnvironment.getType(inductiveArgument));
				recursiveDefinitionInfo.setInductiveArgument(ident, factory);
			}
		}
		if (!error) {
			ISCRecursiveOperatorDefinition scDefinition = createSCDefinition(recursiveOperatorDefinition, target,
					recursiveDefinitionInfo, monitor);
			// processor modules
			{
				initProcessorModules(recursiveOperatorDefinition, repository, monitor);
				processModules(recursiveOperatorDefinition, scDefinition, repository, monitor);
				endProcessorModules(recursiveOperatorDefinition, repository, monitor);
			}
			if (!recursiveDefinitionInfo.isAccurate()) {
				operatorInformation.setHasError();
			} else {
				target.setWDCondition(makeBTRUE(factory), monitor);
				operatorInformation.setD_WDCondition(makeBTRUE(factory));
			}
		}
	}

	private ISCRecursiveOperatorDefinition createSCDefinition(IRecursiveOperatorDefinition source,
			ISCNewOperatorDefinition target, RecursiveDefinitionInfo info, IProgressMonitor monitor)
			throws CoreException {
		ISCRecursiveOperatorDefinition scDefinition = target.getRecursiveOperatorDefinition(source.getElementName());
		scDefinition.create(null, monitor);
		scDefinition.setSource(source, monitor);
		scDefinition.setInductiveArgument(info.getInductiveArgument().getName(), monitor);
		return scDefinition;
	}

	@Override
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	@Override
	public void initModule(IRodinElement element, ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		super.initModule(element, repository, monitor);
		theoryAccuracyInfo = (TheoryAccuracyInfo) repository.getState(TheoryAccuracyInfo.STATE_TYPE);
		operatorInformation = (OperatorInformation) repository.getState(OperatorInformation.STATE_TYPE);
		typeEnvironment = repository.getTypeEnvironment();
		factory = repository.getFormulaFactory();
	}

	@Override
	public void endModule(IRodinElement element, ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		theoryAccuracyInfo = null;
		operatorInformation = null;
		typeEnvironment = null;
		factory = null;
		super.endModule(element, repository, monitor);
	}
}
