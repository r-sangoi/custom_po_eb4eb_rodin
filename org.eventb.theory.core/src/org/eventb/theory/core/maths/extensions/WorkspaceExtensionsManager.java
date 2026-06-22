/*******************************************************************************
 * Copyright (c) 2010, 2020 University of Southampton and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University of Southampton - initial API and implementation
 *     Systerel -  refactored after imports only concern deployed theories
 *     University of Southampton - Caching the formula factory
 *     CentraleSupélec - rewrite cache using FormulaExtensionCache
 *******************************************************************************/
package org.eventb.theory.core.maths.extensions;

import static org.eventb.theory.core.DatabaseUtilities.getDeployedTheories;
import static org.eventb.theory.core.DatabaseUtilities.getNonTempSCTheoryPaths;
import static org.eventb.theory.core.TheoryHierarchyHelper.getImportedTheories;
import static org.eventb.theory.core.TheoryHierarchyHelper.getTheoryPathImports;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IEventBRoot;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.theory.core.DatabaseUtilities;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.IFormulaExtensionsSource;
import org.eventb.theory.core.ISCTheoryPathRoot;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.ITheoryRoot;
import org.eventb.theory.core.maths.extensions.dependencies.DeployedTheoriesGraph;
import org.eventb.theory.core.util.CoreUtilities;
import org.eventb.theory.internal.core.util.FormulaExtensionCache;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IElementChangedListener;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;

/**
 * Manager for theory dependencies and formula extensions throughout the
 * workspace.
 * 
 * @author maamria
 * @author beauger
 * @author htson - Caching the formula factory for Event-B roots
 *               - Added debugging option
 * @version
 * @see
 * @since 1.0
 */
public class WorkspaceExtensionsManager implements IElementChangedListener {

	// Extension for conditional expression COND.
	private static final Set<IFormulaExtension> COND_EXTS = Collections
			.<IFormulaExtension> singleton(FormulaFactory.getCond());


	// The basic formula factory which contains only COND extension. 
	private final FormulaFactory basicFactory = FormulaFactory
			.getInstance(COND_EXTS);

	// The private singleton instance.
	private static final WorkspaceExtensionsManager INSTANCE = new WorkspaceExtensionsManager();

	// cache for SC theory direct dependencies (not a closure, ask deployedGraph
	// for that)
	private final Map<ISCTheoryRoot, Set<IDeployedTheoryRoot>> scDependencies = new HashMap<ISCTheoryRoot, Set<IDeployedTheoryRoot>>();

	// cache for deployed theory dependency graph
	private final DeployedTheoriesGraph deployedGraph = new DeployedTheoriesGraph();

	// cache for extensions provided by theories (both SC and deployed)
	private final Map<IFormulaExtensionsSource, Set<IFormulaExtension>> extensions = new HashMap<IFormulaExtensionsSource, Set<IFormulaExtension>>();

	// TODO add TheoryPath cache
	// Cache for extensions for Event-B roots.
	private final FormulaExtensionCache extensionsCache = new FormulaExtensionCache();
	
	private final Queue<IDeployedTheoryRoot> changedDeployed = new ConcurrentLinkedQueue<IDeployedTheoryRoot>();
	private final Queue<ISCTheoryRoot> changedSC = new ConcurrentLinkedQueue<ISCTheoryRoot>();

	/**
	 * The debug flag. This is set by the option when the plug-in is launched.
	 * Client should not try to reset this flag.
	 * @author htson
	 * @since 4.0.0
	 */
	public static boolean DEBUG = false;

	/**
	 * <p>
	 * Private default constructor for singleton class.
	 * </p>
	 * <ol>
	 * <li>Register itself as a element-changed listener of the Rodin Database.</li>
	 * <li>Initialise the deployed graph.
	 * </ol>
	 */
	private WorkspaceExtensionsManager() {
		RodinCore.addElementChangedListener(this);
		initDeployedGraph();
	}

	/**
	 * Utility method to initialise the deployed graph.
	 */
	private void initDeployedGraph() {
		try {
			for (IRodinProject project : RodinCore.getRodinDB()
					.getRodinProjects()) {
				for (IDeployedTheoryRoot deployed : getDeployedTheories(project)) {
					deployedGraph.addElement(deployed);
				}
			}
		} catch (CoreException e) {
			CoreUtilities.log(e, "While initializing deployed theory graph");
		}
	}

