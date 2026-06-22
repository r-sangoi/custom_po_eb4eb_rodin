/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.sc.modules;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.ILabeledElement;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.extensions.maths.AstUtilities;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.state.ILabelSymbolInfo;
import org.eventb.core.sc.state.ILabelSymbolTable;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.internal.core.sc.modules.LabeledElementModule;
import org.eventb.internal.core.sc.symbolTable.IdentifierSymbolTable;
import org.eventb.internal.core.sc.symbolTable.StackedIdentifierSymbolTable;
import org.eventb.theory.core.IDirectOperatorDefinition;
import org.eventb.theory.core.INewOperatorDefinition;
import org.eventb.theory.core.IRecursiveOperatorDefinition;
import org.eventb.theory.core.ISCNewOperatorDefinition;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.ITheoryRoot;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.Messages;
import org.eventb.theory.core.sc.TheoryGraphProblem;
import org.eventb.theory.core.sc.states.OperatorInformation;
import org.eventb.theory.core.sc.states.OperatorsLabelSymbolTable;
import org.eventb.theory.core.sc.states.TheoryAccuracyInfo;
import org.eventb.theory.core.sc.states.TheorySymbolFactory;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;

/**
 * 
 * @author maamria
 *
 */
@SuppressWarnings("restriction")
public class OperatorModule extends LabeledElementModule{

	private final IModuleType<OperatorModule> MODULE_TYPE = 
		SCCore.getModuleType(TheoryPlugin.PLUGIN_ID + ".operatorModule");
	
	private TheoryAccuracyInfo theoryAccuracyInfo;
	private ITypeEnvironmentBuilder globalTypeEnvironment;
	private FormulaFactory factory;
	private IdentifierSymbolTable identifierSymbolTable;
	
	@Override
	public void process(IRodinElement element, IInternalElement target,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		IRodinFile file = (IRodinFile) element;
		ITheoryRoot root = (ITheoryRoot) file.getRoot();
		ISCTheoryRoot targetRoot = (ISCTheoryRoot) target;
		monitor.subTask(Messages.progress_TheoryOperators);
		monitor.worked(1);
		INewOperatorDefinition[] newOpDefs = root.getNewOperatorDefinitions();
		ILabelSymbolInfo[] operators = fetchOperators(newOpDefs, root.getComponentName(), repository, monitor);
		ISCNewOperatorDefinition scNewOpDefs[] = new ISCNewOperatorDefinition[newOpDefs.length];
		commitOperators(newOpDefs, targetRoot, scNewOpDefs,operators, monitor);
		processOperators(newOpDefs, root.getComponentName(), scNewOpDefs, repository, operators, monitor);
		monitor.worked(2);
		
	}

	@Override
	public void initModule(IRodinElement element,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		super.initModule(element, repository, monitor);
		theoryAccuracyInfo = (TheoryAccuracyInfo) repository.getState(TheoryAccuracyInfo.STATE_TYPE);
		factory = repository.getFormulaFactory();
		globalTypeEnvironment = repository.getTypeEnvironment();
		identifierSymbolTable = (IdentifierSymbolTable) repository.getState(IdentifierSymbolTable.STATE_TYPE);
		
	}

	@Override
	public void endModule(IRodinElement element, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		repository.setTypeEnvironment(globalTypeEnvironment);
		theoryAccuracyInfo = null;
		identifierSymbolTable = null;
		factory = null;
		globalTypeEnvironment = null;
		super.endModule(element, repository, monitor);
	}
	
	@Override
	public IModuleType<?> getModuleType() {
		// TODO Auto-generated method stub
		return MODULE_TYPE;
	}

	@Override
	protected ILabelSymbolTable getLabelSymbolTableFromRepository(
			ISCStateRepository repository) throws CoreException {
		return (OperatorsLabelSymbolTable) repository.getState(OperatorsLabelSymbolTable.STATE_TYPE);
	}

	@Override
	protected ILabelSymbolInfo createLabelSymbolInfo(String symbol,
			ILabeledElement element, String component) throws CoreException {
		return TheorySymbolFactory.getInstance().makeLocalOperator(symbol, true, element,
				component, false);
	}
	
	private ILabelSymbolInfo[] fetchOperators(INewOperatorDefinition[] newOpDefs, String theoryName,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		boolean accurate = true;
		initFilterModules(repository, monitor);
		ILabelSymbolInfo[] labelSymbolInfos = new ILabelSymbolInfo[newOpDefs.length];
		for(int i = 0 ; i < newOpDefs.length; i++){
			INewOperatorDefinition opDef = newOpDefs[i];
			labelSymbolInfos[i] = fetchLabel(opDef, theoryName, monitor);
			if(labelSymbolInfos[i] == null){
				accurate = false;
				continue;
			}
			if (!filterModules(opDef, repository, null)) {
				labelSymbolInfos[i].setError();
				accurate = false;
			}
		}
		endFilterModules(repository, null);
		if (!accurate){
			theoryAccuracyInfo.setNotAccurate();
		}
		return labelSymbolInfos;
	}
	
