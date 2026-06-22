/**
 * 
 */
package org.eventb.theory.language.ui.editor.html;

import static org.eventb.ui.prettyprint.PrettyPrintUtils.getHTMLBeginForCSSClass;
import static org.eventb.ui.prettyprint.PrettyPrintUtils.getHTMLEndForCSSClass;
import static org.eventb.ui.prettyprint.PrettyPrintUtils.wrapString;

import org.eventb.ui.prettyprint.DefaultPrettyPrinter;
import org.eventb.ui.prettyprint.IPrettyPrintStream;
import org.eventb.ui.prettyprint.PrettyPrintAlignments.HorizontalAlignment;
import org.eventb.ui.prettyprint.PrettyPrintAlignments.VerticalAlignement;
import org.rodinp.core.IInternalElement;

/**
 * @author Renato Silva
 *
 */
public class RootPrettyPrinter extends DefaultPrettyPrinter {

	/**
	 * 
	 */
	public RootPrettyPrinter() {
		// TODO Auto-generated constructor stub
	}
	
	private static final String ROOT_FILE_NAME = "componentName";
	private static final String ROOT_FILE_SEPARATOR_BEGIN = null;
	private static final String ROOT_FILE_SEPARATOR_END = null;

	@Override
	public void prettyPrint(IInternalElement elt, IInternalElement parent,
			IPrettyPrintStream ps) {
		final String bareName = elt.getRodinFile().getBareName();
		appendElementName(ps, wrapString(bareName));
	}

	protected static void appendElementName(IPrettyPrintStream ps, String label) {
		ps.appendString(label, //
				getHTMLBeginForCSSClass(ROOT_FILE_NAME,
						HorizontalAlignment.LEFT, //
						VerticalAlignement.MIDDLE), //
				getHTMLEndForCSSClass(ROOT_FILE_NAME, //
						HorizontalAlignment.LEFT, //
						VerticalAlignement.MIDDLE), //
						ROOT_FILE_SEPARATOR_BEGIN, //
						ROOT_FILE_SEPARATOR_END);
	}

}
