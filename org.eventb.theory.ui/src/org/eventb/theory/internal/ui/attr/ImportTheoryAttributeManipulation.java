/**
 * 
 */
package org.eventb.theory.internal.ui.attr;

import static org.eventb.theory.core.TheoryHierarchyHelper.getImportedTheories;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.theory.core.DatabaseUtilities;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.IImportTheory;
import org.eventb.theory.core.IImportTheoryElement;
import org.eventb.theory.core.IImportTheoryProject;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.ITheoryRoot;
import org.eventb.theory.core.TheoryAttributes;
import org.eventb.theory.internal.ui.TheoryUIUtils;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

/**
 * @author asiehsalehi
 *
 */
public class ImportTheoryAttributeManipulation extends AbstractImportTheoryAttributeManipulation {
	
	/**
	 * 
	 */
	public ImportTheoryAttributeManipulation() {
	}

	/* (non-Javadoc)
	 * @see org.eventb.internal.ui.eventbeditor.manipulation.IAttributeManipulation#setDefaultValue(org.rodinp.core.IRodinElement, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void setDefaultValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		// TODO Auto-generated method stub
	}

	/* (non-Javadoc)
	 * @see org.eventb.internal.ui.eventbeditor.manipulation.IAttributeManipulation#hasValue(org.rodinp.core.IRodinElement, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public boolean hasValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		return asImportTheoryElement(element).hasImportTheory();
	}

	/* (non-Javadoc)
	 * @see org.eventb.internal.ui.eventbeditor.manipulation.IAttributeManipulation#getValue(org.rodinp.core.IRodinElement, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public String getValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		return asImportTheoryElement(element).getImportTheory().getComponentName();
	}

	/* (non-Javadoc)
	 * @see org.eventb.internal.ui.eventbeditor.manipulation.IAttributeManipulation#setValue(org.rodinp.core.IRodinElement, java.lang.String, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void setValue(IRodinElement element, String value,
			IProgressMonitor monitor) throws RodinDBException {
		IImportTheory importTheory = asImportTheoryElement(element);
		IDeployedTheoryRoot deployedRoot = DatabaseUtilities.getDeployedTheory(value, importTheory.getImportTheoryProject());
		
		if (deployedRoot != null && deployedRoot.exists()){
			importTheory.setImportTheory(deployedRoot, monitor);
		}
	}

	/* (non-Javadoc)
	 * @see org.eventb.internal.ui.eventbeditor.manipulation.IAttributeManipulation#removeAttribute(org.rodinp.core.IRodinElement, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void removeAttribute(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		asImportTheoryElement(element).removeAttribute(TheoryAttributes.AVAILABLE_THEORY_ATTRIBUTE, monitor);
	}
	
	@Override
	public String[] getPossibleValues(IRodinElement element,
			IProgressMonitor monitor) {
		final IImportTheory theoryElement = asImportTheoryElement(element);
		final Set<String> results = new HashSet<String>();
		try {
			IRodinProject rodinProject = theoryElement.getImportTheoryProject();
			results.addAll(getTheoryNames(theoryElement));
			//remove self
			if (rodinProject.equals(theoryElement.getRodinProject()))
				results.remove(theoryElement.getRoot().getElementName());
			//remove already selected theories
			results.removeAll(getAlreadySelectedTheories(theoryElement, ((ITheoryRoot) theoryElement.getParent().getParent()).getImportTheoryProjects()));
			//remove cycles
			removeCycle(theoryElement, results);
		} catch (CoreException e) {
			TheoryUIUtils.log(e, "Error while populating potential imports");
		}
		return results.toArray(new String[results.size()]);

	}
	
	private Set<String> getTheoryNames(IImportTheory element)throws CoreException {
		IRodinProject rodinProject = element.getImportTheoryProject();
		final HashSet<String> result = new HashSet<String>();
		
		if(rodinProject!=null){
			for(IDeployedTheoryRoot deployedTheory: DatabaseUtilities.getDeployedTheories(rodinProject))
				result.add(TheoryUIUtils.getImportTheoryString(rodinProject, deployedTheory));
		}
		return result;
	}

	protected void removeCycle(IImportTheoryElement element,Set<String> theories) throws CoreException {
		final ITheoryRoot root = (ITheoryRoot) element.getRoot();
		final IRodinProject prj = element.getImportTheoryProject();
		final Iterator<String> iter = theories.iterator();
		while (iter.hasNext()) {
			final String name = iter.next();
			final ITheoryRoot theory = DatabaseUtilities.getTheory(name, prj);
			if (isImportedBy(root, theory)) {
				iter.remove();
			}
		}
	}
	
	private static boolean isImportedBy(ITheoryRoot abstractRoot,
			ITheoryRoot root) throws CoreException {
		for (ISCTheoryRoot scThy : getImportedTheories(root)) {
			final ITheoryRoot thy = scThy.getTheoryRoot();
			if (thy.equals(abstractRoot) || isImportedBy(abstractRoot, thy))
				return true;
		}
		return false;
	}
	
	private Set<String> getAlreadySelectedTheories(IImportTheory theoryElement, IImportTheoryProject[] importProjects) throws RodinDBException{
		final Set<String> results = new HashSet<String>();
		
		for(IImportTheoryProject prj: importProjects){
			for(IImportTheory importTheory: prj.getImportTheories()){
				if (theoryElement.equals(importTheory)) {
					// Do not return our own theory
					continue;
				}
				if(importTheory.getImportTheoryProject()!=null && importTheory.getImportTheoryProject().equals(prj.getTheoryProject()) && importTheory.hasImportTheory())
						results.add(importTheory.getImportTheory().getComponentName());
			}		
		}
		
		return results;
	}

}
