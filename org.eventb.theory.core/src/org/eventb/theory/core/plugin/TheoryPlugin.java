package org.eventb.theory.core.plugin;


import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eventb.theory.core.maths.extensions.WorkspaceExtensionsManager;
import org.eventb.theory.internal.core.util.DeployedStatusUpdater;
import org.eventb.theory.internal.core.util.TheoryPathListener;
import org.osgi.framework.BundleContext;
import org.rodinp.core.RodinCore;

/**
 * <p>
 *
 * </p>
 *
 * @author maamria - Initial API and implementation
 * @author htson - Added debugging options
 * @version 1.0
 * @see
 * @since 1.0
 *
 */
public class TheoryPlugin extends Plugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.eventb.theory.core";

	// The shared instance
	private static TheoryPlugin plugin;
	
	// Trace Options
//	private static final String GLOBAL_TRACE = PLUGIN_ID + "/debug"; //$NON-NLS-1$

	private static final String EVENTBEDITOR_TRACE = PLUGIN_ID
			+ "/debug/wksp_exts_mng"; //$NON-NLS-1$

	/**
	 * The constructor
	 */
	public TheoryPlugin() {
	}
	
	public void start(BundleContext context) throws Exception {
		super.start(context);
		if (isDebugging())
			configureDebugOptions();

		plugin = this;
		RodinCore.addElementChangedListener(new TheoryPathListener());
		DeployedStatusUpdater.getInstance().initAndSchedule();
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}
	
	/**
	 * Process debugging/tracing options coming from Eclipse.
	 */
	private void configureDebugOptions() {
//		UIUtils.DEBUG = parseOption(GLOBAL_TRACE);
		WorkspaceExtensionsManager.DEBUG = parseOption(EVENTBEDITOR_TRACE);
	}

	/**
	 * Utility method for parsing debug option
	 * 
	 * @param key
	 *            the key for the debug retrieving the debug option
	 * @return <code>true</code> if the debug option is enabled,
	 *         <code>false</code> otherwise.
	 */
	private static boolean parseOption(String key) {
		final String option = Platform.getDebugOption(key);
		return "true".equalsIgnoreCase(option); //$NON-NLS-1$
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static TheoryPlugin getDefault() {
		return plugin;
	}
}
