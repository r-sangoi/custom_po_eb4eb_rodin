package org.eventb.theory.ui.explorer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.OpenWithMenu;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionConstants;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonMenuConstants;
import org.eventb.internal.ui.EventBImage;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.YesToAllMessageDialog;
import org.eventb.theory.core.DatabaseUtilities;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.ITheoryRoot;
import org.eventb.theory.core.TheoryHierarchyHelper;
import org.eventb.theory.internal.ui.ITheoryImages;
import org.eventb.theory.internal.ui.TheoryImage;
import org.eventb.theory.internal.ui.TheoryUIUtils;
import org.eventb.theory.ui.plugin.TheoryUIPlugIn;
import org.eventb.theory.ui.wizard.deploy.SimpleDeployWizard;
import org.eventb.theory.ui.wizard.deploy.UndeployWizard;
import org.eventb.ui.IEventBSharedImages;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

import fr.systerel.internal.explorer.navigator.actionProviders.ActionCollection;

@SuppressWarnings("restriction")
public class TheoryRootActionProvider extends CommonActionProvider {

	protected ICommonActionExtensionSite site;

	protected StructuredViewer viewer;

	private static String GROUP_META = "meta";
	private String GROUP_DELETE = "delete";;

	public void fillActionBars(IActionBars actionBars) {
		// forward doubleClick to doubleClickAction
		actionBars.setGlobalActionHandler(ICommonActionConstants.OPEN, ActionCollection.getOpenAction(site));
		// forwards pressing the delete key to deleteAction
		actionBars.setGlobalActionHandler(ActionFactory.DELETE.getId(), getDeleteAction(site));
	}

	public void fillContextMenu(IMenuManager menu) {
		menu.appendToGroup(ICommonMenuConstants.GROUP_OPEN, ActionCollection.getOpenAction(site));
		menu.appendToGroup(ICommonMenuConstants.GROUP_OPEN_WITH, buildOpenWithMenu());
		menu.add(new Separator(GROUP_META));
		menu.appendToGroup(GROUP_META, getDeployTheoryAction());
		//menu.appendToGroup(GROUP_META, getUndeployTheoryAction());
		menu.add(new Separator(GROUP_DELETE));
		menu.appendToGroup(GROUP_DELETE, getDeleteAction(site));
	}

