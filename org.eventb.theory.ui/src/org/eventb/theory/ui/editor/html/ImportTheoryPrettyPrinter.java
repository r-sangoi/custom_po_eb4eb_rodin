/**
 * 
 */
package org.eventb.theory.ui.editor.html;

import static org.eventb.ui.prettyprint.PrettyPrintUtils.getHTMLBeginForCSSClass;
import static org.eventb.ui.prettyprint.PrettyPrintUtils.getHTMLEndForCSSClass;
import static org.eventb.ui.prettyprint.PrettyPrintUtils.wrapString;

import org.eventb.theory.core.IImportTheory;
import org.eventb.theory.internal.ui.TheoryUIUtils;
import org.eventb.ui.prettyprint.DefaultPrettyPrinter;
import org.eventb.ui.prettyprint.IPrettyPrintStream;
import org.eventb.ui.prettyprint.PrettyPrintAlignments.HorizontalAlignment;
import org.eventb.ui.prettyprint.PrettyPrintAlignments.VerticalAlignement;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

/**
 * @author asiehsalehi
 *
 */
public class ImportTheoryPrettyPrinter extends DefaultPrettyPrinter {
	
	private static final String ELEMENT_CLAUSE = "convergence";
	private static final String ELEMENT_CLAUSE_SEPARATOR_BEGIN = null;
	private static final String ELEMENT_CLAUSE_SEPARATOR_END = null;
	private static final String TAB = "  ";

	@Override
	public void prettyPrint(IInternalElement elt, IInternalElement parent,
			IPrettyPrintStream ps) {
		assert elt instanceof IImportTheory;
		try{
			StringBuffer includeMchClause= new StringBuffer();
			IImportTheory includeMachine = (IImportTheory)elt;
			includeMchClause.append(TAB + includeMachine.getImportTheory().getComponentName());
			appendClause(ps, wrapString(includeMchClause.toString()));
		} catch (RodinDBException e) {
			TheoryUIUtils.log(e, "Cannot get the import target for import "
					+ elt.getElementName());
		}
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
