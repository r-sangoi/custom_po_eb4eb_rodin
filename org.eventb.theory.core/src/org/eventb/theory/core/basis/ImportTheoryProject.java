/**
 * 
 */
package org.eventb.theory.core.basis;

import static org.eventb.theory.core.TheoryAttributes.IMPORT_THEORY_PROJECT_ATTRIBUTE;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.basis.EventBElement;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.IImportTheory;
import org.eventb.theory.core.IImportTheoryProject;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

/**
 * @author asiehsalehi
 *
 */
public class ImportTheoryProject extends EventBElement implements IImportTheoryProject {

	public ImportTheoryProject(String name, IRodinElement parent) {
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
		return hasAttribute(IMPORT_THEORY_PROJECT_ATTRIBUTE);
	}

	@Override
	public IRodinProject getTheoryProject() throws RodinDBException {
		return (IRodinProject) getAttributeValue(IMPORT_THEORY_PROJECT_ATTRIBUTE);
	}

	@Override
	public void setTheoryProject(IRodinProject rodinProject,
			IProgressMonitor monitor) throws RodinDBException {
		setAttributeValue(IMPORT_THEORY_PROJECT_ATTRIBUTE, rodinProject, monitor);
	}

	@Override
	public IImportTheory[] getImportTheories() throws RodinDBException {
		return getChildrenOfType(IImportTheory.ELEMENT_TYPE);
	}

	@Override
	public IImportTheory getImportTheory(String name) throws RodinDBException {
		return getInternalElement(IImportTheory.ELEMENT_TYPE, name);
	}

	@Override
	public IDeployedTheoryRoot[] getDeployedTheories() throws RodinDBException {
		return getChildrenOfType(IDeployedTheoryRoot.ELEMENT_TYPE);
	}

/*	@Override
	public IDeployedTheoryRoot[] getDeployedTheories() throws RodinDBException {
		IImportTheory[] theories = getTheories();
		IDeployedTheoryRoot[] deployedTheories = new IDeployedTheoryRoot[theories.length]; 
		for(int i=0;i<theories.length;i++){
			deployedTheories[i] = theories[i].getDeployedTheory();
		}
		return deployedTheories;
	}*/
}
