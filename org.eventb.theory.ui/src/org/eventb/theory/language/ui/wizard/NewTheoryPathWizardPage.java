/*******************************************************************************
 * Copyright (c) 2012, 2020 University of Southampton and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.language.ui.wizard;

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
import org.eventb.theory.core.ITheoryPathRoot;
import org.eventb.theory.internal.ui.Messages;
import org.eventb.theory.internal.ui.TheoryUIUtils;
import org.eventb.theory.ui.plugin.TheoryUIPlugIn;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;


/**
 * <p>The "New" wizard page allows setting the container for the new file
 * as well as the file name. The page will only accept file name without
 * the extension OR with the extension that matches the expected one(but).
 * </p>
 * @author maamria
 * 
 */
@SuppressWarnings("restriction")
public class NewTheoryPathWizardPage extends WizardPage {
	// Some text areas.
	private Text projectText;
	private ISelection selection;
	
	private final String DEFAULT_THEORY_PATH_NAME = "TheoryPath";

	public NewTheoryPathWizardPage(ISelection selection) {
		super("wizardPage");
		setTitle(Messages.wizard_newTheoryPathTitle);
		setDescription(Messages.wizard_newTheoryPathDesc);
		this.selection = selection;
	}

	/**
	 * Creating the components of the dialog.
	 * <p>
	 * 
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
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
	
	public String getTheoryName() {
		return DEFAULT_THEORY_PATH_NAME;
	}

	/**
	 * Ensures that both text fields are set correctly.
	 */
	void dialogChanged() {
		final String projectName = getProjectName();
		String theoryName = getTheoryName();
		if (projectName.length() == 0) {
			updateStatus(Messages.wizard_errorProjMustBeSelected);
			return;
		}

		IRodinProject rodinProject = RodinCore.getRodinDB().getRodinProject(projectName);
		if (!rodinProject.exists()) {
			updateStatus(Messages.wizard_errorProjMustBeValid);
			return;
		}

		if (rodinProject.isReadOnly()) {
			updateStatus(Messages.wizard_errorProjMustBeWritable);
			return;
		}

		if (theoryName.length() == 0) {
			updateStatus(Messages.wizard_errorTheoryNameMustBeSpecified);
			return;
		}
		
		try {
			if(rodinProject.getRootElementsOfType(ITheoryPathRoot.ELEMENT_TYPE).length!=0){
				updateStatus(Messages.wizard_multipleTheoryPathError);
				return;
			}
			
			for(IResource resource: rodinProject.getProject().members()){
				if(resource.getName().startsWith(theoryName)){
					updateStatus(Messages.wizard_errorFileClash);
					return;
				}
			}
		} catch (CoreException e1) {
			TheoryUIUtils.log(e1, "Unable to retrieve file from project " + rodinProject.getElementName());
		}
		
		
//		if (!DatabaseUtilities.isMathExtensionsProject(rodinProject)){
//			IRodinProject mathsProject = DatabaseUtilities.getDeploymentProject(null);
//			try {
//				for (ITheoryRoot root : mathsProject.getRootElementsOfType(ITheoryRoot.ELEMENT_TYPE)){
//					if (root.getElementName().equals(theoryName)){
//						updateStatus(Messages.bind(Messages.wizard_errorGlobalTheoryNameClash,rodinProject.getElementName()));
//						return;
//					}
//				}
//			} catch (RodinDBException e) {
//				TheoryUIUtils.log(e, "unable to retrieve theories of maths project");
//			}
//		}
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
			rodinProject = TheoryUIPlugIn.getRodinDatabase().getRodinProject(
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

//	private IRodinFile getTheoryFile(IRodinProject project, String theoryName) {
//		return project
//				.getRodinFile(DatabaseUtilities.getTheoryFullName(theoryName));
//	}
	
//	private IRodinFile getTheoryPathFile(IRodinProject project) {
//		return project.getRodinFile(DatabaseUtilities.getTheoryFullName(theoryName));
//	}

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