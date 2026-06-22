package org.eventb.theory.ui.editor.html;

import static org.eventb.ui.prettyprint.PrettyPrintUtils.getHTMLBeginForCSSClass;
import static org.eventb.ui.prettyprint.PrettyPrintUtils.getHTMLEndForCSSClass;
import static org.eventb.ui.prettyprint.PrettyPrintUtils.wrapString;

import org.eventb.theory.core.IMetavariable;
import org.eventb.ui.prettyprint.DefaultPrettyPrinter;
import org.eventb.ui.prettyprint.IPrettyPrintStream;
import org.eventb.ui.prettyprint.PrettyPrintAlignments.HorizontalAlignment;
import org.eventb.ui.prettyprint.PrettyPrintAlignments.VerticalAlignement;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

public class MetavariablesPrettyPrinter extends DefaultPrettyPrinter {

	private static final String STYLE = "constantIdentifier";
	private static final String VAR_IDENT_SEPARATOR_BEGIN = null;
	private static final String VAR_IDENT_SEPARATOR_END = null;
	private static final String ONE_SPACES = " ";
	private static final String MEMB_SYMB = "\u2208";
	
	public void prettyPrint(IInternalElement elt, IInternalElement parent, IPrettyPrintStream ps) {
		if(elt instanceof IMetavariable){
			IMetavariable var = (IMetavariable) elt;
			try {
				ps.appendString(
						wrapString("\u25aa"+ONE_SPACES+var.getIdentifierString()+ 
						ONE_SPACES +MEMB_SYMB+ONE_SPACES+ 
						var.getType()), 
						getHTMLBeginForCSSClass(STYLE, //
								HorizontalAlignment.LEFT, //
								VerticalAlignement.MIDDLE), //
						getHTMLEndForCSSClass(STYLE, //
								HorizontalAlignment.LEFT, //
								VerticalAlignement.MIDDLE),  
						VAR_IDENT_SEPARATOR_BEGIN, 
						VAR_IDENT_SEPARATOR_END);
			} catch (RodinDBException e) {
				e.printStackTrace();
			}
		}
	}
}
