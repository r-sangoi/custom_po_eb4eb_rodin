package org.eventb.theory.ui.editor.html;

import static org.eventb.ui.prettyprint.PrettyPrintUtils.getHTMLBeginForCSSClass;
import static org.eventb.ui.prettyprint.PrettyPrintUtils.getHTMLEndForCSSClass;
import static org.eventb.ui.prettyprint.PrettyPrintUtils.wrapString;

import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;
import org.eventb.theory.core.IGiven;
import org.eventb.ui.prettyprint.DefaultPrettyPrinter;
import org.eventb.ui.prettyprint.IPrettyPrintStream;
import org.eventb.ui.prettyprint.PrettyPrintAlignments.HorizontalAlignment;
import org.eventb.ui.prettyprint.PrettyPrintAlignments.VerticalAlignement;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

@SuppressWarnings("restriction")
public class GivenPrettyPrinter extends DefaultPrettyPrinter {

	private static final String G_PRED = "axiomPredicate"; 
	private static final String G_HYP = "variantExpression"; 
	
	private static final String ONE_SPACES = " ";
	private static final String TWO_SPACES = "  ";
	
	private static final String G_IDENT_SEPARATOR_BEGIN = null;
	private static final String G_IDENT_SEPARATOR_END = null;
	
	@Override
	public void prettyPrint(IInternalElement elt, IInternalElement parent, 
			IPrettyPrintStream ps) {
		if(elt instanceof IGiven){
			IGiven g = (IGiven) elt;
			try {
				String pred = g.getPredicateString();
				ps.appendString(wrapString("\u25aa"+ONE_SPACES+pred)+ TWO_SPACES, 
						getHTMLBeginForCSSClass(G_PRED, //
								HorizontalAlignment.LEFT, //
								VerticalAlignement.MIDDLE), //
						getHTMLEndForCSSClass(G_PRED, //
								HorizontalAlignment.LEFT, //
								VerticalAlignement.MIDDLE), 
								G_IDENT_SEPARATOR_BEGIN, 
								G_IDENT_SEPARATOR_END);
				String inHyp = ONE_SPACES;
				if (g.isHyp())
					inHyp = "  (in hypothesis)";
				ps.appendString(wrapString(inHyp), 
						getHTMLBeginForCSSClass(G_HYP, //
						HorizontalAlignment.LEFT, //
						VerticalAlignement.MIDDLE), 
						getHTMLEndForCSSClass(G_HYP, //
						HorizontalAlignment.LEFT, //
						VerticalAlignement.MIDDLE), //
						G_IDENT_SEPARATOR_BEGIN, //
						G_IDENT_SEPARATOR_END);
			} catch (RodinDBException e) {
				EventBEditorUtils.debugAndLogError(
						e,
						"Cannot get the details for given "
								+ g.getElementName());
			}
			
		}
	}
	
	
	@Override
	public boolean appendSpecialPrefix(IInternalElement parent,
			String defaultKeyword, IPrettyPrintStream ps, boolean empty) {
		return true;
	}
	
}
