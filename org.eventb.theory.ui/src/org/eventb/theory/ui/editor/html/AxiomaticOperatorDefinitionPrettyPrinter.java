package org.eventb.theory.ui.editor.html;

import static org.eventb.ui.prettyprint.PrettyPrintUtils.getHTMLBeginForCSSClass;
import static org.eventb.ui.prettyprint.PrettyPrintUtils.getHTMLEndForCSSClass;
import static org.eventb.ui.prettyprint.PrettyPrintUtils.wrapString;

import org.eventb.core.ast.extension.IOperatorProperties.FormulaType;
import org.eventb.core.ast.extension.IOperatorProperties.Notation;
import org.eventb.theory.core.IAxiomaticOperatorDefinition;
import org.eventb.theory.core.IOperatorArgument;
import org.eventb.ui.prettyprint.DefaultPrettyPrinter;
import org.eventb.ui.prettyprint.IPrettyPrintStream;
import org.eventb.ui.prettyprint.PrettyPrintAlignments.HorizontalAlignment;
import org.eventb.ui.prettyprint.PrettyPrintAlignments.VerticalAlignement;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

public class AxiomaticOperatorDefinitionPrettyPrinter extends
		DefaultPrettyPrinter {

	private static final String NORMAL_STYLE = ".extended";
	private static final String LABEL = "eventLabel";
	private static final String BLACK_STYLE = "extended";

	private static final String SEPARATOR_BEGIN = null;
	private static final String SEPARATOR_END = null;
	private static final String SPACE = " ";
	private static final String TWO_SPACES = "  ";
	private static final String EXPRESSION_STRING = "EXPRESSION";
	private static final String PREDICATE_STRING = "PREDICATE";
	private static final String ASSOC = "assoc";
	private static final String COMMUT = "commut";
	private static final String LABEL_SEPARATOR = ":";
	private static final String PROP = "variantExpression";
	private static final String OPEN_PAR = "(";
	private static final String CLOSE_PAR = ")";

	@Override
	public void prettyPrint(IInternalElement elt, IInternalElement parent,
			IPrettyPrintStream ps) {

		if (elt instanceof IAxiomaticOperatorDefinition) {
			final IAxiomaticOperatorDefinition aod = (IAxiomaticOperatorDefinition) elt;

			try {
				stringBuilder(aod, ps);
			} catch (RodinDBException e) {
				e.printStackTrace();
			}
		}
	}

	private void stringBuilder(IAxiomaticOperatorDefinition aod,
			IPrettyPrintStream ps) throws RodinDBException {
		Notation not = aod.getNotationType();
		boolean ass = aod.isAssociative();
		boolean com = aod.isCommutative();
		IOperatorArgument[] args = aod.getOperatorArguments();

		ps.appendString(wrapString("\u2022" + aod.getLabel() + LABEL_SEPARATOR
				+ TWO_SPACES), getHTMLBeginForCSSClass(LABEL, //
				HorizontalAlignment.LEFT, //
				VerticalAlignement.MIDDLE), //
				getHTMLEndForCSSClass(LABEL, //
						HorizontalAlignment.LEFT, //
						VerticalAlignement.MIDDLE), //
				SEPARATOR_BEGIN, //
				SEPARATOR_END);

		String tmpString = aod.getLabel() + OPEN_PAR;
		boolean first = true;

		for (IOperatorArgument a : args) {
			String id = a.getIdentifierString();
			String expr = a.getExpressionString();
			if (first) {
				tmpString = tmpString + id + " : " + expr;
				first = false;
			} else {
				tmpString = tmpString + ", " + id + " : " + expr;
			}
		}

		tmpString = tmpString + CLOSE_PAR;

		ps.appendString(wrapString(tmpString),
				getHTMLBeginForCSSClass(NORMAL_STYLE, //
						HorizontalAlignment.LEFT, //
						VerticalAlignement.MIDDLE),
				getHTMLBeginForCSSClass(NORMAL_STYLE, //
						HorizontalAlignment.LEFT, //
						VerticalAlignement.MIDDLE), SEPARATOR_BEGIN,
				SEPARATOR_END);

		String lab = "";
		if (aod.getFormulaType() == FormulaType.EXPRESSION) {
			lab = lab + SPACE + EXPRESSION_STRING;
		} else {
			lab = lab + SPACE + PREDICATE_STRING;
		}
		lab = lab + SPACE + not.toString();

		ps.appendString(wrapString(lab), getHTMLBeginForCSSClass(BLACK_STYLE, //
				HorizontalAlignment.LEFT, //
				VerticalAlignement.MIDDLE),
				getHTMLBeginForCSSClass(BLACK_STYLE, //
						HorizontalAlignment.LEFT, //
						VerticalAlignement.MIDDLE), SEPARATOR_BEGIN,
				SEPARATOR_END);
		if (ass) {
			ps.appendString(wrapString(TWO_SPACES + ASSOC),
					getHTMLBeginForCSSClass(PROP, //
							HorizontalAlignment.LEFT, //
							VerticalAlignement.MIDDLE),
					getHTMLEndForCSSClass(PROP, //
							HorizontalAlignment.LEFT, //
							VerticalAlignement.MIDDLE), //
					SEPARATOR_BEGIN, SEPARATOR_END);
		}
		if (com) {
			ps.appendString(wrapString(TWO_SPACES + COMMUT),
					getHTMLBeginForCSSClass(PROP, //
							HorizontalAlignment.LEFT, //
							VerticalAlignement.MIDDLE),
					getHTMLEndForCSSClass(PROP, //
							HorizontalAlignment.LEFT, //
							VerticalAlignement.MIDDLE), //
					SEPARATOR_BEGIN, SEPARATOR_END);
		}
		ps.appendString(wrapString(TWO_SPACES + aod.getType()),
				getHTMLBeginForCSSClass(NORMAL_STYLE, //
						HorizontalAlignment.LEFT, //
						VerticalAlignement.MIDDLE),
				getHTMLEndForCSSClass(NORMAL_STYLE, //
						HorizontalAlignment.LEFT, //
						VerticalAlignement.MIDDLE), //
				SEPARATOR_BEGIN, SEPARATOR_END);
	}
}
