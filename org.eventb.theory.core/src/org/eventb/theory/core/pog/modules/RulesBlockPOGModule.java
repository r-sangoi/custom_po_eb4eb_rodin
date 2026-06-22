/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.pog.modules;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.extensions.maths.AstUtilities;
import org.eventb.core.pog.POGCore;
import org.eventb.core.pog.POGProcessorModule;
import org.eventb.core.pog.state.IPOGStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.theory.core.ISCMetavariable;
import org.eventb.theory.core.ISCProofRulesBlock;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;

/**
 * @author maamria
 * 
 */
public class RulesBlockPOGModule extends POGProcessorModule {

	private final IModuleType<RulesBlockPOGModule> MODULE_TYPE = POGCore
			.getModuleType(TheoryPlugin.PLUGIN_ID + ".rulesBlockPOGModule"); //$NON-NLS-1$
	private ITypeEnvironmentBuilder typeEnvironment;
	private FormulaFactory factory;

	@Override
	public void initModule(IRodinElement element,
			IPOGStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		super.initModule(element, repository, monitor);
		typeEnvironment = repository.getTypeEnvironment();
		factory = repository.getFormulaFactory();
	}

	@Override
	public void process(IRodinElement element, IPOGStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		IRodinFile scTheoryFile = (IRodinFile) element;
		ISCTheoryRoot root = (ISCTheoryRoot) scTheoryFile.getRoot();
		ISCProofRulesBlock[] rulesBlocks = root.getProofRulesBlocks();
		for (ISCProofRulesBlock rulesBlock : rulesBlocks) {
			ITypeEnvironmentBuilder localEnvironment = AstUtilities
					.getTypeEnvironmentForFactory(typeEnvironment, factory);
			ISCMetavariable[] metavariables = rulesBlock.getMetavariables();
			for (ISCMetavariable var : metavariables) {
				localEnvironment.addName(var.getIdentifierString(),
						var.getType(factory));
			}
			repository.setTypeEnvironment(localEnvironment);
			{
				initProcessorModules(rulesBlock, repository, monitor);
				processModules(rulesBlock, repository, monitor);
				endProcessorModules(rulesBlock, repository, monitor);
			}
			repository.setTypeEnvironment(typeEnvironment);
		}

	}

	@Override
	public void endModule(IRodinElement element,
			IPOGStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		typeEnvironment = null;
		factory = null;
		super.endModule(element, repository, monitor);
	}

	@Override
	public IModuleType<?> getModuleType() {
		// TODO Auto-generated method stub
		return MODULE_TYPE;
	}

}
