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

import static org.eventb.core.EventBAttributes.IDENTIFIER_ATTRIBUTE;
import static org.eventb.theory.core.sc.TheoryGraphProblem.InvalidIdentForConstructor;
import static org.eventb.theory.core.sc.TheoryGraphProblem.InvalidIdentForDestructor;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Type;
import org.eventb.core.sc.GraphProblem;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.SCProcessorModule;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.theory.core.IConstructorArgument;
import org.eventb.theory.core.IDatatypeConstructor;
import org.eventb.theory.core.IDatatypeDefinition;
import org.eventb.theory.core.ISCConstructorArgument;
import org.eventb.theory.core.ISCDatatypeConstructor;
import org.eventb.theory.core.ISCDatatypeDefinition;
import org.eventb.theory.core.TheoryAttributes;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.TheoryGraphProblem;
import org.eventb.theory.core.sc.states.DatatypeTable;
import org.eventb.theory.core.sc.states.TheoryAccuracyInfo;
import org.eventb.theory.core.util.CoreUtilities;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;

/**
 * 
 * @author maamria
 * 
 */
public class DatatypeConstructorModule extends SCProcessorModule {

	private final IModuleType<DatatypeConstructorModule> MODULE_TYPE = SCCore.getModuleType(TheoryPlugin.PLUGIN_ID
			+ ".datatypeConstructorModule");
	
	private TheoryAccuracyInfo theoryAccuracyInfo;

	private DatatypeTable datatypeTable;

	@Override
	public void initModule(IRodinElement element, ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		super.initModule(element, repository, monitor);
		datatypeTable = (DatatypeTable) repository.getState(DatatypeTable.STATE_TYPE);
		theoryAccuracyInfo = (TheoryAccuracyInfo) repository.getState(TheoryAccuracyInfo.STATE_TYPE);
	}

	@Override
	public void process(IRodinElement element, IInternalElement target, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		IDatatypeDefinition datatypeDefinition = (IDatatypeDefinition) element;
		ISCDatatypeDefinition scDefinition = (ISCDatatypeDefinition) target;
		IDatatypeConstructor[] constructors = datatypeDefinition.getDatatypeConstructors();
		if (constructors.length < 1) {
			createProblemMarker(datatypeDefinition, EventBAttributes.IDENTIFIER_ATTRIBUTE,
					TheoryGraphProblem.DatatypeHasNoConsError, datatypeDefinition.getIdentifierString());
			datatypeTable.setErrorProne();
			theoryAccuracyInfo.setNotAccurate();
			return;
		}
		processConstructors(constructors, scDefinition, repository, monitor);
		monitor.worked(1);

	}

	@Override
	public void endModule(IRodinElement element, ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		datatypeTable = null;
		theoryAccuracyInfo = null;
		super.endModule(element, repository, monitor);
	}

	@Override
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	/**
	 * Processes the supplied constructors and creates their statically checked
	 * counterparts where appropriate.
	 * <p>
	 * Potential issues that might arise include:
	 * <li>Constructor name is missing;</li>
	 * <li>Name clash exists.</li>
	 * <p>
	 * 
	 * @param constructors
	 *            the array of datatype constructors
	 * @param scDefinition
	 *            the target SC datatype definition
	 * @param repository
	 *            the state repository
	 * @param monitor
	 *            the progress monitor
	 * @throws CoreException
	 */
	protected void processConstructors(IDatatypeConstructor[] constructors, ISCDatatypeDefinition scDefinition, ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		FormulaFactory factory = repository.getFormulaFactory();
		ITypeEnvironment typeEnvironment = repository.getTypeEnvironment();
		for (IDatatypeConstructor cons : constructors) {
			if (!checkConstructorName(cons, factory, typeEnvironment, datatypeTable)) {
				datatypeTable.setErrorProne();
				theoryAccuracyInfo.setNotAccurate();
				continue;
			}
			ISCDatatypeConstructor scCons = ModulesUtils.createSCIdentifierElement(ISCDatatypeConstructor.ELEMENT_TYPE,
					cons, scDefinition, monitor);
			scCons.setSource(cons, monitor);
			
			try {
				datatypeTable.addConstructor(cons.getIdentifierString());
			} catch(IllegalArgumentException e) {
				createProblemMarker(cons, TheoryGraphProblem.DatatypeError,
						e.getMessage());
			}
			processDestructors(cons, scCons, repository, monitor);
		}

	}
	
	
	private void processDestructors(IDatatypeConstructor constructor,
			ISCDatatypeConstructor scConstructor,
			ISCStateRepository repository, IProgressMonitor monitor) throws CoreException {
		FormulaFactory factory = repository.getFormulaFactory();
		ITypeEnvironment typeEnvironment = repository.getTypeEnvironment();
		for (IConstructorArgument consArg : constructor.getConstructorArguments()) {
			if (!checkDestructorName(consArg, factory, typeEnvironment, datatypeTable)) {
				datatypeTable.setErrorProne();
				theoryAccuracyInfo.setNotAccurate();
				continue;
			}
			final IParseResult parseResult = datatypeTable.parseType(consArg.getType());
			if (CoreUtilities.issueASTProblemMarkers(consArg,
					TheoryAttributes.TYPE_ATTRIBUTE, parseResult, this)) {
				datatypeTable.setErrorProne();
				theoryAccuracyInfo.setNotAccurate();
				continue;
			}
			// Note: the parsed type, which is serialized hereafter, contains 
			// a given type for the datatype type, instead of a parametric type.
			// This is convenient for IDatatypeBuilder. 
			final Type type = parseResult.getParsedType();

			if (type != null && checkTypeParameters(type, consArg, typeEnvironment, datatypeTable)) {
				// check first before we commit the SC destructor
				try {
					datatypeTable.addDestructor(consArg.getIdentifierString(), type);
				} catch(IllegalArgumentException e) {
					// destructor is not admissible
					createProblemMarker(consArg, TheoryAttributes.TYPE_ATTRIBUTE, 
							TheoryGraphProblem.DatatypeError, e.getMessage());
					datatypeTable.setErrorProne();
					theoryAccuracyInfo.setNotAccurate();
					continue;
				}
				ISCConstructorArgument scConsArg = ModulesUtils.createSCIdentifierElement(
						ISCConstructorArgument.ELEMENT_TYPE, consArg, scConstructor, monitor);
				scConsArg.setSource(consArg, monitor);
				scConsArg.setType(type, monitor);
			}
			else {
				datatypeTable.setErrorProne();
				theoryAccuracyInfo.setNotAccurate();
			}
		}
	}

