/*******************************************************************************
 * Copyright (c) 2006, 2022 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added scuser_LoadingRootModuleError
 *     University of Dusseldorf - added theorem attribute
 *     Systerel - adapt datatypes to Rodin 3.0 API
 *******************************************************************************/
package org.eventb.theory.core.sc;

/**
 * @author maamria
 *
 */
import java.text.MessageFormat;

import org.eclipse.osgi.util.NLS;

public final class Messages {

	// build
	public static String build_cleaning;
	public static String build_extracting;
	public static String build_runningSC;
	public static String progress_TheoryImportTheories;
	public static String progress_TheoryTypeParameters;
	public static String progress_TheoryOperators;
	public static String progress_TheoryDatatypes;
	public static String progress_TheoryProofRules;
	public static String progress_TheoryAxiomaticBlocks;
	public static String progress_TheoryInferenceRules;
	public static String progress_TheoryRewriteRules;
	public static String progress_TheoryTheorems;
	public static String progress_notifyTheoryPath;
	public static String scuser_IndRedundantImportWarn;
	public static String scuser_LhsAndRhsNotSynClassMatching;
	public static String scuser_LHSUndef;
	public static String scuser_RewriteRuleLabelConflict;
	public static String scuser_RHSIdentsNotSubsetOfLHSIdents;
	public static String scuser_RHSFormulaMissing;
	public static String scuser_RuleSideNotTheoryFormula;
	public static String scuser_RuleTypeMismatch;
	public static String scuser_TypeParameterNameConflict;
	public static String scuser_UntypedTypeParameterError;
	public static String scuser_RhsLabelConflict;
	public static String scuser_CondUndef;
	public static String scuser_CondAttrUndef;
	public static String scuser_RuleWithNoRHSs;
	public static String scuser_CompleteUndefWarning;
	public static String scuser_NoRuleDescWarning;
	public static String scuser_DescNotSupplied;
	public static String scuser_LHSIsIdentErr;
	public static String scuser_RHSPredVarsNOTSubsetOFLHS;
	public static String scuser_DatatypeNameAlreadyATypeParError;
	public static String scuser_TypeArgMissingError;
	public static String scuser_TypeArgNotDefinedError;
	public static String scuser_TypeArgRedundWarn;
	public static String scuser_DatatypeHasNoConsError;
	public static String scuser_ConstructorNameAlreadyATypeParError;
	public static String scuser_DestructorNameAlreadyATypeParError;
	public static String scuser_MissingDestructorNameError;
	public static String scuser_MissingConstructorNameError;
	public static String scuser_MissingDatatypeNameError;
	public static String scuser_MissingDestructorTypeError;
	public static String scuser_TypeIsNotRefTypeError;
	public static String scuser_IdenIsExistingNameError;
	public static String scuser_IdenIsAAxiomaticTypeNameError;
	public static String scuser_DatatypeHasNoBaseConsError;
	public static String scuser_OperatorSynConflictError;
	public static String scuser_OperatorIDExistsError;
	public static String scuser_OperatorSynMissingError;
	public static String scuser_OperatorSynExistsError;
	public static String scuser_OperatorFormTypeMissingError;
	public static String scuser_OperatorNotationTypeMissingError;
	public static String scuser_OperatorAssocMissingWarning;
	public static String scuser_OperatorCommutMissingWarning;
	public static String scuser_UntypedOperatorArgumentError;
	public static String scuser_OperatorArgumentNameConflict;
	public static String scuser_TypeAttrMissingError;
	public static String scuser_IdentIsNotTypeParError;
	public static String scuser_OperatorCannotBeCommutError;
	public static String scuser_OperatorCannotBeAssosError;
	public static String scuser_TheoremPredMissingError;
	public static String scuser_WDPredMissingError;
	public static String scuser_OpCannotReferToTheseIdents;
	public static String scuser_OperatorSynIsATypeParError;
	public static String scuser_OperatorHasMoreThan1DefError;
	public static String scuser_MissingFormulaError;
	public static String scuser_OperatorHasNoDefError;
	public static String scuser_OperatorDefNotExpError;
	public static String scuser_OperatorDefNotPredError;
	public static String scuser_OperatorWithSameSynJustBeenAddedError;
	public static String scuser_OperatorInvalidSynError;
	public static String scuser_RulesBlockLabelProblemError;
	public static String scuser_AxiomaticBlockLabelProblemError;
	public static String scuser_RulesBlockLabelProblemWarning;
	public static String scuser_UntypedMetavariableError;
	public static String scuser_MetavariableNameConflictError;
	public static String scuser_TheoremLabelProblemError;
	public static String scuser_TheoremLabelProblemWarning;
	public static String scuser_NonTypeParOccurError;
	public static String scuser_OperatorPredOnlyPrefix;
	public static String scuser_OperatorPredNeedOneOrMoreArgs;
	public static String scuser_OperatorExpInfixNeedsAtLeastTwoArgs;
	public static String scuser_OperatorExpPrefixCannotBeAssos;
	public static String scuser_OperatorPredCannotBeAssos;
	public static String scuser_OperatorExpCannotBePostfix;
	public static String scuser_InferenceRuleLabelConflict;
	public static String scuser_InferenceRuleNotApplicableError;
	public static String scuser_RuleInfersError;
	public static String scuser_InferenceGivenBTRUEPredWarn;
	public static String scuser_InferenceInferBTRUEPredErr;
	public static String scuser_InferenceRuleBackward;
	public static String scuser_InferenceRuleForward;
	public static String scuser_InferenceRuleBoth;
	public static String scuser_CondIdentsNotSubsetOfLHSIdents;
	public static String scuser_CondTypesNotSubsetOfLHSTypes;
	public static String scuser_RHSTypesNotSubsetOfLHSTypes;
	public static String scuser_TheoryProjectInImportMissing;
	public static String scuser_TheoryInImportMissing;
	public static String scuser_ImportTheoryNotExist;
	public static String scuser_RedundantImportWarning;
	public static String scuser_LHS_IsNotWDStrict;
	public static String scuser_ImportDepCircularity;
	public static String scuser_ImportConflict;
	public static String scuser_ArgumentNotExistOrNotParametric;
	public static String scuser_InductiveCaseMissing;
	public static String scuser_ExprIsNotDatatypeConstr;
	public static String scuser_ConstrAlreadyCovered;
	public static String scuser_ExprNotApproInductiveCase;
	public static String scuser_ConstrArgumentNotIdentifier;
	public static String scuser_OperatorCannotBePostfix;
	public static String scuser_InductiveArgMissing;
	public static String scuser_NoRecCasesError;
	public static String scuser_InductiveCaseNotAppropriateExp;
	public static String scuser_ConsArgNotIdentInCase;
	public static String scuser_IdentCannotBeUsedAsConsArg;
	public static String scuser_IdentAlreadyUsedInCase;
	public static String scuser_UnableToTypeCase;
	public static String scuser_RecCaseAlreadyCovered;
	public static String scuser_TypeMissmatchOfRecDef;
	public static String scuser_NoCoverageAllRecCase;
	public static String scuser_RecOpTypeNotConsistent;
	public static String database_SCTypeParseFailure;
	public static String scuser_OpArgExprNotSet;
	public static String scuser_ApplicabilityUndefError;
	public static String scuser_DatatypeError;
	public static String scuser_InvalidIdentForDatatype;
	public static String scuser_InvalidIdentForConstructor;
	public static String scuser_InvalidIdentForDestructor;
	public static String progress_TheoryProjects;
	public static String scuser_NoTheoryProjectError;
	public static String scuser_DuplicatedTheoryProjectError;
	public static String scuser_NoSelectedTheoriesError;
	public static String progress_TheoryPathTheories;
	public static String scuser_DuplicatedTheoryError;
	public static String scuser_DeployedTheoryNotExistError;
	public static String scuser_NoTheorySelectedError;
	public static String scuser_DeployedTheoryRedudantWarning;
	public static String scuser_DeployedTheoriesConflictError;
	public static String scuser_MultipleTheoryPathProjectError;
	public static String scuser_TheoryPathProjectNotExistError;
	public static String scuser_AxiomaticTypeNameAlreadyATypeParError;
	public static String scuser_AxiomaticPredicateOpDoesNotReqTypeWarn;
	public static String scuser_AxiomaticInvalidTypeError;
	public static String scuser_AxiomLabelProblemError;
	public static String scuser_AxiomPredMissingError;
	public static String scuser_TheoryPathProjectIsThisProject;
	public static String database_SCPredicatePatternParseFailure;
	public static String database_SCPredicatePatternTCFailure;

	private static final String BUNDLE_NAME = "org.eventb.theory.core.sc.messages";

	static {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class); 
	}

	private Messages() {
		// Do not instantiate
	}

	/**
	 * Bind the given message's substitution locations with the given string
	 * values.
	 * 
	 * @param message
	 *            the message to be manipulated
	 * @param bindings
	 *            An array of objects to be inserted into the message
	 * @return the manipulated String
	 */
	public static String bind(String message, Object... bindings) {
		return MessageFormat.format(message, bindings);
	}

}