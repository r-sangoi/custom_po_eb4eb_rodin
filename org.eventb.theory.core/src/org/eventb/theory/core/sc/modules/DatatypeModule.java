/*******************************************************************************
 * Copyright (c) 2011, 2014 University of Southampton and others.
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

import static org.eventb.core.EventBAttributes.IDENTIFIER_ATTRIBUTE;
import static org.eventb.theory.core.sc.TheoryGraphProblem.InvalidIdentForDatatype;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.extensions.maths.AstUtilities;
import org.eventb.core.ast.extensions.maths.IDatatypeOrigin;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.SCProcessorModule;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.theory.core.IDatatypeDefinition;
import org.eventb.theory.core.ISCDatatypeDefinition;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.ISCTypeArgument;
import org.eventb.theory.core.ITheoryRoot;
import org.eventb.theory.core.ITypeArgument;
import org.eventb.theory.core.TheoryAttributes;
import org.eventb.theory.core.maths.extensions.FormulaExtensionsLoader;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.Messages;
import org.eventb.theory.core.sc.TheoryGraphProblem;
import org.eventb.theory.core.sc.states.DatatypeTable;
import org.eventb.theory.core.sc.states.TheoryAccuracyInfo;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;

/**
 * 
 * @author maamria
 * 
 */
public class DatatypeModule extends SCProcessorModule {

	private final IModuleType<DatatypeModule> MODULE_TYPE = SCCore.getModuleType(TheoryPlugin.PLUGIN_ID
			+ ".datatypeModule");

	private TheoryAccuracyInfo theoryAccuracyInfo;

	@Override
	public void process(IRodinElement element, IInternalElement target, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		IRodinFile file = (IRodinFile) element;
		ITheoryRoot root = (ITheoryRoot) file.getRoot();
		ISCTheoryRoot targetRoot = (ISCTheoryRoot) target;
		IDatatypeDefinition[] dtdef = root.getDatatypeDefinitions();
		if (dtdef.length != 0) {
			monitor.subTask(Messages.progress_TheoryDatatypes);
			monitor.worked(1);
			// set the datatype table state
			DatatypeTable datatypeTable = new DatatypeTable(repository.getFormulaFactory());
			repository.setState(datatypeTable);
			processDatatypes(dtdef, targetRoot, datatypeTable, repository, monitor);
			monitor.worked(2);
		}
	}

	@Override
	public void initModule(IRodinElement element, ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		super.initModule(element, repository, monitor);
		theoryAccuracyInfo = (TheoryAccuracyInfo) repository.getState(TheoryAccuracyInfo.STATE_TYPE);

	}

	@Override
	public void endModule(IRodinElement element, ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		theoryAccuracyInfo = null;
		super.endModule(element, repository, monitor);
	}

