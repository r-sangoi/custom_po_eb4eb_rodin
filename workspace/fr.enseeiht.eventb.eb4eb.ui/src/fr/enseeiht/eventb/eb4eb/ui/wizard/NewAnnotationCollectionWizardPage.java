package fr.enseeiht.eventb.eb4eb.ui.wizard;

import static org.rodinp.core.RodinCore.asRodinElement;

import java.util.Iterator;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eventb.internal.ui.RodinProjectSelectionDialog;
import org.eventb.ui.EventBUIPlugin;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;

public class NewAnnotationCollectionWizardPage extends WizardPage {


	private Text projectText;
	private ISelection selection;
	
	private final String DEFAULT_ANNOTATION_NAME = "Annotation";
	
	protected NewAnnotationCollectionWizardPage(ISelection selection) {
		super("wizardPage");
		setTitle("New Annotation (File?)");
		setDescription("This wizard generate a file providing the  possibility to construct POG Annotations.");
		this.selection = selection;
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 3;
		layout.verticalSpacing = 5;
		Label label = new Label(container, SWT.NULL);
		label.setText("&Project:");

		projectText = new Text(container, SWT.BORDER | SWT.SINGLE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		projectText.setLayoutData(gd);
		projectText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});

		Button button = new Button(container, SWT.PUSH);
		button.setText("Browse...");
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleBrowse();
			}
		});
		initialize();
		dialogChanged();
		setControl(container);
	}
	
	/**
	 * Get the name of the project.
	 * <p>
	 * 
	 * @return The name of the project
	 */
	public String getProjectName() {
		return projectText.getText();
	}
	
	public String getAnnotationName() {
		return DEFAULT_ANNOTATION_NAME;
	}
	
	/**
	 * Ensures that both text fields are set correctly.
	 */
	void dialogChanged() {
		final String projectName = getProjectName();
		String theoryName = getAnnotationName();
		if (projectName.length() == 0) {
			updateStatus("Project must be selected");
			return;
		}

		IRodinProject rodinProject = RodinCore.getRodinDB().getRodinProject(projectName);
		if (!rodinProject.exists()) {
			updateStatus("Project name must be valid");
			return;
		}

		if (rodinProject.isReadOnly()) {
			updateStatus("Project must be writable");
			return;
		}

		if (theoryName.length() == 0) {
			updateStatus("Annotation name must be specified");
			return;
		}
		
		try {
			for(IResource resource: rodinProject.getProject().members()){
				if(resource.getName().startsWith(theoryName)){
					updateStatus("There exists already a file with this name");
					return;
				}
			}
		} catch (CoreException e1) {
			e1.printStackTrace(); //TODO
//			TheoryUIUtils.log(e1, "Unable to retrieve file from project " + rodinProject.getElementName());
		}
		
		updateStatus(null);
	}

	/**
	 * Uses the RODIN project selection dialog to choose the new value for the
	 * project field.
	 */
	void handleBrowse() {
		final String projectName = getProjectName();
		IRodinProject rodinProject;
		if (projectName.equals(""))
			rodinProject = null;
		else
			rodinProject = EventBUIPlugin.getRodinDatabase().getRodinProject(
					projectName);

		RodinProjectSelectionDialog dialog = new RodinProjectSelectionDialog(
				getShell(), rodinProject, false, "Project Selection",
				"Select a RODIN project");
		if (dialog.open() == Window.OK) {
			Object[] result = dialog.getResult();
			if (result.length == 1) {
				projectText.setText(((IRodinProject) result[0])
						.getElementName());
			}
		}
	}

	/**
	 * Tests if the current workbench selection is a suitable container to use.
	 */

	private void initialize() {
		IRodinProject project = null;
		project = getProjectFromSelection();
		
		if (project != null) {
			projectText.setText(project.getElementName());
		} else {
			projectText.setFocus();
		}
	}

	private IRodinProject getProjectFromSelection() {
		if (!(selection instanceof IStructuredSelection))
			return null;
		final Iterator<?> iter = ((IStructuredSelection) selection).iterator();
		while (iter.hasNext()) {
			final Object obj = iter.next();
			final IRodinElement element = asRodinElement(obj);
			if (element != null) {
				return element.getRodinProject();
			}
		}
		return null;
	}
	
	private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}

}
