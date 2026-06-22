package org.eventb.theory.ui.editor.html;

import static org.eventb.ui.prettyprint.PrettyPrintUtils.getHTMLBeginForCSSClass;
import static org.eventb.ui.prettyprint.PrettyPrintUtils.getHTMLEndForCSSClass;
import static org.eventb.ui.prettyprint.PrettyPrintUtils.wrapString;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.ast.extension.IOperatorProperties.Notation;
import org.eventb.theory.core.IDirectOperatorDefinition;
import org.eventb.theory.core.INewOperatorDefinition;
import org.eventb.theory.core.IOperatorArgument;
import org.eventb.theory.internal.ui.TheoryUIUtils;
import org.eventb.ui.prettyprint.DefaultPrettyPrinter;
import org.eventb.ui.prettyprint.IPrettyPrintStream;
import org.eventb.ui.prettyprint.PrettyPrintAlignments.HorizontalAlignment;
import org.eventb.ui.prettyprint.PrettyPrintAlignments.VerticalAlignement;
import org.rodinp.core.IInternalElement;

public class OperatorDefinitionPrettyPrinter extends DefaultPrettyPrinter {

	static final String NOP_SYMBOL = "\u2259";
	private static final String OP_DETAILS = ".extended";
	private static final String OP_IDENT_SEPARATOR_BEGIN = null;
	private static final String OP_IDENT_SEPARATOR_END = null;
	private static final String TWO_SPACES = "  ";

	private static final String ONE_SPACES = " ";

	@Override
	public void prettyPrint(IInternalElement elt, IInternalElement parent,
			IPrettyPrintStream ps) {
		if (elt instanceof IDirectOperatorDefinition) {
			final IDirectOperatorDefinition nod = (IDirectOperatorDefinition) elt;
			ps.appendString(getOperatorText(nod),
					getHTMLBeginForCSSClass(OP_DETAILS, //
							HorizontalAlignment.LEFT, //
							VerticalAlignement.MIDDLE), //
					getHTMLEndForCSSClass(OP_DETAILS, //
							HorizontalAlignment.LEFT, //
							VerticalAlignement.MIDDLE), //
					OP_IDENT_SEPARATOR_BEGIN, //
					OP_IDENT_SEPARATOR_END);

		}
	}

	protected static String getOperatorText(IDirectOperatorDefinition def) {
		INewOperatorDefinition opDef = def.getAncestor(INewOperatorDefinition.ELEMENT_TYPE);
		StringBuilder builder = new StringBuilder();
		try {
			if (opDef.getNotationType().equals(Notation.INFIX)) {
				IOperatorArgument args[] = opDef.getOperatorArguments();
				if (args.length == 0) {
					builder.append(opDef.getLabel() + TWO_SPACES);
				} else {
					int i = 0;
					for (IOperatorArgument arg : args) {
						builder.append("(" + arg.getIdentifierString()
								+ ONE_SPACES + ":" + ONE_SPACES + arg.getExpressionString()
								+ ")");
						if (i < args.length - 1) {
							builder.append(ONE_SPACES + opDef.getLabel()
									+ ONE_SPACES);
						}
						i++;
					}
					builder.append(TWO_SPACES);
				}
			} else {
				builder.append(opDef.getLabel());
				IOperatorArgument args[] = opDef.getOperatorArguments();
				if (args.length > 0) {
					builder.append("(");
					int i = 0;
					for (IOperatorArgument arg : args) {
						builder.append(arg.getIdentifierString() + ONE_SPACES
								+ ":" + ONE_SPACES + arg.getExpressionString());
						if (i < args.length - 1) {
							builder.append(", ");
						}
						i++;
					}
					builder.append(") ");
				}
			}
			builder.append(NOP_SYMBOL + ONE_SPACES + def.getFormula());
			return wrapString(builder.toString());
		} catch (CoreException e) {
			TheoryUIUtils.log(e, "Unable to retrive operator details.");
		}
		return "ERROR";
	}

}
