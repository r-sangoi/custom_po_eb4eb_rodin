/*******************************************************************************
 * Copyright (c) 2010, 2021 University of Southampton and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.utils;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eventb.core.IEventBRoot;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.ISpecialization;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.UntranslatableException;
import org.eventb.theory.core.DatabaseUtilities;
import org.eventb.theory.core.IReasoningTypeElement.ReasoningType;
import org.eventb.theory.core.ISCInferenceRule;
import org.eventb.theory.core.ISCMetavariable;
import org.eventb.theory.core.ISCProofRulesBlock;
import org.eventb.theory.core.ISCRewriteRule;
import org.eventb.theory.core.ISCRewriteRuleRightHandSide;
import org.eventb.theory.core.ISCRule;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.ISCTypeParameter;
import org.eventb.theory.rbp.plugin.RbPPlugin;
import org.eventb.theory.rbp.rulebase.IPOContext;
import org.eventb.theory.rbp.rulebase.basis.IDeployedGiven;
import org.eventb.theory.rbp.rulebase.basis.IDeployedInferenceRule;
import org.eventb.theory.rbp.rulebase.basis.IDeployedRule;
import org.eventb.theory.rbp.rulebase.basis.POContext;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

/**
 * Some utilities used by RbP.
 * 
 * @author maamria
 * @author htson - added utility methods
 * @version 1.2
 * @since 1.0
 */
public class ProverUtilities {

	public static boolean DEBUG = true;

	/**
	 * <p>
	 * Merges all the lists of rules in the <code>Map</code>
	 * <code>allRules</code>.
	 * </p>
	 * 
	 * @param allRules
	 * @return the merged list
	 */
	public static <E, F extends IDeployedRule> List<F> mergeLists(Map<E, List<F>> allRules) {
		List<F> result = new ArrayList<F>();
		for (E key : allRules.keySet()) {
			result.addAll(allRules.get(key));
		}
		return result;
	}

	/**
	 * <p>
	 * Utility method to parse a string as a formula knowing beforehand whether
	 * it is a an expression or predicate.
	 * </p>
	 * <p>
	 * Use only for theory formulas.
	 * </p>
	 * 
	 * @param formStr
	 *            the formula string
	 * @param isExpression
	 *            whether to parse an expression or a predicate
	 * @return the parsed formula or <code>null</code> if there was an error
	 */
	public static Formula<?> parseFormula(String formStr,boolean isExpression, FormulaFactory factory) {
		Formula<?> form = null;
		if (isExpression) {
			IParseResult r = factory.parseExpressionPattern(formStr, null);
			form = r.getParsedExpression();
		} else {
			IParseResult r = factory.parsePredicatePattern(formStr, null);
			form = r.getParsedPredicate();
		}
		return form;
	}

	// to parse a Theory formula i.e. predicate or expression
	public static Formula<?> parseFormulaPattern(String formula, FormulaFactory factory) {
		IParseResult res = factory.parseExpressionPattern(formula, null);
		if (res.hasProblem()) {
			res = factory.parsePredicatePattern(formula, null);
			if (res.hasProblem()) {
				return null;
			} else
				return res.getParsedPredicate();
		} else
			return res.getParsedExpression();

	}

	/**
	 * <p>
	 * Utility to print items in a list in a displayable fashion.
	 * </p>
	 * <p>
	 * The return of this method will be of the shape: {<}item_0,...,item_n{>}
	 * </p>
	 * 
	 * @param items
	 * @return the displayable string
	 */
	public static String printListedItems(List<String> items) {
		if (items.size() == 0) {
			return "";
		}
		String result = "";
		int i = 0;
		for (String str : items) {
			if (i == 0) {

				result = str;
			} else {
				result += "," + str;
			}
			i++;
		}
		result = "<" + result + ">";
		return result;
	}

	/**
	 * <p>
	 * Checks whether two objects are of the same class.
	 * </p>
	 * 
	 * @param o1
	 * @param o2
	 * @return whether the two objects are of the same class
	 */
	public static boolean sameClass(Object o1, Object o2) {
		return o1.getClass().equals(o2.getClass());
	}

	/**
	 * Combines the hashcodes of the given objects.
	 * @param os the objects
	 * @return the combined hashcodes
	 */
	public static int combineHashCode(Object... os) {
		int result = 0;
		int i = 1;
		for (Object o : os) {
			result += o.hashCode() * i * 7;
			i++;
		}
		return result;
	}

	/**
	 * Returns the given list if it is not <code>null</code>, and en empty list otherwise.
	 * @param <E> the type of elements
	 * @param list the list
	 * @return a safe list
	 */
	public static <E> List<E> safeList(List<E> list) {
		if (list == null) {
			return new ArrayList<E>();
		}
		return list;
	}

