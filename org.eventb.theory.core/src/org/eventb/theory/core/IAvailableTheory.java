/**
 * 
 */
package org.eventb.theory.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ILabeledElement;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * @author Renato Silva
 *
 */
public interface IAvailableTheory extends ILabeledElement {
	
	IInternalElementType<IAvailableTheory> ELEMENT_TYPE = 
			RodinCore.getInternalElementType(TheoryPlugin.PLUGIN_ID + ".availableTheory");
	
	public boolean hasAvailableTheory() throws RodinDBException;
	
	public IDeployedTheoryRoot getDeployedTheory() throws RodinDBException;

	public void setAvailableTheory(IDeployedTheoryRoot deployedRoot, IProgressMonitor monitor) throws RodinDBException;

	public IRodinProject getAvailableTheoryProject() throws RodinDBException;
}
