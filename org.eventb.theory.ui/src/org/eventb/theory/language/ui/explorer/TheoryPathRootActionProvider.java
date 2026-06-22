/**
 *
 */
package org.eventb.theory.language.ui.explorer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
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
import org.eventb.theory.core.DatabaseUtilitiesTheoryPath;
import org.eventb.theory.core.ITheoryPathRoot;
import org.eventb.theory.internal.ui.TheoryUIUtils;
import org.eventb.theory.ui.plugin.TheoryUIPlugIn;
import org.eventb.ui.IEventBSharedImages;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

import fr.systerel.internal.explorer.navigator.actionProviders.ActionCollection;

/**
 * @author renatosilva
 *
 */
@SuppressWarnings("restriction")
public class TheoryPathRootActionProvider extends CommonActionProvider {

	protected ICommonActionExtensionSite site;

	protected StructuredViewer viewer;

	private static String GROUP_META = "meta";
	private String GROUP_DELETE = "delete";

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
		menu.add(new Separator(GROUP_DELETE));
		menu.appendToGroup(GROUP_DELETE, getDeleteAction(site));
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
									if (rodinFile.getRootElementType().equals(ITheoryPathRoot.ELEMENT_TYPE)) {
										ITheoryPathRoot theoryRoot = (ITheoryPathRoot) rodinFile.getRoot();

										IRodinFile theoryPathFile = rodinFile.getRodinProject().getRodinFile(
												DatabaseUtilitiesTheoryPath.getTheoryPathFullName(theoryRoot.getComponentName()));
										if (theoryPathFile.exists()) {
											theoryPathFile.delete(true, new NullProgressMonitor());
										}
									}
									if (rodinFile.exists()) {
										rodinFile.delete(true, new NullProgressMonitor());
									}

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
