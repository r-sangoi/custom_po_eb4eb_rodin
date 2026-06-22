/**
 * 
 */
package org.eventb.theory.language.ui.editor;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eventb.internal.ui.eventbeditor.EventBEditor;
import org.eventb.internal.ui.eventbeditor.editpage.EditPage;
import org.eventb.internal.ui.eventbeditor.htmlpage.HTMLPage;
import org.eventb.theory.core.ITheoryPathRoot;
import org.eventb.theory.internal.ui.TheoryUIUtils;
import org.eventb.theory.ui.plugin.TheoryUIPlugIn;
import org.eventb.ui.eventbeditor.EventBEditorPage;

/**
 * @author Renato Silva
 *
 */
@SuppressWarnings("restriction")
public class TheoryLanguageEditor extends EventBEditor<ITheoryPathRoot> {
	
	public static final String EDITOR_ID = TheoryUIPlugIn.PLUGIN_ID
			+ ".theoryLanguageEditor";

	/**
	 * 
	 */
	public TheoryLanguageEditor() {
		super();
	}

	public void setSelection(ISelection selection) {
		// Pass the selection to Theory Edit Page
		IFormPage page = getActivePageInstance();
		if (page instanceof EditPage) {
			setActivePage(EditPage.PAGE_ID);
			((EditPage) page).setSelection(selection);
		}
	}

	@Override
	protected void addPages() {
		EventBEditorPage htmlPage = new HTMLPage();
		EventBEditorPage editPage = new EditPage();
		try {
			htmlPage.initialize(this);
			addPage(htmlPage);
			editPage.initialize(this);
			addPage(editPage);
		} catch (PartInitException e) {
			TheoryUIUtils.log(e,
					"Failed to initialise page for "+ getRodinInput().getRodinFile().getElementName()+".");
		}
	}

	@Override
	public String getEditorId() {
		return EDITOR_ID;
	}

}
