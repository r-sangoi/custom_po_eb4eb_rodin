/*******************************************************************************
 * Copyright (c) 2006, 2020 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *     Systerel - added default name
 *     Systerel - added renaming of proof files
 *     Systerel - added renaming of occurrences
 *     Systerel - refactored to use commands and handlers
 *     CentraleSupélec - imported in theory plug-in and adapted
 *******************************************************************************/
package org.eventb.theory.internal.ui.handlers;

import static org.eclipse.core.runtime.SubMonitor.convert;
import static org.eclipse.ui.PlatformUI.getWorkbench;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IEventBProject;
import org.eventb.theory.core.DatabaseUtilities;
import org.eventb.theory.core.ITheoryRoot;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

/**
 * Rename a theory.
 */
public class RenameOperation implements IWorkspaceRunnable {

	private final IRodinProject prj;
	private final IRodinFile file;
	private final ITheoryRoot root;
	private final String newBareName;

	/**
	 * Create a rename operation.
	 *
	 * Call {@link run()} to actually execute the renaming.
	 *
	 * @param root theory to be renamed
	 * @param newBareName new name of the theory
	 */
	public RenameOperation(ITheoryRoot root, String newBareName) {
		this.prj = root.getRodinProject();
		this.file = root.getRodinFile();
		this.root = root;
		this.newBareName = newBareName;
	}

	@Override
	public void run(IProgressMonitor monitor) throws RodinDBException {
		monitor.subTask("Performing renaming...");
		// Two default renamings
		final SubMonitor subMonitor = convert(monitor, 2);
		renameTheoryFile(subMonitor.newChild(1));
		renamePRFile(subMonitor.newChild(1));
	}

	private static boolean cancelRenaming(String newName) {
		final String message = "There are already proofs for theory " + newName + " in this project.\\n"
				+ "By continuing this rename operation, these proofs will be lost.\\n"
				+ "Do you want to preserve these proofs and cancel this renaming?";
		class Question implements Runnable {
			private boolean response;

			@Override
			public void run() {
				response = MessageDialog.openQuestion(getWorkbench().getModalDialogShellProvider().getShell(), null,
						message);
			}

			public boolean getResponse() {
				return response;
			}
		}
		final Question question = new Question();
		PlatformUI.getWorkbench().getDisplay().syncExec(question);
		return question.getResponse();
	}

	private void renameTheoryFile(SubMonitor monitor) throws RodinDBException {
		final String newName = DatabaseUtilities.getTheoryFullName(newBareName);
		file.rename(newName, false, monitor);
	}

	private void renamePRFile(IProgressMonitor monitor) throws RodinDBException {
		final IEventBProject evbProject = prj.getAdapter(IEventBProject.class);
		final IRodinFile proofFile = evbProject.getPRFile(root.getElementName());
		if (proofFile.exists()) {
			final String newName = EventBPlugin.getPRFileName(newBareName);
			final IRodinFile pRFile = evbProject.getPRFile(newBareName);
			if (pRFile.exists()) {
				if (cancelRenaming(newBareName)) {
					return;
				}
				proofFile.rename(newName, true, monitor);
			} else {
				proofFile.rename(newName, false, monitor);
			}
		}
	}

}
