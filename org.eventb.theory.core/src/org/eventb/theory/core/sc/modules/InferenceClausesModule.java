/*******************************************************************************
 * Copyright (c) 2010, 2020 University of Southampton and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.sc.modules;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.sc.GraphProblem;
import org.eventb.core.sc.SCProcessorModule;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.theory.core.IGiven;
import org.eventb.theory.core.IInferenceClause;
import org.eventb.theory.core.IInferenceRule;
import org.eventb.theory.core.ISCInferenceClause;
import org.eventb.theory.core.ISCInferenceRule;
import org.eventb.theory.core.sc.states.InferenceIdentifiers;
import org.eventb.theory.core.sc.states.RuleAccuracyInfo;
import org.eventb.theory.core.sc.states.TheoryAccuracyInfo;
import org.eventb.theory.core.util.CoreUtilities;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;

/**
 * @author maamria
 *
 */
@SuppressWarnings("restriction")
public abstract class InferenceClausesModule<C extends IInferenceClause, S extends ISCInferenceClause> 
extends SCProcessorModule{

	protected ITypeEnvironment typeEnvironment;
	protected FormulaFactory factory;
	protected InferenceIdentifiers inferenceIdentifiers;
	protected RuleAccuracyInfo ruleAccuracyInfo;
	private TheoryAccuracyInfo accuracyInfo;
	
	@Override
	public void process(IRodinElement element, IInternalElement target,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		IInferenceRule rule = (IInferenceRule) element;
		ISCInferenceRule scRule = (ISCInferenceRule) target;
		C[] clauses = getClauses(rule);
		if (checkClauses(clauses, rule)){
			processClauses(clauses, rule, scRule, repository, monitor);
		}
		else {
			ruleAccuracyInfo.setNotAccurate();
		}
	}
	
	/**
	 * Performs any necessary checks before processing the clauses.
	 * @param clauses the clauses to check
	 * @param rule the inference rule
	 * @return whether checks are successful
	 */
	protected abstract boolean checkClauses(C[] clauses, IInferenceRule rule) throws CoreException;
	
	protected final void processClauses(C[] clauses, IInferenceRule rule,
			ISCInferenceRule scRule, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException{
		boolean accurate= true;
		for(int i = 0 ; i < clauses.length; i++){
			C clause = clauses[i];
			if(!clause.hasPredicateString() || "".equals(clause.getPredicateString())){
				createProblemMarker(clause, EventBAttributes.PREDICATE_ATTRIBUTE, GraphProblem.PredicateUndefError);
				accurate = false;
				continue;
			}
			Predicate predicate = CoreUtilities.parseAndCheckPredicatePattern(clause, factory, typeEnvironment, this);
			if(predicate != null && checkPredicate(predicate, clause)){
				S scClause = createSCClause(predicate, clause, scRule, repository, monitor);
				if(scClause != null){
					scClause.setPredicate(predicate, monitor);
					// fixed absence of source of clauses shown by test TestAccuracy.testAcc_012/4
					scClause.setSource(clause, null);
					processSCClause(scClause, clause);
					if ((clause instanceof IGiven) && ((IGiven) clause).hasHypAttribute()) {
						if (((IGiven) clause).isHyp())
							addHypIdentifiers(predicate);
					}
					addIdentifiers(predicate);
				}
				else {
					accurate = false;
				}
			}
			else {
				accurate = false;
			}
		}
		if(!accurate) {
			ruleAccuracyInfo.setNotAccurate();
			accuracyInfo.setNotAccurate();
		}
		
	}

	/**
	 * Performs any last ditch processing on the SC clause.
	 * @param scClause the SC clause
	 * @param clause the clause
	 */
	protected abstract  void processSCClause(S scClause, C clause) throws CoreException;

	/**
	 * Checks the predicate for any restrictions placed on the predicate of the clause.
	 * @param predicate the predicate of the clause
	 * @param clause the inference clause
	 * @return whether the predicate passes the checks
	 * @throws CoreException
	 */
	protected abstract boolean checkPredicate(Predicate predicate, C clause) throws CoreException;

	/**
	 * Adds the free identifiers of the predicate to the appropriate collection (i.e., given identifiers or infer identifiers).
	 * @param predicate the clause predicate
	 * @throws CoreException
	 */
	protected abstract void addIdentifiers(Predicate predicate) throws CoreException;
	
	/**
	 * Adds the free identifiers of the predicate to the hyp identifiers collection
	 * @param predicate the clause predicate
	 * @throws CoreException
	 */
	protected abstract void addHypIdentifiers(Predicate predicate) throws CoreException;
	
	/**
	 * Returns a handle to the SC clause whose parent is <code>parent</code> and name is <code>name</code>.
	 * @param parent the parent SC inference rule
	 * @param name the name of the clause
	 * @return the SC clause
	 * @throws CoreException
	 */
	protected abstract S getSCClause(ISCInferenceRule parent, String name) throws CoreException;
	
	/**
	 * Returns the required clauses of the inference rule.
	 * @param rule the inference rule
	 * @return the required clauses
	 * @throws CoreException
	 */
	protected abstract C[] getClauses(IInferenceRule rule) throws CoreException;
	
	@Override
	public void initModule(
			IRodinElement element,
			ISCStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		super.initModule(element, repository, monitor);
		typeEnvironment = repository.getTypeEnvironment();
		factory = repository.getFormulaFactory();	
		inferenceIdentifiers = (InferenceIdentifiers) repository.getState(InferenceIdentifiers.STATE_TYPE);
		ruleAccuracyInfo = (RuleAccuracyInfo) repository.getState(RuleAccuracyInfo.STATE_TYPE);
		accuracyInfo = (TheoryAccuracyInfo) repository.getState(TheoryAccuracyInfo.STATE_TYPE);
	}
	

	@Override
	public void endModule(
			IRodinElement element,
			ISCStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		typeEnvironment = null;
		factory = null;
		inferenceIdentifiers = null;
		ruleAccuracyInfo = null;
		accuracyInfo = null;
		super.endModule(element, repository, monitor);
	}
	
	private S createSCClause(Predicate predicate, C clause,
			ISCInferenceRule scRule, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException{
		S scClause = getSCClause(scRule, clause.getElementName());
		scClause.create(null, monitor);
		return scClause;
		
	}

}
