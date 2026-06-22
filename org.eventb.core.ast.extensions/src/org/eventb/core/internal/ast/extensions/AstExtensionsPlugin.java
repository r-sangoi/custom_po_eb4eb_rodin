/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.internal.ast.extensions;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * 
 * @author maamria
 *
 */
public class AstExtensionsPlugin extends Plugin implements BundleActivator {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.eventb.core.ast.extensions";

	// The shared instance
	private static AstExtensionsPlugin plugin;
	
	/**
	 * The constructor
	 */
	public AstExtensionsPlugin() {
	}
	
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}
	
	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static AstExtensionsPlugin getPlugin() {
		return plugin;
	}
	
	/**
	 * Logging facility
	 * @param exc the exception
	 * @param message the message
	 */
	public static void log(Throwable exc, String message) {
		if (message == null) {
			message = "Unknown context"; //$NON-NLS-1$
		}
		final IStatus status = new Status(IStatus.ERROR, PLUGIN_ID,
				IStatus.ERROR, message, exc);
		getPlugin().getLog().log(status);
	}
	
}
