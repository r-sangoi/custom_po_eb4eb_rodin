package fr.enseeiht.eventb.eb4eb.ui.editors.html;

import static org.eventb.ui.prettyprint.PrettyPrintUtils.getHTMLBeginForCSSClass;
import static org.eventb.ui.prettyprint.PrettyPrintUtils.getHTMLEndForCSSClass;
import static org.eventb.ui.prettyprint.PrettyPrintUtils.wrapString;

import org.eventb.ui.prettyprint.DefaultPrettyPrinter;
import org.eventb.ui.prettyprint.IPrettyPrintStream;
import org.eventb.ui.prettyprint.PrettyPrintAlignments.HorizontalAlignment;
import org.eventb.ui.prettyprint.PrettyPrintAlignments.VerticalAlignement;
import org.rodinp.core.IInternalElement;

public class AnnotationCollectionPrettyPrinter extends DefaultPrettyPrinter {
	
	private static final String COMPONENT_NAME = "componentName";
	private static final String COMPONENT_NAME_SEPARATOR_BEGIN = null;
	private static final String COMPONENT_NAME_SEPARATOR_END = null;

	@Override
	public void prettyPrint(IInternalElement elt, IInternalElement parent,
			IPrettyPrintStream ps) {
		final String bareName = elt.getRodinFile().getBareName();
		appendComponentName(ps, wrapString(bareName));
	}

	protected static void appendComponentName(IPrettyPrintStream ps, String label) {
		ps.appendString(label, //
				getHTMLBeginForCSSClass(COMPONENT_NAME,
						HorizontalAlignment.LEFT, //
						VerticalAlignement.MIDDLE), //
				getHTMLEndForCSSClass(COMPONENT_NAME, //
						HorizontalAlignment.LEFT, //
						VerticalAlignement.MIDDLE), //
				COMPONENT_NAME_SEPARATOR_BEGIN, //
				COMPONENT_NAME_SEPARATOR_END);
	}

}
