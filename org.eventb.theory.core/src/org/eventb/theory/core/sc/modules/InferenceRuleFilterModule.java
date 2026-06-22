/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.sc.modules;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.state.ILabelSymbolInfo;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.theory.core.IInferenceRule;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.rodinp.core.IRodinElement;

/**
 * 
 * @author maamria
 * 
 */
public class InferenceRuleFilterModule extends RuleFilterModule<IInferenceRule> {

	private final IModuleType<InferenceRuleFilterModule> MODULE_TYPE = SCCore
			.getModuleType(TheoryPlugin.PLUGIN_ID + ".inferenceRuleFilterModule");

	@Override
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	@Override
	protected IInferenceRule getRule(IRodinElement element) {
		return (IInferenceRule) element;
	}

	@Override
	protected boolean furtherCheck(IInferenceRule rule,
			ILabelSymbolInfo symbolInfo, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		return true;
	}

}
