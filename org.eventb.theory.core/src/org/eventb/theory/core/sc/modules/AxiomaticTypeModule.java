package org.eventb.theory.core.sc.modules;

import java.util.Collections;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.ast.extensions.maths.AstUtilities;
import org.eventb.core.ast.extensions.maths.IAxiomaticTypeOrigin;
import org.eventb.core.ast.extensions.maths.MathExtensionsFactory;
import org.eventb.core.sc.GraphProblem;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.SCProcessorModule;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.theory.core.IAxiomaticDefinitionsBlock;
import org.eventb.theory.core.IAxiomaticTypeDefinition;
import org.eventb.theory.core.ISCAxiomaticDefinitionsBlock;
import org.eventb.theory.core.ISCAxiomaticTypeDefinition;
import org.eventb.theory.core.maths.extensions.FormulaExtensionsLoader;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.TheoryGraphProblem;
import org.eventb.theory.core.sc.states.TheoryAccuracyInfo;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;

/**
 * 
 * @author maamria
 * 
 */
public class AxiomaticTypeModule extends SCProcessorModule {

	private final IModuleType<AxiomaticTypeModule> MODULE_TYPE = SCCore.getModuleType(TheoryPlugin.PLUGIN_ID
			+ ".axiomaticTypeModule");

	private TheoryAccuracyInfo theoryAccuracyInfo;

	@Override
	public void process(IRodinElement element, IInternalElement target, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		IAxiomaticDefinitionsBlock block = (IAxiomaticDefinitionsBlock) element;
		ISCAxiomaticDefinitionsBlock scBlock = (ISCAxiomaticDefinitionsBlock) target;
		IAxiomaticTypeDefinition[] typeDefinitions = block.getAxiomaticTypeDefinitions();
		for (IAxiomaticTypeDefinition typeDefinition : typeDefinitions) {
			processTypeDefinitions(typeDefinition, scBlock, repository, monitor);
		}
	}

	private void processTypeDefinitions(IAxiomaticTypeDefinition typeDefinition, ISCAxiomaticDefinitionsBlock scBlock,
			ISCStateRepository repository, IProgressMonitor monitor) throws CoreException {
		if (checkTypeName(typeDefinition, repository.getFormulaFactory(), repository.getTypeEnvironment())) {
			ISCAxiomaticTypeDefinition target = ModulesUtils.createSCIdentifierElement(
					ISCAxiomaticTypeDefinition.ELEMENT_TYPE, typeDefinition, scBlock, monitor);
			target.setSource(typeDefinition, monitor);
			IAxiomaticTypeOrigin origin = FormulaExtensionsLoader.makeAxiomaticTypeOrigin(target);
			// need to update ff and type environment
			FormulaFactory newFf = repository.getFormulaFactory().withExtensions(
					Collections.singleton((IFormulaExtension) MathExtensionsFactory.getAxiomaticTypeExtension(
							typeDefinition.getIdentifierString(), typeDefinition.getIdentifierString(), origin)));
			repository.setFormulaFactory(newFf);
			repository.setTypeEnvironment(AstUtilities.getTypeEnvironmentForFactory(repository.getTypeEnvironment(), newFf));
		} else {
			theoryAccuracyInfo.setNotAccurate();
		}
	}

	// checks the type name/identifier
	private boolean checkTypeName(IAxiomaticTypeDefinition axmType, FormulaFactory factory, ITypeEnvironment typeEnvironment)
			throws CoreException {
		if (!axmType.hasIdentifierString() || axmType.getIdentifierString().equals("")) {
			createProblemMarker(axmType, EventBAttributes.IDENTIFIER_ATTRIBUTE, GraphProblem.IdentifierUndefError);
			return false;
		}		
		FreeIdentifier ident = ModulesUtils.parseIdentifier(axmType.getIdentifierString(), axmType,
				EventBAttributes.IDENTIFIER_ATTRIBUTE, factory, this);
		if (ident != null && typeEnvironment.contains(ident.getName())) {
			createProblemMarker(axmType, EventBAttributes.IDENTIFIER_ATTRIBUTE,
					TheoryGraphProblem.AxiomaticTypeNameAlreadyATypeParError, ident.getName());
			return false;

		} else if (ident == null) {
			//covers the conflicting case with the imported theories
			//createProblemMarker(axmType, EventBAttributes.IDENTIFIER_ATTRIBUTE,
					//TheoryGraphProblem.IdenIsAAxiomaticTypeNameError, axmType.getIdentifierString());
			return false;
		}
		return true;
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

}
