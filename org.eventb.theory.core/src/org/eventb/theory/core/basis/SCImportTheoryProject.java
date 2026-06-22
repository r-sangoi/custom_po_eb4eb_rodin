/**
 * 
 */
package org.eventb.theory.core.basis;

import static org.eventb.theory.core.TheoryAttributes.IMPORT_THEORY_PROJECT_ATTRIBUTE;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.basis.EventBElement;
import org.eventb.theory.core.ISCImportTheory;
import org.eventb.theory.core.ISCImportTheoryProject;
import org.eventb.theory.core.TheoryAttributes;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

/**
 * @author asiehsalehi
 *
 */
public class SCImportTheoryProject extends EventBElement implements ISCImportTheoryProject {

	/**
	 * @param name
	 * @param parent
	 */
	public SCImportTheoryProject(String name, IRodinElement parent) {
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
	public ISCImportTheoryProject getSCTheoryProject(String name)
			throws RodinDBException {
		return getInternalElement(ISCImportTheoryProject.ELEMENT_TYPE, name);
	}

	@Override
	public ISCImportTheory[] getSCImportTheories()
			throws RodinDBException {
		return getChildrenOfType(ISCImportTheory.ELEMENT_TYPE);
	}

	@Override
	public IRodinProject getSCImportTheoryProject() throws RodinDBException {
		 return (IRodinProject) getAttributeValue(IMPORT_THEORY_PROJECT_ATTRIBUTE);
	}

	@Override
	public void setSCTheoryProject(IRodinProject theoryProject,
			IProgressMonitor monitor) throws RodinDBException {
		setAttributeValue(TheoryAttributes.IMPORT_THEORY_PROJECT_ATTRIBUTE ,theoryProject, monitor);
	}

	@Override
	public ISCImportTheory getSCImportTheory(String name)
			throws RodinDBException {
		return getInternalElement(ISCImportTheory.ELEMENT_TYPE, name);
	}
}
