/**
 * 
 */
package org.eventb.theory.core.basis;

import static org.eventb.theory.core.TheoryAttributes.THEORY_PROJECT_ATTRIBUTE;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.basis.EventBElement;
import org.eventb.theory.core.IAvailableTheory;
import org.eventb.theory.core.IAvailableTheoryProject;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

/**
 * @author renatosilva
 *
 */
public class AvailableTheoryProject extends EventBElement implements IAvailableTheoryProject {

	public AvailableTheoryProject(String name, IRodinElement parent) {
		super(name, parent);
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.basis.InternalElement#getElementType()
	 */
	@Override
	public IInternalElementType<? extends IInternalElement> getElementType() {
		return ELEMENT_TYPE;
	}

	@Override
	public boolean hasTheoryProject() throws RodinDBException {
		return hasAttribute(THEORY_PROJECT_ATTRIBUTE);
	}

	@Override
	public IRodinProject getTheoryProject() throws RodinDBException {
		return (IRodinProject) getAttributeValue(THEORY_PROJECT_ATTRIBUTE);
	}

	@Override
	public void setTheoryProject(IRodinProject rodinProject,
			IProgressMonitor monitor) throws RodinDBException {
		setAttributeValue(THEORY_PROJECT_ATTRIBUTE, rodinProject, monitor);
	}

	@Override
	public IAvailableTheory[] getTheories() throws RodinDBException {
		return getChildrenOfType(IAvailableTheory.ELEMENT_TYPE);
	}

	@Override
	public IAvailableTheory getTheory(String name) throws RodinDBException {
		return getInternalElement(IAvailableTheory.ELEMENT_TYPE, name);
	}

	@Override
	public IDeployedTheoryRoot[] getDeployedTheories() throws RodinDBException {
		IAvailableTheory[] theories = getTheories();
		IDeployedTheoryRoot[] deployedTheories = new IDeployedTheoryRoot[theories.length]; 
		for(int i=0;i<theories.length;i++){
			deployedTheories[i] = theories[i].getDeployedTheory();
		}
		return deployedTheories;
	}
}
