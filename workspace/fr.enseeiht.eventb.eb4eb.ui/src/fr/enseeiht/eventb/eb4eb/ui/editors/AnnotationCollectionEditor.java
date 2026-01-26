package fr.enseeiht.eventb.eb4eb.ui.editors;

import org.eclipse.ui.PartInitException;
import org.eventb.internal.ui.eventbeditor.EventBEditor;
import org.eventb.internal.ui.eventbeditor.editpage.EditPage;
import org.eventb.internal.ui.eventbeditor.htmlpage.HTMLPage;
import org.eventb.ui.eventbeditor.EventBEditorPage;

import fr.enseeiht.eventb.eb4eb.IAnnotationCollectionRoot;
import fr.enseeiht.eventb.eb4eb.ui.EB4EBUIPlugin;

@SuppressWarnings("restriction") //TODO : bizarre
public class AnnotationCollectionEditor extends EventBEditor<IAnnotationCollectionRoot> {
	
	public static final String EDITOR_ID = EB4EBUIPlugin.PLUGIN_ID
			+ ".annotationEditor";

	@Override
	public String getEditorId() {
		return EDITOR_ID;
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
			e.printStackTrace(); //TODO
//			TheoryUIUtils.log(e,
//					"Failed to initialise page for "+ getRodinInput().getRodinFile().getElementName()+".");
		}
	}

}
