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
 * An implementation of an automatic rewrite reasoner input
 * <ul>
 * <li>Proof-rule metadata (inherited from {@link PRMetadataReasonerInput}).</li>
 * </ul>
 * 
 * </p>
 * 
 * @author htson
 * @version 1.0
 * @since 4.0.0
 */
public class AutoRewriteInput extends PRMetadataReasonerInput {
	
	/**
	 * Constructs an input with the given parameters.
	 * 
	 * @param prMetadata
	 *            the proof-rule metadata
	 */
	public AutoRewriteInput(IPRMetadata prMetadata) {
		super(prMetadata);
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
	public AutoRewriteInput(IReasonerInputReader reader)
			throws SerializeException {
		// Call the super method to deserialise the proof-rule metadata.
		super(reader);			
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
	 * @param writer 
	 * @throws SerializeException 
	 * 
	 */
	public void serialize(IReasonerInputWriter writer) throws SerializeException {
		super.serialise(writer);
	}

}
