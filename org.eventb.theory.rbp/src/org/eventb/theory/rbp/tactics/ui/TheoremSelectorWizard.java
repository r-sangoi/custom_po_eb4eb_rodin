/*******************************************************************************
 * Copyright (c) 2011, 2020 University of Southampton and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.tactics.ui;

import java.util.List;

import org.eclipse.jface.wizard.Wizard;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.theory.internal.rbp.rulebase.TheoremsRetriever;
import org.eventb.theory.rbp.rulebase.IPOContext;

public class TheoremSelectorWizard extends Wizard {

	private TheoremSelectorWizardPageOne pageOne;
	private TheoremSelectorWizardPageTwo pageTwo;
	
	private TheoremsRetriever retriever;
	private ITypeEnvironment typeEnvironment;
	
	private List<String> theoremsToAdd = null;
	
	public TheoremSelectorWizard(IPOContext poContext, ITypeEnvironment typeEnvironment) {
		setWindowTitle("Select theorem");
		retriever = new TheoremsRetriever(poContext);
		this.typeEnvironment = typeEnvironment;
	}

	@Override
	public void addPages() {
		pageOne = new TheoremSelectorWizardPageOne(retriever);
		pageTwo = new TheoremSelectorWizardPageTwo(typeEnvironment);
		addPage(pageOne);
		addPage(pageTwo);
	}

	@Override
	public boolean performFinish() {
		// nothing to do
		return true;
	}
	
	@Override
	public boolean performCancel() {
		if (theoremsToAdd != null)
			theoremsToAdd.clear();
		return super.performCancel();
	}
	
	public List<String> getTheorems(){
		return theoremsToAdd;
	}
	
	public void setTheorems(List<String> theoremsToAdd){
		this.theoremsToAdd = theoremsToAdd;
	}
	
	@Override
	public boolean canFinish() {
		return theoremsToAdd != null && theoremsToAdd.size() > 0;
	}
}
