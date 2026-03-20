package fr.enseeiht.eventb.eb4eb.ui.explorer;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eventb.core.IEventBRoot;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

import fr.enseeiht.eventb.eb4eb.IAnnotationCollectionRoot;

public class AnnotationCollectionContentProvider implements ITreeContentProvider {

	private static final Object[] NO_OBJECT = new Object[0];
	
	@Override
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof IProject) {
			IRodinProject proj = RodinCore.valueOf((IProject) parentElement);
			if (proj.exists()) {
				try {
					return getRootChildren(proj);
				} catch (RodinDBException e) {
					e.printStackTrace(); //TODO
//					TheoryUIUtils.log(e, "when accessing " + rootType.getName()
//							+ " roots of " + proj);
				}
			}
		}
		return NO_OBJECT;
	}

	@Override
	public Object getParent(Object element) {
		if (element instanceof IAnnotationCollectionRoot) {
			return ((IAnnotationCollectionRoot) element).getParent().getParent();
		}
		return null;
	}

	@Override
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
	
	protected IEventBRoot[] getRootChildren(IRodinProject project)
			throws RodinDBException {
		return (IAnnotationCollectionRoot[]) getAnnotationCollectionChildren(project);
	}
	
	private IRodinElement[] getAnnotationCollectionChildren(IRodinProject proj) {
		try {
			return proj.getRootElementsOfType(IAnnotationCollectionRoot.ELEMENT_TYPE);
		} catch (RodinDBException e) {
//			TheoryUIUtils.log(e, "Error while retrieving "
//					+ IAnnotationRoot.ELEMENT_TYPE + " from " + proj); TODO
			e.printStackTrace();
			return null;
		}
	}

}
