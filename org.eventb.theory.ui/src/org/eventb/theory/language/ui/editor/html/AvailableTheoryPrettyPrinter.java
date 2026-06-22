/**
 * 
 */
package org.eventb.theory.language.ui.editor.html;

import static org.eventb.ui.prettyprint.PrettyPrintUtils.getHTMLBeginForCSSClass;
import static org.eventb.ui.prettyprint.PrettyPrintUtils.getHTMLEndForCSSClass;
import static org.eventb.ui.prettyprint.PrettyPrintUtils.wrapString;

import org.eventb.theory.core.IAvailableTheory;
import org.eventb.theory.internal.ui.TheoryUIUtils;
import org.eventb.ui.prettyprint.DefaultPrettyPrinter;
import org.eventb.ui.prettyprint.IPrettyPrintStream;
import org.eventb.ui.prettyprint.PrettyPrintAlignments.HorizontalAlignment;
import org.eventb.ui.prettyprint.PrettyPrintAlignments.VerticalAlignement;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Renato Silva
 *
 */
public class AvailableTheoryPrettyPrinter extends DefaultPrettyPrinter {
	
	private static final String ELEMENT_CLAUSE = "convergence";
	private static final String ELEMENT_CLAUSE_SEPARATOR_BEGIN = null;
	private static final String ELEMENT_CLAUSE_SEPARATOR_END = null;
	private static final String TAB = "  ";

	@Override
	public void prettyPrint(IInternalElement elt, IInternalElement parent,
			IPrettyPrintStream ps) {
		assert elt instanceof IAvailableTheory;
		StringBuffer includeMchClause= new StringBuffer();
		try {
			IAvailableTheory includeMachine = (IAvailableTheory)elt;
			includeMchClause.append(TAB + includeMachine.getLabel());
		} catch (RodinDBException e) {
			TheoryUIUtils.log(e,
					"Cannot get sees available theory clause of " + elt.getRodinFile().getElementName());
		}
		appendClause(ps, wrapString(includeMchClause.toString()));
	}

	protected static void appendClause(IPrettyPrintStream ps, String label) {
		ps.appendString(label, //
				getHTMLBeginForCSSClass(ELEMENT_CLAUSE,
						HorizontalAlignment.RIGHT, //
						VerticalAlignement.MIDDLE), //
				getHTMLEndForCSSClass(ELEMENT_CLAUSE, //
						HorizontalAlignment.LEFT, //
						VerticalAlignement.MIDDLE), //
						ELEMENT_CLAUSE_SEPARATOR_BEGIN, //
						ELEMENT_CLAUSE_SEPARATOR_END);
	}

}
