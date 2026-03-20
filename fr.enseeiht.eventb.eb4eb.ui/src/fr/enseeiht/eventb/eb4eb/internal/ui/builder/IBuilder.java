package fr.enseeiht.eventb.eb4eb.internal.ui.builder;

import org.eclipse.core.runtime.CoreException;

public interface IBuilder {
	
	/** Build the linked resource.
	 * <p>Need to be called after giving all data to the builder and only once</p>
	 * @throws CoreException
	 */
	public void build() throws CoreException;

}
