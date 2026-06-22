/*******************************************************************************
 * Copyright (c) 2010, 2022 University of Southampton and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.sc;

import java.text.MessageFormat;

import org.eclipse.core.resources.IMarker;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.rodinp.core.IRodinProblem;

/**
 * @author maamria
 * 
 */
public enum TheoryGraphProblem implements IRodinProblem {
			MetavariableNameConflictError(IMarker.SEVERITY_ERROR, Messages.scuser_MetavariableNameConflictError), 
			TypeParameterNameConflictError(IMarker.SEVERITY_ERROR,Messages.scuser_TypeParameterNameConflict), 
			OperatorArgumentNameConflictError(IMarker.SEVERITY_ERROR,Messages.scuser_OperatorArgumentNameConflict), 
			OperatorHasMoreThan1DefError(IMarker.SEVERITY_ERROR,Messages.scuser_OperatorHasMoreThan1DefError), 
			OperatorCannotBeCommutError(IMarker.SEVERITY_ERROR,Messages.scuser_OperatorCannotBeCommutError), 
			OperatorCannotBeAssosError(IMarker.SEVERITY_ERROR,Messages.scuser_OperatorCannotBeAssosError), 
			OperatorWithSameSynJustBeenAddedError(IMarker.SEVERITY_ERROR,Messages.scuser_OperatorWithSameSynJustBeenAddedError), 
			OpCannotReferToTheseIdents(IMarker.SEVERITY_ERROR,Messages.scuser_OpCannotReferToTheseIdents), 
			UntypedTypeParameterError(IMarker.SEVERITY_ERROR, Messages.scuser_UntypedTypeParameterError), 
			UntypedOperatorArgumentError(IMarker.SEVERITY_ERROR,Messages.scuser_UntypedOperatorArgumentError), 
			DatatypeNameAlreadyATypeParError(IMarker.SEVERITY_ERROR,Messages.scuser_DatatypeNameAlreadyATypeParError), 
			TypeArgMissingError(IMarker.SEVERITY_ERROR, Messages.scuser_TypeArgMissingError), 
			TypeArgNotDefinedError(IMarker.SEVERITY_ERROR, Messages.scuser_TypeArgNotDefinedError), 
			TypeArgRedundWarn(IMarker.SEVERITY_WARNING, Messages.scuser_TypeArgRedundWarn), 
			DatatypeHasNoConsError(IMarker.SEVERITY_ERROR, Messages.scuser_DatatypeHasNoConsError), 
			DatatypeHasNoBaseConsError(IMarker.SEVERITY_ERROR, Messages.scuser_DatatypeHasNoBaseConsError), 
			ConstructorNameAlreadyATypeParError(IMarker.SEVERITY_ERROR, Messages.scuser_ConstructorNameAlreadyATypeParError), 
			DestructorNameAlreadyATypeParError(IMarker.SEVERITY_ERROR,Messages.scuser_DestructorNameAlreadyATypeParError), 
			MissingDestructorNameError(IMarker.SEVERITY_ERROR, Messages.scuser_MissingDestructorNameError), 
			MissingDestructorTypeError(IMarker.SEVERITY_ERROR, Messages.scuser_MissingDestructorTypeError), 
			MissingConstructorNameError(IMarker.SEVERITY_ERROR, Messages.scuser_MissingConstructorNameError), 
			MissingDatatypeNameError(IMarker.SEVERITY_ERROR, Messages.scuser_MissingDatatypeNameError), 
			TypeIsNotRefTypeError(IMarker.SEVERITY_ERROR, Messages.scuser_TypeIsNotRefTypeError), 
			IdentIsNotTypeParError(IMarker.SEVERITY_ERROR, Messages.scuser_IdentIsNotTypeParError), 
			IdenIsExistingNameError(IMarker.SEVERITY_ERROR, Messages.scuser_IdenIsExistingNameError), 
			IdenIsAAxiomaticTypeNameError(IMarker.SEVERITY_ERROR, Messages.scuser_IdenIsAAxiomaticTypeNameError), 
			OperatorSynConflictError(IMarker.SEVERITY_ERROR, Messages.scuser_OperatorSynConflictError), 
			OperatorIDExistsError(IMarker.SEVERITY_ERROR, Messages.scuser_OperatorIDExistsError), 
			OperatorSynMissingError(IMarker.SEVERITY_ERROR, Messages.scuser_OperatorSynMissingError), 
			OperatorSynExistsError(IMarker.SEVERITY_ERROR, Messages.scuser_OperatorSynExistsError), 
			OperatorSynIsATypeParError(IMarker.SEVERITY_ERROR, Messages.scuser_OperatorSynIsATypeParError), 
			OperatorFormTypeMissingError(IMarker.SEVERITY_ERROR,Messages.scuser_OperatorFormTypeMissingError), 
			OperatorNotationTypeMissingError(IMarker.SEVERITY_ERROR,Messages.scuser_OperatorNotationTypeMissingError), 
			OperatorAssocMissingError(IMarker.SEVERITY_ERROR, Messages.scuser_OperatorAssocMissingWarning), 
			OperatorCommutMissingError(IMarker.SEVERITY_ERROR, Messages.scuser_OperatorCommutMissingWarning), 
			TypeAttrMissingError(IMarker.SEVERITY_ERROR,Messages.scuser_TypeAttrMissingError), 
			WDPredMissingError(IMarker.SEVERITY_ERROR, Messages.scuser_WDPredMissingError), 
			MissingFormulaError(IMarker.SEVERITY_ERROR, Messages.scuser_MissingFormulaError), 
			OperatorHasNoDefError(IMarker.SEVERITY_ERROR, Messages.scuser_OperatorHasNoDefError), 
			OperatorDefNotExpError(IMarker.SEVERITY_ERROR, Messages.scuser_OperatorDefNotExpError), 
			OperatorDefNotPredError(IMarker.SEVERITY_ERROR, Messages.scuser_OperatorDefNotPredError), 
			OperatorInvalidSynError(IMarker.SEVERITY_ERROR, Messages.scuser_OperatorInvalidSynError), 
			RulesBlockLabelProblemError(IMarker.SEVERITY_ERROR, Messages.scuser_RulesBlockLabelProblemError), 
			AxiomaticBlockLabelProblemError(IMarker.SEVERITY_ERROR, Messages.scuser_AxiomaticBlockLabelProblemError),
			TheoremPredMissingError(IMarker.SEVERITY_ERROR, Messages.scuser_TheoremPredMissingError), 
			AxiomPredMissingError(IMarker.SEVERITY_ERROR, Messages.scuser_AxiomPredMissingError), 
			TheoremLabelProblemError(IMarker.SEVERITY_ERROR, Messages.scuser_TheoremLabelProblemError), 
			AxiomLabelProblemError(IMarker.SEVERITY_ERROR, Messages.scuser_AxiomLabelProblemError),
			ApplicabilityUndefError(IMarker.SEVERITY_ERROR, Messages.scuser_ApplicabilityUndefError),
			RedundantImportWarning(IMarker.SEVERITY_WARNING, Messages.scuser_RedundantImportWarning),
			ImportTheoryProjectMissing(IMarker.SEVERITY_ERROR,Messages.scuser_TheoryProjectInImportMissing),
			ImportTheoryMissing(IMarker.SEVERITY_ERROR,Messages.scuser_TheoryInImportMissing),
			ImportTheoryNotExist(IMarker.SEVERITY_ERROR, Messages.scuser_ImportTheoryNotExist), 
			InferenceGivenBTRUEPredWarn(IMarker.SEVERITY_WARNING, Messages.scuser_InferenceGivenBTRUEPredWarn),
			InferenceInferBTRUEPredErr(IMarker.SEVERITY_ERROR,Messages.scuser_InferenceInferBTRUEPredErr),
			UntypedMetavariableError(IMarker.SEVERITY_ERROR, Messages.scuser_UntypedMetavariableError),
			DescNotSupplied(IMarker.SEVERITY_INFO, Messages.scuser_DescNotSupplied), 
			CompleteUndefWarning(IMarker.SEVERITY_WARNING, Messages.scuser_CompleteUndefWarning), 
			LhsAndRhsNotSynClassMatching(IMarker.SEVERITY_ERROR,Messages.scuser_LhsAndRhsNotSynClassMatching), 
			LHSFormulaMissingError(IMarker.SEVERITY_ERROR, Messages.scuser_LHSUndef),
			RHSTypesNotSubsetOfLHSTypes(IMarker.SEVERITY_ERROR, Messages.scuser_RHSTypesNotSubsetOfLHSTypes),
			CondTypesNotSubsetOfLHSTypes(IMarker.SEVERITY_ERROR, Messages.scuser_CondTypesNotSubsetOfLHSTypes),
			CondIdentsNotSubsetOfLHSIdents(IMarker.SEVERITY_ERROR,Messages.scuser_CondIdentsNotSubsetOfLHSIdents), 
			RHSIdentsNotSubsetOfLHSIdents(IMarker.SEVERITY_ERROR,Messages.scuser_RHSIdentsNotSubsetOfLHSIdents), 
			RHSFormulaMissingError(IMarker.SEVERITY_ERROR, Messages.scuser_RHSFormulaMissing), 
			RuleSideNotTheoryFormula(IMarker.SEVERITY_ERROR, Messages.scuser_RuleSideNotTheoryFormula), 
			RuleTypeMismatchError(IMarker.SEVERITY_ERROR, Messages.scuser_RuleTypeMismatch), 
			RewriteRuleLabelConflictError(IMarker.SEVERITY_ERROR, Messages.scuser_RewriteRuleLabelConflict), 
			InferenceRuleLabelConflictError(IMarker.SEVERITY_ERROR, Messages.scuser_InferenceRuleLabelConflict), 
			OperatorExpPrefixCannotBeAssos(IMarker.SEVERITY_ERROR,Messages.scuser_OperatorExpPrefixCannotBeAssos),
			@Deprecated OperatorPredOnlyPrefix(IMarker.SEVERITY_ERROR, Messages.scuser_OperatorPredOnlyPrefix),
			OperatorExpCannotBePostfix(IMarker.SEVERITY_ERROR, Messages.scuser_OperatorExpCannotBePostfix),
			OperatorPredNeedOneOrMoreArgs(IMarker.SEVERITY_ERROR, Messages.scuser_OperatorPredNeedOneOrMoreArgs), 
			OperatorPredCannotBeAssos(IMarker.SEVERITY_ERROR, Messages.scuser_OperatorPredCannotBeAssos), 
			OperatorExpInfixNeedsAtLeastTwoArgs(IMarker.SEVERITY_ERROR, Messages.scuser_OperatorExpInfixNeedsAtLeastTwoArgs), 
			RhsLabelConflictError(IMarker.SEVERITY_ERROR, Messages.scuser_RhsLabelConflict), 
			CondUndefError(IMarker.SEVERITY_ERROR, Messages.scuser_CondUndef), 
			RuleNoRhsError(IMarker.SEVERITY_ERROR, Messages.scuser_RuleWithNoRHSs), 
			RuleInfersError(IMarker.SEVERITY_ERROR, Messages.scuser_RuleInfersError),
			NoRuleDescWarning(IMarker.SEVERITY_WARNING, Messages.scuser_NoRuleDescWarning), 
			LHSIsIdentErr(IMarker.SEVERITY_ERROR, Messages.scuser_LHSIsIdentErr), 
			RHSPredVarsNOTSubsetOFLHS(IMarker.SEVERITY_ERROR, Messages.scuser_RHSPredVarsNOTSubsetOFLHS), 
			NonTypeParOccurError(IMarker.SEVERITY_ERROR, Messages.scuser_NonTypeParOccurError),
			InferenceRuleNotApplicableError(IMarker.SEVERITY_ERROR, Messages.scuser_InferenceRuleNotApplicableError),
			InferenceRuleBackward(IMarker.SEVERITY_INFO, Messages.scuser_InferenceRuleBackward),
			InferenceRuleForward(IMarker.SEVERITY_INFO, Messages.scuser_InferenceRuleForward),
			InferenceRuleBoth(IMarker.SEVERITY_INFO, Messages.scuser_InferenceRuleBoth),
			LHS_IsNotWDStrict(IMarker.SEVERITY_ERROR,Messages.scuser_LHS_IsNotWDStrict), 
			
