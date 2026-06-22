package org.eventb.theory.ui.explorer;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eventb.core.IEventBRoot;
import org.eventb.theory.core.ITheoryRoot;
import org.eventb.theory.internal.ui.TheoryUIUtils;
import org.eventb.theory.ui.explorer.model.TheoryModelController;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

public class TheoryContentProvider implements ITreeContentProvider {

	private static final Object[] NO_OBJECT = new Object[0];
	
	protected final IInternalElementType<? extends IEventBRoot> rootType;
	
	public TheoryContentProvider() {
		rootType = ITheoryRoot.ELEMENT_TYPE;
	}
	
	public void dispose() {
		// ignore
	}

	public Object[] getChildren(Object element) {
		// ensure the existence on the instance
		TheoryModelController.createInstance();
		if (element instanceof IProject) {
			IRodinProject proj = RodinCore.valueOf((IProject) element);
			if (proj.exists()) {
				TheoryModelController.processProject(proj);
				try {
					return getRootChildren(proj);
				} catch (RodinDBException e) {
					TheoryUIUtils.log(e, "when accessing " + rootType.getName()
							+ " roots of " + proj);
				}
			}
		}
		return NO_OBJECT;
	}

	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	public boolean hasChildren(Object element) {
		if (element instanceof IProject) {
			IRodinProject proj = RodinCore.valueOf((IProject) element);
			if (proj.exists()) {
				try {
					return getRootChildren(proj).length > 0;
				} catch (RodinDBException e) {
					return false;
				}
			}
		}
		return false;
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		
	}

	public Object getParent(Object element) {
		if (element instanceof ITheoryRoot) {
			return ((ITheoryRoot) element).getParent().getParent();
		}
		return null;
	}

	protected IEventBRoot[] getRootChildren(IRodinProject project)
			throws RodinDBException {
		return (ITheoryRoot[]) getTheoryChildren(project);
	}

	private IRodinElement[] getTheoryChildren(IRodinProject proj) {
		try {
			return proj.getRootElementsOfType(ITheoryRoot.ELEMENT_TYPE);
		} catch (RodinDBException e) {
			TheoryUIUtils.log(e, "Error while retrieving "
					+ ITheoryRoot.ELEMENT_TYPE + " from " + proj);
			e.printStackTrace();
			return null;
		}
	}

}
