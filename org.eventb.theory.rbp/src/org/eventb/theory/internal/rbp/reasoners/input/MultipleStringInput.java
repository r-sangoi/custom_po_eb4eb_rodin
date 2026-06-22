/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.internal.rbp.reasoners.input;

import java.util.List;

import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.proofBuilder.ReplayHints;

/**
 * An implementation of a multiple strings input for context-aware reasoners.
 * 
 * @since 1.2
 * @author maamria
 *
 */
public class MultipleStringInput implements IReasonerInput {

	public List<String> strings;
	
	public MultipleStringInput(List<String> strings) {
		this.strings = strings;
	}

	/* (non-Javadoc)
	 * @see IReasonerInput#hasError()
	 */
	@Override
	public boolean hasError() {
		return false;
	}

	/* (non-Javadoc)
	 * @see IReasonerInput#getError()
	 */
	@Override
	public String getError() {
		return null;
	}

	/* (non-Javadoc)
	 * @see IReasonerInput#applyHints(org.eventb.core.seqprover.proofBuilder.ReplayHints)
	 */
	@Override
	public void applyHints(ReplayHints renaming) {
		// Do nothing
		
	}

}
