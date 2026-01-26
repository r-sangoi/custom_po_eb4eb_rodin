package fr.enseeiht.eventb.eb4eb.ui.wizard;

import static org.eclipse.ui.wizards.newresource.BasicNewResourceWizard.selectAndReveal;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
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
import org.eventb.ui.EventBUIPlugin;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

import fr.enseeiht.eventb.eb4eb.DatabaseUtilitiesAnnotation;
import fr.enseeiht.eventb.eb4eb.internal.ui.EB4EBUIUtils;
import fr.enseeiht.eventb.eb4eb.ui.EB4EBUIPlugin;

public class NewAnnotationCollectionWizard extends Wizard implements INewWizard {

	/**
	 * The identifier of the new EB4EB context wizard (value
	 * <code>"fr.enseeiht.eventb.eb4eb.ui.wizards.NewAnnotation"</code>).
	 */
	public static final String WIZARD_ID = EB4EBUIPlugin.PLUGIN_ID
			+ ".wizards.NewAnnotation";
	
	// The wizard page.
	private NewAnnotationCollectionWizardPage page;

	// The selection when the wizard is launched.
	private ISelection selection;

	// The workbench when the wizard is launched
	private IWorkbench workbench;

	/**
	 * Constructor: This wizard needs a progress monitor.
	 */
	public NewAnnotationCollectionWizard() {
		super();
		setNeedsProgressMonitor(true);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.IWizard#addPages()
	 */
	@Override
	public void addPages() {
		page = new NewAnnotationCollectionWizardPage(selection);
		addPage(page);
	}
	
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.workbench = workbench;
		this.selection = selection;
	}

	@Override
	public boolean performFinish() {
		final String fileName = page.getAnnotationName() + ".ael";
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
		
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject newProject = root.getProject(projectName);
		selectAndReveal(newProject, workbench.getActiveWorkbenchWindow());
		return true;
	}
	
	/**
	 * The worker method. This will create a new annotation file (provided that it does
	 * not exist before).
	 * <p>
	 * 
	 * @param projectName
	 *            the name of the project
	 * @param fileName
	 * 			  the name of the annotation
	 * @param monitor
	 *            a progress monitor
	 * @throws RodinDBException
	 *             a core exception throws when creating a new project
	 */
	void doFinish(final String fileName, final String projectName,
			IProgressMonitor monitor) throws CoreException {
		monitor.beginTask("Creating " + fileName, 2);
		// Creating a project handle
		final IRodinProject rodinProject = EventBUIPlugin.getRodinDatabase().getRodinProject(projectName);
		RodinCore.run(new IWorkspaceRunnable() {
			public void run(IProgressMonitor pMonitor) throws CoreException {
				final IRodinFile rodinFile = rodinProject.getRodinFile(fileName);
				rodinFile.create(false, pMonitor);
				final IInternalElement rodinRoot = rodinFile.getRoot();
				((IConfigurationElement) rodinRoot).setConfiguration(DatabaseUtilitiesAnnotation.ANNOTATION_CONFIGURATION, pMonitor);
				rodinFile.save(null, true);
			}

		}, monitor);

		monitor.worked(1);

		monitor.setTaskName("Opening file for editing...");
		getShell().getDisplay().asyncExec(new Runnable() {
			public void run() {
				EB4EBUIUtils.linkToEventBEditor(rodinProject.getRodinFile(fileName));
			}
		});
		monitor.worked(1);
	}

}