			ImportDepCircularity(IMarker.SEVERITY_ERROR,Messages.scuser_ImportDepCircularity),
			IndRedundantImportWarn(IMarker.SEVERITY_WARNING,Messages.scuser_IndRedundantImportWarn),
			ImportConflict(IMarker.SEVERITY_ERROR, Messages.scuser_ImportConflict), 
			ArgumentNotExistOrNotParametric(IMarker.SEVERITY_ERROR,Messages.scuser_ArgumentNotExistOrNotParametric), 
			InductiveCaseMissing(IMarker.SEVERITY_ERROR, Messages.scuser_InductiveCaseMissing), 
			ExprIsNotDatatypeConstr(IMarker.SEVERITY_ERROR, Messages.scuser_ExprIsNotDatatypeConstr), 
			ConstrAlreadyCovered(IMarker.SEVERITY_ERROR, Messages.scuser_ConstrAlreadyCovered), 
			ExprNotApproInductiveCase(IMarker.SEVERITY_ERROR, Messages.scuser_ExprNotApproInductiveCase), 
			ConstrArgumentNotIdentifier(IMarker.SEVERITY_ERROR, Messages.scuser_ConstrArgumentNotIdentifier), 
			OperatorCannotBePostfix(IMarker.SEVERITY_ERROR, Messages.scuser_OperatorCannotBePostfix), 
			InductiveArgMissing(IMarker.SEVERITY_ERROR, Messages.scuser_InductiveArgMissing), 
			NoRecCasesError(IMarker.SEVERITY_ERROR, Messages.scuser_NoRecCasesError), 
			InductiveCaseNotAppropriateExp(IMarker.SEVERITY_ERROR, Messages.scuser_InductiveCaseNotAppropriateExp), 
			ConsArgNotIdentInCase(IMarker.SEVERITY_ERROR, Messages.scuser_ConsArgNotIdentInCase), 
			IdentCannotBeUsedAsConsArg(IMarker.SEVERITY_ERROR, Messages.scuser_IdentCannotBeUsedAsConsArg), 
			IdentAlreadyUsedInCase(IMarker.SEVERITY_ERROR, Messages.scuser_IdentAlreadyUsedInCase),
			UnableToTypeCase(IMarker.SEVERITY_ERROR, Messages.scuser_UnableToTypeCase), 
			RecCaseAlreadyCovered(IMarker.SEVERITY_ERROR, Messages.scuser_RecCaseAlreadyCovered), 
			TypeMissmatchOfRecDef(IMarker.SEVERITY_ERROR, Messages.scuser_TypeMissmatchOfRecDef), 
			NoCoverageAllRecCase(IMarker.SEVERITY_ERROR, Messages.scuser_NoCoverageAllRecCase), 
			RecOpTypeNotConsistent(IMarker.SEVERITY_ERROR, Messages.scuser_RecOpTypeNotConsistent), 
			OpArgExprNotSet(IMarker.SEVERITY_ERROR, Messages.scuser_OpArgExprNotSet),
			DatatypeError(IMarker.SEVERITY_ERROR, Messages.scuser_DatatypeError),
			InvalidIdentForDatatype(IMarker.SEVERITY_ERROR, Messages.scuser_InvalidIdentForDatatype), 
			InvalidIdentForConstructor(IMarker.SEVERITY_ERROR, Messages.scuser_InvalidIdentForConstructor), 
			InvalidIdentForDestructor(IMarker.SEVERITY_ERROR, Messages.scuser_InvalidIdentForDestructor), 
			NoTheoryProjectClausesError(IMarker.SEVERITY_ERROR, Messages.scuser_NoTheoryProjectError),
			DuplicatedTheoryProjectError(IMarker.SEVERITY_ERROR, Messages.scuser_DuplicatedTheoryProjectError),
			NoSelectedTheoriesError(IMarker.SEVERITY_ERROR, Messages.scuser_NoSelectedTheoriesError),
			TheoryPathProjectIsThisProject(IMarker.SEVERITY_ERROR, Messages.scuser_TheoryPathProjectIsThisProject),
			DuplicatedTheoryError(IMarker.SEVERITY_ERROR, Messages.scuser_DuplicatedTheoryError),
			DeployedTheoryNotExistError(IMarker.SEVERITY_ERROR, Messages.scuser_DeployedTheoryNotExistError),
			NoTheoryClausesError(IMarker.SEVERITY_ERROR, Messages.scuser_NoTheorySelectedError),
			RedundantDeployedTheoryWarning(IMarker.SEVERITY_WARNING, Messages.scuser_DeployedTheoryRedudantWarning),
			TheoriesConflictError(IMarker.SEVERITY_ERROR, Messages.scuser_DeployedTheoriesConflictError),
			MultipleTheoryPathProjectError(IMarker.SEVERITY_ERROR, Messages.scuser_MultipleTheoryPathProjectError),
			TheoryProjectDoesNotExistError(IMarker.SEVERITY_ERROR, Messages.scuser_TheoryPathProjectNotExistError),
			AxiomaticTypeNameAlreadyATypeParError(IMarker.SEVERITY_ERROR, Messages.scuser_AxiomaticTypeNameAlreadyATypeParError),
			AxiomaticPredicateOpDoesNotReqTypeWarn(IMarker.SEVERITY_WARNING, Messages.scuser_AxiomaticPredicateOpDoesNotReqTypeWarn),
			AxiomaticInvalidTypeError(IMarker.SEVERITY_ERROR, Messages.scuser_AxiomaticInvalidTypeError)
		;

	private int arity;

	private final String errorCode;

	private final String message;

	private final int severity;

	private TheoryGraphProblem(int severity, String message) {
		this.severity = severity;
		this.message = message;
		this.errorCode = TheoryPlugin.PLUGIN_ID + "." + name();
		arity = -1;
	}

	/**
	 * Returns the number of parameters needed by the message of this problem,
	 * i.e. the length of the object array to be passed to
	 * <code>getLocalizedMessage()</code>.
	 * 
	 * @return the number of parameters needed by the message of this problem
	 */
	public int getArity() {
		if (arity == -1) {
			MessageFormat mf = new MessageFormat(message);
			arity = mf.getFormatsByArgumentIndex().length;
		}
		return arity;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public String getLocalizedMessage(Object[] args) {
		return MessageFormat.format(message, args);
	}

	public int getSeverity() {
		return severity;
	}

	public static TheoryGraphProblem valueOfErrorCode(String errorCode) {
		String instName = errorCode.substring(errorCode.lastIndexOf('.') + 1);
		return valueOf(instName);
	}
}
