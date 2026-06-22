/**
 * 
 */
package org.eventb.theory.core.basis;

import static org.eventb.theory.core.TheoryAttributes.AVAILABLE_THEORY_ATTRIBUTE;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.basis.EventBElement;
import org.eventb.theory.core.IAvailableTheory;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.ISCAvailableTheory;
import org.eventb.theory.core.ISCAvailableTheoryProject;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

/**
 * @author renatosilva
 *
 */
public class SCAvailableTheory extends EventBElement implements
		ISCAvailableTheory {

	/**
	 * @param name
	 * @param parent
	 */
	public SCAvailableTheory(String name, IRodinElement parent) {
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
	public ISCAvailableTheory getSCAvailableTheory(String name) throws RodinDBException {
		return getInternalElement(ISCAvailableTheory.ELEMENT_TYPE, name);
	}

	@Override
	public IDeployedTheoryRoot getSCDeployedTheoryRoot()
			throws RodinDBException {
		IDeployedTheoryRoot deployedTheoryRoot = (IDeployedTheoryRoot) getAttributeValue(AVAILABLE_THEORY_ATTRIBUTE);
		if (deployedTheoryRoot.getParent().exists())
			return deployedTheoryRoot;
		//when availThy is undeployed
		else
			return null;	
	}

	@Override
	public IRodinProject getSCAvailableTheoryProject()
			throws RodinDBException {
		if(parent!=null && parent instanceof ISCAvailableTheoryProject){
			ISCAvailableTheoryProject proj = (ISCAvailableTheoryProject) parent;
			return proj.getSCAvailableTheoryProject();
		}
		
		return null;
	}

	@Override
	public void setSCTheory(IAvailableTheory theory, IProgressMonitor monitor) throws RodinDBException{
		setAttributeValue(AVAILABLE_THEORY_ATTRIBUTE ,theory.getDeployedTheory(), monitor);
	}
	
	

}
