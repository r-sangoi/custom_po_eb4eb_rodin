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
 * @author asiehsalehi
 *
 */
public interface IImportTheoryProject extends IInternalElement{
	
	IInternalElementType<IImportTheoryProject> ELEMENT_TYPE = 
			RodinCore.getInternalElementType(TheoryPlugin.PLUGIN_ID + ".importTheoryProject");
	
	public boolean hasTheoryProject() throws RodinDBException;
	
	public IRodinProject getTheoryProject() throws RodinDBException;
	
	public void setTheoryProject(IRodinProject rodinProject, IProgressMonitor monitor) throws RodinDBException;
	
	public IImportTheory[] getImportTheories() throws RodinDBException;
	
	public IDeployedTheoryRoot[] getDeployedTheories() throws RodinDBException;
	
	public IImportTheory getImportTheory(String name) throws RodinDBException;

}
