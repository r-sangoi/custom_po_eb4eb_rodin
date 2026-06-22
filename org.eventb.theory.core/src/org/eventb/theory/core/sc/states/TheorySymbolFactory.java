/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.sc.states;

import org.eventb.core.EventBAttributes;
import org.eventb.core.IIdentifierElement;
import org.eventb.core.sc.IMarkerDisplay;
import org.eventb.core.sc.state.IIdentifierSymbolInfo;
import org.eventb.core.sc.state.ILabelSymbolInfo;
import org.eventb.core.sc.state.ISymbolInfo;
import org.eventb.internal.core.sc.symbolTable.ISymbolProblem;
import org.eventb.internal.core.sc.symbolTable.ITypedSymbolProblem;
import org.eventb.internal.core.sc.symbolTable.IdentifierSymbolInfo;
import org.eventb.internal.core.sc.symbolTable.LabelSymbolInfo;
import org.eventb.theory.core.ISCAxiomaticDefinitionAxiom;
import org.eventb.theory.core.ISCAxiomaticDefinitionsBlock;
import org.eventb.theory.core.ISCAxiomaticOperatorDefinition;
import org.eventb.theory.core.ISCInferenceRule;
import org.eventb.theory.core.ISCMetavariable;
import org.eventb.theory.core.ISCNewOperatorDefinition;
import org.eventb.theory.core.ISCOperatorArgument;
import org.eventb.theory.core.ISCProofRulesBlock;
import org.eventb.theory.core.ISCRewriteRule;
import org.eventb.theory.core.ISCRewriteRuleRightHandSide;
import org.eventb.theory.core.ISCTheorem;
import org.eventb.theory.core.ISCTypeParameter;
import org.eventb.theory.core.sc.TheoryGraphProblem;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinProblem;
import org.rodinp.core.RodinDBException;

/**
 * @author maamria
 * 
 */
@SuppressWarnings("restriction")
public class TheorySymbolFactory {

	private static TheorySymbolFactory factory = new TheorySymbolFactory();

	private static LocalTypeParameterSymbolProblem localTypeParameterSymbolProblem = new LocalTypeParameterSymbolProblem();
	private static OperatorSynSymbolProblem operatorSynProblem = new OperatorSynSymbolProblem();
	private static LocalOperatorArgumentSymbolProblem operatorArgumentSymbolProblem = new LocalOperatorArgumentSymbolProblem();
	private static RulesBlockSymbolProblem rulesBlockSymbolProblem = new RulesBlockSymbolProblem();
	private static AxiomaticBlockSymbolProblem axiomaticBlockSymbolProblem = new AxiomaticBlockSymbolProblem();
	private static TheoremSymbolProblem theoremSymbolProblem = new TheoremSymbolProblem();
	private static AxiomSymbolProblem axmSymbolProblem = new AxiomSymbolProblem();
	private static LocalMetavariableSymbolProblem localMetavariableSymbolProblem = new LocalMetavariableSymbolProblem();
	private static RewriteRuleSymbolProblem rewriteRuleSymbolProblem = new RewriteRuleSymbolProblem();
	private static InferenceRuleSymbolProblem inferenceRuleSymbolProblem = new InferenceRuleSymbolProblem();
	private static RhsSymbolProblem rhsSymbolProblem = new RhsSymbolProblem();

	public ILabelSymbolInfo makeLocalRewriteRule(String symbol,
			boolean persistent, IInternalElement problemElement,
			String component) {
		return new LabelSymbolInfo(symbol, ISCRewriteRule.ELEMENT_TYPE,
				persistent, problemElement, EventBAttributes.LABEL_ATTRIBUTE,
				component, rewriteRuleSymbolProblem);
	}
	
	public ILabelSymbolInfo makeLocalInferenceRule(String symbol,
			boolean persistent, IInternalElement problemElement,
			String component) {
		return new LabelSymbolInfo( symbol, ISCInferenceRule.ELEMENT_TYPE,
				persistent, problemElement, EventBAttributes.LABEL_ATTRIBUTE,
				component, inferenceRuleSymbolProblem);
	}

	public ILabelSymbolInfo makeLocalRulesBlock(String symbol,
			boolean persistent, IInternalElement problemElement,
			String component) {
		return new LabelSymbolInfo(symbol, ISCProofRulesBlock.ELEMENT_TYPE,
				persistent, problemElement, EventBAttributes.LABEL_ATTRIBUTE,
				component, rulesBlockSymbolProblem);
	}
	
