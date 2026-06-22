/**
 * 
 */
package org.eventb.theory.core.basis;

import static org.eventb.theory.core.TheoryAttributes.THEORY_PROJECT_ATTRIBUTE;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.basis.EventBElement;
import org.eventb.theory.core.ISCAvailableTheory;
import org.eventb.theory.core.ISCAvailableTheoryProject;
import org.eventb.theory.core.TheoryAttributes;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

/**
 * @author renatosilva
 *
 */
public class SCAvailableTheoryProject extends EventBElement implements ISCAvailableTheoryProject {

	/**
	 * @param name
	 * @param parent
	 */
	public SCAvailableTheoryProject(String name, IRodinElement parent) {
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
	public ISCAvailableTheoryProject getSCTheoryProject(String name)
			throws RodinDBException {
		return getInternalElement(ISCAvailableTheoryProject.ELEMENT_TYPE, name);
	}

	@Override
	public ISCAvailableTheory[] getSCAvailableTheories()
			throws RodinDBException {
		return getChildrenOfType(ISCAvailableTheory.ELEMENT_TYPE);
	}

	@Override
	public IRodinProject getSCAvailableTheoryProject() throws RodinDBException {
		 return (IRodinProject) getAttributeValue(THEORY_PROJECT_ATTRIBUTE);
	}

	@Override
	public void setSCTheoryProject(IRodinProject theoryProject,
			IProgressMonitor monitor) throws RodinDBException {
		setAttributeValue(TheoryAttributes.THEORY_PROJECT_ATTRIBUTE ,theoryProject, monitor);
	}

	@Override
	public ISCAvailableTheory getSCAvailableTheory(String name)
			throws RodinDBException {
		return getInternalElement(ISCAvailableTheory.ELEMENT_TYPE, name);
	}
}
