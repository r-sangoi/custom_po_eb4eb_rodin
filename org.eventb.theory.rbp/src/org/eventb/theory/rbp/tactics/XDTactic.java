/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.tactics;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

import java.util.List;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.theory.rbp.tactics.applications.XDTacticApplication;
import org.eventb.ui.prover.ITacticApplication;
import org.eventb.ui.prover.ITacticProvider;

/**
 * A tactic provider to reduce all extended formulae to the classical Event-B equivalent.
 * @author maamria
 * @since 1.0
 */
public class XDTactic implements ITacticProvider {

	@Override
	public List<ITacticApplication> getPossibleApplications(IProofTreeNode node, Predicate hyp, String globalInput) {
		if (node != null && node.isOpen()) {
			ITacticApplication appli = new XDTacticApplication();
			return singletonList(appli);
		}
		return emptyList();
	}
}