	public ILabelSymbolInfo makeLocalAxiomaticBlock(String symbol,
			boolean persistent, IInternalElement problemElement,
			String component){
		return new LabelSymbolInfo(symbol, ISCAxiomaticDefinitionsBlock.ELEMENT_TYPE,
				persistent, problemElement, EventBAttributes.LABEL_ATTRIBUTE, component,
				axiomaticBlockSymbolProblem);
	}

	public ILabelSymbolInfo makeLocalTheorem(String symbol, boolean persistent,
			IInternalElement problemElement, String component) {
		return new LabelSymbolInfo(symbol, ISCTheorem.ELEMENT_TYPE, persistent,
				problemElement, EventBAttributes.LABEL_ATTRIBUTE, component,
				theoremSymbolProblem);
	}
	
	public ILabelSymbolInfo makeLocalAxiom(String symbol, boolean persistent,
			IInternalElement problemElement, String component) {
		return new LabelSymbolInfo(symbol, ISCAxiomaticDefinitionAxiom.ELEMENT_TYPE, persistent,
				problemElement, EventBAttributes.LABEL_ATTRIBUTE, component,
				axmSymbolProblem);
	}

	public IIdentifierSymbolInfo makeLocalOperatorArgument(String name,
			boolean persistent, IIdentifierElement element, String parentName) {
		return new IdentifierSymbolInfo(name, ISCOperatorArgument.ELEMENT_TYPE,
				persistent, element, EventBAttributes.IDENTIFIER_ATTRIBUTE,
				parentName, operatorArgumentSymbolProblem);
	}

	public ILabelSymbolInfo makeLocalOperator(String symbol,
			boolean persistent, IInternalElement problemElement,
			String component, boolean isAxiomatic) {
		if (isAxiomatic)
		{
			return new LabelSymbolInfo(symbol,
					ISCAxiomaticOperatorDefinition.ELEMENT_TYPE, persistent,
					problemElement, EventBAttributes.LABEL_ATTRIBUTE, component,
					operatorSynProblem); 
		}
		return new LabelSymbolInfo(symbol,
				ISCNewOperatorDefinition.ELEMENT_TYPE, persistent,
				problemElement, EventBAttributes.LABEL_ATTRIBUTE, component,
				operatorSynProblem);
	}

	public IIdentifierSymbolInfo makeLocalTypeParameter(String symbol,
			boolean persistent, IInternalElement problemElement,
			String component) {
		return new IdentifierSymbolInfo(symbol, ISCTypeParameter.ELEMENT_TYPE,
				persistent, problemElement,
				EventBAttributes.IDENTIFIER_ATTRIBUTE, component,
				localTypeParameterSymbolProblem);
	}

	public IIdentifierSymbolInfo makeLocalMetavariable(String symbol,
			boolean persistent, IInternalElement problemElement,
			String component) {
		return new IdentifierSymbolInfo(symbol, ISCMetavariable.ELEMENT_TYPE,
				persistent, problemElement,
				EventBAttributes.IDENTIFIER_ATTRIBUTE, component,
				localMetavariableSymbolProblem);
	}

	public ILabelSymbolInfo makeLocalRHS(String symbol, boolean persistent,
			IInternalElement problemElement, String component) {
		return new LabelSymbolInfo(symbol,
				ISCRewriteRuleRightHandSide.ELEMENT_TYPE, persistent,
				problemElement, EventBAttributes.LABEL_ATTRIBUTE, component,
				rhsSymbolProblem);
	}

	public static TheorySymbolFactory getInstance() {
		return factory;
	}

	private static class LocalTypeParameterSymbolProblem extends
			TypeParameterSymbolProblem {

		public LocalTypeParameterSymbolProblem() {
			// public constructor
		}

		public void createConflictError(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(symbolInfo.getProblemElement(),
					symbolInfo.getProblemAttributeType(),
					TheoryGraphProblem.TypeParameterNameConflictError,
					symbolInfo.getSymbol());
		}

		public void createConflictWarning(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			// should not be needed, all such problems are error
		}

	}

	private static class LocalMetavariableSymbolProblem extends
			MetavariableSymbolProblem {

		public LocalMetavariableSymbolProblem() {
			// public constructor
		}

		public void createConflictError(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(symbolInfo.getProblemElement(),
					symbolInfo.getProblemAttributeType(),
					TheoryGraphProblem.MetavariableNameConflictError,
					symbolInfo.getSymbol());
		}

		public void createConflictWarning(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			// should not be needed, all such problems are error
		}

	}

	private static class LocalOperatorArgumentSymbolProblem extends
			OperatorArgumentSymbolProblem {

		public LocalOperatorArgumentSymbolProblem() {
			// public constructor
		}

		public void createConflictError(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(symbolInfo.getProblemElement(),
					symbolInfo.getProblemAttributeType(),
					TheoryGraphProblem.OperatorArgumentNameConflictError,
					symbolInfo.getSymbol());
		}

		public void createConflictWarning(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			// should not be needed, all such problems are error
		}

	}

