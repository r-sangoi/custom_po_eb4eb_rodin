/*******************************************************************************
 * Copyright (c) 2016 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University of Southampton - initial API and implementation
 *******************************************************************************/

package org.eventb.theory.internal.rbp.reasoners.input;

/**
 * <p>
 * An implementation for proof-rule meta-data.
 * </p>
 *
 * @author htson
 * @version 0.1
 * @see
 * @since 3.1.0
 */
public class PRMetadata implements IPRMetadata {

	// The project name.
	private String projectName;

	// The theory name.
	private String theoryName;

	// The rule name.
	private String ruleName;

	/**
	 * Creates a proof-rule meta-data with the given project name, theory name,
	 * and rule name.
	 * 
	 * @param projectName
	 *            the project name.
	 * @param theoryName
	 *            the theory name.
	 * @param ruleName
	 *            the rule name.
	 */
	public PRMetadata(String projectName, String theoryName, String ruleName) {
		this.projectName = projectName;
		this.theoryName = theoryName;
		this.ruleName = ruleName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IPRMetadata#getProjectName()
	 */
	@Override
	public String getProjectName() {
		return projectName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IPRMetadata#getTheoryName()
	 */
	@Override
	public String getTheoryName() {
		return theoryName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IPRMetadata#getRuleName()
	 */
	@Override
	public String getRuleName() {
		return ruleName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IPRMetadata#getDescription()
	 */
	@Override
	public String getDescription() {
		return "RbP Rewrite using rule \"" + ruleName + "\" of theory \""
				+ theoryName + "\" in project \"" + projectName + "\"";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return projectName + "::" + theoryName + "::" + ruleName;
	}

}
