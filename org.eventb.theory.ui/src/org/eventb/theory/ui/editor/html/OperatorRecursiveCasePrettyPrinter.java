package org.eventb.theory.ui.editor.html;

import static org.eventb.ui.prettyprint.PrettyPrintUtils.getHTMLBeginForCSSClass;
import static org.eventb.ui.prettyprint.PrettyPrintUtils.getHTMLEndForCSSClass;
import static org.eventb.ui.prettyprint.PrettyPrintUtils.wrapString;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.ast.extension.IOperatorProperties.Notation;
import org.eventb.theory.core.INewOperatorDefinition;
import org.eventb.theory.core.IOperatorArgument;
import org.eventb.theory.core.IRecursiveDefinitionCase;
import org.eventb.theory.core.IRecursiveOperatorDefinition;
import org.eventb.theory.internal.ui.TheoryUIUtils;
import org.eventb.ui.prettyprint.DefaultPrettyPrinter;
import org.eventb.ui.prettyprint.IPrettyPrintStream;
import org.eventb.ui.prettyprint.PrettyPrintAlignments.HorizontalAlignment;
import org.eventb.ui.prettyprint.PrettyPrintAlignments.VerticalAlignement;
import org.rodinp.core.IInternalElement;

public class OperatorRecursiveCasePrettyPrinter extends DefaultPrettyPrinter {

	private static final String CASE_DETAILS = ".extended";
	private static final String CASE_IDENT_SEPARATOR_BEGIN = null;
	private static final String CASE_IDENT_SEPARATOR_END = null;
	private static final String TWO_SPACES = "  ";

	private static final String ONE_SPACES = " ";
	
	@Override
	public void prettyPrint(IInternalElement elt, IInternalElement parent,
			IPrettyPrintStream ps) {
		if (elt instanceof IRecursiveDefinitionCase) {
			IRecursiveDefinitionCase defCase = (IRecursiveDefinitionCase) elt;
			String caseStr = getOperatorText(defCase, parent);
			ps.appendString(caseStr,
					getHTMLBeginForCSSClass(CASE_DETAILS, //
							HorizontalAlignment.LEFT, //
							VerticalAlignement.MIDDLE), //
					getHTMLEndForCSSClass(CASE_DETAILS, //
							HorizontalAlignment.LEFT, //
							VerticalAlignement.MIDDLE), //
					CASE_IDENT_SEPARATOR_BEGIN, //
					CASE_IDENT_SEPARATOR_END);

		}
	}

	protected String getOperatorText(IRecursiveDefinitionCase def, IInternalElement parent) {
		INewOperatorDefinition opDef = def.getAncestor(INewOperatorDefinition.ELEMENT_TYPE);
		StringBuilder builder = new StringBuilder();
		try {
			String inductiveArg = ((IRecursiveOperatorDefinition) parent).getInductiveArgument();
			if (opDef.getNotationType().equals(Notation.INFIX)) {
				IOperatorArgument args[] = opDef.getOperatorArguments();
				if (args.length == 0) {
					builder.append(opDef.getLabel() + TWO_SPACES);
				} else {
					int i = 0;
					for (IOperatorArgument arg : args) {
						String argStr = arg.getIdentifierString();
						if (argStr.equals(inductiveArg)){
							argStr = def.getExpressionString();
						}
						builder.append(argStr );
						if (i < args.length - 1) {
							builder.append(ONE_SPACES + opDef.getLabel() + ONE_SPACES);
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
						String argStr = arg.getIdentifierString();
						if (argStr.equals(inductiveArg)){
							argStr = def.getExpressionString();
						}
						builder.append(argStr);
						if (i < args.length - 1) {
							builder.append(", ");
						}
						i++;
					}
					builder.append(") ");
				}
			}
			builder.append(OperatorDefinitionPrettyPrinter.NOP_SYMBOL + ONE_SPACES + def.getFormula());
			return wrapString(builder.toString());
		} catch (CoreException e) {
			TheoryUIUtils.log(e, "Unable to retrive operator details.");
		}
		return "ERROR";
	}

	@Override
	public boolean appendSpecialPrefix(IInternalElement parent,
			String defaultKeyword, IPrettyPrintStream ps, boolean empty) {
		return true;
	}
}
