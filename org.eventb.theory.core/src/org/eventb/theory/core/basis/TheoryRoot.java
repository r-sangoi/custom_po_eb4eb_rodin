/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.theory.core.basis;

import org.eventb.core.basis.EventBRoot;
import org.eventb.theory.core.DatabaseUtilities;
import org.eventb.theory.core.IAxiomaticDefinitionsBlock;
import org.eventb.theory.core.IDatatypeDefinition;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.IImportTheoryProject;
import org.eventb.theory.core.INewOperatorDefinition;
import org.eventb.theory.core.IProofRulesBlock;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.ITheorem;
import org.eventb.theory.core.ITheoryRoot;
import org.eventb.theory.core.ITypeParameter;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;
/**
 * 
 * @author maamria
 *
 */
public class TheoryRoot extends EventBRoot implements ITheoryRoot {

	public TheoryRoot(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public IInternalElementType<? extends IInternalElement> getElementType() {
		return ELEMENT_TYPE;
	}
	
	@Override
	public ITypeParameter getTypeParameter(String name) {
		return getInternalElement(ITypeParameter.ELEMENT_TYPE, name);
	}

	@Override
	public ITypeParameter[] getTypeParameters() throws RodinDBException {
		return getChildrenOfType(ITypeParameter.ELEMENT_TYPE);
	}

	
	@Override
	public INewOperatorDefinition getNewOperatorDefinition(String name) {
		return getInternalElement(INewOperatorDefinition.ELEMENT_TYPE, name);
	}

	@Override
	public INewOperatorDefinition[] getNewOperatorDefinitions()
			throws RodinDBException {
		return getChildrenOfType(INewOperatorDefinition.ELEMENT_TYPE);
	}

	@Override
	public IProofRulesBlock getProofRulesBlock(String name) {
		return getInternalElement(IProofRulesBlock.ELEMENT_TYPE, name);
	}

	@Override
	public IProofRulesBlock[] getProofRulesBlocks() throws RodinDBException {
		return getChildrenOfType(IProofRulesBlock.ELEMENT_TYPE);
	}

	@Override
	public IDatatypeDefinition getDatatypeDefinition(String name) {
		return getInternalElement(IDatatypeDefinition.ELEMENT_TYPE, name);
	}

	@Override
	public IDatatypeDefinition[] getDatatypeDefinitions()
			throws RodinDBException {
		return getChildrenOfType(IDatatypeDefinition.ELEMENT_TYPE);
	}

	@Override
	public ITheorem getTheorem(String name) {
		return getInternalElement(ITheorem.ELEMENT_TYPE, name);
	}

	@Override
	public ITheorem[] getTheorems() throws RodinDBException {
		return getChildrenOfType(ITheorem.ELEMENT_TYPE);
	}
	
	public IRodinFile getSCTheoryFile(String bareName) {
		String fileName = DatabaseUtilities.getSCTheoryFullName(bareName);
		IRodinFile file = getRodinProject().getRodinFile(fileName);
		return file;
	}

	public ISCTheoryRoot getSCTheoryRoot() {
		return getSCTheoryRoot(getElementName());
	}

	public ISCTheoryRoot getSCTheoryRoot(String bareName) {
		ISCTheoryRoot root = (ISCTheoryRoot) getSCTheoryFile(bareName)
				.getRoot();
		return root;
	}
	
	@Override
	public IRodinFile getDeployedTheoryFile(String bareName) {
		String fileName = DatabaseUtilities.getDeployedTheoryFullName(bareName);
		IRodinFile file = getRodinProject().getRodinFile(fileName);
		return file;
	}

	@Override
	public IDeployedTheoryRoot getDeployedTheoryRoot() {
		return getDeployedTheoryRoot(getElementName());
	}

	@Override
	public IDeployedTheoryRoot getDeployedTheoryRoot(String bareName) {
		IDeployedTheoryRoot root = (IDeployedTheoryRoot) getDeployedTheoryFile(
				bareName).getRoot();
		return root;
	}
	
	@Override
	public IImportTheoryProject getImportTheoryProject(String name) {
		return getInternalElement(IImportTheoryProject.ELEMENT_TYPE, name);
	}
	
	@Override
	public IImportTheoryProject[] getImportTheoryProjects()
			throws RodinDBException {
		return getChildrenOfType(IImportTheoryProject.ELEMENT_TYPE);
	}
	
	@Override
	public boolean hasDeployedVersion() {
		return getDeployedTheoryRoot().exists();
	}

	@Override
	public IAxiomaticDefinitionsBlock getAxiomaticDefinitionsBlock(String name) {
		return getInternalElement(IAxiomaticDefinitionsBlock.ELEMENT_TYPE, name);
	}

	@Override
	public IAxiomaticDefinitionsBlock[] getAxiomaticDefinitionsBlocks() throws RodinDBException {
		return getChildrenOfType(IAxiomaticDefinitionsBlock.ELEMENT_TYPE);
	}
}
