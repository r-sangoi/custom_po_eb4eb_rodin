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

import java.util.Arrays;

import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;

/**
 * <p>
 * A common abstract implementation of combinable tactic is implemented. Clients
 * must implement the abstract method
 * {@link #performApply(IProofTreeNode, IProofMonitor)} to apply the tactic. The
 * {@link #apply(IProofTreeNode, IProofMonitor)} method is a wrapper to check
 * the pre-/post-conditions of the contract.
 * </p>
 * <p>
 * There are two combinators implemented (as static methods).
 * <ul>
 * <li>{@link #sequentialCompose(ICombinableTactic...)}: To create a sequential
 * composed tactic.</li>
 * <li>{@link #repeat(ICombinableTactic)}: To create a repeated tactic.</li>
 * </ul>
 * </p>
 * 
 * @author htson
 * @version 0.1
 * @since 4.0.0
 */
public abstract class CombinableTactic implements ICombinableTactic {

	/**
	 * Abstract method to perform the tactic application. The contract is the
	 * same as that of
	 * {@link ICombinableTactic#apply(IProofTreeNode, IProofMonitor)}
	 * 
	 * @param ptNode
	 *            The proof tree node at which this tactic should be applied
	 * @param pm
	 *            The proof monitor to monitor the progress of the tactic
	 * @return <code>null</code> iff the application was successful.
	 */
	protected abstract Object performApply(IProofTreeNode ptNode, IProofMonitor pm);
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ITactic#apply(IProofTreeNode, IProofMonitor)
	 */
	@Override
	public final Object apply(IProofTreeNode ptNode, IProofMonitor pm) {
		// Assert preconditions
		assert ptNode != null;
		
		if (!ptNode.isOpen()){
			return "Root already has children";
		}
		Object result = performApply(ptNode, pm);
		
		// Assert post-conditions
		assert !(result == null && ptNode.isOpen());
		assert !(result != null && ptNode.isClosed());

		return result;
	}

	/**
	 * Returns a tactic that is a sequential composition of the input array of
	 * tactics. The way to application of tactic can be explained recursively as
	 * follows:
	 * <ol>
	 * <li>Get the first tactic of the array and apply it to the current proof
	 * tree node.</li>
	 * <li>If this is the only tactic then return the result.</li>
	 * <li>If there are more tactics, apply their sequential composition to ALL
	 * OPEN SUB-GOALS resulting from the first tactic application (except the
	 * first WD sub-goal). Note that in the case where the first tactic fails,
	 * this is reduced to just the current proof tree node.</li>
	 * </ol>
	 * The combined tactic is successful if one of the sub-tactic application is
	 * successful.
	 * 
	 * @param tactics
	 *            an array of combinable tactics.
	 * @return the combined tactic
	 * @precondition there must at least one input tactic.
	 */
	public static ICombinableTactic sequentialCompose(final ICombinableTactic... tactics) {
		// Precondition 
		assert tactics.length != 0;
		return new CombinableTactic() {
			
			@Override
			protected Object performApply(IProofTreeNode ptNode, IProofMonitor pm) {
				ITactic tactic = tactics[0];
				Object result = tactic.apply(ptNode, pm);
				int length = tactics.length;
				if (length == 1)
					return result;

				boolean applicable = false;
				if (result == null)
					applicable = true;

				ICombinableTactic[] copy = Arrays.copyOfRange(tactics, 1, length);
				ITactic restTactic = sequentialCompose(copy);
				
				if (applicable) {
					IProofTreeNode[] subGoals = ptNode.getOpenDescendants();
					// Ignore the first subgoal which is a WD
					for (int i = 1; i < subGoals.length; ++i) {
						result = restTactic.apply(subGoals[i], pm);
						if (result == null)
							applicable = true;
					}
				} else {
					result = restTactic.apply(ptNode, pm);
					if (result == null)
						applicable = true;
				}
				

				// If one of the tactics succeeds then the composed tactic succeeds.
				return applicable ? null : result;
			}
		};
	}

	/**
	 * Returns a tactic that is a repeated application of a combinable tactic.
	 * The way to application of tactic can be explained recursively as follows:
	 * <ol>
	 * <li>Apply the input tactic to the current proof tree node.</li>
	 * <li>If this fails then return the result (i.e., the explanation).</li>
	 * <li>Else (i.e., the application is successful), apply the composed tactic
	 * to ALL OPEN SUB-GOALS resulting from the first tactic application (except
	 * the first WD sub-goal).</li>
	 * </ol>
	 * The combined tactic is successful if the first tactic application is
	 * successful.
	 * 
	 * @param tactic
	 *            a combinable tactic
	 * @return the tactic composed by repeatedly applying the input tactic.
	 */
	public static ICombinableTactic repeat(final ICombinableTactic tactic) {
		return new CombinableTactic() {
			
			@Override
			protected Object performApply(IProofTreeNode ptNode, IProofMonitor pm) {
				Object result = tactic.apply(ptNode, pm);
				if (result != null)
					return result;
				IProofTreeNode[] subGoals = ptNode.getOpenDescendants();
				// Ignore the first WD sub-goal
				for (int i = 1; i < subGoals.length; ++i) {
					this.apply(subGoals[i], pm);
				}
				return null;
			}
		};
	}
}