	/**
	 * Returns the shared instance.
	 * @return the shared instance.
	 */
	public static WorkspaceExtensionsManager getInstance() {
		return INSTANCE;
	}

	// unique get/put access point for 'extensions' field
	private Set<IFormulaExtension> fetchExtensions(
			IFormulaExtensionsSource source,
			Set<IFormulaExtension> requiredExtns) throws CoreException {
		Set<IFormulaExtension> extns = extensions.get(source);
		if (extns == null) {
			final FormulaFactory factory = basicFactory
					.withExtensions(requiredExtns);
			final FormulaExtensionsLoader loader = new FormulaExtensionsLoader(
					source, factory);
			extns = loader.load();
			extensions.put(source, extns);
		}
		return extns;
	}

	// unique access point for 'scDependencies' field
	private Set<IDeployedTheoryRoot> fetchSCDependencies(ISCTheoryRoot scTheory)
			throws CoreException {
		while (!changedSC.isEmpty()) {
			final ISCTheoryRoot changedRoot = changedSC.remove();
			// forget what we used to know about that theory
			extensions.remove(changedRoot);
			scDependencies.put(changedRoot, getImportedTheories(changedRoot));
		}
		Set<IDeployedTheoryRoot> requiredTheories = scDependencies
				.get(scTheory);
		if (requiredTheories == null) {
			requiredTheories = getImportedTheories(scTheory);
			scDependencies.put(scTheory, requiredTheories);
		}
		return requiredTheories;
	}

	// unique access point for 'deployedGraph' field
	private List<IDeployedTheoryRoot> fetchDependencies(
			IDeployedTheoryRoot deployedRoot) throws CoreException {
		if (!deployedRoot.exists()) {
			return Collections.emptyList();
		}
		synchronized (deployedGraph) {
			while(!changedDeployed.isEmpty()) {
				final IDeployedTheoryRoot deployed = changedDeployed.remove();
				// forget what we used to know about that theory
				extensions.remove(deployedRoot);
				if (deployed.exists()) {
					deployedGraph.addElement(deployed);
				} else {
					deployedGraph.removeElement(deployed);
				}
			}
			return deployedGraph.getUpperSet(deployedRoot);
		}
	}

	private Set<IFormulaExtension> getDeployedExtensionClosure(
			IDeployedTheoryRoot deployedRoot) throws CoreException {
		final Set<IFormulaExtension> extns = new LinkedHashSet<IFormulaExtension>();
		List<IDeployedTheoryRoot> dependencies = fetchDependencies(deployedRoot);
		for (IDeployedTheoryRoot neededTheory : dependencies) {
			extns.addAll(fetchExtensions(neededTheory, extns));
		}
		extns.addAll(fetchExtensions(deployedRoot, extns));
		return extns;
	}

	private Set<IFormulaExtension> getExtensionClosure(
			Iterable<IDeployedTheoryRoot> deployedRoots) throws CoreException {
		final Set<IFormulaExtension> extns = new LinkedHashSet<IFormulaExtension>();
		for (IDeployedTheoryRoot deployedRoot : deployedRoots) {
			extns.addAll(getDeployedExtensionClosure(deployedRoot));
		}
		return extns;
	}

	private Set<IFormulaExtension> getSCExtensionClosure(ISCTheoryRoot scTheory)
			throws CoreException {
		// @htson: (Bug fixed) Using LinkedHashSet instead of HashSet so that
		// the order of extensions reflect the theory dependencies.
		final Set<IFormulaExtension> extns = new LinkedHashSet<IFormulaExtension>();
		final Set<IDeployedTheoryRoot> requiredTheories = fetchSCDependencies(scTheory);
		extns.addAll(getExtensionClosure(requiredTheories));
		extns.addAll(fetchExtensions(scTheory, extns));
		return extns;
	}

