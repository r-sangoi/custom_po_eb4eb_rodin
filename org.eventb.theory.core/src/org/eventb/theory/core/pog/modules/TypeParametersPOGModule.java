/*******************************************************************************
 * Copyright (c) 2006, 2008 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *     University of Dusseldorf - added theorem attribute
 *******************************************************************************/
package org.eventb.theory.core.pog.modules;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPOIdentifier;
import org.eventb.core.IPOPredicateSet;
import org.eventb.core.IPORoot;
import org.eventb.core.ISCIdentifierElement;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.Type;
import org.eventb.core.pog.POGCore;
import org.eventb.core.pog.POGProcessorModule;
import org.eventb.core.pog.state.IPOGStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.ISCTypeParameter;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;


/**
 * @author Stefan Hallerstede
 *
 */
public class TypeParametersPOGModule extends POGProcessorModule {

	public static final String ABS_HYP_NAME = "ABSHYP";
	
	private final IModuleType<TypeParametersPOGModule> MODULE_TYPE = 
		POGCore.getModuleType(TheoryPlugin.PLUGIN_ID + ".typeParametersPOGModule"); //$NON-NLS-1$
	
	@Override
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}
	
	private IPORoot target;
	private ITypeEnvironmentBuilder typeEnvironment;
	private FormulaFactory factory;
	
	@Override
	public void process(IRodinElement element, IPOGStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initModule(
			IRodinElement element, 
			IPOGStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		super.initModule(element, repository, monitor);
		factory = repository.getFormulaFactory();
		typeEnvironment = repository.getTypeEnvironment();
		
		IRodinFile scTheoryFile = (IRodinFile) element;
		ISCTheoryRoot scTheoryRoot = (ISCTheoryRoot) scTheoryFile.getRoot();
		target = repository.getTarget();
		
		IPOPredicateSet rootSet = target.getPredicateSet(ABS_HYP_NAME);
		rootSet.create(null, monitor);
		fetchTypeParameters(scTheoryRoot, rootSet, monitor);
	}

	protected void fetchTypeParameters(
			ISCTheoryRoot theory, 
			IPOPredicateSet rootSet, 
			IProgressMonitor monitor) throws CoreException {
		for (ISCTypeParameter set : theory.getSCTypeParameters()) {
			FreeIdentifier identifier = fetchIdentifier(set);
			createIdentifier(rootSet, identifier, monitor);
		}
	}

	protected void createIdentifier(
			IPOPredicateSet predSet, 
			FreeIdentifier identifier, 
			IProgressMonitor monitor) throws RodinDBException {
		String idName = identifier.getName();
		Type type = identifier.getType();
		IPOIdentifier poIdentifier = predSet.getIdentifier(idName);
		poIdentifier.create(null, monitor);
		poIdentifier.setType(type, monitor);
	}

	protected FreeIdentifier fetchIdentifier(ISCIdentifierElement ident) throws CoreException {
		FreeIdentifier identifier = ident.getIdentifier(factory);
		typeEnvironment.add(identifier);
		return identifier;
	}

	@Override
	public void endModule(
			IRodinElement element, 
			IPOGStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		target = null;
		typeEnvironment = null;
		factory = null;
		super.endModule(element, repository, monitor);
	}

	
}
