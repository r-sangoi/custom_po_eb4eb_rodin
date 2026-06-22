/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.sc.states;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.ast.extension.IOperatorProperties.FormulaType;
import org.eventb.core.ast.extension.IOperatorProperties.Notation;
import org.eventb.core.ast.extension.IPredicateExtension;
import org.eventb.core.ast.extensions.maths.AstUtilities;
import org.eventb.core.ast.extensions.maths.Definition;
import org.eventb.core.ast.extensions.maths.DirectDefinition;
import org.eventb.core.ast.extensions.maths.MathExtensionsFactory;
import org.eventb.core.ast.extensions.maths.OperatorExtensionProperties;
import org.eventb.core.ast.extensions.maths.RecursiveDefinition;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.state.ISCState;
import org.eventb.core.tool.IStateType;
import org.eventb.internal.core.tool.state.State;
import org.eventb.theory.core.IApplicabilityElement.RuleApplicability;
import org.eventb.theory.core.INewOperatorDefinition;
import org.eventb.theory.core.ISCMetavariable;
import org.eventb.theory.core.ISCProofRulesBlock;
import org.eventb.theory.core.ISCRewriteRule;
import org.eventb.theory.core.ISCRewriteRuleRightHandSide;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.rodinp.core.RodinDBException;

/**
 * A simple implementation of an operator information state.
 * 
 * @author maamria
 * 
 */
@SuppressWarnings("restriction")
public class OperatorInformation extends State implements ISCState{

	private static final Predicate[] NO_PREDICATES = new Predicate[0];

	private final String operatorID;
	private String syntax;
	private FormulaType formulaType;
	private Notation notation;
	private boolean isAssociative = false;
	private boolean isCommutative = false;
	private List<Predicate> wdConditions;
	private Map<String, Type> opArguments;
	private Type expressionType;
	private Definition definition;
	
	private Predicate dWDCondition;

	private final FormulaFactory factory;

	private IFormulaExtension formulaExtension = null;

	private boolean hasError = false;

	private final ITypeEnvironmentBuilder typeEnvironment;

	public final static IStateType<OperatorInformation> STATE_TYPE = SCCore
			.getToolStateType(TheoryPlugin.PLUGIN_ID + ".operatorInformation");

	public OperatorInformation(String operatorID, ITypeEnvironment typeEnvironment) {
		this.operatorID = operatorID;
		this.opArguments = new LinkedHashMap<String, Type>();
		this.wdConditions = new ArrayList<Predicate>();
		this.typeEnvironment = typeEnvironment.makeBuilder();
		this.factory = this.typeEnvironment.getFormulaFactory();
	}
	
	@Override
	public void makeImmutable() {
		opArguments = Collections.unmodifiableMap(opArguments);
		wdConditions = Collections.unmodifiableList(wdConditions);
		super.makeImmutable();
	}

	public boolean isAllowedIdentifier(FreeIdentifier ident) throws CoreException {
		assertMutable();
		return typeEnvironment.contains(ident);
	}

	public boolean isExpressionOperator() {
		return formulaType.equals(FormulaType.EXPRESSION);
	}

	/**
	 * @return the syntax
	 */
	public String getSyntax() {
		return syntax;
	}

	/**
	 * @param syntax
	 *            the syntax to set
	 * @throws CoreException 
	 */
	public void setSyntax(String syntax) throws CoreException {
		assertMutable();
		this.syntax = syntax;
	}

	/**
	 * @return the formulaType
	 */
	public FormulaType getFormulaType() {
		return formulaType;
	}

	/**
	 * @param formulaType
	 *            the formulaType to set
	 * @throws CoreException 
	 */
	public void setFormulaType(FormulaType formulaType) throws CoreException {
		assertMutable();
		this.formulaType = formulaType;
	}

	/**
	 * @param notation
	 *            the notation to set
	 * @throws CoreException 
	 */
	public void setNotation(Notation notation) throws CoreException {
		assertMutable();
		this.notation = notation;
	}

	/**
	 * @param isAssociative
	 *            the isAssociative to set
	 * @throws CoreException 
	 */
	public void setAssociative(boolean isAssociative) throws CoreException {
		assertMutable();
		this.isAssociative = isAssociative;
	}

