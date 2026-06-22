package org.eventb.theory.ui.wizard.deploy;

import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eventb.theory.core.DatabaseUtilities;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.TheoryHierarchyHelper;
import org.eventb.theory.internal.ui.Messages;
import org.eventb.theory.internal.ui.TheoryUIUtils;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

public class DeployWizardPageOne extends WizardPage {
	private TableViewer theoriesTableViewer;
	private ComboViewer projectComboViewer;

	private IRodinProject rodinProject;
	private ISCTheoryRoot[] selectedTheories;

	/**
	 * Create the wizard.
	 */
	public DeployWizardPageOne() {
		super("deployWizardPage");
		setTitle(Messages.wizard_deployTitle);
		//setDescription(Messages.wizard_deployDescription);
	}

	/**
	 * Create contents of the wizard.
	 * 
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		container.setLayout(new GridLayout(2, false));

		Label projectLabel = new Label(container, SWT.SHADOW_IN);
		projectLabel.setText("Project");

		projectComboViewer = new ComboViewer(container, SWT.READ_ONLY);
		Combo projectCombo = projectComboViewer.getCombo();
		projectCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label theoryLabel = new Label(container, SWT.NONE);
		theoryLabel.setText("Theories");
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);

		theoriesTableViewer = new TableViewer(container, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
		Table theoriesTable = theoriesTableViewer.getTable();
		GridData gd_theoriesTable = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		gd_theoriesTable.heightHint = 145;
		theoriesTable.setLayoutData(gd_theoriesTable);
		setup();
		dialogChanged();
		setControl(container);
	}

	private void setup() {
		projectComboViewer.setContentProvider(new ArrayContentProvider());
		projectComboViewer.setInput(getProjects());
		projectComboViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				IRodinProject rodinProject = (IRodinProject) element;
				return rodinProject.getElementName();
			}
		});
		projectComboViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IRodinProject selectedProject = (IRodinProject) ((StructuredSelection) projectComboViewer
						.getSelection()).getFirstElement();

				if (selectedProject != null && selectedProject.equals(rodinProject)) {
					return;
				}
				if (selectedProject != null && !selectedProject.equals(rodinProject)) {
					rodinProject = selectedProject;
					java.util.List<ISCTheoryRoot> deployableSCTheories;
					try {
						deployableSCTheories = DatabaseUtilities.getDeployableSCTheories(rodinProject);
						theoriesTableViewer.setInput(deployableSCTheories
								.toArray(new ISCTheoryRoot[deployableSCTheories.size()]));
					} catch (CoreException e) {
						TheoryUIUtils.log(e, "unable to fetch SC theories for project " + rodinProject.getElementName());
						theoriesTableViewer.setInput(new ISCTheoryRoot[0]);
					}
				} else {
					rodinProject = null;
				}
				selectedTheories = null;
				dialogChanged();
			}
		});
		theoriesTableViewer.setContentProvider(new ArrayContentProvider());

		theoriesTableViewer.setLabelProvider(new LabelProvider() {

			@Override
			public Image getImage(Object element) {
				ISCTheoryRoot root = (ISCTheoryRoot) element;
				return TheoryUIUtils.getTheoryImage(root);
			}

			@Override
			public String getText(Object element) {
				ISCTheoryRoot root = (ISCTheoryRoot) element;
				return root.getElementName();
			}
		});
		theoriesTableViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent arg0) {
				StructuredSelection selection = (StructuredSelection) theoriesTableViewer.getSelection();
				if (selection == null) {
					selectedTheories = null;
				}
				Object[] selectedArray = selection.toArray();
				selectedTheories = new ISCTheoryRoot[selectedArray.length];
				int i = 0;
				for (Object obj : selectedArray) {
					selectedTheories[i++] = (ISCTheoryRoot) obj;
				}
				dialogChanged();
			}
		});
	}

	protected void dialogChanged() {
		if (rodinProject == null) {
			updateStatus(Messages.wizard_errorProjMustBeSelected);
			return;
		}
		if (selectedTheories == null) {
			updateStatus(Messages.wizard_errorTheoriesMustBeSelected);
			return;
		}
		if (getSelectedTheories().size() == 0){
			updateStatus(Messages.wizard_errorUndefined);
			return;
		}
		updateStatus(null);
	}

	private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}

	private IRodinProject[] getProjects() {
		try {
			IRodinProject[] projects = RodinCore.getRodinDB().getRodinProjects();
			return projects;
		} catch (RodinDBException e) {
			TheoryUIUtils.log(e, "error retrieving available rodin projects.");
		}
		return new IRodinProject[0];
	}

	public IRodinProject getRodinProject() {
		return rodinProject;
	}

	/**
	 * Returns the set of selected theories together with any required dependencies.
	 * @return the set of selected theories and their dependencies
	 */
	public Set<ISCTheoryRoot> getSelectedTheories() {
		try {
			return TheoryHierarchyHelper.getAllTheoriesToDeploy(selectedTheories);
		} catch (CoreException e) {
			TheoryUIUtils.log(e, "unable to calculate theories to deploy");
			return new LinkedHashSet<ISCTheoryRoot>();
		}
	}
}
