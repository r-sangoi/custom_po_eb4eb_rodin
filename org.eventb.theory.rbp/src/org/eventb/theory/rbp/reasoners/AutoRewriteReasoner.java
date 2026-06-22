/*******************************************************************************
 * Copyright (c) 2010,2016 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.reasoners;

import org.eventb.core.seqprover.IReasoner;
import org.eventb.theory.internal.rbp.reasoners.input.IPRMetadata;
import org.eventb.theory.rbp.plugin.RbPPlugin;
import org.eventb.theory.rbp.reasoning.AbstractRulesApplyer;
import org.eventb.theory.rbp.reasoning.AutoRewriter;
import org.eventb.theory.rbp.rulebase.IPOContext;

/**
 * <p>
 * Reasoner used for automatic rewrite tactic.
 * </p>
 *
 * @author maamria
 * @author htson - re-implemented as a context-dependent reasoner.
 * @version 2.0
 * @see AutoRewriter
 * @since 1.0
 */
public class AutoRewriteReasoner extends AbstractAutoRewriteReasoner
		implements IReasoner {

	private static final String REASONER_ID = RbPPlugin.PLUGIN_ID + ".autoRewriteReasoner";
		
	/*
	 * (non-Javadoc)
	 * 
	 * @see IReasoner#getReasonerID()
	 */
	public String getReasonerID() {
		return REASONER_ID;
	}

	// can be overridden to provide alternative display name
	public String getReasonerDisplayName() {
		return " (auto rewrite)";
	}
	
	// can be overridden to provide alternative rewriter
	public AbstractRulesApplyer getRewriter(IPOContext context,
			IPRMetadata prMetadata) {
		return new AutoRewriter(context, prMetadata);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Object#toString()
	 */
	@Override
	public String toString() {	
		return REASONER_ID;
	}

	
}
