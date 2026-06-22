/**
 * 
 */
package org.eventb.theory.core.basis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eventb.core.basis.EventBRoot;
import org.eventb.theory.core.ISCAvailableTheory;
import org.eventb.theory.core.ISCAvailableTheoryProject;
import org.eventb.theory.core.ISCTheoryPathRoot;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author renatosilva
 *
 */
public class SCTheoryPathRoot extends EventBRoot implements ISCTheoryPathRoot {

	/**
	 * @param name
	 * @param parent
	 */
	public SCTheoryPathRoot(String name, IRodinElement parent) {
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
	public ISCAvailableTheoryProject[] getSCAvailableTheoryProjects()
			throws RodinDBException {
		return getChildrenOfType(ISCAvailableTheoryProject.ELEMENT_TYPE);
	}

	@Override
	public ISCAvailableTheoryProject getSCAvailableTheoryProject(String name)
			throws RodinDBException {
		return getInternalElement(ISCAvailableTheoryProject.ELEMENT_TYPE, name);
	}

	@Override
	public ISCAvailableTheory[] getSCAvailableTheories()
			throws RodinDBException {
		List<ISCAvailableTheory> theories =  new ArrayList<ISCAvailableTheory>();
		
		for(ISCAvailableTheoryProject proj: getSCAvailableTheoryProjects()){
			theories.addAll(Arrays.asList(proj.getSCAvailableTheories()));
		}
		
		return theories.toArray(new ISCAvailableTheory[theories.size()]);
	}
}