	/**
	 * @param isCommutative
	 *            the isCommutative to set
	 * @throws CoreException 
	 */
	public void setCommutative(boolean isCommutative) throws CoreException {
		assertMutable();
		this.isCommutative = isCommutative;
	}

	/**
	 * @return the wdCondition
	 */
	public Predicate getWdCondition() {
		return AstUtilities.conjunctPredicates(wdConditions, factory);
	}

	/**
	 * @param wdCondition
	 *            the wdCondition to set
	 * @throws CoreException 
	 */
	public void addWDCondition(Predicate wdCondition) throws CoreException {
		assertMutable();
		wdConditions.add(wdCondition);
	}

	public Type getResultantType() {
		return expressionType;
	}
	
	public Predicate getD_WDCondition() {
		return dWDCondition;
	}

	public void setD_WDCondition(Predicate dWDCondition) throws CoreException {
		assertMutable();
		this.dWDCondition = dWDCondition;
	}

	public void addOperatorArgument(String ident, Type type) throws CoreException {
		assertMutable();
		if (!opArguments.containsKey(ident)) {
			for (GivenType gtype : AstUtilities.getGivenTypes(type)) {
				typeEnvironment.addGivenSet(gtype.getName());
			}
			typeEnvironment.addName(ident, type);
			opArguments.put(ident, type);
		}

	}

	public void setHasError() throws CoreException {
		assertMutable();
		this.hasError = true;
	}

	public boolean hasError() {
		return hasError;
	}

	public void setDefinition(Definition definition) throws CoreException {
		assertMutable();
		this.definition = definition;
	}

	public IFormulaExtension getExtension(Object sourceOfExtension) throws CoreException {
		assertImmutable();
		if (!hasError) {
			OperatorExtensionProperties properties = new OperatorExtensionProperties(operatorID, syntax, formulaType, notation, null);
			if(dWDCondition == null){
				dWDCondition = getWdCondition();
			}
			if (formulaType.equals(FormulaType.EXPRESSION)) {
//				formulaExtension = MathExtensionsFactory.getExpressionExtension(properties, isCommutative, isAssociative, 
//						opArguments, expressionType, getWdCondition(), dWDCondition, definition, sourceOfExtension);
				formulaExtension = MathExtensionsFactory.getExpressionExtension(properties, isCommutative, isAssociative, 
						opArguments, expressionType, getWdCondition(), makeBTRUE(), definition, sourceOfExtension);
			} else {
//				formulaExtension = MathExtensionsFactory.getPredicateExtension(properties, isCommutative, opArguments,
//						getWdCondition(), dWDCondition, definition,sourceOfExtension);
				formulaExtension = MathExtensionsFactory.getPredicateExtension(properties, isCommutative, opArguments,
						getWdCondition(), makeBTRUE(), definition,sourceOfExtension);
			}
			return formulaExtension;
		} else
			return null;

	}

	public IFormulaExtension getInterimExtension() throws CoreException {
		assertMutable();
		IFormulaExtension formulaExtension = null;
		OperatorExtensionProperties properties = new OperatorExtensionProperties(operatorID, syntax, formulaType, notation, null);
		if (expressionType != null) {
			/*
			 * DWD condition is set to true as a temporary solution, since we commented the WD PO for the inf rules 
			 * and DWD condition would not be used else where.
			 */
//			formulaExtension = MathExtensionsFactory.getExpressionExtension(properties, isCommutative, isAssociative, 
//					opArguments, expressionType, getWdCondition(), getWdCondition(), null, null);
			formulaExtension = MathExtensionsFactory.getExpressionExtension(properties, isCommutative, isAssociative, 
					opArguments, expressionType, getWdCondition(), makeBTRUE(), null, null);
		} else {
//			formulaExtension = MathExtensionsFactory.getPredicateExtension(properties, isCommutative, opArguments,
//					getWdCondition(), getWdCondition(), null, null);
			formulaExtension = MathExtensionsFactory.getPredicateExtension(properties, isCommutative, opArguments,
					getWdCondition(), makeBTRUE(), null, null);
		}
		return formulaExtension;
	}

	public void setResultantType(Type resultantType) throws CoreException {
		assertMutable();
		this.expressionType = resultantType;
	}