	private Action getDeployTheoryAction() {
		Action action = new Action() {
			public void run() {
				IStructuredSelection sel = (IStructuredSelection) site.getStructuredViewer().getSelection();
				if (!(sel.isEmpty())) {
					IRodinProject project = null;
					final Set<ISCTheoryRoot> toDeploy = new LinkedHashSet<ISCTheoryRoot>();
					Set<ISCTheoryRoot> errorTheories = new LinkedHashSet<ISCTheoryRoot>();
					for (Object obj : sel.toArray()) {
						if (obj instanceof ITheoryRoot) {
							ITheoryRoot theory = (ITheoryRoot) obj;
							project = theory.getRodinProject();
							ISCTheoryRoot scRoot = theory.getSCTheoryRoot();
							if (!DatabaseUtilities.doesTheoryHaveErrors(scRoot)) {
								toDeploy.add(scRoot);
// do not need to deploy the imported theoris, as they have been deployed before
// just deployed theories can be imported
/*								try {
									Set<ISCTheoryRoot> allTheoriesToDeploy = TheoryHierarchyHelper.getAllTheoriesToDeploy(scRoot);
									for (ISCTheoryRoot otherTheory : allTheoriesToDeploy) {
										if (!DatabaseUtilities.doesTheoryHaveErrors(otherTheory)) {
											toDeploy.add(otherTheory);
										} else {
											errorTheories.add(otherTheory);
										}
									}
									toDeploy.addAll(allTheoriesToDeploy);
								} catch (CoreException e) {
									MessageDialog.openError(site.getViewSite().getShell(), "Deploy Error","Could not deploy theories.");
									TheoryUIUtils.log(e, "unable to calculate set of theories to deploy");
									return;
								}*/
							} else {
								errorTheories.add(scRoot);
							}
						}
					}
					if (!errorTheories.isEmpty()) {
						MessageDialog.openError(site.getViewSite().getShell(), "Deploy Error",
								"The theory you selected have errors: "+ DatabaseUtilities.getElementNames(errorTheories));
					} else if (!toDeploy.isEmpty()) {
						final IRodinProject rodinProject;
						if (project != null) {
							rodinProject = project;
							BusyIndicator.showWhile(site.getViewSite().getShell().getDisplay(), new Runnable() {
								public void run() {
									SimpleDeployWizard wizard = new SimpleDeployWizard(rodinProject, toDeploy);
									WizardDialog dialog = new WizardDialog(site.getViewSite().getShell(), wizard);
									dialog.setTitle(wizard.getWindowTitle());
									dialog.open();
								}
							});
						}
					}
				}
			}

			@Override
			public boolean isEnabled() {
				IStructuredSelection sel = (IStructuredSelection) site.getStructuredViewer().getSelection();
				if (!(sel.isEmpty())) {
					IRodinProject project = null;
					for (Object obj : sel.toArray()) {
						if (!(obj instanceof ITheoryRoot)) {
							return false;
						}
						ITheoryRoot root = (ITheoryRoot) obj;
						if (project == null) {
							project = root.getRodinProject();
						}
						// from the same project
						if (!root.getRodinProject().equals(project)) {
							return false;
						}
						if (!(root.getSCTheoryRoot().exists())) {
							return false;
						}
						try {
							if (root.hasDeployedVersion() && 
								root.getDeployedTheoryRoot().hasOutdatedAttribute() &&
								!root.getDeployedTheoryRoot().isOutdated()) {
								return false;
							}
						} catch (CoreException e) {
							e.printStackTrace();
						}
					}
					return true;
				} else
					return false;
			}
		};
		action.setText("Deploy");
		action.setToolTipText("Deploy theory and its dependencies");
		action.setImageDescriptor(TheoryImage.getImageDescriptor(ITheoryImages.IMG_DTHEORY_PATH));
		return action;
	}

	@SuppressWarnings("unused")
	private Action getUndeployTheoryAction() {
		Action action = new Action() {
			public void run() {
				IStructuredSelection sel = (IStructuredSelection) site.getStructuredViewer().getSelection();
				if (!(sel.isEmpty())) {
					IRodinProject project = null;
					final Set<IDeployedTheoryRoot> toUndeploy = new LinkedHashSet<IDeployedTheoryRoot>();
					for (Object obj : sel.toArray()) {
						if (obj instanceof ITheoryRoot) {
							ITheoryRoot theory = (ITheoryRoot) obj;
							project = theory.getRodinProject();
							IDeployedTheoryRoot deployedRoot = theory.getDeployedTheoryRoot();
							if (deployedRoot.exists()) {
								toUndeploy.add(deployedRoot);
								try {
									toUndeploy.addAll(TheoryHierarchyHelper.getAllTheoriesToUndeploy(deployedRoot));
								} catch (CoreException e) {
									MessageDialog.openError(site.getViewSite().getShell(), "Undeploy Error", "Could not undeploy theories.");
									TheoryUIUtils.log(e, "unable to calculate set of theories to undeploy");
									return;
								}
							}
						}
					}
					final IRodinProject rodinProject;
					if (project != null) {
						rodinProject = project;
						BusyIndicator.showWhile(site.getViewSite().getShell().getDisplay(), new Runnable() {
							public void run() {
								UndeployWizard wizard = new UndeployWizard(rodinProject, toUndeploy);
								WizardDialog dialog = new WizardDialog(site.getViewSite().getShell(), wizard);
								dialog.setTitle(wizard.getWindowTitle());
								dialog.open();
							}
						});
					}

				}
			}

			@Override
			public boolean isEnabled() {
				IStructuredSelection sel = (IStructuredSelection) site.getStructuredViewer().getSelection();
				if (!(sel.isEmpty())) {
					IRodinProject project = null;
					for (Object obj : sel.toArray()) {
						if (!(obj instanceof ITheoryRoot)) {
							return false;
						}
						ITheoryRoot root = (ITheoryRoot) obj;
						if (project == null) {
							project = root.getRodinProject();
						}
						// from the same project
						if (!root.getRodinProject().equals(project)) {
							return false;
						}
						if (!(root.hasDeployedVersion())) {
							return false;
						}
					}
					return true;
				} else
					return false;
			}
		};
		action.setText("Undeploy");
		action.setToolTipText("Undeploy theory and its dependents");
		action.setImageDescriptor(TheoryImage.getImageDescriptor(ITheoryImages.IMG_THEORY_PATH));
		return action;
	}

