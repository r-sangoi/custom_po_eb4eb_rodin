package org.eventb.theory.ui.editor.html;

import static org.eventb.ui.prettyprint.PrettyPrintUtils.getHTMLBeginForCSSClass;
import static org.eventb.ui.prettyprint.PrettyPrintUtils.getHTMLEndForCSSClass;
import static org.eventb.ui.prettyprint.PrettyPrintUtils.wrapString;

import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;
import org.eventb.theory.core.IInfer;
import org.eventb.theory.core.IInferenceRule;
import org.eventb.ui.prettyprint.DefaultPrettyPrinter;
import org.eventb.ui.prettyprint.IPrettyPrintStream;
import org.eventb.ui.prettyprint.PrettyPrintAlignments.HorizontalAlignment;
import org.eventb.ui.prettyprint.PrettyPrintAlignments.VerticalAlignement;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

@SuppressWarnings("restriction")
public class InferPrettyPrinter extends DefaultPrettyPrinter {

	private static final String I_PRED = "axiomPredicate"; 
	
	private static final String ONE_SPACES = " ";
	
	private static final String I_IDENT_SEPARATOR_BEGIN = null;
	private static final String I_IDENT_SEPARATOR_END = null;
	
	@Override
	public void prettyPrint(IInternalElement elt, IInternalElement parent, 
			IPrettyPrintStream ps) {
		if(elt instanceof IInfer){
			IInfer i = (IInfer) elt;
			try {
				String pred = i.getPredicateString();
				ps.appendString(wrapString("\u25aa"+ONE_SPACES+pred), 
						getHTMLBeginForCSSClass(I_PRED, //
								HorizontalAlignment.LEFT, //
								VerticalAlignement.MIDDLE), //
						getHTMLEndForCSSClass(I_PRED, //
								HorizontalAlignment.LEFT, //
								VerticalAlignement.MIDDLE), 
								I_IDENT_SEPARATOR_BEGIN, 
								I_IDENT_SEPARATOR_END);
			} catch (RodinDBException e) {
				EventBEditorUtils.debugAndLogError(
						e,
						"Cannot get the details for infer "
								+ i.getElementName());
			}
			
		}
	}
	
	@Override
	public boolean appendSpecialPrefix(IInternalElement parent,
			String defaultKeyword, IPrettyPrintStream ps, boolean empty) {
		if (parent instanceof IInferenceRule){
			try {
				int l = ((IInferenceRule) parent).getGivens().length + ((IInferenceRule) parent).getInfers().length;
				if(l > 0){
					ps.appendLevelBegin();
					ps.appendString("--------------------------------------------------", getHTMLBeginForCSSClass(I_PRED, //
											HorizontalAlignment.LEFT, //
											VerticalAlignement.MIDDLE), //
									getHTMLEndForCSSClass(I_PRED, //
											HorizontalAlignment.LEFT, //
											VerticalAlignement.MIDDLE), 
											I_IDENT_SEPARATOR_BEGIN, 
											I_IDENT_SEPARATOR_END);
					ps.appendLevelEnd();
				}
			} catch (RodinDBException e) {
				EventBEditorUtils.debugAndLogError(
						e,
						"Cannot get the children for rule "
								+ parent.getElementName());
			}
		}
		
		return true;
	}
}
