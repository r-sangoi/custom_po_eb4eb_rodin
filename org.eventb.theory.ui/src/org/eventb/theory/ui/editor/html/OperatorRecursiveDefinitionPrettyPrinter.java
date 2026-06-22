package org.eventb.theory.ui.editor.html;

import static org.eventb.ui.prettyprint.PrettyPrintUtils.getHTMLBeginForCSSClass;
import static org.eventb.ui.prettyprint.PrettyPrintUtils.getHTMLEndForCSSClass;
import static org.eventb.ui.prettyprint.PrettyPrintUtils.wrapString;

import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;
import org.eventb.theory.core.IRecursiveOperatorDefinition;
import org.eventb.ui.prettyprint.DefaultPrettyPrinter;
import org.eventb.ui.prettyprint.IPrettyPrintStream;
import org.eventb.ui.prettyprint.PrettyPrintAlignments.HorizontalAlignment;
import org.eventb.ui.prettyprint.PrettyPrintAlignments.VerticalAlignement;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

@SuppressWarnings("restriction")
public class OperatorRecursiveDefinitionPrettyPrinter extends DefaultPrettyPrinter {

	private static final String REC_DETAILS = "axiomPredicate";
	private static final String REC_IDENT_SEPARATOR_BEGIN = null;
	private static final String REC_IDENT_SEPARATOR_END = null;
	
	@Override
	public void prettyPrint(IInternalElement elt, IInternalElement parent,
			IPrettyPrintStream ps) {
		if (elt instanceof IRecursiveOperatorDefinition) {
			final IRecursiveOperatorDefinition nod = (IRecursiveOperatorDefinition) elt;
			ps.appendString(getInductiveArgStr(nod),
					getHTMLBeginForCSSClass(REC_DETAILS, //
							HorizontalAlignment.LEFT, //
							VerticalAlignement.MIDDLE), //
					getHTMLEndForCSSClass(REC_DETAILS, //
							HorizontalAlignment.LEFT, //
							VerticalAlignement.MIDDLE), //
					REC_IDENT_SEPARATOR_BEGIN, //
					REC_IDENT_SEPARATOR_END);

		}
	}

	private String getInductiveArgStr(IRecursiveOperatorDefinition nod) 
	{
		String str = "case ";
		try {
			if (nod.hasInductiveArgument())
				str += nod.getInductiveArgument();
		} catch (RodinDBException e) {
			EventBEditorUtils.debugAndLogError(
					e,
					"Cannot get the details for rec def "
							+ nod.getElementName());
		}
		return wrapString(str);
	}

}