	public void generateDefinitionalRule(INewOperatorDefinition originDefinition, ISCTheoryRoot theoryRoot, FormulaFactory enhancedFactory) throws CoreException {
		assertImmutable();
		// a rule block
		ISCProofRulesBlock newRulesbBlock = theoryRoot.getProofRulesBlock("generatedBlock");
		if (!newRulesbBlock.exists()) {
			newRulesbBlock.create(null, null);
			newRulesbBlock.setSource(originDefinition, null);
		}
		Map<FreeIdentifier, Expression> possibleSubstitution = new HashMap<FreeIdentifier, Expression>();
		for (String arg : opArguments.keySet()) {
			Type argType = opArguments.get(arg).translate(enhancedFactory);
			FreeIdentifier argIdent = enhancedFactory.makeFreeIdentifier(arg, null, argType);
			ISCMetavariable var = newRulesbBlock.getMetavariable(arg);
			while (var.exists() && !var.getType(enhancedFactory).equals(argType)) {
				String newName = var.getIdentifierString() + "_";
				var = newRulesbBlock.getMetavariable(newName);	
			}
			possibleSubstitution.put(argIdent, enhancedFactory.makeFreeIdentifier(var.getIdentifierString(), null, argType));
			if (!var.exists()) {
				var.create(null, null);
				var.setType(argType, null);
				var.setSource(originDefinition, null);
			}
		}
		// one rewrite rule
		if (definition instanceof DirectDefinition) {
			ISCRewriteRule rewRule = createRewriteRule(newRulesbBlock, operatorID, syntax + " expansion");
			rewRule.setSource(originDefinition, null);
			Formula<?> lhs = makeLhs(enhancedFactory).substituteFreeIdents(possibleSubstitution);
			rewRule.setSCFormula(lhs, null);
			ISCRewriteRuleRightHandSide rhs = rewRule.getRuleRHS(syntax + " rhs");
			rhs.create(null, null);
			rhs.setLabel(syntax + " rhs", null);
			rhs.setPredicate(makeBTRUE(), null);
			rhs.setSCFormula(((DirectDefinition) definition).getDefinition().translate(enhancedFactory).substituteFreeIdents(possibleSubstitution), null);
			rhs.setSource(originDefinition, null);
		} else if (definition instanceof RecursiveDefinition) {
			RecursiveDefinition recursiveDefinition = (RecursiveDefinition) definition;
			Map<Expression, Formula<?>> recursiveCases = recursiveDefinition.getRecursiveCases();
			FreeIdentifier inductiveIdent = (FreeIdentifier) recursiveDefinition.getOperatorArgument().translate(enhancedFactory);
			int index = 0;
			for (Expression indCase : recursiveCases.keySet()) {
				ISCRewriteRule rewRule = createRewriteRule(newRulesbBlock, operatorID + " case " + index++, syntax + " expansion");
				rewRule.setSource(originDefinition, null);
				for (FreeIdentifier identifier : indCase.getFreeIdentifiers()) {
					String name = identifier.getName();
					Type type = identifier.getType();
					type = type.translate(enhancedFactory);
					if (!AstUtilities.isGivenSet(typeEnvironment, name)) {
						ISCMetavariable scVar = newRulesbBlock.getMetavariable(name);
						while (scVar.exists() && !scVar.getType(enhancedFactory).equals(type)) {
							String newName = scVar.getIdentifierString() + "_";
							scVar = newRulesbBlock.getMetavariable(newName);
							possibleSubstitution.put(identifier, enhancedFactory.makeFreeIdentifier(newName, null, type));
						}
						if (!scVar.exists()) {
							scVar.create(null, null);
							scVar.setType(type, null);
							scVar.setSource(originDefinition, null);
						}

					}
				}
				Formula<?> lhs = makeLhs(enhancedFactory).substituteFreeIdents(possibleSubstitution);
				Map<FreeIdentifier, Expression> indSub = new HashMap<FreeIdentifier, Expression>();
				// substitute the free identifier for the inductive var so that it reflects the namings above
				// FIXED BUG
				FreeIdentifier newInductiveIdentifier = (FreeIdentifier) inductiveIdent.substituteFreeIdents(
						possibleSubstitution);
				indSub.put(newInductiveIdentifier, indCase.translate(enhancedFactory));
				// substitute the inductive ident if necessary
				lhs = lhs.substituteFreeIdents(indSub);
				// substitute the vars of the inductive case if necessary
				lhs = lhs.substituteFreeIdents(possibleSubstitution);
				rewRule.setSCFormula(lhs, null);
				ISCRewriteRuleRightHandSide rhs = rewRule.getRuleRHS(syntax + " rhs");
				rhs.create(null, null);
				rhs.setSource(originDefinition, null);
				rhs.setLabel(syntax + " rhs", null);
				rhs.setPredicate(makeBTRUE(), null);
				// get the rhs 
				Formula<?> indCaseDefinitionFormula = recursiveCases.get(indCase).translate(enhancedFactory);
				// apply substitution if necessary
				indCaseDefinitionFormula = indCaseDefinitionFormula.substituteFreeIdents(possibleSubstitution);
				rhs.setSCFormula(indCaseDefinitionFormula, null);
			}
		}

	}

