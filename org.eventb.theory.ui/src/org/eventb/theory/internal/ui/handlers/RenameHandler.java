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
import static org.eclipse.ui.handlers.HandlerUtil.getActiveShell;
import static org.eclipse.ui.handlers.HandlerUtil.getCurrentSelection;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eventb.theory.core.DatabaseUtilities;
import org.eventb.theory.core.ITheoryRoot;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * The handler for the 'rename' command on theories.
 */
public class RenameHandler extends AbstractHandler {

	private static class TheoryNameValidator implements IInputValidator {

		private IRodinProject project;

		public TheoryNameValidator(IRodinProject project) {
			this.project = project;
		}

		@Override
		public String isValid(String newText) {
			IRodinFile file = project.getRodinFile(DatabaseUtilities.getTheoryFullName(newText));
			if (file != null && file.exists()) {
				return "File name " + newText + " already exists.";
			}
			return null;
		}
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final ISelection currentSelection = getCurrentSelection(event);
		if (!(currentSelection instanceof IStructuredSelection)) {
			return null;
		}
		final Shell shell = getActiveShell(event);
		if (shell == null) {
			return null;
		}
		final ITheoryRoot root = getSelectedRoot((IStructuredSelection) currentSelection);
		if (root == null) {
			return null;
		}
		final IRodinProject prj = root.getRodinProject();
		final String fileName = root.getElementName();
		final InputDialog dialog = new InputDialog(shell, "Rename theory", "New name of the theory:", fileName, new TheoryNameValidator(prj));
		dialog.open();
		if (dialog.getReturnCode() == InputDialog.CANCEL)
			return null;

		final String newBareName = dialog.getValue();
		assert newBareName != null;

		final ProgressMonitorDialog progress = new ProgressMonitorDialog(shell);
		runWithRunnableContext(progress, shell, new RenameTask(root, newBareName));

		return null;
	}

	public static void runWithRunnableContext(IRunnableContext context, Shell shell, final IRunnableWithProgress op) {
		try {
			context.run(true, true, op);
		} catch (InterruptedException exception) {
			Thread.currentThread().interrupt();
			return;
		} catch (InvocationTargetException exception) {
			final Throwable realException = exception.getTargetException();
			realException.printStackTrace();
			String message = realException.getLocalizedMessage();
			MessageDialog.openError(shell, "Unexpected error. See log for details.", message);
			return;
		}
	}

	/**
	 * Return the selected element if it is a {@link ITheoryRoot}. Else return null.
	 */
	private ITheoryRoot getSelectedRoot(IStructuredSelection selection) {
		if (selection.size() != 1)
			return null;

		final Object obj = selection.getFirstElement();
		if (!(obj instanceof IInternalElement))
			return null;

		final IInternalElement root = (IInternalElement) obj;
		if (!(root instanceof ITheoryRoot))
			return null;

		return (ITheoryRoot) root;
	}

	private static class RenameTask implements IRunnableWithProgress {

		private final ITheoryRoot root;
		private final String newBareName;

		public RenameTask(ITheoryRoot root, String newBareName) {
			this.root = root;
			this.newBareName = newBareName;
		}

		@Override
		public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
			final String msg = "Renaming " + root.getElementName() + " to " + newBareName;
			final SubMonitor subMonitor = convert(monitor, msg, 8);
			if (monitor.isCanceled())
				return;
			final IWorkspaceRunnable op = new RenameOperation(root, newBareName);
			try {
				RodinCore.run(op, subMonitor.newChild(8));
			} catch (RodinDBException e) {
				throw new InvocationTargetException(e);
			} finally {
				monitor.done();
			}
		}

	}

}
