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
 * A common interface for proof rule meta-data for a proof rule. The meta-data
 * contains "pointer" to the proof rule, e.g., the rule name, the theory name,
 * and the project name.
 * </p>
 *
 * @author htson
 * @version 0.1
 * @see PRMetadata
 * @since 3.1.0
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IPRMetadata {

	/**
	 * Returns the project name.
	 * 
	 * @return the project name.
	 */
	public String getProjectName();

	/**
	 * Returns the theory name.
	 * 
	 * @return the theory name.
	 */
	public String getTheoryName();

	/**
	 * Returns the rule name.
	 * 
	 * @return the rule name.
	 */
	public String getRuleName();

	/**
	 * Returns the description of the rule.
	 * 
	 * @return the description of the rule.
	 */
	public String getDescription();

}
