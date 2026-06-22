/**
 * 
 */
package org.eventb.theory.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ITraceableElement;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * @author renatosilva
 *
 */
public interface ISCAvailableTheoryProject extends ITraceableElement {
	
	IInternalElementType<ISCAvailableTheoryProject> ELEMENT_TYPE = RodinCore
			.getInternalElementType(TheoryPlugin.PLUGIN_ID + ".scAvailableTheoryProject");
	
	public ISCAvailableTheoryProject getSCTheoryProject(String name) throws RodinDBException;
	
	public ISCAvailableTheory[] getSCAvailableTheories() throws RodinDBException;
	
	public IRodinProject getSCAvailableTheoryProject() throws RodinDBException;
	
	public void setSCTheoryProject(IRodinProject iRodinProject,
			IProgressMonitor monitor) throws RodinDBException;
	
	public ISCAvailableTheory getSCAvailableTheory(String name) throws RodinDBException;
		

}
