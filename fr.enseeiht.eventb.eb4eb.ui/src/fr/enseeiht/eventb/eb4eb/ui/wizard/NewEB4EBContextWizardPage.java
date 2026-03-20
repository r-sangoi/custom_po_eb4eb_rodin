package fr.enseeiht.eventb.eb4eb.ui.wizard;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eventb.core.ISCMachineRoot;

public class NewEB4EBContextWizardPage extends WizardPage {

	// The selected machine
	private ISCMachineRoot selection;
	
	// A Text area for entering the project name
	private Text projectText;

	// A Text area for entering the context name
	private Text contextText;

	public NewEB4EBContextWizardPage(ISCMachineRoot selection) {
		super("wizardPage");
		setTitle("New EB4EB Context");
		setDescription("This wizard generate the EB4EB model of a machine.");
		this.selection = selection;
		
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 2;
		layout.verticalSpacing = 9;
		Label label = new Label(container, SWT.NULL);
		label.setText("&Project name:");

		ModifyListener listener = new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		};
		
		projectText = new Text(container, SWT.BORDER | SWT.SINGLE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		projectText.setLayoutData(gd);
		projectText.addModifyListener(listener);
		
		label = new Label(container, SWT.NULL);
		label.setText("&Context name:");

		contextText = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		contextText.setLayoutData(gd);
		contextText.addModifyListener(listener);
		
		initialize();
		dialogChanged();
		setControl(container);
	}

	/**
	 * Set the initial value for the text area.
	 */
	private void initialize() {
		
		projectText.setText(selection.getElementName() + "_project");
//		projectText.selectAll();
		projectText.setFocus();
		
		contextText.setText("ctx_" + selection.getElementName());
	}

	/**
	 * Ensures that input is valid.
	 */
	void dialogChanged() {
		String projectName = getProjectName();
		IResource container = ResourcesPlugin.getWorkspace().getRoot()
				.findMember(new Path(projectName));

		if (projectName.length() == 0) {
			updateStatus("Project name must be specified");
			return;
		}
		if (container != null) {
			updateStatus("A project with this name already exists.");
			return;
		}
		
		String contextName = getContextName();
		if (contextName.length() == 0) {
			updateStatus("Context name must be specified");
			return;
		}
		
		updateStatus(null);
	}

	/**
	 * Update the status of this dialog.
	 * <p>
	 * 
	 * @param message
	 *            A string message
	 */
	private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}

	/**
	 * Get the name of the generated project.
	 * <p>
	 * 
	 * @return The name of the generated project
	 */
	public String getProjectName() {
		return projectText.getText();
	}
	
	/**
	 * Get the name of the generated context.
	 * <p>
	 * 
	 * @return The name of the generated context (without extension)
	 */
	public String getContextName() {
		return contextText.getText();
	}
}
