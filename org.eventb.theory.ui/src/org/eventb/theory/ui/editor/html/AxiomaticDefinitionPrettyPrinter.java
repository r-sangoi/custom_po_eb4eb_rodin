package org.eventb.theory.ui.editor.html;

import static org.eventb.internal.ui.UIUtils.HTMLWrapUp;
import static org.eventb.ui.prettyprint.PrettyPrintUtils.getHTMLBeginForCSSClass;
import static org.eventb.ui.prettyprint.PrettyPrintUtils.getHTMLEndForCSSClass;

import org.eventb.theory.core.IAxiomaticDefinitionsBlock;
import org.eventb.ui.prettyprint.DefaultPrettyPrinter;
import org.eventb.ui.prettyprint.IElementPrettyPrinter;
import org.eventb.ui.prettyprint.IPrettyPrintStream;
import org.eventb.ui.prettyprint.PrettyPrintAlignments.HorizontalAlignment;
import org.eventb.ui.prettyprint.PrettyPrintAlignments.VerticalAlignement;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

public class AxiomaticDefinitionPrettyPrinter extends DefaultPrettyPrinter
		implements IElementPrettyPrinter {

	private static final String NORMAL_STYLE = "eventLabel";
	private static final String SEPARATOR_BEGIN = null;
	private static final String SEPARATOR_END = null;

	@SuppressWarnings("restriction")
	@Override
	public void prettyPrint(IInternalElement elt, IInternalElement parent,
			IPrettyPrintStream ps) {
		if (elt instanceof IAxiomaticDefinitionsBlock) {
			final IAxiomaticDefinitionsBlock ad = (IAxiomaticDefinitionsBlock) elt;

			try {
				ps.appendString(HTMLWrapUp(ad.getLabel()),
						getHTMLBeginForCSSClass(NORMAL_STYLE, //
								HorizontalAlignment.LEFT, //
								VerticalAlignement.MIDDLE), //
						getHTMLEndForCSSClass(NORMAL_STYLE, //
								HorizontalAlignment.LEFT, //
								VerticalAlignement.MIDDLE), //
						SEPARATOR_BEGIN, //
						SEPARATOR_END);
			} catch (RodinDBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

}
