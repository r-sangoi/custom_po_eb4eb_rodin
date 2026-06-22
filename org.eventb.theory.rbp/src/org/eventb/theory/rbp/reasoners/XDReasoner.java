/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.reasoners;

import org.eventb.theory.internal.rbp.reasoners.input.IPRMetadata;
import org.eventb.theory.rbp.plugin.RbPPlugin;
import org.eventb.theory.rbp.reasoning.AbstractRulesApplyer;
import org.eventb.theory.rbp.reasoning.XDAutoRewriter;
import org.eventb.theory.rbp.rulebase.IPOContext;

/**
 * 
 * @author maamria
 * 
 */
public class XDReasoner extends AutoRewriteReasoner {

	private static final String REASONER_ID = RbPPlugin.PLUGIN_ID + ".reduceToClassicLanguageReasoner";
	
	@Override
	public String getReasonerID() {
		return REASONER_ID;
	}
		
	public AbstractRulesApplyer getRewriter(IPOContext context, IPRMetadata prMetadata) {
		 return new XDAutoRewriter(context, prMetadata);
	}
	
	// can be overridden to provide alternative display name
	public String getReasonerDisplayName() {
		return "(exp. def.)";
	}

}
