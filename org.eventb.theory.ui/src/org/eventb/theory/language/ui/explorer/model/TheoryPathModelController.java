/*******************************************************************************
 * Copyright (c) 2012, 2020 University of Southampton and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.language.ui.explorer.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.runtime.ListenerList;
import org.eventb.core.IPORoot;
import org.eventb.core.IPSRoot;
import org.eventb.theory.core.ITheoryPathRoot;
import org.eventb.theory.core.ITheoryRoot;
import org.eventb.theory.internal.ui.TheoryUIUtils;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IElementChangedListener;
import org.rodinp.core.IRodinDB;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

import fr.systerel.internal.explorer.model.IModelElement;
import fr.systerel.internal.explorer.model.IModelListener;
import fr.systerel.internal.explorer.navigator.ExplorerUtils;

/**
 * @author RenatoSilva
 *
 */
@SuppressWarnings("restriction")
public class TheoryPathModelController implements IElementChangedListener {
	
	private static TheoryPathModelController instance;
	private static HashMap<IRodinProject, TheoryPathModelProject> projects = new HashMap<IRodinProject, TheoryPathModelProject>();
	
	private final ListenerList<IModelListener> listeners = new ListenerList<IModelListener>();


	/* (non-Javadoc)
	 * @see org.rodinp.core.IElementChangedListener#elementChanged(org.rodinp.core.ElementChangedEvent)
	 */
	@Override
	public void elementChanged(ElementChangedEvent event) {
		TheoryPathDeltaProcessor processor = new TheoryPathDeltaProcessor(
				event.getDelta());
		final ArrayList<IRodinElement> toRefresh = processor.getToRefresh();
		final ArrayList<IRodinElement> toRemove = processor.getToRemove();

		cleanUpModel(toRemove);
		// refresh the model
		for (IRodinElement elem : toRefresh) {
			refreshModel(elem);

		}
		notifyListeners(toRefresh);
	}
	
	public static void createInstance() {
		if (instance == null) {
			instance = new TheoryPathModelController();
		}
	}
	
	public static IModelElement getModelElement(Object element) {
		if (element instanceof IModelElement) {
			return (IModelElement) element;
		}
		if (element instanceof IRodinProject) {
			return projects.get(element);
		}
		if (element instanceof IRodinElement) {
			TheoryPathModelProject project = projects.get(((IRodinElement) element).getRodinProject());
			if (project != null) {
				return project.getModelElement((IRodinElement) element);
			}
		}
		if (ExplorerUtils.DEBUG) {
			System.out.println("Element not found by TheoryPathModelController: "
					+ element);
		}
		return null;
	}
	
	/**
	 * Refreshes the model
	 * 
	 * @param element
	 *            The element to refresh
	 */
	public void refreshModel(IRodinElement element) {
		if (!(element instanceof IRodinDB)) {
			TheoryPathModelProject project = projects.get(element.getRodinProject());
			if (project != null) {
				if (element instanceof IRodinProject) {
					project.needsProcessing = true;
					processProject((IRodinProject) element);
				}
				if (element instanceof ITheoryPathRoot) {
					project.processTheoryPath((ITheoryPathRoot) element);
				}
				if (element instanceof IPORoot) {
					IPORoot root = (IPORoot) element;
					ITheoryPathRoot thyRoot = getTheoryPathRoot(root.getElementName(),
							root.getRodinProject());
					if (thyRoot == null) {
						return;
					}
					if (thyRoot.exists()) {
						ModelTheoryPath theory = getTheoryPath(thyRoot);
						theory.poNeedsProcessing = true;
						theory.processPORoot();
						// process the statuses as well
						theory.psNeedsProcessing = true;
						theory.processPSRoot();
					}
				}
				if (element instanceof IPSRoot) {
					IPSRoot root = (IPSRoot) element;
					ITheoryPathRoot thyRoot = getTheoryPathRoot(root.getElementName(),
							root.getRodinProject());
					if (thyRoot == null) {
						return;
					}
					if (thyRoot.exists()) {
						ModelTheoryPath theory = getTheoryPath(thyRoot);
						theory.psNeedsProcessing = true;
						theory.processPSRoot();
					}
				}
			}
		}
	}
	
