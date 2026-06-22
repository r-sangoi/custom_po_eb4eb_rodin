/*******************************************************************************
 * Copyright (c) 2020 CentraleSupélec.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     CentraleSupélec - initial implementation
 *******************************************************************************/
package org.eventb.theory.rbp.tactics;

/**
 * Manual rewrites tactic provider with cached results.
 *
 * Tactic provider classes listed in this plugin's plugin.xml are automatically
 * constructed with their default constructor. This class merely extends
 * {@link CachedApplicationTactic} with a default constructor that sets the
 * cached tactic provider to {@link RewritesManualTactic}.
 *
 * @author Guillaume Verdier
 * @see RewritesManualTactic
 */
public class CachedRewritesManualTactic extends CachedApplicationTactic {

	/**
	 * Initializes this cached tactic provider with a rewrites manual tactic
	 * provider.
	 */
	public CachedRewritesManualTactic() {
		super(new RewritesManualTactic());
	}

}
