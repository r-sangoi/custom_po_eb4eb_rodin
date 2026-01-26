package fr.enseeiht.eventb.eb4eb.internal.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import fr.enseeiht.eventb.eb4eb.IAnnotationCollectionRoot;
import fr.enseeiht.eventb.eb4eb.ui.EB4EBUIPlugin;

public class AnnotationImage {
	/**
	 * Initialize the image registry. Additional image should be added here
	 * <p>
	 * 
	 * @param registry
	 *            The image registry
	 */
	public static void initializeImageRegistry(ImageRegistry registry) {
		initializeRegistry(registry);
	}

	/**
	 * Initialize the image registry. Additional image should be added here
	 * <p>
	 * 
	 * @param registry
	 *            The image registry
	 */
	public static void initializeRegistry(ImageRegistry registry) {
		
		registerImage(registry, IAnnotationCollectionRoot.IMG,
				IAnnotationCollectionRoot.IMG_PATH);
		
	}
	
	/**
	 * Register an image with the image registry
	 * <p>
	 * 
	 * @param registry
	 *            the image registry
	 * @param key
	 *            the key to retrieve the image later
	 * @param path
	 *            the path to the location of the image file within this Event-B
	 *            UI plugin
	 */
	public static void registerImage(ImageRegistry registry, String key,
			String path) {
		ImageDescriptor desc = getImageDescriptor(path);
		registry.put(key, desc);
	}/**
	 * Returns an image descriptor for the image file within the Event-B UI
	 * Plugin at the given plug-in relative path
	 * <p>
	 * 
	 * @param path
	 *            relative path of the image within this Event-B UI Plugin
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return getImageDescriptor(EB4EBUIPlugin.PLUGIN_ID, path);
	}
	
	/**
	 * Returns an image descriptor for the image file at the given plug-in and
	 * the relative path within the plugin
	 * <p>
	 * 
	 * @param path
	 *            relative path of the image
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String pluginID,
			String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin(pluginID, path);
	}
}
