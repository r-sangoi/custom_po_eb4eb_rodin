/**
 * 
 */
package org.eventb.theory.language.ui.wizard;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eventb.core.IConfigurationElement;
import org.eventb.theory.core.DatabaseUtilities;
import org.eventb.theory.core.DatabaseUtilitiesTheoryPath;
import org.eventb.theory.internal.ui.TheoryUIUtils;
import org.eventb.theory.ui.plugin.TheoryUIPlugIn;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * @author RenatoSilva
 *
 */
public class NewTheoryPathWizard extends Wizard implements INewWizard {
	
	public static final String WIZARD_ID = TheoryUIPlugIn.PLUGIN_ID
			+ ".newTheoryPathWizard";

	private ISelection selection;
	private NewTheoryPathWizardPage page;

	public NewTheoryPathWizard() {
		super();
		setNeedsProgressMonitor(true);
	}
	
	@Override
	public void addPages() {
		setWindowTitle("New Theory");
		page = new NewTheoryPathWizardPage(selection);
		addPage(page);
	}


	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}

	@Override
	public boolean performFinish() {
		final String fileName = DatabaseUtilitiesTheoryPath.getTheoryPathFullName(page.getTheoryName());
		final String projectName = page.getProjectName();
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor)
					throws InvocationTargetException {
				try {
					doFinish(fileName, projectName, monitor);
				} catch (CoreException e) {
					throw new InvocationTargetException(e);
				} finally {
					monitor.done();
				}
			}
		};
		try {
			getContainer().run(true, false, op);
		} catch (InterruptedException e) {
			return false;
		} catch (InvocationTargetException e) {
			Throwable realException = e.getTargetException();
			MessageDialog.openError(getShell(), "Error", realException
					.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * The worker method. This will create a new theory file (provided that it does
	 * not exist before).
	 * <p>
	 * 
	 * @param projectName
	 *            the name of the project
	 * @param fileName
	 * 			  the name of the theory
	 * @param categories
	 * 			  the categories of the theory
	 * @param monitor
	 *            a progress monitor
	 * @throws RodinDBException
	 *             a core exception throws when creating a new project
	 */
	void doFinish(final String fileName, final String projectName,
			IProgressMonitor monitor) throws CoreException {
		monitor.beginTask("Creating " + fileName, 2);
		// Creating a project handle
		final IRodinProject rodinProject = DatabaseUtilities.getRodinProject(projectName);
		RodinCore.run(new IWorkspaceRunnable() {
			public void run(IProgressMonitor pMonitor) throws CoreException {
				final IRodinFile rodinFile = rodinProject.getRodinFile(fileName);
				rodinFile.create(false, pMonitor);
				final IInternalElement rodinRoot = rodinFile.getRoot();
				((IConfigurationElement) rodinRoot).setConfiguration(DatabaseUtilitiesTheoryPath.THEORY_PATH_CONFIGURATION, pMonitor);
				rodinFile.save(null, true);
			}

		}, monitor);

		monitor.worked(1);

		monitor.setTaskName("Opening file for editing...");
		getShell().getDisplay().asyncExec(new Runnable() {
			public void run() {
				TheoryUIUtils.linkToTheoryPathEventBEditor(rodinProject.getRodinFile(fileName));
			}
		});
		monitor.worked(1);
	}

}
