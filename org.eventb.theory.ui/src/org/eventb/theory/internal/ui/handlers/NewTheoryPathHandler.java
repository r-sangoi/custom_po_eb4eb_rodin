/*******************************************************************************
 * Copyright (c) 2020 CentraleSupélec.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.internal.ui.handlers;

import static org.eclipse.ui.PlatformUI.getWorkbench;
import static org.eclipse.ui.handlers.HandlerUtil.getActiveShell;
import static org.eclipse.ui.handlers.HandlerUtil.getCurrentSelection;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Shell;
import org.eventb.theory.language.ui.wizard.NewTheoryPathWizard;

/**
 * Command handler to build a wizard for creating a TheoryPath file.
 *
 * @author Guillaume Verdier
 */
public class NewTheoryPathHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final Shell shell = getActiveShell(event);
		if (shell == null) {
			return null;
		}
		ISelection selection = getCurrentSelection(event);
		final NewTheoryPathWizard wizard = new NewTheoryPathWizard();
		if (selection instanceof IStructuredSelection) {
			wizard.init(getWorkbench(), (IStructuredSelection) selection);
		} else {
			wizard.init(getWorkbench(), StructuredSelection.EMPTY);
		}
		BusyIndicator.showWhile(shell.getDisplay(), new Runnable() {
			@Override
			public void run() {
				final WizardDialog dialog = new WizardDialog(shell, wizard);
				dialog.create();
				dialog.open();
			}
		});
		return null;
	}

}
