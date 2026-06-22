/*******************************************************************************
 * Copyright (c) 2010,2016 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.internal.rbp.reasoners.input;

import java.util.Set;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerInputReader;
import org.eventb.core.seqprover.IReasonerInputWriter;
import org.eventb.core.seqprover.SerializeException;
import org.eventb.core.seqprover.proofBuilder.ReplayHints;

/**
 * <p>
 * An implementation of an rewrite reasoner input. The input to this reasoner
 * includes the predicate, the position as well as rule-related information.
 * </p>
 * 
 * @author maamria
 * @author htson: Re-implemented based on {@link PRMetadataReasonerInput}.
 * @version 2.0
 * @since 1.0
 */
public class RewriteInput extends PRMetadataReasonerInput implements IReasonerInput {
	
	private static final String POSITION_KEY = "pos";

	private IPosition position;
	private Predicate predicate;
	
	/**
	 * Constructs a rewrite reasoner input with the predicate to be rewritten,
	 * the position where rewriting occurs, and the meta-data of the proof rule
	 * that is used for rewriting.
	 * 
	 * @param predicate
	 *            a predicate to be rewritten. A <code>non-null</code> indicates
	 *            that it is a hypothesis. Otherwise, <code>null</code>
	 *            indicates that the goal is rewritten.
	 * @param position
	 *            the position (within the hypothesis or goal) where rewriting
	 *            occurs.
	 * @param prMetadata
	 *            the meta-data of the proof rule that is used for rewriting.
	 */
	public RewriteInput(Predicate predicate, IPosition position,
			IPRMetadata prMetadata) {
		super(prMetadata);
		this.position = position;
		this.predicate = predicate;
	}
	
	/**
	 * @param reader
	 * @throws SerializeException 
	 */
	public RewriteInput(IReasonerInputReader reader) throws SerializeException {
		super(reader);
		String posString = reader.getString(POSITION_KEY);
		this.position = FormulaFactory.makePosition(posString);
		Set<Predicate> neededHyps = reader.getNeededHyps();

		final int length = neededHyps.size();
		if (length == 0) {
			// Goal rewriting
			this.predicate = null;
		} else if (length != 1) {
			throw new SerializeException(new IllegalStateException(
					"Expected exactly one needed hypothesis!"));
		} else {
			this.predicate = neededHyps.iterator().next();
		}

	}

	@Override
	public void applyHints(ReplayHints renaming) {
		if (predicate != null) {
			predicate = renaming.applyHints(predicate);
		}
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
	public Predicate getPredicate() {
		return predicate;
	}

	/**
	 * @return
	 */
	public IPosition getPosition() {
		return position;
	}
	
	public void serialise(IReasonerInputWriter writer)
			throws SerializeException {
		super.serialise(writer);
		writer.putString(POSITION_KEY, this.position.toString());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return predicate + "@" + position + " using " + super.toString();
	}

	
}
