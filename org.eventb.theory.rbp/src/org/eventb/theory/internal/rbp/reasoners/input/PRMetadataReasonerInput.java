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

import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerInputReader;
import org.eventb.core.seqprover.IReasonerInputWriter;
import org.eventb.core.seqprover.SerializeException;

/**
 * <p>
 *
 * </p>
 *
 * @author htson
 * @version 1.0
 * @see
 * @since
 */
public abstract class PRMetadataReasonerInput implements IReasonerInput {

	private static final String V0_RULE_KEY = "inferenceRule";
	private static final String V0_THEORY_KEY = "theory";
	private static final String V0_PROJECT_KEY = "project";

	private static final String V1_RULE_KEY = "Rule";
	private static final String V1_THEORY_KEY = "Thy";
	private static final String V1_PROJECT_KEY = "ThyProject";

	private IPRMetadata prMetadata;
	
	public PRMetadataReasonerInput(IPRMetadata prMetadata) {
		this.prMetadata = prMetadata;
	}
	
	public PRMetadataReasonerInput(IReasonerInputReader reader)
			throws SerializeException {
		if (deserializeV1(reader))
			return;
		if (deserializeV0(reader))
			return;
		
		throw new SerializeException(new IllegalStateException(
				"Error when deserialise proof-rule metadata: " + reader));
	}
	
	/**
	 * @param reader 
	 * @return
	 */
	private boolean deserializeV1(IReasonerInputReader reader) {
		try {
			String projectName = reader.getString(V1_PROJECT_KEY);
			String theoryName = reader.getString(V1_THEORY_KEY);
			String ruleName = reader.getString(V1_RULE_KEY);
			this.prMetadata = new PRMetadata(projectName, theoryName, ruleName);
			return true;
		} catch (SerializeException e) {
			return false;
		}
	}

	/**
	 * @param reader 
	 * @return
	 */
	private boolean deserializeV0(IReasonerInputReader reader) {
		try {
			String projectName = reader.getString(V0_PROJECT_KEY);
			String theoryName = reader.getString(V0_THEORY_KEY);
			String ruleName = reader.getString(V0_RULE_KEY);
			this.prMetadata = new PRMetadata(projectName, theoryName, ruleName);
			return true;
		} catch (SerializeException e) {
			return false;
		}
	}

	public IPRMetadata getPRMetadata() {
		return prMetadata;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see IReasonerInput#hasError()
	 */
	@Override
	public final boolean hasError() {
		return this.getError() != null;
	}

	protected void serialise(IReasonerInputWriter writer)
			throws SerializeException {
		writer.putString(V1_PROJECT_KEY, prMetadata.getProjectName());
		writer.putString(V1_THEORY_KEY, prMetadata.getTheoryName());
		writer.putString(V1_RULE_KEY, prMetadata.getRuleName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return prMetadata.toString();
	}
	
}