	public Map<String, Type> getOperatorArguments() {
		return Collections.unmodifiableMap(opArguments);
	}

	public Notation getNotation() {
		return notation;
	}

	@Override
	public IStateType<?> getStateType() {
		return OperatorInformation.STATE_TYPE;
	}

	private ISCRewriteRule createRewriteRule(ISCProofRulesBlock newRulesbBlock, String name, String description) throws RodinDBException {
		ISCRewriteRule rewRule = newRulesbBlock.getRewriteRule(name);
		rewRule.create(null, null);
		rewRule.setDefinitional(true, null);
		rewRule.setApplicability(RuleApplicability.INTERACTIVE, null);
		rewRule.setComplete(true, null);
		rewRule.setDescription(description, null);
		rewRule.setLabel(name, null);
		rewRule.setAccuracy(true, null);
		return rewRule;
	}

	/**
	 * Returns the formula corresponding to the lhs of the definitional rule
	 * e.g., op(a1,a2).
	 * 
	 * @param ff
	 *            the formula factory with <code>formulaExtension</code> added
	 * @return the lhs formula
	 */
	private Formula<?> makeLhs(FormulaFactory ff) {
		Formula<?> lhs = null;
		Expression[] childExpressionsForLhs = getChildExpressionsForLhs(ff);
		if (isExpressionOperator()) {
			lhs = ff.makeExtendedExpression((IExpressionExtension) formulaExtension, childExpressionsForLhs, NO_PREDICATES, null);
			// FIXED Bug in case of generic operator (e.g., {}), this is done to
			// type check the left hand side
			if (definition instanceof DirectDefinition && childExpressionsForLhs.length == 0) {
				DirectDefinition directDefintion = (DirectDefinition) definition;
				Expression expressionDefinition = (Expression) directDefintion.getDefinition().translate(ff);
				RelationalPredicate typeCheckPredicate = ff.makeRelationalPredicate(Formula.EQUAL, (Expression) lhs, expressionDefinition, null);
				ITypeEnvironment tEnv = typeEnvironment.translate(ff);
				ITypeCheckResult typeCheckResult = typeCheckPredicate.typeCheck(tEnv);
				if (typeCheckResult.hasProblem()){
					throw new IllegalStateException("Could not type check left hand side of rule for statically checked operator.");
				}
				lhs = typeCheckPredicate.getLeft();
			}

		} else {
			lhs = ff.makeExtendedPredicate((IPredicateExtension) formulaExtension, childExpressionsForLhs, NO_PREDICATES, null);
		}
		return lhs;
	}

	/**
	 * Returns the child expressions corresponding to the operator arguments.
	 * 
	 * @param ff
	 *            the formula factory with <code>formulaExtension</code> added
	 * @return the child expressions
	 */
	private Expression[] getChildExpressionsForLhs(FormulaFactory ff) {
		Expression[] exps = new Expression[opArguments.size()];
		int i = 0;
		for (String name : opArguments.keySet()){
			exps[i] = ff.makeFreeIdentifier(name, null, opArguments.get(name).translate(ff));
			i ++;
		}
		return exps;
	}

	private Predicate makeBTRUE() {
		return AstUtilities.makeBTRUE(factory);
	}
}
