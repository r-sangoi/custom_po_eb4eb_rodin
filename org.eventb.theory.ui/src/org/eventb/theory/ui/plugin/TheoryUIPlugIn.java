/*******************************************************************************
 * Copyright (c) 2010, 2020 University of Southampton and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.ui.plugin;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eventb.theory.internal.ui.TheoryImage;
import org.osgi.framework.BundleContext;
import org.rodinp.core.IRodinDB;
import org.rodinp.core.RodinCore;

/**
 * The activator class controls the plug-in life cycle
 */
public class TheoryUIPlugIn extends AbstractUIPlugin {
	// The plug-in ID
	public static final String PLUGIN_ID = "org.eventb.theory.ui";
	// The shared instance
	private static TheoryUIPlugIn plugin;
	
	public TheoryUIPlugIn() {
		super();
		plugin = this;
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	@Override
	protected void initializeImageRegistry(ImageRegistry reg) {
		TheoryImage.initializeImageRegistry(reg);
		super.initializeImageRegistry(reg);
	}
	
	/**
	 * Getting the current active page from the active workbench window.
	 * <p>
	 * 
	 * @return current active workbench page
	 */
	private IWorkbenchPage internalGetActivePage() {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
	}
	
	/**
	 * Get the active workbench page.
	 * <p>
	 * 
	 * @return current active workbench page
	 */
	public static IWorkbenchPage getActivePage() {
		return getDefault().internalGetActivePage();
	}

	public static TheoryUIPlugIn getDefault() {
		return plugin;
	}

	/**
	 * Returns the Rodin database element.
	 * 
	 * @return the Rodin database
	 */
	public static IRodinDB getRodinDatabase() {
		return RodinCore.getRodinDB();
	}
}
