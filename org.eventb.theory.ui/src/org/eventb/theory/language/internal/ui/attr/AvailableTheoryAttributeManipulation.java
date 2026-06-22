/**
 * 
 */
package org.eventb.theory.language.internal.ui.attr;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.theory.core.DatabaseUtilities;
import org.eventb.theory.core.IAvailableTheory;
import org.eventb.theory.core.IAvailableTheoryProject;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.TheoryAttributes;
import org.eventb.theory.internal.ui.TheoryUIUtils;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

/**
 * @author Renato Silva
 *
 */
public class AvailableTheoryAttributeManipulation extends AbstractAvailableTheoryAttributeManipulation {
	
	/**
	 * 
	 */
	public AvailableTheoryAttributeManipulation() {
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
		return asAvailableTheoryElement(element).hasAvailableTheory();
	}

	/* (non-Javadoc)
	 * @see org.eventb.internal.ui.eventbeditor.manipulation.IAttributeManipulation#getValue(org.rodinp.core.IRodinElement, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public String getValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		return asAvailableTheoryElement(element).getDeployedTheory().getComponentName();
	}

	/* (non-Javadoc)
	 * @see org.eventb.internal.ui.eventbeditor.manipulation.IAttributeManipulation#setValue(org.rodinp.core.IRodinElement, java.lang.String, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void setValue(IRodinElement element, String value,
			IProgressMonitor monitor) throws RodinDBException {
		IAvailableTheory availableTheory = asAvailableTheoryElement(element);
		IDeployedTheoryRoot deployedRoot = DatabaseUtilities.getDeployedTheory(value, availableTheory.getAvailableTheoryProject());
		
		if (deployedRoot != null && deployedRoot.exists()){
			availableTheory.setAvailableTheory(deployedRoot, monitor);
		}
	}

	/* (non-Javadoc)
	 * @see org.eventb.internal.ui.eventbeditor.manipulation.IAttributeManipulation#removeAttribute(org.rodinp.core.IRodinElement, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void removeAttribute(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		asAvailableTheoryElement(element).removeAttribute(TheoryAttributes.AVAILABLE_THEORY_ATTRIBUTE, monitor);
	}

	/* (non-Javadoc)
	 * @see org.eventb.internal.ui.eventbeditor.manipulation.IAttributeManipulation#getPossibleValues(org.rodinp.core.IRodinElement, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public String[] getPossibleValues(IRodinElement element,
			IProgressMonitor monitor) {
		final IAvailableTheory theoryElement = asAvailableTheoryElement(element);
		
		final Set<String> results = new HashSet<String>();
		
		try {
			IRodinProject rodinProject = theoryElement.getAvailableTheoryProject();
			
			if(rodinProject!=null){
				for(IDeployedTheoryRoot deployedTheory: DatabaseUtilities.getDeployedTheories(rodinProject)){
					results.add(TheoryUIUtils.getAvailableTheoryString(rodinProject, deployedTheory));
//					final Set<String> usedTheoryNames = getUsedTheoryNames(deployedTheory);
//					results.addAll(usedTheoryNames);
				}
				results.removeAll(getAlreadySelectedTheories(theoryElement, rodinProject));
				
				
//					IUseTheory[] usedTheories = deployedTheory.getUsedTheories();
//					for(IUseTheory importTheory: usedTheories){
//						IDeployedTheoryRoot usedTheory = importTheory.getUsedTheory();
//						results.add(TheoryUIUtils.getAvailableTheoryString(usedTheory.getRodinProject(), usedTheory));
//				}
			}
//		}
			
			
//			final Set<String> theoryNames = getTheoryNames(theoryElement);
//			final Set<String> usedTheoryNames = getUsedTheoryNames(theoryElement);
//			final String elementValue = getElementValue(theoryElement);
//			final Set<String> valueToRemove = new HashSet<String>();
//			valueToRemove.addAll(usedTheoryNames);
//			valueToRemove.remove(elementValue);
//
//			results.addAll(theoryNames);
//			results.removeAll(valueToRemove);
//			removeCycle(theoryElement, results);
		} catch (CoreException e) {
			TheoryUIUtils.log(e, "Error while populating potential imports");
		}
		return results.toArray(new String[results.size()]);
	}
	
	// Returns all theories that are already referenced in the same project, except the one of theoryElement.
	private Set<String> getAlreadySelectedTheories(IAvailableTheory theoryElement, IRodinProject rodinProject) throws RodinDBException{
		final Set<String> results = new HashSet<String>();
		IAvailableTheoryProject project = (IAvailableTheoryProject)theoryElement.getParent();
		
		for(IAvailableTheory availableTheory: project.getTheories()){
			if (theoryElement.equals(availableTheory)) {
				// Do not return our own theory
				continue;
			}
			if(availableTheory.getAvailableTheoryProject()!=null && availableTheory.getAvailableTheoryProject().equals(rodinProject) && availableTheory.hasAvailableTheory())
				results.add(availableTheory.getDeployedTheory().getComponentName());
		}
		
		return results;
	}
	
//	private Set<String> getTheoryNames(IInternalElement element)throws CoreException {
//		final IRodinProject rodinProject = element.getRodinProject();
//		IDeployedTheoryRoot[] deployedTheoryRoots;
//		final HashSet<String> result = new HashSet<String>();
//		deployedTheoryRoots = rodinProject.getRootElementsOfType(IDeployedTheoryRoot.ELEMENT_TYPE);
//		for (IDeployedTheoryRoot deployedRoot : deployedTheoryRoots) {
//			result.add(deployedRoot.getComponentName());
//		}
//		return result;
//	}
	
//	public Set<String> getUsedTheoryNames(IDeployedTheoryRoot root) throws CoreException {
//		Set<String> usedNames = new HashSet<String>();
////		IDeployedTheoryRoot root = (IDeployedTheoryRoot) element.getRoot();
//		// First add myself
//		usedNames.add(root.getElementName());
//		for (IUseTheory clause : root.getUsedTheories()) {
////			if (hasValue(clause, null))
//				usedNames.add(clause.getUsedTheory().getComponentName());
//
//		}
//		return usedNames;
//	}
	
//	private String getElementValue(IAvailableTheory element)throws CoreException {
//		if (element.exists() && hasValue(element, null))
//			return getValue(element, null);
//		else
//			return "";
//
//	}

//	protected void removeCycle(IAvailableTheory element,Set<String> theories) throws CoreException {
//		final ITheoryRoot root = (ITheoryRoot) element.getRoot();
//		final IRodinProject prj = root.getRodinProject();
//		final Iterator<String> iter = theories.iterator();
//		while (iter.hasNext()) {
//			final String name = iter.next();
//			final ITheoryRoot theory = DatabaseUtilities.getTheory(name, prj);
//			if (isImportedBy(root, theory)) {
//				iter.remove();
//			}
//		}
//	}

//	private boolean isImportedBy(ITheoryRoot abstractRoot, ITheoryRoot root)throws CoreException {
//		for (ITheoryRoot thy : getImportedTheories(root)) {
//			if (thy.equals(abstractRoot) || isImportedBy(abstractRoot, thy))
//				return true;
//		}
//		return false;
//	}

}