	private void commitOperators(INewOperatorDefinition[] newOpDefs, ISCTheoryRoot targetRoot,
			ISCNewOperatorDefinition[] scNewOpDefs,
			ILabelSymbolInfo[] labelSymbolInfos, IProgressMonitor monitor) 
	throws CoreException{
		for (int i = 0; i < newOpDefs.length; i++) {
			if (labelSymbolInfos[i] != null && !labelSymbolInfos[i].hasError()) {
				scNewOpDefs[i] = createSCNewOperatorDefinition(targetRoot, labelSymbolInfos[i],
						newOpDefs[i], monitor);
			}
			else {
				theoryAccuracyInfo.setNotAccurate();
			}
		}
	}
	
	private ISCNewOperatorDefinition createSCNewOperatorDefinition(ISCTheoryRoot targetRoot,
			ILabelSymbolInfo symbolInfo,
			INewOperatorDefinition newOpDef,
			IProgressMonitor monitor) throws CoreException {
		
		ILabeledElement scNewOpDef = symbolInfo.createSCElement(targetRoot,
				null, monitor);
		return (ISCNewOperatorDefinition) scNewOpDef;
	}
	
	private void processOperators(INewOperatorDefinition[] newOpDefs, String theoryName, ISCNewOperatorDefinition[] scNewOpDefs,
			ISCStateRepository repository, ILabelSymbolInfo[] operators,
			IProgressMonitor monitor) throws CoreException{
		for (int i = 0; i < newOpDefs.length; i++) {
			if (operators[i] != null && !operators[i].hasError()) {
				INewOperatorDefinition opDef = newOpDefs[i];
				// get latest factory and environment
				factory = repository.getFormulaFactory();
				globalTypeEnvironment = repository.getTypeEnvironment();
				ITypeEnvironmentBuilder opTypeEnvironment = factory.makeTypeEnvironment();
				opTypeEnvironment.addAll(globalTypeEnvironment);
				repository.setTypeEnvironment(opTypeEnvironment);
				// needed states
				repository.setState(new StackedIdentifierSymbolTable(
						identifierSymbolTable, ModulesUtils.IDENT_SYMTAB_SIZE,
						factory));
				String opID = theoryName + "." + operators[i].getSymbol();
				OperatorInformation operatorInformation = new OperatorInformation(opID, globalTypeEnvironment);
				repository.setState(operatorInformation);
				// copying of information
				operatorInformation.setFormulaType(opDef.getFormulaType());
				operatorInformation.setNotation(opDef.getNotationType());
				operatorInformation.setSyntax(opDef.getLabel());
				operatorInformation.setAssociative(opDef.isAssociative());
				operatorInformation.setCommutative(opDef.isCommutative());
				// Check number of definitions
				{
					IDirectOperatorDefinition[] opDefs = opDef.getDirectOperatorDefinitions();
					IRecursiveOperatorDefinition[] recDefs = opDef.getRecursiveOperatorDefinitions();
					if (opDefs.length + recDefs.length != 1) {
						if (opDefs.length + recDefs.length == 0) {
							createProblemMarker(opDef,
									EventBAttributes.LABEL_ATTRIBUTE,
									TheoryGraphProblem.OperatorHasNoDefError,
									opDef.getLabel());
						} else {
							createProblemMarker(opDef,
									EventBAttributes.LABEL_ATTRIBUTE,
									TheoryGraphProblem.OperatorHasMoreThan1DefError,
									opDef.getLabel());
						}
						operatorInformation.setHasError();
						theoryAccuracyInfo.setNotAccurate();
					}
				}
				// children processors
				{
					initProcessorModules(opDef, repository, null);
					processModules(opDef, scNewOpDefs[i], repository, monitor);
					endProcessorModules(opDef, repository, null);
				}
				repository.setState(identifierSymbolTable);
				// update the factory
				if(!operatorInformation.hasError()){
					factory = repository.getFormulaFactory();
					globalTypeEnvironment = AstUtilities.getTypeEnvironmentForFactory(globalTypeEnvironment, factory);
					repository.setFormulaFactory(factory);
					repository.setTypeEnvironment(globalTypeEnvironment);
				}
				else {
					scNewOpDefs[i].setHasError(true, monitor);
					// restore type environment without erroneous operator
					repository.setTypeEnvironment(globalTypeEnvironment);
				}
			}
			monitor.worked(1);
		}
	}
}
