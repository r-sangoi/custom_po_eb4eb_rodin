package org.eventb.theory.ui.wizard.deploy;

import java.util.Set;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.internal.ui.Messages;
import org.eventb.theory.internal.ui.TheoryUIUtils;

public class UndeployWizardPage extends WizardPage {

	private TableViewer theoriesTableViewer;
	//private Button rebuildCheckButton;
	
	private Set<IDeployedTheoryRoot> deployedRoots;
	
	/**
	 * Create the wizard.
	 */
	public UndeployWizardPage(Set<IDeployedTheoryRoot> deployedRoots) {
		super("wizardPage");
		setTitle(Messages.wizard_undeployTitle);
		setDescription(Messages.wizard_undeployDescription);
		this.deployedRoots = deployedRoots;
	}

	/**
	 * Create contents of the wizard.
	 * 
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		container.setLayout(new GridLayout(4, false));
		Label label = new Label(container, SWT.NONE);
		label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 4, 1));
		label.setText(Messages.wizard_undeployPageMessage);
		new Label(container, SWT.NONE);

		theoriesTableViewer = new TableViewer(container, SWT.BORDER);
		Table table = theoriesTableViewer.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));

		//rebuildCheckButton = new Button(container, SWT.CHECK);
		//rebuildCheckButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 4, 1));
		//rebuildCheckButton.setText(Messages.wizard_rebuild);
		setup();
		setControl(container);
	}

	private void setup() {
		theoriesTableViewer.setContentProvider(new IStructuredContentProvider() {

			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}

			@Override
			public void dispose() {
			}

			@Override
			public Object[] getElements(Object inputElement) {
				@SuppressWarnings("unchecked")
				Set<IDeployedTheoryRoot> input = (Set<IDeployedTheoryRoot>) inputElement;
				return input.toArray();
			}
		});
		theoriesTableViewer.setLabelProvider(new LabelProvider() {

			@Override
			public Image getImage(Object element) {
				IDeployedTheoryRoot root = (IDeployedTheoryRoot) element;
				return TheoryUIUtils.getTheoryImage(root);
			}

			@Override
			public String getText(Object element) {
				IDeployedTheoryRoot root = (IDeployedTheoryRoot) element;
				return root.getElementName();
			}
		});
		theoriesTableViewer.setInput(deployedRoots);
		//rebuildCheckButton.setSelection(true);
	}
	
/*	public boolean rebuildProject(){
		return rebuildCheckButton.getSelection();
	}*/

}
