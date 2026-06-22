/*******************************************************************************
 * Copyright (c) 2011, 2022 University of Southampton and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.tactics.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eventb.theory.core.ISCTheorem;
import org.eventb.theory.core.util.CoreUtilities;
import org.eventb.theory.internal.rbp.rulebase.TheoremsRetriever;
import org.rodinp.core.RodinDBException;

public class TheoremSelectorWizardPageOne extends WizardPage {
	
	private static final String THEOREM_COL = "Theorem";
	private static final String NAME_COL = "Name";
	
	private Combo projectCombo;
	private Combo theoryCombo;
	private TreeViewer treeViewer;
	
	private TheoremsRetriever retriever;
	private String selectedProject = null;
	private static String lastSelectedProject = null;
	private String selectedTheory = null;
	private static String lastSelectedTheory = null;
	private List<ISCTheorem> selectedTheorems = null;

	public TheoremSelectorWizardPageOne(TheoremsRetriever retriever) {
		super("selectTheorems");
		setTitle("Select theorems");
		setDescription("Select polymorphic theorems to instantiate");
		this.retriever = retriever;
	}

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		container.setLayout(new GridLayout(2, false));
		
		Label lblProject = new Label(container, SWT.NONE);
		lblProject.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblProject.setText("Project:");
		
		projectCombo = new Combo(container, SWT.READ_ONLY | SWT.BORDER
				| SWT.SINGLE);
		projectCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		final String[] projects = retriever.getRodinProjects();
		projectCombo.setItems(projects);
		projectCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				selectProjectItem(projectCombo.getSelectionIndex());
				dialogChanged();
			}
		});
		
		Label lblTheory = new Label(container, SWT.NONE);
		lblTheory.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblTheory.setText("Theory:");
		
		theoryCombo = new Combo(container, SWT.READ_ONLY | SWT.BORDER
				| SWT.SINGLE);
		theoryCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		theoryCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				selectTheoryItem(theoryCombo.getSelectionIndex());
				dialogChanged();
			}
		});

		new Label(container, SWT.NONE);
		FilteredTree filteredTree = new FilteredTree(container, SWT.FULL_SELECTION | SWT.BORDER | SWT.MULTI,
				new PatternFilter() {
					@Override
					protected boolean isLeafMatch(Viewer viewer, Object element) {
						try {
							return wordMatches(((ISCTheorem) element).getLabel());
						} catch (RodinDBException e) {
							CoreUtilities.log(e, "Error while filtering theorems in theorem instantiation dialog");
							return false;
						}
					}
				}, true, true);
		filteredTree.setInitialText("Filter by theorem name...");
		treeViewer = filteredTree.getViewer();
		treeViewer.getTree().setHeaderVisible(true);
		treeViewer.getTree().setLinesVisible(true);
		TreeViewerColumn colName = new TreeViewerColumn(treeViewer, SWT.NONE);
		colName.getColumn().setText(NAME_COL);
		colName.getColumn().setWidth(150);
		TreeViewerColumn colThm = new TreeViewerColumn(treeViewer, SWT.NONE);
		colThm.getColumn().setText(THEOREM_COL);
		colThm.getColumn().setWidth(550);
		treeViewer.setContentProvider(new ITreeContentProvider() {
			@Override
			public boolean hasChildren(Object element) {
				return false;
			}
			@Override
			public Object getParent(Object element) {
				return null;
			}
			@Override
			public Object[] getElements(Object inputElement) {
				return (Object[]) inputElement;
			}
			@Override
			public Object[] getChildren(Object parentElement) {
				return null;
			}
		});
		treeViewer.setLabelProvider(new TheoremLabelProvider());
		treeViewer.addSelectionChangedListener(event -> {
			ISelection sel = event.getSelection();
			if (sel.isEmpty()) {
				selectedTheorems = null;
			} else if (sel instanceof IStructuredSelection) {
				selectedTheorems = new ArrayList<ISCTheorem>();
				for (Object o : (IStructuredSelection) sel) {
					selectedTheorems.add((ISCTheorem) o);
				}
			}
			dialogChanged();
		});

		// Restore previous selection if available
		if (lastSelectedProject != null) {
			for (int i = 0; i < projects.length; i++) {
				if (lastSelectedProject.equals(projects[i])) {
					selectProjectItem(i);
					break;
				}
			}
			if (lastSelectedTheory != null) {
				String[] theories = theoryCombo.getItems();
				for (int i = 0; i < theories.length; i++) {
					if (lastSelectedTheory.equals(theories[i])) {
						selectTheoryItem(i);
						break;
					}
				}
			}
		}
		// Auto-select project if there is only one option
		if (selectedProject == null && projects.length == 1) {
			selectProjectItem(0);
		}
		
		dialogChanged();
		setControl(container);
	}

	protected void selectProjectItem(int i) {
		if (projectCombo.getSelectionIndex() != i) {
			projectCombo.select(i);
		}
		String value = projectCombo.getItem(i);
		if (value.equals(selectedProject)) {
			return;
		}
		selectedProject = value;
		lastSelectedProject = value;
		setTheoryComboItemsFromProject();
	}

	protected void setTheoryComboItemsFromProject() {
		selectedTheory = null;
		if (selectedProject != null) {
			String[] theories = retriever.getTheories(selectedProject);
			theoryCombo.setItems(theories);
			if (theories.length == 1) {
				theoryCombo.select(0);
				selectedTheory = theories[0];
				lastSelectedTheory = selectedTheory;
			}
		} else {
			theoryCombo.removeAll();
		}
		setTheoremListFromTheory();
	}

	protected void selectTheoryItem(int i) {
		if (theoryCombo.getSelectionIndex() != i) {
			theoryCombo.select(i);
		}
		String value = theoryCombo.getItem(i);
		if (value.equals(selectedTheory)) {
			return;
		}
		selectedTheory = value;
		lastSelectedTheory = value;
		setTheoremListFromTheory();
	}

	protected void setTheoremListFromTheory() {
		selectedTheorems = null;
		if (selectedProject != null && selectedTheory != null) {
			treeViewer.setInput(retriever.getSCTheorems(selectedProject, selectedTheory).toArray());
		} else {
			treeViewer.setInput(null);
		}
	}
	
	/**
	 * Ensures that both text fields are set correctly.
	 */
	protected void dialogChanged() {
		if(selectedProject == null){
			theoryCombo.setEnabled(false);
			updateStatus("Project must be specified");
			return;
		}
		theoryCombo.setEnabled(true);
		if (selectedTheory == null) {
			updateStatus("Theory must be specified");
			return;
		}
		if(selectedTheorems == null){
			updateStatus("Theorem must be specified");
			return;
		}
		updateStatus(null);
	}

	private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);

	}

	public String getSelectedProject() {
		return selectedProject;
	}

	public String getSelectedTheory() {
		return selectedTheory;
	}

	public List<ISCTheorem> getSelectedTheorem() {
		return selectedTheorems;
	}
	
	@Override
	public TheoremSelectorWizard getWizard() {
		return (TheoremSelectorWizard) super.getWizard();
	}

	private static class TheoremLabelProvider extends BaseLabelProvider implements ITableLabelProvider {
		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {
			try {
				switch (columnIndex) {
				case 0:
					return ((ISCTheorem) element).getLabel();
				case 1:
					return ((ISCTheorem) element).getPredicateString();
				default:
					return "Unknown column";
				}
			} catch (RodinDBException e) {
				CoreUtilities.log(e, "Error while generating table labels in theorem instantiation dialog");
				return "Internal error, see log";
			}
		}
	}
	
}
