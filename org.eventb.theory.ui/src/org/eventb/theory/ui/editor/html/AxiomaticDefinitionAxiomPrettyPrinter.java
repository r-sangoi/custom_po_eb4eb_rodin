package org.eventb.theory.ui.editor.html;

import static org.eventb.internal.ui.UIUtils.HTMLWrapUp;
import static org.eventb.ui.prettyprint.PrettyPrintUtils.getHTMLBeginForCSSClass;
import static org.eventb.ui.prettyprint.PrettyPrintUtils.getHTMLEndForCSSClass;

import org.eventb.theory.core.IAxiomaticDefinitionAxiom;
import org.eventb.ui.prettyprint.DefaultPrettyPrinter;
import org.eventb.ui.prettyprint.IPrettyPrintStream;
import org.eventb.ui.prettyprint.PrettyPrintAlignments.HorizontalAlignment;
import org.eventb.ui.prettyprint.PrettyPrintAlignments.VerticalAlignement;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

public class AxiomaticDefinitionAxiomPrettyPrinter extends DefaultPrettyPrinter {

	private static final String STYLE = ".extended";
	private static final String SEPARATOR_BEGIN = null;
	private static final String SEPARATOR_END = null;
	private static final String AXIOM_LABEL = "axiomLabel";
	private static final String LABEL_SEPARATOR = ":";
	private static final String TWO_SPACES = "  ";


	@SuppressWarnings("restriction")
	public void prettyPrint(IInternalElement elt, IInternalElement parent,
			IPrettyPrintStream ps){
		if (elt instanceof IAxiomaticDefinitionAxiom) {
			IAxiomaticDefinitionAxiom ada = (IAxiomaticDefinitionAxiom) elt;
			try {
				ps.appendString(
						HTMLWrapUp(ada.getLabel()+LABEL_SEPARATOR+TWO_SPACES),
						getHTMLBeginForCSSClass(AXIOM_LABEL, HorizontalAlignment.LEFT,
								VerticalAlignement.MIDDLE),
						getHTMLEndForCSSClass(AXIOM_LABEL, HorizontalAlignment.LEFT,
								VerticalAlignement.MIDDLE),
						SEPARATOR_BEGIN,
						SEPARATOR_END);
				ps.appendString(
						ada.getPredicateString(),
						getHTMLBeginForCSSClass(STYLE, HorizontalAlignment.LEFT,
								VerticalAlignement.MIDDLE),
						getHTMLEndForCSSClass(STYLE, HorizontalAlignment.LEFT,
								VerticalAlignement.MIDDLE),
						SEPARATOR_BEGIN,
						SEPARATOR_END);

			} catch (RodinDBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			
		}
	}


}
