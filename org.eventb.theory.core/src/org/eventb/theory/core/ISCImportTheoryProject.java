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
 * @author asiehsalehi
 *
 */
public interface ISCImportTheoryProject extends ITraceableElement {
	
	IInternalElementType<ISCImportTheoryProject> ELEMENT_TYPE = RodinCore
			.getInternalElementType(TheoryPlugin.PLUGIN_ID + ".scImportTheoryProject");
	
	public ISCImportTheoryProject getSCTheoryProject(String name) throws RodinDBException;
	
	public ISCImportTheory[] getSCImportTheories() throws RodinDBException;
	
	public IRodinProject getSCImportTheoryProject() throws RodinDBException;
	
	public void setSCTheoryProject(IRodinProject iRodinProject,
			IProgressMonitor monitor) throws RodinDBException;
	
	public ISCImportTheory getSCImportTheory(String name) throws RodinDBException;
		

}
