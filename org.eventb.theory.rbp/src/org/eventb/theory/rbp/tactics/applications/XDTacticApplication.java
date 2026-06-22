/*******************************************************************************
 * Copyright (c) 2011,2016 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.tactics.applications;

import org.eclipse.swt.graphics.Image;
import org.eventb.core.seqprover.ITactic;
import org.eventb.theory.rbp.plugin.RbPPlugin;
import org.eventb.theory.rbp.tactics.XDAutoTactic;
import org.eventb.ui.prover.IPredicateApplication;

/**
 * A special tactic application for the translation of all extended formulae to
 * classical Event-B language.
 * 
 * @author maamria
 * @htson - Use XDAutoTactic as the encapsulated tactic.
 * @version 1.1
 * @since 1.0
 */
public class XDTacticApplication implements IPredicateApplication {
	
	private static final String TACTIC_ID = RbPPlugin.PLUGIN_ID + ".RbPxd";

	@Override
	public String getTacticID() {
		return TACTIC_ID;
	}

	/**
	 * @author htson
	 */
	@Override
	public ITactic getTactic(String[] inputs, String globalInput) {
		return new XDAutoTactic();
	}

	@Override
	public Image getIcon() {
		return null;
	}

	@Override
	public String getTooltip() {
		return null;
	}
}