	/**
	 * Returns formula extensions needed to parse formulas within the given file
	 * root.
	 * 
	 * @param root
	 *            a file root
	 * @return a set of extensions
	 * @throws CoreException
	 *             if something bad happens
	 */
	public synchronized Set<IFormulaExtension> getFormulaExtensions(
			IEventBRoot root) throws CoreException {
		if (extensionsCache.containsKey(root)) {
			return extensionsCache.get(root);
		}
		
		if (!DatabaseUtilities.originatedFromTheory(root.getRodinFile())) {
			final IRodinProject project = root.getRodinProject();
			final ISCTheoryPathRoot[] paths = getNonTempSCTheoryPaths(project);
			// theories cannot depend on theory path, so the presence of a
			// theory path should not change the input language of a theory
			if (paths.length == 1) {
				final List<IDeployedTheoryRoot> deployedRoots = getTheoryPathImports(paths[0]);
				Set<IFormulaExtension> allExtensions = getExtensionClosure(deployedRoots);
				extensionsCache.put(root, allExtensions);
				return allExtensions;
			}
			Set<IFormulaExtension> emptySet = Collections.emptySet();
			extensionsCache.put(root, emptySet);
			return emptySet;
		}

		final Set<IFormulaExtension> extns = new LinkedHashSet<IFormulaExtension>(
				COND_EXTS);
		extensionsCache.put(root, extns);

		// case unchecked Theory: no imports resolved => basic extension set
		if (root instanceof ITheoryRoot) {
			return extns;
		}

		// case Deployed Theory: basic set + from needed theories + from given
		// theory
		if (root instanceof IDeployedTheoryRoot) {
			extns.addAll(getDeployedExtensionClosure((IDeployedTheoryRoot) root));
			return extns;
		}

		// case SC Theory or other theory file (bpo, bps, bpr):
		// basic set + from needed theories + from SC theory
		final ISCTheoryRoot scRoot = DatabaseUtilities.getSCTheory(
				root.getComponentName(), root.getRodinProject());
		if (!scRoot.exists()) {
			// TODO try tmp SC ?
			throw new IllegalStateException("Cannot compute extensions for "
					+ root.getPath() + " without an associated SC theory");
		}

		extns.addAll(getSCExtensionClosure(scRoot));
		return extns;
	}

	/**
	 * Returns the closure of deployed theories imported by the given SC theory.
	 * 
	 * @param scRoot
	 *            a SC root (must not be a deployed root)
	 * @return
	 * @throws CoreException
	 */
	public synchronized List<IDeployedTheoryRoot> getTheoryImportClosure(
			ISCTheoryRoot scRoot) throws CoreException {
		if (scRoot instanceof IDeployedTheoryRoot) {
			throw new IllegalArgumentException(
					"Must be called with a SC theory, but was "
							+ scRoot.getPath());
		}
		final Set<IDeployedTheoryRoot> imported = new LinkedHashSet<IDeployedTheoryRoot>();
		for (IDeployedTheoryRoot neededTheory : fetchSCDependencies(scRoot)) {
			imported.addAll(fetchDependencies(neededTheory));
			imported.add(neededTheory);
		}
		return new ArrayList<IDeployedTheoryRoot>(imported);
	}

	private void processDelta(IRodinElementDelta delta) {
		IRodinElement element = delta.getElement();
		IRodinElementDelta[] affected = delta.getAffectedChildren();

		if (element instanceof IRodinFile) {
			final IRodinFile file = (IRodinFile) element;
			if (file.getRootElementType() == IDeployedTheoryRoot.ELEMENT_TYPE) {
				changedDeployed.add((IDeployedTheoryRoot) file.getRoot());
			}
			IInternalElement root = file.getRoot();
			if (root instanceof IEventBRoot) {
				extensionsCache.remove((IEventBRoot)root);
			}
		} else {
			for (IRodinElementDelta d : affected) {
				processDelta(d);
			}
		}
	}

	@Override
	public void elementChanged(ElementChangedEvent event) {
		final IRodinElementDelta delta = event.getDelta();
		processDelta(delta);
	}

	/**
	 * Informs that the given SC theory has changed and reloads its extensions.
	 * 
	 * @param scTheory
	 *            the changed SC theory root
	 * @throws CoreException
	 *             if extensions could not be reloaded
	 */
	public void scTheoryChanged(ISCTheoryRoot scTheory) throws CoreException {
		changedSC.add(scTheory);
	}

}