	// customised to delete the deployed theory file as well
	private Action getDeleteAction(final ICommonActionExtensionSite site) {
		Action deleteAction = new Action() {
			@Override
			public void run() {
				if (!(site.getStructuredViewer().getSelection().isEmpty())) {

					// Putting the selection into a set which does not contains
					// any pair
					// of parent and child
					Collection<IRodinElement> set = new ArrayList<IRodinElement>();

					IStructuredSelection ssel = (IStructuredSelection) site.getStructuredViewer().getSelection();

					for (Iterator<?> it = ssel.iterator(); it.hasNext();) {
						final Object obj = it.next();
						if (!(obj instanceof IRodinElement)) {
							continue;
						}
						IRodinElement elem = (IRodinElement) obj;
						if (elem.isRoot()) {
							elem = elem.getParent();
						}
						set = UIUtils.addToTreeSet(set, elem);
					}

					int answer = YesToAllMessageDialog.YES;
					for (IRodinElement element : set) {
						if (element instanceof IRodinFile) {
							IRodinFile rodinFile = (IRodinFile) element;
							if (answer != YesToAllMessageDialog.YES_TO_ALL) {
								answer = YesToAllMessageDialog.openYesNoToAllQuestion(site.getViewSite().getShell(),
										"Confirm File Delete",
										"Are you sure you want to delete theory file '" + rodinFile.getElementName()
												+ "' in project '" + element.getParent().getElementName() + "' ?");
							}
							if (answer == YesToAllMessageDialog.NO_TO_ALL)
								break;
							// FIXED BUG here when u press the close button (X)
							// it deletes the theory anyway
							if (answer != YesToAllMessageDialog.NO && answer != -1) {
								try {
									TheoryUIUtils.closeOpenedEditors(rodinFile);
									// delete the deployed version
									if (rodinFile.getRootElementType().equals(ITheoryRoot.ELEMENT_TYPE)) {
										ITheoryRoot theoryRoot = (ITheoryRoot) rodinFile.getRoot();

										IRodinFile deployedFile = rodinFile.getRodinProject().getRodinFile(
												DatabaseUtilities.getDeployedTheoryFullName(theoryRoot
														.getComponentName()));
										if (deployedFile.exists()) {
											deployedFile.delete(true, new NullProgressMonitor());
										}
									}
									rodinFile.delete(true, new NullProgressMonitor());

								} catch (PartInitException e) {
									MessageDialog.openError(null, "Error", "Could not delete file");
								} catch (RodinDBException e) {
									MessageDialog.openError(null, "Error", "Could not delete file");
								}
							}
						}
					}

				}
			}
		};
		deleteAction.setText("&Delete");
		deleteAction.setToolTipText("Delete theory");
		deleteAction.setImageDescriptor(EventBImage.getImageDescriptor(IEventBSharedImages.IMG_DELETE_PATH));
		return deleteAction;
	}

	@Override
	public void init(ICommonActionExtensionSite aSite) {
		super.init(aSite);
		site = aSite;
		viewer = aSite.getStructuredViewer();
	}

	/**
	 * Builds an Open With menu.
	 * 
	 * @return the built menu
	 */
	public MenuManager buildOpenWithMenu() {
		MenuManager menu = new MenuManager("Open With", ICommonMenuConstants.GROUP_OPEN_WITH);
		ISelection selection = site.getStructuredViewer().getSelection();
		Object obj = ((IStructuredSelection) selection).getFirstElement();
		menu.add(new OpenWithMenu(TheoryUIPlugIn.getActivePage(), ((IInternalElement) obj).getRodinFile().getResource()));
		return menu;
	}
}
