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
import org.eventb.theory.rbp.reasoners.AutoRewriteReasoner;
import org.eventb.theory.rbp.rulebase.BaseManager;
import org.eventb.theory.rbp.rulebase.IPOContext;


/**
 * The automatic tactic for applying automatic rewrite rules.
 * 
 * <p> Only unconditional rewrite rules can be applied automatically.
 * 
 * @since 1.0
 * @author maamria
 *
 */
public class RewritesAutoTactic extends AbstractRewritesAutoTactic implements
		ITactic {

	/* (non-Javadoc)
	 * @see AbstractRewritesAutoTactic#getNoApplicableRuleMessage()
	 */
	@Override
	public final String getNoApplicableRuleMessage() {
		return "There are no applicable rewrite rules";
	}

	/* (non-Javadoc)
	 * @see AbstractRewritesAutoTactic#getRewritesRules(IPOContext, Class)
	 */
	@Override
	public List<IGeneralRule> getRewritesRules(IPOContext context,
			Class<?> clazz) {
		BaseManager manager = BaseManager.getDefault();
		return manager.getRewriteRules(true, clazz, context);
	}

	/* (non-Javadoc)
	 * @see org.eventb.theory.rbp.tactics.AbstractRewritesAutoTactic#getReasoner()
	 */
	@Override
	public IReasoner getReasoner() {
		return new AutoRewriteReasoner();
	}

}
