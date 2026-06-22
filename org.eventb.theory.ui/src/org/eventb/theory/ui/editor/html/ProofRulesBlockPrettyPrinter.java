package org.eventb.theory.ui.editor.html;

import static org.eventb.ui.prettyprint.PrettyPrintUtils.getHTMLBeginForCSSClass;
import static org.eventb.ui.prettyprint.PrettyPrintUtils.getHTMLEndForCSSClass;
import static org.eventb.ui.prettyprint.PrettyPrintUtils.wrapString;

import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;
import org.eventb.theory.core.IProofRulesBlock;
import org.eventb.ui.prettyprint.DefaultPrettyPrinter;
import org.eventb.ui.prettyprint.IPrettyPrintStream;
import org.eventb.ui.prettyprint.PrettyPrintAlignments.HorizontalAlignment;
import org.eventb.ui.prettyprint.PrettyPrintAlignments.VerticalAlignement;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

@SuppressWarnings("restriction")
public class ProofRulesBlockPrettyPrinter extends DefaultPrettyPrinter {

	private static final String STYLE = "eventLabel";
	private static final String PRB_IDENT_SEPARATOR_BEGIN = null;
	private static final String PRB_IDENT_SEPARATOR_END = ":";
	
	@Override
	public void prettyPrint(IInternalElement elt, IInternalElement parent, 
			IPrettyPrintStream ps) {
		if(elt instanceof IProofRulesBlock){
			IProofRulesBlock block = (IProofRulesBlock) elt;
			try {
				ps.appendString(wrapString(block.getLabel()), 
						getHTMLBeginForCSSClass(STYLE, //
								HorizontalAlignment.LEFT, //
								VerticalAlignement.MIDDLE), //
						getHTMLEndForCSSClass(STYLE, //
								HorizontalAlignment.LEFT, //
								VerticalAlignement.MIDDLE),
						PRB_IDENT_SEPARATOR_BEGIN, 
						PRB_IDENT_SEPARATOR_END);
			} catch (RodinDBException e) {
				EventBEditorUtils.debugAndLogError(
						e,
						"Cannot get the details for rules block "
								+ block.getElementName());
			}
		}
	}

}