	/**
	 * Returns the given collection if it is not <code>null</code>, and an empty collection otherwise.
	 * @param <E> the type of elements
	 * @param col the collection
	 * @return a safe collection
	 */
	public static <E> Collection<E> safeCollection(Collection<E> col) {
		if (col == null)
			return new LinkedHashSet<E>();
		return col;
	}

	/**
	 * Returns the integer that is represented in <code>string</code>.
	 * @param string the string representation
	 * @return the integer
	 */
	public static int parseInteger(String string) {
		try {
			int num = Integer.parseInt(string);
			return num;
		} catch (NumberFormatException e) {
			return -1;
		}

	}
	
	/**
	 * Returns a string representation of the given list of objects.
	 * 
	 * @param list
	 *            the list of strings
	 * @return the representing string
	 */
	public static <E> String toString(List<E> list) {
		String result = "";
		for (int i = 0; i < list.size(); i++) {
			result += list.get(i).toString();
			if (i < list.size() - 1) {
				result += ", ";
			}
		}
		return result;
	}
	
	/**
	 * Logs the given exception with the message.
	 * @param exc the exception
	 * @param message the message
	 */
	public static void log(Throwable exc, String message) {
		if (exc instanceof RodinDBException) {
			final Throwable nestedExc = ((RodinDBException) exc).getException();
			if (nestedExc != null) {
				exc = nestedExc;
			}
		}
		if (message == null) {
			message = "Unknown context"; //$NON-NLS-1$
		}
		IStatus status = new Status(IStatus.ERROR, RbPPlugin.PLUGIN_ID,
				IStatus.ERROR, message, exc);
		RbPPlugin.getDefault().getLog().log(status);
	}
	
	/**
	 * Checks whether the name <code>name</code> is a given set in the given
	 * type environment.
	 * 
	 * @param typeEnvironment
	 *            the type environment
	 * @param name
	 *            the name
	 * @return whether <code>name</code> is a given set
	 */
	public static boolean isGivenSet(ITypeEnvironment typeEnvironment, String name) {
		Type type = typeEnvironment.getType(name);
		if (type == null) {
			return false;
		}
		final Type baseType = type.getBaseType();
		if (baseType instanceof GivenType) {
			GivenType givenType = (GivenType) baseType;
			return givenType.getName().equals(name);
		}
		return false;
	}
	