	private static class InferenceRuleSymbolProblem implements ISymbolProblem {

		public InferenceRuleSymbolProblem() {
			// public constructor
		}

		public void createConflictError(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(symbolInfo.getProblemElement(),
					symbolInfo.getProblemAttributeType(),
					TheoryGraphProblem.InferenceRuleLabelConflictError,
					symbolInfo.getSymbol());

		}

		public void createConflictWarning(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			// should not be needed, all such problems are error

		}
	}
	private static class RewriteRuleSymbolProblem implements ISymbolProblem {

		public RewriteRuleSymbolProblem() {
			// public constructor
		}

		public void createConflictError(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(symbolInfo.getProblemElement(),
					symbolInfo.getProblemAttributeType(),
					TheoryGraphProblem.RewriteRuleLabelConflictError,
					symbolInfo.getSymbol());

		}

		public void createConflictWarning(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			// should not be needed, all such problems are error

		}

	}

	private static class RulesBlockSymbolProblem implements ISymbolProblem {

		public RulesBlockSymbolProblem() {
			// public constructor
		}

		public void createConflictError(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(symbolInfo.getProblemElement(),
					symbolInfo.getProblemAttributeType(),
					TheoryGraphProblem.RulesBlockLabelProblemError,
					symbolInfo.getSymbol());

		}

		public void createConflictWarning(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			// should not be needed, all such problems are error

		}

	}
	
	private static class AxiomaticBlockSymbolProblem implements ISymbolProblem {

		public AxiomaticBlockSymbolProblem() {
			// public constructor
		}

		public void createConflictError(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(symbolInfo.getProblemElement(),
					symbolInfo.getProblemAttributeType(),
					TheoryGraphProblem.AxiomaticBlockLabelProblemError,
					symbolInfo.getSymbol());

		}

		public void createConflictWarning(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			// should not be needed, all such problems are error

		}

	}

	private static class TheoremSymbolProblem implements ISymbolProblem {

		public TheoremSymbolProblem() {
			// public constructor
		}

		public void createConflictError(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(symbolInfo.getProblemElement(),
					symbolInfo.getProblemAttributeType(),
					TheoryGraphProblem.TheoremLabelProblemError,
					symbolInfo.getSymbol());

		}

		public void createConflictWarning(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			// should not be needed, all such problems are error

		}

	}

	private static class AxiomSymbolProblem implements ISymbolProblem {

		public AxiomSymbolProblem() {
			// public constructor
		}

		public void createConflictError(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(symbolInfo.getProblemElement(),
					symbolInfo.getProblemAttributeType(),
					TheoryGraphProblem.AxiomLabelProblemError,
					symbolInfo.getSymbol());

		}

		public void createConflictWarning(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			// should not be needed, all such problems are error

		}

	}
	
	private static class OperatorSynSymbolProblem implements ISymbolProblem {

		public OperatorSynSymbolProblem() {
			// public constructor
		}

		public void createConflictError(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(symbolInfo.getProblemElement(),
					symbolInfo.getProblemAttributeType(),
					TheoryGraphProblem.OperatorSynConflictError,
					symbolInfo.getSymbol());

		}

		public void createConflictWarning(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			// should not be needed, all such problems are error
		}

	}

	private abstract static class TypeParameterSymbolProblem implements
			ITypedSymbolProblem {

		public TypeParameterSymbolProblem() {
			// public constructor
		}

		public IRodinProblem getUntypedError() {
			return TheoryGraphProblem.UntypedTypeParameterError;
		}

	}

	private abstract static class MetavariableSymbolProblem implements
			ITypedSymbolProblem {

		public MetavariableSymbolProblem() {
			// public constructor
		}

		public IRodinProblem getUntypedError() {
			return TheoryGraphProblem.UntypedMetavariableError;
		}

	}

	private abstract static class OperatorArgumentSymbolProblem implements
			ITypedSymbolProblem {

		public OperatorArgumentSymbolProblem() {
			// public constructor
		}

		public IRodinProblem getUntypedError() {
			return TheoryGraphProblem.UntypedOperatorArgumentError;
		}

	}

	private static class RhsSymbolProblem implements ISymbolProblem {

		public RhsSymbolProblem() {
			// public constructor
		}

		public void createConflictError(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(symbolInfo.getProblemElement(),
					symbolInfo.getProblemAttributeType(),
					TheoryGraphProblem.RhsLabelConflictError,
					symbolInfo.getSymbol());
		}

		public void createConflictWarning(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			// should not be needed, all such problems are error
		}

	}
}
