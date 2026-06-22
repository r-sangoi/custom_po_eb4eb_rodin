/**
 * 
 */
package org.eventb.theory.ui.editor.html;

import static org.eventb.ui.prettyprint.PrettyPrintUtils.getHTMLBeginForCSSClass;
import static org.eventb.ui.prettyprint.PrettyPrintUtils.getHTMLEndForCSSClass;
import static org.eventb.ui.prettyprint.PrettyPrintUtils.wrapString;

import org.eventb.theory.core.IAvailableTheoryProject;
import org.eventb.theory.core.IImportTheoryProject;
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
public class ImportProjectPrettyPrinter extends DefaultPrettyPrinter {

	private static final String ELEMENT_CLAUSE = "eventLabel";
	private static final String ELEMENT_CLAUSE_SEPARATOR_BEGIN = null;
	private static final String ELEMENT_CLAUSE_SEPARATOR_END = null;

	@Override
	public void prettyPrint(IInternalElement elt, IInternalElement parent,
			IPrettyPrintStream ps) {
		assert elt instanceof IAvailableTheoryProject;
		StringBuffer importTheoryProjectClause= new StringBuffer();
		try {
			IImportTheoryProject importTheory = (IImportTheoryProject)elt;
			if(importTheory.hasTheoryProject()){
				importTheoryProjectClause.append("[");
				importTheoryProjectClause.append(importTheory.getTheoryProject().getElementName());
				importTheoryProjectClause.append("] ");
			}
		} catch (RodinDBException e) {
			TheoryUIUtils.log(e,
					"Cannot get sees import theory project clause of " + elt.getRodinFile().getElementName());
		}
		appendClause(ps, wrapString(importTheoryProjectClause.toString()));
	}

	protected static void appendClause(IPrettyPrintStream ps, String label) {
		ps.appendString(label, //
				getHTMLBeginForCSSClass(ELEMENT_CLAUSE,
						HorizontalAlignment.RIGHT, //
						VerticalAlignement.MIDDLE), //
				getHTMLEndForCSSClass(ELEMENT_CLAUSE, //
						HorizontalAlignment.RIGHT, //
						VerticalAlignement.MIDDLE), //
						ELEMENT_CLAUSE_SEPARATOR_BEGIN, //
						ELEMENT_CLAUSE_SEPARATOR_END);
	}

}
