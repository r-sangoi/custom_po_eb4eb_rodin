/**
 * 
 */
package org.eventb.theory.internal.ui.attr;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.theory.core.TheoryAttributes;
import org.eventb.theory.internal.ui.TheoryUIUtils;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * @author asiehsalehi
 *
 */
public class ImportProjectTheoryAttributeManipulation extends AbstractImportTheoryAttributeManipulation  {
	
	/* (non-Javadoc)
	 * @see org.eventb.internal.ui.eventbeditor.manipulation.IAttributeManipulation#setDefaultValue(org.rodinp.core.IRodinElement, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void setDefaultValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		asImportTheoryProjectElement(element).setTheoryProject(element.getRodinProject(), monitor);
	}

	/* (non-Javadoc)
	 * @see org.eventb.internal.ui.eventbeditor.manipulation.IAttributeManipulation#hasValue(org.rodinp.core.IRodinElement, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public boolean hasValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		return asImportTheoryProjectElement(element).hasTheoryProject();
	}

	/* (non-Javadoc)
	 * @see org.eventb.internal.ui.eventbeditor.manipulation.IAttributeManipulation#getValue(org.rodinp.core.IRodinElement, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public String getValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		return asImportTheoryProjectElement(element).getTheoryProject().getElementName();
	}

	/* (non-Javadoc)
	 * @see org.eventb.internal.ui.eventbeditor.manipulation.IAttributeManipulation#setValue(org.rodinp.core.IRodinElement, java.lang.String, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void setValue(IRodinElement element, String value,
			IProgressMonitor monitor) throws RodinDBException {
		IRodinProject rodinProject = RodinCore.getRodinDB().getRodinProject(value);
		
		if (rodinProject != null && rodinProject.exists()){
			asImportTheoryProjectElement(element).setTheoryProject(rodinProject, monitor);
		}
	}

	/* (non-Javadoc)
	 * @see org.eventb.internal.ui.eventbeditor.manipulation.IAttributeManipulation#removeAttribute(org.rodinp.core.IRodinElement, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void removeAttribute(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		asImportTheoryProjectElement(element).removeAttribute(TheoryAttributes.THEORY_PROJECT_ATTRIBUTE, monitor);
	}

	/* (non-Javadoc)
	 * @see org.eventb.internal.ui.eventbeditor.manipulation.IAttributeManipulation#getPossibleValues(org.rodinp.core.IRodinElement, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public String[] getPossibleValues(IRodinElement element,
			IProgressMonitor monitor) {
		IRodinProject[] rodinProjects;
		try {
			rodinProjects = RodinCore.getRodinDB().getRodinProjects();
			List<String> rodinProjectNames = new ArrayList<String>(rodinProjects.length);
			for(IRodinProject rodinProject: rodinProjects){
				rodinProjectNames.add(rodinProject.getElementName());
			}
			return rodinProjectNames.toArray(new String[rodinProjects.length]);
		} catch (RodinDBException e) {
			TheoryUIUtils.log(e, "Exception occurred when retrieving the projects for this workspace");
		}
		
		return EMPTY_LIST;
	}

}
