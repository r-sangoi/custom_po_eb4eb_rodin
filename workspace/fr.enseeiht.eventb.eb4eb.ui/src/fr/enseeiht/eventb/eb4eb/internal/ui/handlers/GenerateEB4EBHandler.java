package fr.enseeiht.eventb.eb4eb.internal.ui.handlers;

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

import fr.enseeiht.eventb.eb4eb.ui.wizard.NewEB4EBContextWizard;

public class GenerateEB4EBHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final Shell shell = getActiveShell(event);
		if (shell == null) {
			return null;
		}
		ISelection selection = getCurrentSelection(event);
		final NewEB4EBContextWizard wizard = new NewEB4EBContextWizard();
		
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
