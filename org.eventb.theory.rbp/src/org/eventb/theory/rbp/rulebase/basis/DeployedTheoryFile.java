/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.rulebase.basis;

import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IEventBRoot;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.theory.core.IExtensionRulesSource;
import org.eventb.theory.core.IFormulaExtensionsSource;
import org.eventb.theory.core.ISCProofRulesBlock;
import org.eventb.theory.core.ISCTypeParameter;
import org.eventb.theory.rbp.utils.ProverUtilities;

/**
 * @author maamria
 * 
 */
public final class DeployedTheoryFile<R extends IEventBRoot & IFormulaExtensionsSource & IExtensionRulesSource>
		implements IDeployedTheoryFile {

	private FormulaFactory factory;
	private List<IDeployedRewriteRule> rewriteRules;
	private List<IDeployedInferenceRule> inferenceRules;
	private List<IDeployedTheorem> theorems; 

	private ITypeEnvironmentBuilder typeEnvironment;
	private R theoryRoot;

	/**
	 * <p>
	 * Constructs a deployed theory object with the given name (including
	 * extension).
	 * </p>
	 * 
	 * @param theoryRoot
	 *            the theory root
	 * @param factory
	 * 				the formula factory
	 */
	public DeployedTheoryFile(R theoryRoot, FormulaFactory factory) {
		this.theoryRoot = theoryRoot;
		this.factory = factory;
		this.typeEnvironment = factory.makeTypeEnvironment();
		this.rewriteRules = new ArrayList<IDeployedRewriteRule>();
		this.inferenceRules = new ArrayList<IDeployedInferenceRule>();
		this.theorems = new ArrayList<IDeployedTheorem>();
		loadTheory();
	}

	@Override
	public List<IDeployedRewriteRule> getRewriteRules() {
		return unmodifiableList(rewriteRules);
	}

	@Override
	public ITypeEnvironment getGloablTypeEnvironment() {
		return typeEnvironment.makeSnapshot();
	}

	@Override
	public List<IDeployedInferenceRule> getInferenceRules() {
		return unmodifiableList(inferenceRules);
	}

	@Override
	public String getTheoryName() {
		return theoryRoot.getComponentName();
	}

	@Override
	public boolean isEmpty() {
		return rewriteRules.size() + inferenceRules.size() == 0;
	}

	private void loadTheory() {
		try {
			ISCTypeParameter[] types = theoryRoot.getSCTypeParameters();
			for (ISCTypeParameter par : types) {
				typeEnvironment.addGivenSet(par.getIdentifier(factory)
						.getName());
			}
			theorems.addAll(DeployedObjectsFactory.getDeployedTheorems(theoryRoot, typeEnvironment));
			ISCProofRulesBlock[] blocks = theoryRoot.getProofRulesBlocks();
			for (ISCProofRulesBlock b : blocks) {
				rewriteRules.addAll(DeployedObjectsFactory.getDeployedRewriteRules(b, factory, typeEnvironment));
				inferenceRules.addAll(DeployedObjectsFactory.getDeployedInferenceRules(b, factory,typeEnvironment));
			}
		} catch (CoreException e) {
			// cleanup
			rewriteRules = new ArrayList<IDeployedRewriteRule>();
			inferenceRules = new ArrayList<IDeployedInferenceRule>();
			typeEnvironment = factory.makeTypeEnvironment();
			ProverUtilities.log(e, "unable to load theory properly");
		}

	}

	public String toString() {
		return theoryRoot.getComponentName() + "\n rew: " + rewriteRules
				+ "\n inf: " + inferenceRules + "\n";
	}

	@Override
	public List<IDeployedTheorem> getTheorems() {
		return unmodifiableList(theorems);
	}

}
