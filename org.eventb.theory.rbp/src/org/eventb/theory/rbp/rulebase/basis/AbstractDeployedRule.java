/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.rulebase.basis;

import org.eventb.core.ast.ITypeEnvironment;

/**
 * Common implementation of deployed rules.
 * 
 * @author maamria
 *
 */
public abstract class AbstractDeployedRule implements IDeployedRule{
	
	protected String description;
	protected ITypeEnvironment globalTypeEnv;
	protected boolean isAutomatic;
	protected boolean isInteractive;
	protected boolean isSound;
	protected String ruleName;
	protected String theoryName;
	protected String projectName;
	protected String toolTip;
	
	protected AbstractDeployedRule(String ruleName, String theoryName,String projectName,
			boolean isAutomatic, boolean isInteractive, 
			boolean isSound, String toolTip, String description,
			ITypeEnvironment typeEnv){
		this.ruleName = ruleName;
		this.projectName = projectName;
		this.theoryName = theoryName;
		this.isAutomatic = isAutomatic;
		this.isSound = isSound;
		this.isInteractive = isInteractive;
		this.toolTip = toolTip;
		this.description = description;
		this.globalTypeEnv = typeEnv;
	}

	public String getDescription() {
		return description;
	}

	public String getRuleName() {
		return ruleName;
	}

	
	public String getTheoryName() {
		return theoryName;
	}

	
	public String getToolTip() {
		return toolTip;
	}

	
	public ITypeEnvironment getTypeEnvironment() {
		return globalTypeEnv.makeSnapshot();
	}

	
	public boolean isAutomatic() {
		return isAutomatic;
	}

	public boolean isInteracive() {
		return isInteractive;
	}

	
	public boolean isSound() {
		return isSound;
	}
	
	public String getProjectName() {
		// TODO Auto-generated method stub
		return projectName;
	}
}
