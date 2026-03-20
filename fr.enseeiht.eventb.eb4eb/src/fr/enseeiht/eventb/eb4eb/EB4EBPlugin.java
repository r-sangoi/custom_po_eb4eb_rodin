package fr.enseeiht.eventb.eb4eb;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.rodinp.core.RodinCore;

public class EB4EBPlugin implements BundleActivator {

	// The plug-in ID
	public static final String PLUGIN_ID = "fr.enseeiht.eventb.eb4eb"; //$NON-NLS-1$
	
	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	public void start(BundleContext bundleContext) throws Exception {
		EB4EBPlugin.context = bundleContext;
		setEB4EBConfig();
	}

	public void stop(BundleContext bundleContext) throws Exception {
		EB4EBPlugin.context = null;
	}
	
	/**
	 * Registers a file configuration setter for our plugin.
	 */
	public static void setEB4EBConfig() {
		RodinCore.addElementChangedListener(new ConfSettor());
	}

}
