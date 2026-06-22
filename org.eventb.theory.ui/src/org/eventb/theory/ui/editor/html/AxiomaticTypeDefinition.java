package org.eventb.theory.ui.editor.html;

import static org.eventb.ui.prettyprint.PrettyPrintUtils.getHTMLBeginForCSSClass;
import static org.eventb.ui.prettyprint.PrettyPrintUtils.getHTMLEndForCSSClass;

import org.eventb.theory.core.IAxiomaticTypeDefinition;
import org.eventb.ui.prettyprint.DefaultPrettyPrinter;
import org.eventb.ui.prettyprint.IPrettyPrintStream;
import org.eventb.ui.prettyprint.PrettyPrintAlignments.HorizontalAlignment;
import org.eventb.ui.prettyprint.PrettyPrintAlignments.VerticalAlignement;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

public class AxiomaticTypeDefinition extends DefaultPrettyPrinter {

	private static final String STYLE = ".extended";
	private static final String SEPARATOR_BEGIN = null;
	private static final String SEPARATOR_END = null;

	public void prettyPrint(IInternalElement elt, IInternalElement parent,
			IPrettyPrintStream ps) {

		if (elt instanceof IAxiomaticTypeDefinition) {
			final IAxiomaticTypeDefinition atd = (IAxiomaticTypeDefinition) elt;

			try {
				ps.appendString(
						atd.getIdentifierString(),
						getHTMLBeginForCSSClass(STYLE, HorizontalAlignment.LEFT,
								VerticalAlignement.MIDDLE),
						getHTMLEndForCSSClass(STYLE, HorizontalAlignment.LEFT,
								VerticalAlignement.MIDDLE), SEPARATOR_BEGIN,
						SEPARATOR_END);
			} catch (RodinDBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

}
