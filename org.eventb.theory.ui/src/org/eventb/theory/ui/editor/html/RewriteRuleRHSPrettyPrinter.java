package org.eventb.theory.ui.editor.html;

import static org.eventb.ui.prettyprint.PrettyPrintUtils.getHTMLBeginForCSSClass;
import static org.eventb.ui.prettyprint.PrettyPrintUtils.getHTMLEndForCSSClass;
import static org.eventb.ui.prettyprint.PrettyPrintUtils.wrapString;

import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;
import org.eventb.theory.core.IRewriteRuleRightHandSide;
import org.eventb.ui.prettyprint.DefaultPrettyPrinter;
import org.eventb.ui.prettyprint.IPrettyPrintStream;
import org.eventb.ui.prettyprint.PrettyPrintAlignments.HorizontalAlignment;
import org.eventb.ui.prettyprint.PrettyPrintAlignments.VerticalAlignement;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

@SuppressWarnings("restriction")
public class RewriteRuleRHSPrettyPrinter extends DefaultPrettyPrinter {

	private static final String RHS_LABEL = "actionLabel"; 
	private static final String RHS_COND = "axiomPredicate";
	private static final String RHS_RHS= "guardPredicate";
	
	private static final String ONE_SPACES = " ";
	
	private static final String RHS_IDENT_SEPARATOR_BEGIN = null;
	private static final String RHS_IDENT_SEPARATOR_END = null;
	
	@Override
	public void prettyPrint(IInternalElement elt, IInternalElement parent, 
			IPrettyPrintStream ps) {
		if(elt instanceof IRewriteRuleRightHandSide){
			IRewriteRuleRightHandSide rhs = (IRewriteRuleRightHandSide) elt;
			try {
				String label = rhs.getLabel();
				String cond = rhs.getPredicateString();
				String rhsForm = rhs.getFormula();
				ps.incrementLevel();
				ps.appendString(wrapString("\u25aa"+ONE_SPACES+label), 
						getHTMLBeginForCSSClass(RHS_LABEL, //
								HorizontalAlignment.LEFT, //
								VerticalAlignement.MIDDLE), //
						getHTMLEndForCSSClass(RHS_LABEL, //
								HorizontalAlignment.LEFT, //
								VerticalAlignement.MIDDLE), 
								RHS_IDENT_SEPARATOR_BEGIN, 
								":");
				ps.appendString(wrapString(cond), 
						getHTMLBeginForCSSClass(RHS_COND, //
								HorizontalAlignment.LEFT, //
								VerticalAlignement.MIDDLE), //
						getHTMLEndForCSSClass(RHS_COND, //
								HorizontalAlignment.LEFT, //
								VerticalAlignement.MIDDLE), 
								RHS_IDENT_SEPARATOR_BEGIN, 
								"\u25b6");
				ps.appendString(wrapString(rhsForm), 
						getHTMLBeginForCSSClass(RHS_RHS, //
								HorizontalAlignment.LEFT, //
								VerticalAlignement.MIDDLE), //
						getHTMLEndForCSSClass(RHS_RHS, //
								HorizontalAlignment.LEFT, //
								VerticalAlignement.MIDDLE), 
								RHS_IDENT_SEPARATOR_BEGIN, 
								RHS_IDENT_SEPARATOR_END);
				ps.decrementLevel();
			} catch (RodinDBException e) {
				EventBEditorUtils.debugAndLogError(
						e,
						"Cannot get the details for rhs "
								+ rhs.getElementName());
			}
			
		}
	}
	
	@Override
	public boolean appendSpecialPrefix(IInternalElement parent,
			String defaultKeyword, IPrettyPrintStream ps, boolean empty) {
		// return false to let the default prefix be used
		return true;
	}

}
