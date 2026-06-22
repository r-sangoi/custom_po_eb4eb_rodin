/*******************************************************************************
 * Copyright (c) 2011,2016 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.tactics;

import java.util.List;

import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.ITactic;
import org.eventb.theory.core.IGeneralRule;
import org.eventb.theory.rbp.reasoners.XDReasoner;
import org.eventb.theory.rbp.rulebase.BaseManager;
import org.eventb.theory.rbp.rulebase.IPOContext;

/**
 * An auto tactic for expanding definitions of mathematical extensions.
 * 
 * @author maamria
 * @author htson
 * @version 2.0
 * @see ContextDependentTactic
 * @since 1.2
 */
public class XDAutoTactic extends AbstractRewritesAutoTactic implements ITactic {

	/* (non-Javadoc)
	 * @see AbstractRewritesAutoTactic#getNoApplicableRuleMessage()
	 */
	@Override
	public String getNoApplicableRuleMessage() {
		return "There are no applicable definitions";
	}

	/* (non-Javadoc)
	 * @see AbstractRewritesAutoTactic#getRewritesRules(IPOContext, Class)
	 */
	@Override
	public List<IGeneralRule> getRewritesRules(IPOContext context,
			Class<?> clazz) {
		BaseManager manager = BaseManager.getDefault();
		return manager.getDefinitionalRules(clazz, context);
	}

	/* (non-Javadoc)
	 * @see AbstractRewritesAutoTactic#getReasoner()
	 */
	@Override
	public IReasoner getReasoner() {
		return new XDReasoner();
	}
}
