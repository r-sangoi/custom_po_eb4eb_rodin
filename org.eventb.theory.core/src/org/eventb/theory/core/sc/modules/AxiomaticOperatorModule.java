/*******************************************************************************
 * Copyright (c) 2011, 2014 University of Southampton and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University of Southampton - initial API and implementation
 *******************************************************************************/
package org.eventb.theory.core.sc.modules;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ILabeledElement;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.IOperatorProperties.FormulaType;
import org.eventb.core.ast.extensions.maths.AstUtilities;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.state.ILabelSymbolInfo;
import org.eventb.core.sc.state.ILabelSymbolTable;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.internal.core.sc.modules.LabeledElementModule;
import org.eventb.internal.core.sc.symbolTable.IdentifierSymbolTable;
import org.eventb.internal.core.sc.symbolTable.StackedIdentifierSymbolTable;
import org.eventb.theory.core.IAxiomaticDefinitionsBlock;
import org.eventb.theory.core.IAxiomaticOperatorDefinition;
import org.eventb.theory.core.ISCAxiomaticDefinitionsBlock;
import org.eventb.theory.core.ISCAxiomaticOperatorDefinition;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.states.OperatorInformation;
import org.eventb.theory.core.sc.states.OperatorsLabelSymbolTable;
import org.eventb.theory.core.sc.states.TheoryAccuracyInfo;
import org.eventb.theory.core.sc.states.TheorySymbolFactory;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;

/**
 * 
 * @author maamria
 *
 */
@SuppressWarnings("restriction")
public class AxiomaticOperatorModule extends LabeledElementModule{

	private final IModuleType<OperatorModule> MODULE_TYPE = 
		SCCore.getModuleType(TheoryPlugin.PLUGIN_ID + ".axiomaticOperatorModule");
	
	private TheoryAccuracyInfo theoryAccuracyInfo;
	private ITypeEnvironmentBuilder globalTypeEnvironment;
	private FormulaFactory factory;
	private IdentifierSymbolTable identifierSymbolTable;
	
	@Override
	public void process(IRodinElement element, IInternalElement target,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		// First refresh local cache from repository
		factory = repository.getFormulaFactory();
		globalTypeEnvironment = repository.getTypeEnvironment();

		IAxiomaticDefinitionsBlock block = (IAxiomaticDefinitionsBlock) element;
		ISCAxiomaticDefinitionsBlock scBlock = (ISCAxiomaticDefinitionsBlock) target;
		monitor.worked(1);
		IAxiomaticOperatorDefinition[] newOpDefs = block.getAxiomaticOperatorDefinitions();
		ILabelSymbolInfo[] operators = fetchOperators(newOpDefs, block.getParent().getElementName(), repository, monitor);
		ISCAxiomaticOperatorDefinition scNewOpDefs[] = new ISCAxiomaticOperatorDefinition[newOpDefs.length];
		commitOperators(newOpDefs, scBlock, scNewOpDefs,operators, monitor);
		processOperators(newOpDefs, block.getParent().getElementName(), scNewOpDefs, repository, operators, monitor);
		monitor.worked(2);
		
	}

	@Override
	public void initModule(IRodinElement element,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		super.initModule(element, repository, monitor);
		theoryAccuracyInfo = (TheoryAccuracyInfo) repository.getState(TheoryAccuracyInfo.STATE_TYPE);
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
				component, true);
	}
	
	private ILabelSymbolInfo[] fetchOperators(IAxiomaticOperatorDefinition[] newOpDefs, String theoryName,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		boolean accurate = true;
		initFilterModules(repository, monitor);
		ILabelSymbolInfo[] labelSymbolInfos = new ILabelSymbolInfo[newOpDefs.length];
		for(int i = 0 ; i < newOpDefs.length; i++){
			IAxiomaticOperatorDefinition opDef = newOpDefs[i];
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
	
	private void commitOperators(IAxiomaticOperatorDefinition[] newOpDefs, ISCAxiomaticDefinitionsBlock scBlock,
			ISCAxiomaticOperatorDefinition[] scNewOpDefs,
			ILabelSymbolInfo[] labelSymbolInfos, IProgressMonitor monitor) 
	throws CoreException{
		for (int i = 0; i < newOpDefs.length; i++) {
			if (labelSymbolInfos[i] != null && !labelSymbolInfos[i].hasError()) {
				scNewOpDefs[i] = createSCAxiomaticOperatorDefinition(scBlock, labelSymbolInfos[i],
						newOpDefs[i], monitor); 
			}
			else {
				theoryAccuracyInfo.setNotAccurate();
			}
		}
	}
	
	private ISCAxiomaticOperatorDefinition createSCAxiomaticOperatorDefinition(ISCAxiomaticDefinitionsBlock scBlock,
			ILabelSymbolInfo symbolInfo,
			IAxiomaticOperatorDefinition newOpDef,
			IProgressMonitor monitor) throws CoreException {
		
		ILabeledElement scNewOpDef = symbolInfo.createSCElement(scBlock,
				null, monitor);
		return (ISCAxiomaticOperatorDefinition) scNewOpDef;
	}
	
	private void processOperators(IAxiomaticOperatorDefinition[] newOpDefs, String theoryName, 
			ISCAxiomaticOperatorDefinition[] scNewOpDefs,
			ISCStateRepository repository, ILabelSymbolInfo[] operators,
			IProgressMonitor monitor) throws CoreException{
		for (int i = 0; i < newOpDefs.length; i++) {
			if (operators[i] != null && !operators[i].hasError()) {
				IAxiomaticOperatorDefinition opDef = newOpDefs[i];
				// get latest factory and environment
				factory = repository.getFormulaFactory();
				globalTypeEnvironment = AstUtilities.getTypeEnvironmentForFactory(globalTypeEnvironment, factory);
				ITypeEnvironmentBuilder opTypeEnvironment = globalTypeEnvironment.makeBuilder();
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
				final Type type = fetchType(opDef);
				operatorInformation.setResultantType(type);
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
					repository.setTypeEnvironment(globalTypeEnvironment);
				}
				else {
					scNewOpDefs[i].setHasError(true, monitor);
				}
			}
			monitor.worked(1);
		}
		// get the new type environment corresponding to the factory
		globalTypeEnvironment = AstUtilities.getTypeEnvironmentForFactory(globalTypeEnvironment, factory);
		repository.setTypeEnvironment(globalTypeEnvironment);
	}

	private Type fetchType(IAxiomaticOperatorDefinition opDef)
			throws CoreException {
		if (opDef.getFormulaType() != FormulaType.EXPRESSION) {
			return null;
		}
		final String opDefType = opDef.getType();
		final IParseResult result = factory.parseType(opDefType);
		if (result.hasProblem()) {
			// these problems should have been issued in
			// AxiomaticOperatorFilterModule
			throw new IllegalStateException("factory with extensions: "
					+ factory.getExtensions() + "\ndoes not recognize type \""
					+ opDefType + "\"\nparse errors: " + result);
		}
		return result.getParsedType();
	}
}
