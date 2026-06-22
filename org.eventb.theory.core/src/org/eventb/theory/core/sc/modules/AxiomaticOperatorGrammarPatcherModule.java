package org.eventb.theory.core.sc.modules;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.ast.extensions.maths.AstUtilities;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.SCProcessorModule;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.theory.core.ISCAxiomaticOperatorDefinition;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.states.OperatorInformation;
import org.eventb.theory.internal.core.util.GeneralUtilities;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;

public class AxiomaticOperatorGrammarPatcherModule extends SCProcessorModule {

	IModuleType<AxiomaticOperatorGrammarPatcherModule> MODULE_TYPE = SCCore.getModuleType(TheoryPlugin.PLUGIN_ID
			+ ".axiomaticOperatorGrammarPatcherModule");

	private FormulaFactory factory;
	private OperatorInformation operatorInformation;

	@Override
	public void process(IRodinElement element, IInternalElement target, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		ISCAxiomaticOperatorDefinition scAxmOperatorDefinition = (ISCAxiomaticOperatorDefinition) target;
		
		if (!operatorInformation.hasError()) {
			String syntax = operatorInformation.getSyntax();
//removed becuase we do not need to check the uniqueness of the operators 
			if (AstUtilities.checkOperatorSyntaxSymbol(syntax, factory)) {
				operatorInformation.makeImmutable();
				IFormulaExtension formulaExtension = operatorInformation.getExtension(scAxmOperatorDefinition);
				FormulaFactory newFactory = factory.withExtensions(GeneralUtilities.singletonSet(formulaExtension));
				repository.setFormulaFactory(newFactory);
				repository.setTypeEnvironment(AstUtilities.getTypeEnvironmentForFactory(
						repository.getTypeEnvironment(), newFactory));
				factory = repository.getFormulaFactory();
				scAxmOperatorDefinition.setHasError(false, monitor);
				scAxmOperatorDefinition.setOperatorGroup(formulaExtension.getGroupId(), monitor);
				if (operatorInformation.isExpressionOperator()) {
					scAxmOperatorDefinition.setType(operatorInformation.getResultantType(), monitor);
				}
			} /*else {
				createProblemMarker((IAxiomaticOperatorDefinition) element, EventBAttributes.LABEL_ATTRIBUTE,
						TheoryGraphProblem.OperatorWithSameSynJustBeenAddedError, syntax);
				operatorInformation.setHasError();
				operatorInformation.makeImmutable();
			}*/
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
