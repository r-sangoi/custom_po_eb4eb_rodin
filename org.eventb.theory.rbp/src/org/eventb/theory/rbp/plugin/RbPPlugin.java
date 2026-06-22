/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.plugin;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * 
 * @author maamria
 *
 */
public class RbPPlugin extends AbstractUIPlugin {
	
	public static final String PLUGIN_ID = "org.eventb.theory.rbp";

	private static RbPPlugin plugin;
	
	public RbPPlugin(){}
	
	public static RbPPlugin getDefault(){
		return plugin;
	}

	public void start(BundleContext bundleContext) throws Exception {
		super.start(bundleContext);
		plugin = this;
	}
	
	public void stop(BundleContext bundleContext) throws Exception {
		plugin = null;
		super.start(bundleContext);
	}
}
