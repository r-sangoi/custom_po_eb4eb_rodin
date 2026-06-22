package org.eventb.theory.core.sc.modules;

import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.IOperatorProperties.FormulaType;
import org.eventb.core.ast.extension.IOperatorProperties.Notation;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.SCProcessorModule;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.theory.core.IAxiomaticOperatorDefinition;
import org.eventb.theory.core.ISCAxiomaticOperatorDefinition;
import org.eventb.theory.core.TheoryAttributes;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.TheoryGraphProblem;
import org.eventb.theory.core.sc.states.OperatorInformation;
import org.eventb.theory.core.sc.states.TheoryAccuracyInfo;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;

public class AxiomaticOperatorSemanticPropertiesModule extends SCProcessorModule {

	private final IModuleType<AxiomaticOperatorSemanticPropertiesModule> MODULE_TYPE = SCCore
			.getModuleType(TheoryPlugin.PLUGIN_ID + ".axiomaticOperatorSemanticPropertiesModule");

	private TheoryAccuracyInfo theoryAccuracyInfo;
	private OperatorInformation operatorInformation;

	@Override
	public void process(IRodinElement element, IInternalElement target, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		IAxiomaticOperatorDefinition operatorDefinition = (IAxiomaticOperatorDefinition) element;
		ISCAxiomaticOperatorDefinition scOperatorDefinition = (ISCAxiomaticOperatorDefinition) target;
		Map<String, Type> operatorArguments = operatorInformation.getOperatorArguments();
		Notation notation = operatorDefinition.getNotationType();
		FormulaType formType = operatorDefinition.getFormulaType();
		boolean isCommutative = operatorDefinition.isCommutative();
		boolean isAssos = operatorDefinition.isAssociative();

		if (!checkSemanticProperties(operatorDefinition, formType, notation, operatorArguments, isAssos, isCommutative)) {
			operatorInformation.setHasError();
			theoryAccuracyInfo.setNotAccurate();
		} else {
			operatorInformation.setAssociative(isAssos);
			operatorInformation.setCommutative(isCommutative);
		}
		if (operatorInformation.getWdCondition() == null) {
			operatorInformation.setHasError();
		} else {
			scOperatorDefinition.setPredicate(operatorInformation.getWdCondition(), monitor);
			scOperatorDefinition.setWDCondition(operatorInformation.getWdCondition(), monitor);
		}
	}

	protected boolean checkSemanticProperties(IAxiomaticOperatorDefinition operatorDefinition, FormulaType formType,
			Notation notation, Map<String, Type> operatorArguments, boolean isAssociative, boolean isCommutative)
			throws CoreException {
		String opID = operatorDefinition.getLabel();
		// Issues with associativity
		if (isAssociative) {
			// 4- Predicate operators cannot be associative
			if (formType.equals(FormulaType.PREDICATE)) {
				createProblemMarker(operatorDefinition, EventBAttributes.LABEL_ATTRIBUTE,
						TheoryGraphProblem.OperatorPredCannotBeAssos);
				return false;
			} else {
				// 5- Associative and prefix not supported
				if (notation.equals(Notation.PREFIX)) {
					createProblemMarker(operatorDefinition, TheoryAttributes.ASSOCIATIVE_ATTRIBUTE,
							TheoryGraphProblem.OperatorExpPrefixCannotBeAssos);
					return false;
				} else if (notation.equals(Notation.INFIX)) {
					// 6- Check actual associativity
					if (!checkAssociativity(operatorArguments)) {
						createProblemMarker(operatorDefinition, TheoryAttributes.ASSOCIATIVE_ATTRIBUTE,
								TheoryGraphProblem.OperatorCannotBeAssosError, opID);
						return false;
					}
				}
			}
		}
		// Issues with commutativity
		if (isCommutative) {
			// 7- Check actual commutativity
			if (!checkCommutativity(operatorArguments)) {
				createProblemMarker(operatorDefinition, TheoryAttributes.COMMUTATIVE_ATTRIBUTE,
						TheoryGraphProblem.OperatorCannotBeCommutError, opID);
				return false;
			}
		}
		return true;
	}

	@Override
	public void initModule(IRodinElement element, ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		super.initModule(element, repository, monitor);
		operatorInformation = (OperatorInformation) repository.getState(OperatorInformation.STATE_TYPE);
		theoryAccuracyInfo = (TheoryAccuracyInfo) repository.getState(TheoryAccuracyInfo.STATE_TYPE);
	}

	@Override
	public void endModule(IRodinElement element, ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		operatorInformation = null;
		theoryAccuracyInfo = null;
		super.endModule(element, repository, monitor);
	}

	@Override
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	/**
	 * An operator can be associative if it can have at least two arguments of
	 * the same type, which has to be the same as the resultant type.
	 * 
	 * @param args
	 *            the operator arguments
	 * @return whether this operator can be associative
	 */
	protected boolean checkAssociativity(Map<String, Type> args) throws CoreException {
		if (!operatorInformation.isExpressionOperator() || (args.size() != 2)) {
			return false;
		}
		Type type = null;
		for (String arg : args.keySet()) {
			Type currentArgType = args.get(arg);
			if (type == null) {
				type = currentArgType;
			}
			if (!type.equals(currentArgType)) {
				return false;
			}
		}
		if (!type.equals(operatorInformation.getResultantType())) {
			return false;
		}
		return true;
	}

	/**
	 * An operator can be commutative if it can have two arguments of the same
	 * type.
	 * 
	 * @param args
	 *            the operator arguments
	 * @return whether this operator can be commutative
	 */
	protected boolean checkCommutativity(Map<String, Type> args) {
		if (args.size() != 2) {
			return false;
		}
		Type type = null;
		for (String arg : args.keySet()) {
			Type currentArgType = args.get(arg);
			if (type == null) {
				type = currentArgType;
			}
			if (!(type.equals(currentArgType)))
				return false;
		}
		return true;
	}


}
