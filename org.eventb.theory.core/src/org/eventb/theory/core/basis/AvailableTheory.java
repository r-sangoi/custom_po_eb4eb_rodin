/**
 * 
 */
package org.eventb.theory.core.basis;

import static org.eventb.theory.core.TheoryAttributes.AVAILABLE_THEORY_ATTRIBUTE;

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
 * @author Renato Silva
 *
 */
public class AvailableTheory extends EventBElement implements IAvailableTheory {

	public AvailableTheory(String name, IRodinElement parent) {
		super(name, parent);
	}

	/* (non-Javadoc)
	 * @see org.eventb.theory.core.IAvailableTheory#hasAvailableTheory()
	 */
	@Override
	public boolean hasAvailableTheory() throws RodinDBException {
		return hasAttribute(AVAILABLE_THEORY_ATTRIBUTE);
	}

	/* (non-Javadoc)
	 * @see org.eventb.theory.core.IAvailableTheory#getAvailableTheory()
	 */
	@Override
	public IDeployedTheoryRoot getDeployedTheory() throws RodinDBException {
		return (IDeployedTheoryRoot) getAttributeValue(AVAILABLE_THEORY_ATTRIBUTE);
	}

	/* (non-Javadoc)
	 * @see org.eventb.theory.core.IAvailableTheory#setImportTheory(org.eventb.theory.core.IDeployedTheoryRoot, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void setAvailableTheory(IDeployedTheoryRoot deployedRoot,
			IProgressMonitor monitor) throws RodinDBException {
		setAttributeValue(AVAILABLE_THEORY_ATTRIBUTE, deployedRoot, monitor);
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.basis.InternalElement#getElementType()
	 */
	@Override
	public IInternalElementType<? extends IInternalElement> getElementType() {
		return ELEMENT_TYPE;
	}

	@Override
	public IRodinProject getAvailableTheoryProject() throws RodinDBException {
		if(parent!=null){
			IAvailableTheoryProject theoryProject = (IAvailableTheoryProject) parent;
			return theoryProject.getTheoryProject();
		}
		
		return null;
	}
	
	@Override
	public String getLabel() throws RodinDBException {
		if(hasAvailableTheory())
			return getDeployedTheory().getComponentName();
		return "";
	}

}
