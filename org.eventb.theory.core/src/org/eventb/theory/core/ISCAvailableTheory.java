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
public interface ISCAvailableTheory extends ITraceableElement {
	
	IInternalElementType<ISCAvailableTheory> ELEMENT_TYPE = RodinCore
			.getInternalElementType(TheoryPlugin.PLUGIN_ID + ".scAvailableTheory");
	
	
	public ISCAvailableTheory getSCAvailableTheory(String name) throws RodinDBException;
	
	public IDeployedTheoryRoot getSCDeployedTheoryRoot() throws RodinDBException;
	
	public IRodinProject getSCAvailableTheoryProject() throws RodinDBException;

	public void setSCTheory(IAvailableTheory theory, IProgressMonitor monitor) throws RodinDBException;

}
