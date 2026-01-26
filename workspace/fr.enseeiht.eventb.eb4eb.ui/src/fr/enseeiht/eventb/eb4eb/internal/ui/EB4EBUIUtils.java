package fr.enseeiht.eventb.eb4eb.internal.ui;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
import org.rodinp.core.IOpenable;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

import fr.enseeiht.eventb.eb4eb.ui.EB4EBUIPlugin;

public class EB4EBUIUtils {

	public static void log(Throwable exc, String message) {
		if (exc instanceof RodinDBException) {
			final Throwable nestedExc = ((RodinDBException) exc).getException();
			if (nestedExc != null) {
				exc = nestedExc;
			}
		}
		if (message == null) {
			message = "Unknown context"; //$NON-NLS-1$
		}
		IStatus status = new Status(IStatus.ERROR, EB4EBUIPlugin.PLUGIN_ID,
				IStatus.ERROR, message, exc);
		log(status);
	}

	/**
	 * Logs the given status to the Event-B UI plug-in log.
	 */
	public static void log(IStatus status) {
		EB4EBUIPlugin.getDefault().getLog().log(status);
	}
	
	/**
	 * Link the current object to the preferred editor.
	 * <p>
	 * 
	 * @param obj
	 *            the object (e.g. an internal element or a Rodin file)
	 */
	public static void linkToPreferredEditor(Object obj) {
		final IRodinFile component = asRodinFile(obj);
		if (component == null)
			return;
		IEditorDescriptor desc = IDE.getDefaultEditor(component.getResource());
		linkToEditor(component, obj, desc);
	}
	
	/**
	 * Link the current object to the specified editor.
	 * <p>
	 * 
	 * @param obj
	 *            the object (e.g. an internal element or a Rodin file)
	 * @param desc
	 *            the editor descriptor
	 */
	private static void linkToEditor(IRodinFile component, Object obj, IEditorDescriptor desc) {
		try {
			IEditorPart editor = EB4EBUIPlugin.getActivePage().openEditor(
					new FileEditorInput(component.getResource()), desc.getId());
			if (editor == null) {
				// External editor
				return;
			}
			final ISelectionProvider sp = editor.getSite().getSelectionProvider();
			if (sp == null || component.getRoot().equals(obj)) {
				return;
			}
			sp.setSelection(new StructuredSelection(obj));
		} catch (PartInitException e) {
			String errorMsg = "Error opening Editor";
			MessageDialog.openError(null, null, errorMsg);
			log(e, "while trying to open editor for " + component);
		}
	}
	
	/**
	 * Link the current object to an Event-B editor.
	 * <p>
	 * 
	 * @param obj
	 *            the object (e.g. an internal element or a Rodin file)
	 */
	public static void linkToEventBEditor(Object obj) {
		final IRodinFile component = asRodinFile(obj);
		
		if (component == null)
			return;
		
		try {
			IEditorDescriptor desc = PlatformUI.getWorkbench().getEditorRegistry()
					.getDefaultEditor(component.getCorrespondingResource().getName());
			IEditorPart editor = EB4EBUIPlugin.getActivePage()
					.openEditor(new FileEditorInput(component.getResource()), desc.getId());
			
			if (editor == null) {
				// External editor
				return;
			}
			
			final ISelectionProvider sp = editor.getSite().getSelectionProvider();
			if (sp == null || component.getRoot().equals(obj)) {
				return;
			}
			sp.setSelection(new StructuredSelection(obj));
		} catch (PartInitException e) {
			String errorMsg = "Error opening Editor";
			MessageDialog.openError(null, null, errorMsg);
			EB4EBUIPlugin.getDefault().getLog().log(new Status(IStatus.ERROR, EB4EBUIPlugin.PLUGIN_ID, errorMsg, e));
		}
	}
	
	private static IRodinFile asRodinFile(Object obj) {
		if (obj instanceof IRodinProject)
			return null;
		return (IRodinFile) getOpenable(obj);
	}
	
	/**
	 * Method to return the openable for an object (IRodinElement).
	 * <p>
	 * 
	 * @param element
	 *            A Rodin Element
	 * @return The IRodinFile corresponding to the input object
	 */
	public static IOpenable getOpenable(Object element) {
		if (element instanceof IRodinElement) {
			return ((IRodinElement) element).getOpenable();
		} else {
			return null;
		}
	}

}
