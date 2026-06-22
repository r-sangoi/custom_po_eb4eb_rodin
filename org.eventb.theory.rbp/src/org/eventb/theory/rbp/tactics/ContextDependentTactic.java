/*******************************************************************************
 * Copyright (c) 2016, 2020 University of Southampton and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University of Southampton - initial API and implementation
 *     CentraleSupélec - add compatibility with RodinCore AutoTacticChecker
 *******************************************************************************/

package org.eventb.theory.rbp.tactics;

import org.eventb.core.ast.Formula;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ITactic;
import org.eventb.theory.rbp.rulebase.IPOContext;
import org.eventb.theory.rbp.utils.ProverUtilities;

/**
 * <p>
 * An abstract implementation for context dependent tactics. This is a wrapper
 * for the actual tactic that will be constructed based on the PO context using
 * {@link #getTactic(IPOContext)}, which must be implemented by clients.
 * </p>
 *
 * @author htson
 * @version 0.1
 * @see InferenceAutoTactic
 * @since 4.0.0
 */
public abstract class ContextDependentTactic implements ITactic {

	/* (non-Javadoc)
	 * @see ITactic#apply(IProofTreeNode, IProofMonitor)
	 */
	@Override
	public final Object apply(IProofTreeNode ptNode, IProofMonitor pm) {
		IProverSequent sequent = ptNode.getSequent();
		IPOContext context = ProverUtilities.getContext(sequent);
		if (context == null) {
			/*
			 * RodinCore checks that auto provers work by calling this method with a sequent
			 * that has no context, no hypotheses and a true goal. In this case, there are
			 * no rules to apply, but if the application fails, RodinCore believes that the
			 * tactic does not work, so we do nothing and act as if the tactic had
			 * succeeded. Otherwise, there is a problem, so we return an error message.
			 */
			if (!sequent.hypIterable().iterator().hasNext() && sequent.goal().getTag() == Formula.BTRUE) {
				return null;
			}
			return "Cannot find the context of the proof sequent";
		}
		ITactic tactic = getTactic(context);
		return tactic.apply(ptNode, pm);
	}

	/**
	 * Abstract method to get the actual tactic depending on a PO context.
	 * 
	 * @param context
	 *            the PO context.
	 * @precondition the input context is NOT <code>null</code>.
	 * @return the tactic depending on the input PO context.
	 */
	protected abstract ITactic getTactic(IPOContext context);

}
