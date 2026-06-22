/**
 * 
 */
package org.eventb.theory.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * @author renatosilva
 *
 */
public interface IAvailableTheoryProject extends IInternalElement{
	
	IInternalElementType<IAvailableTheoryProject> ELEMENT_TYPE = 
			RodinCore.getInternalElementType(TheoryPlugin.PLUGIN_ID + ".availableTheoryProject");
	
	public boolean hasTheoryProject() throws RodinDBException;
	
	public IRodinProject getTheoryProject() throws RodinDBException;
	
	public void setTheoryProject(IRodinProject rodinProject, IProgressMonitor monitor) throws RodinDBException;
	
	public IAvailableTheory[] getTheories() throws RodinDBException;
	
	public IDeployedTheoryRoot[] getDeployedTheories() throws RodinDBException;
	
	public IAvailableTheory getTheory(String name) throws RodinDBException;

}
