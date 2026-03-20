package fr.enseeiht.eventb.eb4eb.ui.explorer;

import static org.eclipse.ui.IWorkbenchCommandConstants.EDIT_DELETE;
import static org.eclipse.ui.navigator.ICommonActionConstants.OPEN;
import static org.eclipse.ui.navigator.ICommonMenuConstants.GROUP_OPEN;
import static org.eclipse.ui.navigator.ICommonMenuConstants.GROUP_OPEN_WITH;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.actions.OpenWithMenu;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonMenuConstants;
import org.rodinp.core.IInternalElement;
import fr.enseeiht.eventb.eb4eb.ui.EB4EBUIPlugin;
import fr.systerel.internal.explorer.navigator.actionProviders.ActionCollection;

@SuppressWarnings("restriction")
public class RootActionProvider extends CommonActionProvider {

	private static final String GROUP_DELETE = "delete";
	
	@Override
	public void fillActionBars(IActionBars actionBars) {
		super.fillActionBars(actionBars);
		final ICommonActionExtensionSite site = getActionSite();
		// forward doubleClick to doubleClickAction
		actionBars.setGlobalActionHandler(OPEN, ActionCollection.getOpenAction(site));
		// forwards pressing the delete key to deleteAction
		actionBars.setGlobalActionHandler(EDIT_DELETE, ActionCollection.getDeleteAction(site));
	}

	@Override
	public void fillContextMenu(IMenuManager menu) {
		super.fillContextMenu(menu);
		final ICommonActionExtensionSite site = getActionSite();
		menu.appendToGroup(GROUP_OPEN, ActionCollection.getOpenAction(site));
		menu.appendToGroup(GROUP_OPEN_WITH, buildOpenWithMenu());
		menu.add(new Separator(GROUP_DELETE));
		menu.appendToGroup(GROUP_DELETE, ActionCollection.getDeleteAction(site));
	}
	
	 /**
     * Builds an Open With menu.
     * 
     * @return the built menu
     */
	MenuManager buildOpenWithMenu() {
		MenuManager menu = new MenuManager("Open With",
				ICommonMenuConstants.GROUP_OPEN_WITH);
		final StructuredViewer viewer = getActionSite().getStructuredViewer();
		ISelection selection = viewer.getSelection();
		Object obj = ((IStructuredSelection) selection).getFirstElement();
		menu.add(new OpenWithMenu(EB4EBUIPlugin.getActivePage(),
				((IInternalElement) obj).getRodinFile().getResource()));
		return menu;
	}
	
}