	// TODO use symbol table for that
	// check constructor name/identifier
	private boolean checkConstructorName(IDatatypeConstructor constructor, FormulaFactory factory,
			ITypeEnvironment typeEnvironment, DatatypeTable datatypeTable) throws CoreException {
		if (!constructor.hasIdentifierString() || constructor.getIdentifierString().equals("")) {
			createProblemMarker(constructor, EventBAttributes.IDENTIFIER_ATTRIBUTE,
					TheoryGraphProblem.MissingDatatypeNameError);
			return false;
		}
		String name = constructor.getIdentifierString();
		if (!datatypeTable.checkName(name)) {
			createProblemMarker(constructor, IDENTIFIER_ATTRIBUTE,
					InvalidIdentForConstructor, name);
			return false;
		}
		FreeIdentifier ident = ModulesUtils.parseIdentifier(name, constructor, EventBAttributes.IDENTIFIER_ATTRIBUTE,
				factory, this);
		if (ident != null && typeEnvironment.contains(ident.getName())) {
			createProblemMarker(constructor, EventBAttributes.IDENTIFIER_ATTRIBUTE,
					TheoryGraphProblem.ConstructorNameAlreadyATypeParError, ident.getName());
			return false;
		}
		else if (ident == null){
			//covers the conflicting case with the imported theories
			//createProblemMarker(constructor, EventBAttributes.IDENTIFIER_ATTRIBUTE,
					//TheoryGraphProblem.IdenIsAConsNameError, name);
			return false;
		}
		return true;
	}
	
	/**
	 * Checks that the type of the given constructor argument refers only to
	 * referenced type of the datatype.
	 * 
	 * @param type
	 *            the type of the destructor
	 * @param consArg
	 *            the constructor argument
	 * @param typeEnvironment
	 *            the type environment
	 * @param datatypeTable
	 *            the datatype table
	 * @return whether the type of the destructor is acceptable
	 * @throws CoreException
	 */
	private boolean checkTypeParameters(Type type, IConstructorArgument consArg, ITypeEnvironment typeEnvironment,
			DatatypeTable datatypeTable) throws CoreException {
		boolean result = true;
		for (GivenType givenType : type.getGivenTypes()) {
			if (!datatypeTable.isAllowedGivenType(givenType)) {
				if (!typeEnvironment.contains(givenType.getName())) {
					createProblemMarker(consArg, TheoryAttributes.TYPE_ATTRIBUTE,
							GraphProblem.UndeclaredFreeIdentifierError, givenType.getName());
					result = false;
				} else {
					createProblemMarker(consArg,

					TheoryAttributes.TYPE_ATTRIBUTE, TheoryGraphProblem.TypeIsNotRefTypeError, givenType.getName());
					result = false;
				}
			}
		}
		return result;

	}

	// TODO use symbol table for that
	// check constructor name/identifier
	private boolean checkDestructorName(IConstructorArgument destructor, FormulaFactory factory,
			ITypeEnvironment typeEnvironment, DatatypeTable datatypeTable) throws CoreException {
		if (!destructor.hasIdentifierString() || destructor.getIdentifierString().equals("")) {
			createProblemMarker(destructor, EventBAttributes.IDENTIFIER_ATTRIBUTE,
					TheoryGraphProblem.MissingDatatypeNameError);
			return false;
		}
		String name = destructor.getIdentifierString();
		if (!datatypeTable.checkName(name)) {
			createProblemMarker(destructor, IDENTIFIER_ATTRIBUTE,
					InvalidIdentForDestructor, name);
			return false;
		}
		FreeIdentifier ident = ModulesUtils.parseIdentifier(name, destructor, EventBAttributes.IDENTIFIER_ATTRIBUTE,
				factory, this);
		if (ident != null && typeEnvironment.contains(ident.getName())) {
			createProblemMarker(destructor, EventBAttributes.IDENTIFIER_ATTRIBUTE,
					TheoryGraphProblem.DestructorNameAlreadyATypeParError, ident.getName());
			return false;
		}

		if (ident != null && typeEnvironment.contains(name)) {
			createProblemMarker(destructor, EventBAttributes.IDENTIFIER_ATTRIBUTE,
					TheoryGraphProblem.DestructorNameAlreadyATypeParError, name);
			return false;
		}
		else if (ident == null){
			//covers the conflicting case with the imported theories
			createProblemMarker(destructor, EventBAttributes.IDENTIFIER_ATTRIBUTE,
					TheoryGraphProblem.IdenIsExistingNameError, name);
			return false;
		}
		if (!destructor.hasType() || destructor.getType().equals("")) {
			createProblemMarker(destructor, TheoryAttributes.TYPE_ATTRIBUTE,
					TheoryGraphProblem.MissingDestructorTypeError, name);
			return false;
		}
		return true;
	}

}