	public static ITypeEnvironment makeTypeEnvironment(FormulaFactory factory, ISCRule rule) throws CoreException {
		ITypeEnvironmentBuilder typeEnvironment = factory.makeTypeEnvironment();
		try {
		ISCTheoryRoot SCtheoryRoot = DatabaseUtilities.getSCTheory(rule.getRoot().getElementName(), rule.getRoot().getRodinProject());
		ISCTypeParameter[] types = SCtheoryRoot.getSCTypeParameters();
		for (ISCTypeParameter par : types) {
			typeEnvironment.addGivenSet(par.getIdentifier(factory).getName());
		}
		
		//update typeEnv
		ISCProofRulesBlock block = ((ISCProofRulesBlock) rule.getParent());
		ISCMetavariable[] vars = block.getMetavariables();
		for (ISCMetavariable var : vars) {
			typeEnvironment.add(var.getIdentifier(factory));
		}
		
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return typeEnvironment;
	}
	
	public static boolean isConditional(ISCRewriteRule rule, FormulaFactory factory, ITypeEnvironment typeEnv) throws CoreException {
		boolean isCond = true;
		List<ISCRewriteRuleRightHandSide> ruleRHSs = Arrays.asList(rule.getRuleRHSs());
		if(ruleRHSs.size() == 1){
			ISCRewriteRuleRightHandSide rhs0 = ruleRHSs.get(0);
			Predicate cond = rhs0.getPredicate(typeEnv);
			if (cond.equals(factory.makeLiteralPredicate(Predicate.BTRUE, null))) {
				isCond = false;
			}
		}
		return isCond;
	}

	public static ReasoningType getReasoningType(ISCInferenceRule rule, boolean backward, boolean forward) {
		if(backward && forward)
			return ReasoningType.BACKWARD_AND_FORWARD;
		else if(forward)
			return ReasoningType.FORWARD;
		return ReasoningType.BACKWARD;
	}

	/**
	 * Utility method for getting the PO Context of a prover sequent.
	 * 
	 * @param sequent
	 *            the input prover sequent.
	 * @return the PO context corresponding to the input sequent or
	 *         <code>null</code> if the sequent does not have any context.
	 * @precondition the input sequent must NOT be <code>null</code>.
	 * @author htson
	 * @since 4.0
	 */
	public static IPOContext getContext(IProverSequent sequent) {
		// Assert precondition
		assert sequent != null;
		
		Object origin = sequent.getOrigin();
		if (origin instanceof IInternalElement) {
			IInternalElement root = ((IInternalElement) origin).getRoot();
			if (root instanceof IEventBRoot) {
				return new POContext((IEventBRoot) root);
			}
		}
		return null;
	}

	/**
	 * Utility method to check if a formula can be specialized with a
	 * specialization object.
	 * 
	 * @param specialization
	 *            the input specialization object.
	 * @param formula
	 *            the input formula
	 * @return <code>true</code> if the formula can be specialized with the
	 *         input specialization object. Return <code>false</code> otherwise.
	 * @precondition both input must NOT be <code>null</code>        
	 * @author htson
	 * @since 4.0
	 */
	public static boolean canBeSpecialized(ISpecialization specialization,
			Formula<?> formula) {
		// Assert precondition
		assert specialization != null;
		assert formula != null;
		
		for (FreeIdentifier identifier : formula.getFreeIdentifiers()) {
			if (specialization.get(identifier) == null)
				return false;
		}
		return true;
	}

	/**
	 * Utilities method to construct a forward reasoning for a given prover
	 * sequent with the inference rule and the specialization object resulting
	 * from matching the inference rule.
	 * 
	 * @param sequent
	 *            the input prover sequent
	 * @param rule
	 *            the (deployed) inference rule
	 * @param specialization
	 *            the specialization object
	 * @return an array of antecedents resulting from forward reasoning. The
	 *         first antecedent is the WD-subgoal for all instantiation
	 *         expressions of the specialization object. For each of given
	 *         condition (not required) of the rule, generate a corresponding
	 *         sub-goal. Finally, the infer clause of the rule is added as a
	 *         hypothesis for the final sub-goal.
	 * @precondition the specialization must be valid to instantiate the
	 *               inference rule.
	 * @author htson
	 * @since 4.0
	 */
	public static IAntecedent[] forwardReasoning(IProverSequent sequent,
			IDeployedInferenceRule rule, ISpecialization specialization) {
		List<IDeployedGiven> givens = rule.getGivens();
		List<IAntecedent> antecedents = new ArrayList<IAntecedent>(
				givens.size() + 2);		

		// Add the WD sub-goal
		Predicate wdPredicate = getWDPredicate(specialization);
		antecedents.add(ProverFactory.makeAntecedent(wdPredicate));
		
		// Generate a sub-goal for each given (not required) of the inference rule.
		Set<Predicate> addedHypotheses = new HashSet<Predicate>(
				Collections.singleton(wdPredicate));
		for (IDeployedGiven given : givens) {
			Predicate givenPred = given.getGivenClause();
			givenPred = givenPred.translate(specialization.getFactory());
			Predicate subGoal = givenPred.specialize(specialization);
			IAntecedent antecedent = ProverFactory.makeAntecedent(subGoal,
					addedHypotheses,
					ProverFactory.makeSelectHypAction(addedHypotheses));
			antecedents.add(antecedent);
		}

		// add the antecedent corresponding to the infer clause
		Predicate inferClause = rule.getInfer().getInferClause();
		Predicate newHyp = inferClause.specialize(specialization);
		addedHypotheses.add(newHyp);
		IAntecedent mainAntecedent = ProverFactory.makeAntecedent(null,
				addedHypotheses,
				ProverFactory.makeSelectHypAction(addedHypotheses));
		antecedents.add(mainAntecedent);
	
		return antecedents.toArray(new IAntecedent[antecedents.size()]);
	}

	/**
	 * Utilities method to construct a backward reasoning for a given prover
	 * sequent with the inference rule and the specialization object resulting
	 * from matching the inference rule.
	 * 
	 * @param sequent the input prover sequent.
	 * @param rule the input inference rule.
	 * @param specialization the specialization object.
	 * @return an array of antecedents resulting from backward reasoning. The
	 *         first antecedent is the WD-subgoal for all instantiation
	 *         expressions of the specialization object. For each of given
	 *         condition (not required) of the rule, a corresponding
	 *         sub-goal is generated.
	 * @precondition the specialization must be valid to instantiate the
	 *               inference rule.
	 * @author htson
	 * @since 4.0
	 */
	public static IAntecedent[] backwardReasoning(IProverSequent sequent,
			IDeployedInferenceRule rule, ISpecialization specialization) {
		List<IDeployedGiven> givens = rule.getGivens();
		Set<IAntecedent> antecedents = new LinkedHashSet<IAntecedent>(
				givens.size() + 1);

		// Add the WD sub-goal
		Predicate wdPredicate = getWDPredicate(specialization);
		antecedents.add(ProverFactory.makeAntecedent(wdPredicate));

		// Generate a sub-goal for each given (not required) of the inference rule.
		Set<Predicate> addedHypotheses = new HashSet<Predicate>(
				Collections.singleton(wdPredicate));
		for (IDeployedGiven given : givens) {
			Predicate givenPred = given.getGivenClause();
			givenPred = givenPred.translate(specialization.getFactory());
			Predicate subGoal = givenPred.specialize(specialization);
			antecedents.add(ProverFactory.makeAntecedent(subGoal, addedHypotheses,
					ProverFactory.makeSelectHypAction(addedHypotheses)));
		}
		return antecedents.toArray(new IAntecedent[antecedents.size()]);
	}

	/**
	 * Utility method to get a list of all hypotheses of a prover sequent.
	 * 
	 * @param sequent
	 *            the input prover sequent.
	 * @return the list of all hypotheses of the input sequent.
	 * @precondition the input must NOT be <code>null</code>.
	 * @author htson
	 * @since 4.0
	 */
	public static List<Predicate> getAllHypothesis(IProverSequent sequent) {
		// Assert PRECONDITION
		assert sequent != null;

		Iterable<Predicate> hypIterable = sequent.hypIterable();
		List<Predicate> hypotheses = new ArrayList<Predicate>();
		for (Predicate hyp : hypIterable)
			hypotheses.add(hyp);
		return hypotheses;
	}

	/**
	 * Utility method to get a list of givens (not required) of a deployed
	 * inference rule. Each given is translated to the input factory.
	 * 
	 * @param rule
	 *            the input deployed inference rule.
	 * @param factory
	 *            the formula factory
	 * @return the list of predicates correspond to the givens of the input
	 *         rule.
	 * @precondition the input must NOT be <code>null</code>.
	 * @throw {@link UntranslatableException} if any given is untranslatable.
	 * @see Formula#translate(FormulaFactory)
	 * @author htson
	 * @since 4.0
	 */
	public static List<Predicate> getGivenPredicates(
			FormulaFactory factory, IDeployedInferenceRule rule) {
		// Assert PRECONDITION
		assert rule != null;

		List<IDeployedGiven> hypGivens = rule.getHypGivens();
		List<Predicate> predicates = new ArrayList<Predicate>();
		for (IDeployedGiven hypGiven : hypGivens) {
			Predicate given = hypGiven.getGivenClause();
			given = given.translate(factory);
			predicates.add(given);
		}
		return predicates;
	}

	/**
	 * @param deployedRule
	 * @return
	 */
	public static Predicate canGivensBeSpecialized(
			IDeployedInferenceRule deployedRule, ISpecialization specialization) {
		List<IDeployedGiven> givens = deployedRule.getGivens();
		for (IDeployedGiven given : givens) {
			Predicate pred = given.getGivenClause();
			if (!ProverUtilities.canBeSpecialized(specialization, pred)) {
				return pred;
			}
		}
		return null;
	}

	/**
	 * @param specialization
	 * @return
	 */
	public static Set<Expression> getInstantiatingExpressions(
			ISpecialization specialization) {
		FreeIdentifier[] freeIdentifiers = specialization.getFreeIdentifiers();
		Set<Expression> expressions = new LinkedHashSet<Expression>(
				freeIdentifiers.length);
		for (FreeIdentifier freeIdentifier : freeIdentifiers) {
			expressions.add(specialization.get(freeIdentifier));
		}
		return expressions;
	}

	/**
	 * Utility method to return the WD predicate corresponding to a
	 * specialization object. This is the conjunction of the WD-predicates of
	 * all the instantiating expressions.
	 * 
	 * @param specialization
	 *            the input specialization.
	 * @return The WD predicate corresponding to the input specialization
	 *         object.
	 * @precondition the input must NOT be <code>null</code>
	 * @author htson
	 * @since 4.0
	 */
	public static Predicate getWDPredicate(ISpecialization specialization) {
		// Assert PRECONDITION
		assert specialization != null;
		
		Set<Expression> expressions = getInstantiatingExpressions(specialization);
		return getWDPredicate(specialization.getFactory(), expressions);
	}

	/**
	 * Utilities method to get a WD predicate of a set of expressions. The
	 * result is a conjunction of all WD predicate of each expression.
	 * 
	 * @param expressions
	 *            the input set of expressions.
	 * @return the WD predicate of the set of expressions.
	 * @author htson
	 * @since 4.0
	 */
	public static Predicate getWDPredicate(FormulaFactory factory,
			Set<Expression> expressions) {
		Set<Predicate> wdPredicates = new LinkedHashSet<Predicate>(
				expressions.size());
		for (Expression expression : expressions) {
			wdPredicates.add(expression.getWDPredicate());
		}

		if (wdPredicates.size() == 0) {
			return factory.makeLiteralPredicate(Predicate.BTRUE, null);
		}
		if (wdPredicates.size() == 1) {
			return wdPredicates.iterator().next();
		}
		return factory.makeAssociativePredicate(Predicate.LAND, wdPredicates,
				null);
	}

}
