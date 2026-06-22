/*******************************************************************************
 * Copyright (c) 2010, 2020 University of Southampton and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.ui.explorer.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.runtime.ListenerList;
import org.eventb.core.IPORoot;
import org.eventb.core.IPSRoot;
import org.eventb.core.IPSStatus;
import org.eventb.theory.core.ITheoryRoot;
import org.eventb.theory.internal.ui.TheoryUIUtils;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IElementChangedListener;
import org.rodinp.core.IRodinDB;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;
import fr.systerel.internal.explorer.model.IModelElement;
import fr.systerel.internal.explorer.model.IModelListener;
import fr.systerel.internal.explorer.navigator.ExplorerUtils;

/**
 * @author maamria
 * 
 */
@SuppressWarnings("restriction")
public class TheoryModelController implements IElementChangedListener {

	private static TheoryModelController instance;
	private static HashMap<IRodinProject, TheoryModelProject> projects = new HashMap<IRodinProject, TheoryModelProject>();

	private final ListenerList<IModelListener> listeners = new ListenerList<IModelListener>();

	/**
	 * Create this controller and register it in the DataBase for changes.
	 * 
	 * @param viewer
	 */
	public TheoryModelController() {
		RodinCore.addElementChangedListener(this);
	}

	public void addListener(IModelListener listener) {
		listeners.add(listener);
	}

	/**
	 * Removes the corresponding elements from the model
	 * 
	 * @param toRemove
	 */
	public void cleanUpModel(ArrayList<IRodinElement> toRemove) {
		for (IRodinElement element : toRemove) {
			if (element instanceof ITheoryRoot) {
				removeTheory((ITheoryRoot) element);
			}
			if (element instanceof IRodinProject) {
				removeProject((IRodinProject) element);
			}
		}
	}

	/**
	 * React to changes in the database.
	 * 
	 */
	public void elementChanged(ElementChangedEvent event) {

		TheoryDeltaProcessor processor = new TheoryDeltaProcessor(
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

	/**
	 * Refreshes the model
	 * 
	 * @param element
	 *            The element to refresh
	 */
	public void refreshModel(IRodinElement element) {
		if (!(element instanceof IRodinDB)) {
			TheoryModelProject project = projects
					.get(element.getRodinProject());
			if (project != null) {
				if (element instanceof IRodinProject) {
					project.needsProcessing = true;
					processProject((IRodinProject) element);
				}
				if (element instanceof ITheoryRoot) {
					project.processTheory((ITheoryRoot) element);
				}
				if (element instanceof IPORoot) {
					IPORoot root = (IPORoot) element;
					ITheoryRoot thyRoot = getTheoryRoot(root.getElementName(),
							root.getRodinProject());
					if (thyRoot == null) {
						return;
					}
					if (thyRoot.exists()) {
						ModelTheory theory = getTheory(thyRoot);
						theory.poNeedsProcessing = true;
						theory.processPORoot();
						// process the statuses as well
						theory.psNeedsProcessing = true;
						theory.processPSRoot();
					}
				}
				if (element instanceof IPSRoot) {
					IPSRoot root = (IPSRoot) element;
					ITheoryRoot thyRoot = getTheoryRoot(root.getElementName(),
							root.getRodinProject());
					if (thyRoot == null) {
						return;
					}
					if (thyRoot.exists()) {
						ModelTheory theory = getTheory(thyRoot);
						theory.psNeedsProcessing = true;
						theory.processPSRoot();
					}
				}
			}
		}
	}

	public void removeListener(IModelListener listener) {
		listeners.remove(listener);
	}

	private ITheoryRoot getTheoryRoot(String name, IRodinProject proj) {
		ITheoryRoot[] roots = null;
		try {
			roots = proj.getRootElementsOfType(ITheoryRoot.ELEMENT_TYPE);
		} catch (RodinDBException e) {
			e.printStackTrace();
			return null;
		}
		for (ITheoryRoot root : roots) {
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

	public static void createInstance() {
		if (instance == null) {
			instance = new TheoryModelController();
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
			TheoryModelProject project = projects.get(((IRodinElement) element)
					.getRodinProject());
			if (project != null) {
				return project.getModelElement((IRodinElement) element);
			}
		}
		if (ExplorerUtils.DEBUG) {
			System.out.println("Element not found by ModelController: "
					+ element);
		}
		return null;
	}

	/**
	 * Gets the ModelProofObligation for a given IPSStatus
	 * 
	 * @param status
	 *            The IPSStatus to look for
	 * @return The corresponding ModelProofObligation, if there exists one,
	 *         <code>null</code> otherwise
	 */
	public static TheoryModelProofObligation getModelPO(IPSStatus status) {
		TheoryModelProject project = projects.get(status.getRodinProject());
		if (project != null) {
			return project.getProofObligation(status);
		}
		return null;
	}

	/**
	 * Gets the ModelProject for a given RodinProject
	 * 
	 * @param project
	 *            The RodinProjecct to look for
	 * @return The corresponding ModelProject, if there exists one,
	 *         <code>null</code> otherwise
	 */
	public static TheoryModelProject getProject(IRodinProject project) {
		return projects.get(project);
	}

	public static ModelTheory getTheory(ITheoryRoot theoryRoot) {
		TheoryModelProject project = projects.get(theoryRoot.getRodinProject());
		if (project != null) {
			return project.getTheory(theoryRoot);
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
			TheoryModelProject prj;
			if (!projects.containsKey(project)) {
				prj = new TheoryModelProject(project);
				projects.put(project, prj);
			}
			prj = projects.get(project);
			// only process if really needed
			if (prj.needsProcessing) {
				ITheoryRoot[] theories = getRootTheoryChildren(project);
				for (ITheoryRoot theory : theories) {
					prj.processTheory(theory);
				}
				prj.needsProcessing = false;
			}
		} catch (RodinDBException e) {
			TheoryUIUtils.log(e, "when processing project " + project);
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
	public static void removeTheory(ITheoryRoot root) {
		TheoryModelProject project = projects.get(root.getRodinProject());
		if (project != null) {
			project.removeTheory(root);
		}
	}

	private static ITheoryRoot[] getRootTheoryChildren(IRodinProject project)
			throws RodinDBException {
		return project.getRootElementsOfType(ITheoryRoot.ELEMENT_TYPE);
	}
}