	@Override
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	/**
	 * Processes the datatype definitions and checks for errors regarding:
	 * <p>
	 * <li>datatype name is missing;</li>
	 * <li>name clash exists;</li>
	 * <li>issues with type arguments;</li>
	 * <p>
	 * 
	 * @param datatypeDefinitions
	 *            the datatype definitions
	 * @param targetRoot
	 *            the target SC root
	 * @param datatypeTable
	 *            the datatype table
	 * @param repository
	 *            the state repository
	 * @param monitor
	 *            the progress monitor
	 * @throws CoreException
	 */
	protected void processDatatypes(IDatatypeDefinition[] datatypeDefinitions, ISCTheoryRoot targetRoot,
			DatatypeTable datatypeTable, ISCStateRepository repository, IProgressMonitor monitor) throws CoreException {
		boolean theoryAccurate = true;
		// get the ff and type env here as they will most likely change after
		// each iteration of the following loop
		FormulaFactory factory = repository.getFormulaFactory();
		ITypeEnvironmentBuilder typeEnvironment = repository.getTypeEnvironment();
		for (IDatatypeDefinition datatypeDefinition : datatypeDefinitions) {
			if (!checkDatatypeName(datatypeDefinition, factory, typeEnvironment, datatypeTable)) {
				theoryAccurate = false;
				continue;
			}
			ISCDatatypeDefinition target = ModulesUtils.createSCIdentifierElement(ISCDatatypeDefinition.ELEMENT_TYPE,
					datatypeDefinition, targetRoot, monitor);
			target.setSource(datatypeDefinition, monitor);
			// process the type arguments
			List<String> typeArguments = new ArrayList<String>();
			boolean faithful = processTypeArguments(datatypeDefinition,
					typeArguments, target, factory, typeEnvironment, monitor);
			IDatatypeOrigin origin = FormulaExtensionsLoader.makeDatatypeOrigin(target, factory);
			try {
				datatypeTable.addDatatype(
						datatypeDefinition.getIdentifierString(),
						typeArguments, origin);
			} catch (IllegalArgumentException e) {
				createProblemMarker(datatypeDefinition, TheoryAttributes.TYPE_ATTRIBUTE, 
						TheoryGraphProblem.DatatypeError, e.getMessage());
			}
			if (!faithful) {
				datatypeTable.setErrorProne();
				theoryAccurate = false;
			}

			// Run the child modules
			{
				initProcessorModules(datatypeDefinition, repository, monitor);
				processModules(datatypeDefinition, target, repository, monitor);
				endProcessorModules(datatypeDefinition, repository, monitor);
			}
			// check other properties of datatype definitions
			if (!datatypeTable.datatypeHasBaseConstructor()) {
				createProblemMarker(datatypeDefinition, EventBAttributes.IDENTIFIER_ATTRIBUTE,
						TheoryGraphProblem.DatatypeHasNoBaseConsError, datatypeDefinition.getIdentifierString());
				datatypeTable.setErrorProne();
				theoryAccurate = false;
			}
			if (datatypeTable.isErrorProne()) {
				repository.setFormulaFactory(datatypeTable.reset());
				factory = repository.getFormulaFactory();
				typeEnvironment = AstUtilities.getTypeEnvironmentForFactory(typeEnvironment, factory);
				repository.setTypeEnvironment(typeEnvironment);
				target.setHasError(true, monitor);
				continue;
			}
			target.setHasError(false, monitor);
			// update the repository with the latest factory and the
			// corresponding type environment
			repository.setFormulaFactory(datatypeTable.augmentFormulaFactory());
			factory = repository.getFormulaFactory();
			typeEnvironment = AstUtilities.getTypeEnvironmentForFactory(typeEnvironment, factory);
			repository.setTypeEnvironment(typeEnvironment);

		}
		if (!theoryAccurate)
			theoryAccuracyInfo.setNotAccurate();
		datatypeTable.makeImmutable();
	}
	// checks the datatype name/identifier
	private boolean checkDatatypeName(IDatatypeDefinition datatypeDefinition, FormulaFactory factory,
			ITypeEnvironment typeEnvironment, DatatypeTable datatypeTable) throws CoreException {
		if (!datatypeDefinition.hasIdentifierString() || datatypeDefinition.getIdentifierString().equals("")) {
			createProblemMarker(datatypeDefinition, EventBAttributes.IDENTIFIER_ATTRIBUTE,
					TheoryGraphProblem.MissingDatatypeNameError);
			return false;
		}
		String name = datatypeDefinition.getIdentifierString();
		if (!datatypeTable.checkName(name)) {
			createProblemMarker(datatypeDefinition, IDENTIFIER_ATTRIBUTE,
					InvalidIdentForDatatype, name);
			return false;
		}
		FreeIdentifier ident = ModulesUtils.parseIdentifier(name, datatypeDefinition,
				EventBAttributes.IDENTIFIER_ATTRIBUTE, factory, this);
		if (ident != null && typeEnvironment.contains(ident.getName())) {
			createProblemMarker(datatypeDefinition, EventBAttributes.IDENTIFIER_ATTRIBUTE,
					TheoryGraphProblem.DatatypeNameAlreadyATypeParError, ident.getName());
			return false;

		}
		else if (ident == null){
			//covers the conflicting case with the imported theories
			//createProblemMarker(datatypeDefinition, EventBAttributes.IDENTIFIER_ATTRIBUTE,
					//TheoryGraphProblem.IdenIsADatatypeNameError, name);
			return false;
		}
		return true;
	}

	/**
	 * Processes the type arguments of the given datatype definition.
	 * 
	 * @param datatypeDefinition
	 *            the datatype definition
	 * @param toPopulate
	 *            the list to populate with type arguments
	 * @param target
	 *            the SC target datatype definition
	 * @param factory
	 *            the formula factory
	 * @param typeEnvironment
	 *            the type environment
	 * @param monitor
	 *            the progress monitor
	 * @return whether all type arguments have been processed faithfully
	 * @throws CoreException
	 */
	private boolean processTypeArguments(IDatatypeDefinition datatypeDefinition, List<String> toPopulate,
			ISCDatatypeDefinition target, FormulaFactory factory, ITypeEnvironment typeEnvironment,
			IProgressMonitor monitor) throws CoreException {
		ITypeArgument typeArgs[] = datatypeDefinition.getTypeArguments();
		// needed to check for redundancies
		boolean faithful = true;
		for (ITypeArgument typeArg : typeArgs) {
			if (!typeArg.hasGivenType()) {
				createProblemMarker(typeArg, TheoryAttributes.GIVEN_TYPE_ATTRIBUTE,
						TheoryGraphProblem.TypeArgMissingError, target.getElementName());
				faithful = false;
				continue;
			}
			String type = typeArg.getGivenType();
			if (!typeEnvironment.contains(type)) {
				createProblemMarker(typeArg, TheoryAttributes.GIVEN_TYPE_ATTRIBUTE,
						TheoryGraphProblem.TypeArgNotDefinedError, typeArg.getGivenType());
				faithful = false;
				continue;
			}
			if (toPopulate.contains(type)) {
				createProblemMarker(typeArg, TheoryAttributes.GIVEN_TYPE_ATTRIBUTE,
						TheoryGraphProblem.TypeArgRedundWarn, type);
				faithful = false;
				continue;
			}
			ISCTypeArgument scArg = target.getTypeArgument(type);
			scArg.create(null, monitor);
			scArg.setSource(typeArg, monitor);
			scArg.setSCGivenType(factory.makeGivenType(type), monitor);
			toPopulate.add(type);
		}
		return faithful;
	}
}
