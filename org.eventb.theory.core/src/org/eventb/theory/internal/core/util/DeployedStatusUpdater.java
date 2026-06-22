/*******************************************************************************
 * Copyright (c) 2013, 2020 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.theory.internal.core.util;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.sc.modules.ModulesUtils;
import org.eventb.theory.core.util.CoreUtilities;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * Updater for outdated status of deployed theory files.
 * <p>
 * Listens to resource changes, puts them into a blocking queue that for delayed
 * processing in the job. Processes statically checked theories: if the new hash
 * of the file is different from the one stored in the corresponding deployed
 * theory file, the deployed file becomes outdated.
 * </p>
 * 
 * @author beauger
 */
public class DeployedStatusUpdater extends Job {

	private static final DeployedStatusUpdater INSTANCE = new DeployedStatusUpdater();

	private final BlockingQueue<IResourceDelta> deltas = new LinkedBlockingQueue<IResourceDelta>();

	private DeployedStatusUpdater() {
		// singleton
		super("Update deployed theory outdated status");
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		boolean stop = false;
		while (!stop) {
			IResourceDelta delta;
			try {
				delta = deltas.take();
				process(delta, monitor);
			} catch (InterruptedException e) {
				stop = true;
			} catch (RodinDBException e) {
				CoreUtilities.log(e,
						"While updating deployed theory outdated status");
			}
		}
		return Status.CANCEL_STATUS;
	}

	private void process(IResourceDelta delta, IProgressMonitor monitor)
			throws RodinDBException {
		final IResource resource = delta.getResource();
		if (resource instanceof IFile) {
			final IFile file = (IFile) resource;
			if (!file.exists()) {
				return;
			}
			final IRodinFile rodinFile = RodinCore.valueOf(file);
			if (rodinFile == null) {
				return;
			}
			final IInternalElement root = rodinFile.getRoot();
			if (root.getElementType() != ISCTheoryRoot.ELEMENT_TYPE) {
				return;
			}
			final ISCTheoryRoot scRoot = (ISCTheoryRoot) root;
			final IDeployedTheoryRoot deplRoot = scRoot.getDeployedTheoryRoot();
			if (!deplRoot.exists()) {
				return;
			}

			final boolean prevOutdated = getOutdatedAttribute(deplRoot);

			final String hash = ModulesUtils.ComputeHashValue(file);
			final boolean curOutdated = isOutdated(deplRoot, hash);

			if (curOutdated != prevOutdated) {
				deplRoot.setOutdated(curOutdated, monitor);
				deplRoot.getRodinFile().save(monitor, true);
			}
		} else {
			for (IResourceDelta child : delta.getAffectedChildren()) {
				process(child, monitor);
			}
		}

	}

	private static boolean getOutdatedAttribute(IDeployedTheoryRoot deplRoot)
			throws RodinDBException {
		return deplRoot.hasOutdatedAttribute() && deplRoot.isOutdated();
	}

	private static boolean isOutdated(IDeployedTheoryRoot deplRoot,
			String currentSCHash) throws RodinDBException {
		if (!deplRoot.hasModificationHashValueAttribute()) {
			return true;
		}
		final String deplHash = deplRoot.getModificationHashValue();
		return !deplHash.equals(currentSCHash);
	}

	public static DeployedStatusUpdater getInstance() {
		return INSTANCE;
	}

	/**
	 * Initializes and schedules this job.
	 * <p>
	 * This job is set as system job with a low DECORATE priority.
	 * </p>
	 */
	public void initAndSchedule() {
		setSystem(true);
		setPriority(DECORATE);
		ResourcesPlugin.getWorkspace().addResourceChangeListener(
				new IResourceChangeListener() {

					@Override
					public void resourceChanged(IResourceChangeEvent event) {
						deltas.add(event.getDelta());
					}
				}, IResourceChangeEvent.POST_CHANGE);
		schedule();
	}
}
