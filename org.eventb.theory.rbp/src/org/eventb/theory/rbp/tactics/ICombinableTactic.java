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

package org.eventb.theory.rbp.tactics;

import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;

/**
 * <p>
 * A common interface for combinable tactics. Clients should NOT directly
 * implement this interface but extend the abstract implementation
 * {@link CombinableTactic}, where the tactic combinators are implemented.
 * </p>
 *
 * @author htson
 * @version 0.1
 * @see CombinableTactic#sequentialCompose(ICombinableTactic...)
 * @see CombinableTactic#repeat(ICombinableTactic)
 * @since 4.0.0
 */
public interface ICombinableTactic extends ITactic {

	/**
	 * In addition to the contract of the inherited method, combinable tactics
	 * requires that the proof tree node is open. If the tactic is successful, a
	 * sub-proof tree is attached to the current node (hence it becomes closed).
	 * 
	 * @param ptNode
	 *            The proof tree node at which this tactic should be applied
	 * @param pm
	 *            The proof monitor to monitor the progress of the tactic
	 * @return <code>null</code> iff the application was successful.
	 * 
	 * @see IProofMonitor
	 */
	@Override
	Object apply(IProofTreeNode ptNode, IProofMonitor pm);
}