	/**
	 * Gets the ModelProject for a given RodinProject
	 * 
	 * @param project
	 *            The RodinProjecct to look for
	 * @return The corresponding ModelProject, if there exists one,
	 *         <code>null</code> otherwise
	 */
	public static TheoryPathModelProject getProject(IRodinProject project) {
		return projects.get(project);
	}

	public static ModelTheoryPath getTheoryPath(ITheoryPathRoot theoryPathRoot) {
		TheoryPathModelProject project = projects.get(theoryPathRoot.getRodinProject());
		if (project != null) {
			return project.getTheoryPath(theoryPathRoot);
		}
		return null;
	}
	
	/**
	 * Processes a RodinProject. Creates a model for this project (Machines,
	 * Contexts, Invariants etc.). Proof Obligations are not included in
	 * processing.
	 * 
	 * @param project
	 *            The Project to process.
	 */
	public static void processProject(IRodinProject project) {
		try {
			TheoryPathModelProject prj;
			if (!projects.containsKey(project)) {
				prj = new TheoryPathModelProject(project);
				projects.put(project, prj);
			}
			prj = projects.get(project);
			// only process if really needed
			if (prj.needsProcessing) {
				ITheoryPathRoot[] theories = getRootTheoryPathChildren(project);
				for (ITheoryPathRoot theory : theories) {
					prj.processTheoryPath(theory);
				}
				prj.needsProcessing = false;
			}
		} catch (RodinDBException e) {
			TheoryUIUtils.log(e, "when processing project " + project);
		}
	}
	
	private ITheoryPathRoot getTheoryPathRoot(String name, IRodinProject proj) {
		ITheoryPathRoot[] roots = null;
		try {
			roots = proj.getRootElementsOfType(ITheoryPathRoot.ELEMENT_TYPE);
		} catch (RodinDBException e) {
			e.printStackTrace();
			return null;
		}
		for (ITheoryPathRoot root : roots) {
			if (root.getElementName().equals(name)) {
				return root;
			}
		}
		return null;
	}

	private void notifyListeners(List<IRodinElement> toRefresh) {
		for (IModelListener listener : listeners) {
			safeNotify(listener, toRefresh);
		}
	}

	private void safeNotify(IModelListener listener,
			List<IRodinElement> toRefresh) {
		try {
			listener.refresh(toRefresh);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Removes the corresponding elements from the model
	 * 
	 * @param toRemove
	 */
	public void cleanUpModel(ArrayList<IRodinElement> toRemove) {
		for (IRodinElement element : toRemove) {
			if (element instanceof ITheoryRoot) {
				removeTheoryPath((ITheoryPathRoot) element);
			}
			if (element instanceof IRodinProject) {
				removeProject((IRodinProject) element);
			}
		}
	}
	
	/**
	 * Removes the corresponding ModelProject from the Model if it was present.
	 * 
	 * @param project
	 *            The Project to remove.
	 */
	public static void removeProject(IRodinProject project) {
		projects.remove(project);
	}

	/**
	 * Removes a ModelTheory from the Model for a given TheoryRoot
	 * 
	 * @param contextRoot
	 *            The ContextRoot to remove
	 */
	public static void removeTheoryPath(ITheoryPathRoot element) {
		TheoryPathModelProject project = projects.get(element.getRodinProject());
		if (project != null) {
			project.removeTheoryPath(element);
		}
	}
	
	private static ITheoryPathRoot[] getRootTheoryPathChildren(IRodinProject project)
			throws RodinDBException {
		return project.getRootElementsOfType(ITheoryPathRoot.ELEMENT_TYPE);
	}

	public static ModelAvailableTheoryProject getAvailableTheoryProject(
			ITheoryPathRoot root, IRodinProject availableTheoryProject) {
		return (ModelAvailableTheoryProject) getTheoryPath(root).getModelElement(availableTheoryProject);
	}

}
