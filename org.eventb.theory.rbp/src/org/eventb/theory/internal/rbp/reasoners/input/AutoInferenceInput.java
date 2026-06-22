/*******************************************************************************
 * Copyright (c) 2016 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.internal.rbp.reasoners.input;

import org.eventb.core.seqprover.IReasonerInputReader;
import org.eventb.core.seqprover.IReasonerInputWriter;
import org.eventb.core.seqprover.SerializeException;
import org.eventb.core.seqprover.proofBuilder.ReplayHints;

/**
 * <p>
 * An implementation of an automatic inference reasoner input
 * <ul>
 * <li>Proof-rule metadata (inherited from {@link PRMetadataReasonerInput}).</li>
 * 
 * <li>{@link #forward}: A boolean flag indicating if the application is forward
 * (<code>true</code>) or backward (<code>false</code>.</li>
 * </ul>
 * 
 * </p>
 * 
 * @author htson
 * @version 1.0
 * @since 4.0.0
 */
public class AutoInferenceInput extends PRMetadataReasonerInput {

	// The boolean flag
	private boolean forward;
		
	// Keys for version 1 of the reasoner.
	private static final String V1_FORWARD_VAL = "forward";
	private static final String V1_BACKWARD_VAL = "backward";
	private static final String V1_FORWARD_KEY = "isForward";
	
	/**
	 * Constructs an input with the given parameters.
	 * 
	 * @param prMetadata
	 *            the proof-rule metadata
	 * @param forward
	 *            <code>true</code> for forward reasoning, <code>false</code>
	 *            for backward reasoning.
	 */
	public AutoInferenceInput(IPRMetadata prMetadata, boolean forward) {
		super(prMetadata);
		this.forward = forward;
	}

	/**
	 * Constructor to construct an inference input from a reasoner input reader.
	 * This constructor is used for the reasoner to deserialise the input.
	 * 
	 * @param reader
	 *            the reasoner input reader.
	 * @throws SerializeException
	 *             if some unexpected error occurs during the deserialisation.
	 */
	public AutoInferenceInput(IReasonerInputReader reader)
			throws SerializeException {
		// Call the super method to deserialise the proof-rule metadata.
		super(reader);			
		if (deserializeV1(reader))
			return;
		
		if (deserializeV0(reader))
			return;
			
		throw new SerializeException(new IllegalStateException(
				"Error when deserialise inference reasoner input"));
	}

	/**
	 * @param reader
	 * @return
	 */
	private boolean deserializeV1(IReasonerInputReader reader) {
		try {
			// Read the "forward" flag to see if it is forward or backward application.
			forward = V1_FORWARD_VAL.equals(reader.getString(V1_FORWARD_KEY));
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
		return false;
	}

	@Override
	public void applyHints(ReplayHints renaming) {
		// Do nothing
		return;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IReasonerInput#getError()
	 */
	@Override
	public String getError() {
		return null;
	}

	/**
	 * @return
	 */
	public boolean isForward() {
		return forward;
	}

	/**
	 * @param writer 
	 * @throws SerializeException 
	 * 
	 */
	public void serialize(IReasonerInputWriter writer) throws SerializeException {
		super.serialise(writer);
		if (forward)
			writer.putString(V1_FORWARD_KEY, V1_FORWARD_VAL);
		else
			writer.putString(V1_FORWARD_KEY, V1_BACKWARD_VAL);
	}

}
