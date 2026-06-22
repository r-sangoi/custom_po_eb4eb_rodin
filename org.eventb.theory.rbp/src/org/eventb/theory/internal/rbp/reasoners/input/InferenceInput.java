/*******************************************************************************
 * Copyright (c) 2010,2016 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.internal.rbp.reasoners.input;

import java.util.Set;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IReasonerInputReader;
import org.eventb.core.seqprover.IReasonerInputWriter;
import org.eventb.core.seqprover.SerializeException;
import org.eventb.core.seqprover.proofBuilder.ReplayHints;

/**
 * <p>
 * An implementation of an manual inference reasoner input
 * <ul>
 * <li>Proof-rule metadata (inherited from {@link PRMetadataReasonerInput}).</li>
 * 
 * <li>{@link #hyp}: In the case of backward application, an additional
 * predicate is required which will be used to match the first required "given"
 * hypothesis of the rule.</li>
 * </ul>
 * 
 * </p>
 * 
 * @author maamria
 * @author htson: Re-implemented based on {@link PRMetadataReasonerInput}.
 * @version 2.0
 * @since 1.0
 */
public class InferenceInput extends PRMetadataReasonerInput {

	/**
	 * In the case of backward reasoning, this is the hypothesis that will be
	 * matched with the first required given hypothesis of the rule. In the case
	 * of forward reasoning, it is <code>null</code>.
	 */ 
	private Predicate hyp;

	// Keys for version 0 of the reasoner. 
	private static final String V0_FORWARD_VAL = "forward";
	private static final String V0_FORWARD_KEY = "isForward";
	
	// Keys for version 1 of the reasoner.
	private static final String V1_FORWARD_VAL = "forward";
	private static final String V1_FORWARD_KEY = "isForward";
	private static final String V1_HYPS_KEY = "hyps";
	
	// Keys for version 1 of the reasoner.
	private static final String V2_HYP_KEY = "hyp";

	/**
	 * Constructs an input with the given parameters.
	 * 
	 * @param prMetadata
	 *            the proof-rule metadata
	 * @param hyp
	 *            the hypothesis to be matched with the first required given
	 *            hypothesis of the input rule in case of backward reasoning.
	 *            Use <code>null</code> for forward reasoning.
	 */
	public InferenceInput(IPRMetadata prMetadata, Predicate hyp) {
		super(prMetadata);

		this.hyp = hyp;
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
	public InferenceInput(IReasonerInputReader reader)
			throws SerializeException {
		// Call the super method to deserialise the proof-rule metadata.
		super(reader);
		if (deserializeV2(reader))
			return;
			
		if (deserializeV1(reader))
			return;
		
		if (deserializeV0(reader))
			return;
			
		throw new SerializeException(new IllegalStateException(
				"Error when deserialise inference reasoner input"));
	}

	/**
	 * @param reader
	 * @throws SerializeException
	 */
	private boolean deserializeV2(IReasonerInputReader reader)
			throws SerializeException {
		Predicate[] predicates;
		try {
			predicates = reader.getPredicates(V2_HYP_KEY);
			if (predicates.length == 0) {
				this.hyp = null;
				return true;
			}
			if (predicates.length == 1) {
				this.hyp = predicates[0];
				return true;
			}
		} catch (SerializeException e) {
			return false;
		}
		throw new SerializeException(new IllegalStateException(
				"Unexpected number of hypothesis: " + predicates));
	}

	/**
	 * @param reader
	 * @return
	 */
	private boolean deserializeV1(IReasonerInputReader reader) {
		try {
			// Read the "forward" flag to see if it is forward or backward application.
			boolean forward = V1_FORWARD_VAL.equals(reader.getString(V1_FORWARD_KEY));
			if (forward) {
				Predicate[] predicates = reader.getPredicates(V1_HYPS_KEY);
				if (predicates.length == 0) {
					return false;
				}
				hyp = predicates[0];
				return true;
			} else { // backward reasoning
				hyp = null;
				return true;
			}
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
			// Read the "forward" flag to see if it is forward or backward application.
			boolean forward = V0_FORWARD_VAL.equals(reader.getString(V0_FORWARD_KEY));
			if (forward) {
				Set<Predicate> neededHyps = reader.getNeededHyps();
				if (neededHyps.size() == 0) {
					return false;
				} else {
					// The first needed hypothesis will be used.
					hyp = neededHyps.iterator().next();
					return false;
				}
			} else { // backward reasoning
				hyp = null;
				return true;
			}
		} catch (SerializeException e) {
			return false;
		}
	}

	@Override
	public void applyHints(ReplayHints renaming) {
		if (hyp != null)
			hyp = renaming.applyHints(hyp);
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
		return hyp != null;
	}

	/**
	 * @param writer 
	 * @throws SerializeException 
	 * 
	 */
	public void serialize(IReasonerInputWriter writer) throws SerializeException {
		super.serialise(writer);
		if (hyp == null)
			writer.putPredicates(V2_HYP_KEY);
		else
			writer.putPredicates(V2_HYP_KEY, hyp);
	}

	/**
	 * @return
	 */
	public Predicate getHypothesis() {
		return hyp;
	}

}
