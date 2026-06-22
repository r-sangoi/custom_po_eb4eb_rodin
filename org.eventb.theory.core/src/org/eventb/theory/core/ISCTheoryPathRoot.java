/**
 * 
 */
package org.eventb.theory.core;

import org.eventb.core.IAccuracyElement;
import org.eventb.core.IConfigurationElement;
import org.eventb.core.IEventBRoot;
import org.eventb.core.ITraceableElement;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * @author renatosilva
 *
 */
public interface ISCTheoryPathRoot extends IEventBRoot, IAccuracyElement, IConfigurationElement, ITraceableElement{
	
	IInternalElementType<ISCTheoryPathRoot> ELEMENT_TYPE = RodinCore
			.getInternalElementType(TheoryPlugin.PLUGIN_ID + ".scTheoryLanguageRoot");
	
	/**
	 * Returns all sc available theory projects children of this element.
	 * @return all sc available theory projects
	 * @throws RodinDBException
	 */
	public ISCAvailableTheoryProject[] getSCAvailableTheoryProjects() throws RodinDBException;
	
	/**
	 * Returns all sc available theory project children of this element.
	 * @return all sc available theory project
	 * @throws RodinDBException
	 */
	public ISCAvailableTheoryProject getSCAvailableTheoryProject(String name) throws RodinDBException;
	
	/**
	 * Returns all sc theory children of this element.
	 * @return all sc theory projects
	 * @throws RodinDBException
	 */
	public ISCAvailableTheory[] getSCAvailableTheories() throws RodinDBException;

	


}
