/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.reasoning;

import java.util.List;

import org.eventb.core.ast.Formula;
import org.eventb.core.ast.IFormulaRewriter;
import org.eventb.theory.core.IGeneralRule;
import org.eventb.theory.internal.rbp.reasoners.input.IPRMetadata;
import org.eventb.theory.rbp.rulebase.BaseManager;
import org.eventb.theory.rbp.rulebase.IPOContext;
import org.eventb.theory.rbp.rulebase.basis.IDeployedRewriteRule;

public class XDAutoRewriter extends AbstractRulesApplyer implements IFormulaRewriter {

	/**
	 * @param context
	 * @param prMetadata
	 */
	public XDAutoRewriter(IPOContext context, IPRMetadata prMetadata) {
		super(context, prMetadata);
	}
	
	@Override
	public IGeneralRule getRule(Formula<?> original){
		BaseManager manager = BaseManager.getDefault();
		String projectName = prMetadata.getProjectName();
		String theoryName = prMetadata.getTheoryName();
		String ruleName = prMetadata.getRuleName();
		List<IGeneralRule> rules = manager.getDefinitionalRules(
				original.getClass(), context);

		for (IGeneralRule rule : rules) {
			if (rule instanceof IDeployedRewriteRule) {
				IDeployedRewriteRule deployedRule = (IDeployedRewriteRule) rule;
				if (projectName.equals(deployedRule.getProjectName())
						&& theoryName.equals(deployedRule.getTheoryName())
						&& ruleName.equals(deployedRule.getRuleName()))
					return deployedRule; 
			} else { // (rule instanceof ISCRewriteRule)
				throw new UnsupportedOperationException(
						"Unsupported statically checked rule");
			}
		}
		return null;
	}

}
