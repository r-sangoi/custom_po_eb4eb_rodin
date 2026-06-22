package org.eventb.theory.ui.editor.html;

import static org.eventb.ui.prettyprint.PrettyPrintUtils.getHTMLBeginForCSSClass;
import static org.eventb.ui.prettyprint.PrettyPrintUtils.getHTMLEndForCSSClass;
import static org.eventb.ui.prettyprint.PrettyPrintUtils.wrapString;

import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;
import org.eventb.theory.core.IConstructorArgument;
import org.eventb.theory.core.IDatatypeConstructor;
import org.eventb.ui.prettyprint.DefaultPrettyPrinter;
import org.eventb.ui.prettyprint.IPrettyPrintStream;
import org.eventb.ui.prettyprint.PrettyPrintAlignments.HorizontalAlignment;
import org.eventb.ui.prettyprint.PrettyPrintAlignments.VerticalAlignement;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

@SuppressWarnings("restriction")
public class DatatypeConstructorPrettyPrinter extends DefaultPrettyPrinter {

	private static final String STYLE = "convergence";
	private static final String DTC_IDENT_SEPARATOR_BEGIN = null;
	private static final String DTC_IDENT_SEPARATOR_END = null;
	private static final String ONE_SPACES = " ";
	private static final String TRIANGLE = "\u25ba";
	
	@Override
	public void prettyPrint(IInternalElement elt, IInternalElement parent,
			IPrettyPrintStream ps) {
		if (elt instanceof IDatatypeConstructor) {
			final IDatatypeConstructor cons = (IDatatypeConstructor) elt;
			try {
				writeConstructor(cons, ps);
			} catch (RodinDBException e) {
				EventBEditorUtils.debugAndLogError(e,
						"Cannot get the details for constructor "
								+ cons.getElementName());
			}
		}
	}
	
	@Override
	public boolean appendSpecialPrefix(IInternalElement parent,
			String defaultKeyword, IPrettyPrintStream ps, boolean empty) {
		// return false to let the default prefix be used
		return true;
	}
	
	private static void writeConstructor(IDatatypeConstructor cons, IPrettyPrintStream ps)
		throws RodinDBException{
		ps.appendString(wrapString(TRIANGLE+ONE_SPACES+appendCons(cons)), 
				getHTMLBeginForCSSClass("convergence", //
						HorizontalAlignment.LEFT, //
						VerticalAlignement.MIDDLE), //
				getHTMLEndForCSSClass(STYLE, //
						HorizontalAlignment.LEFT, //
						VerticalAlignement.MIDDLE),  
				DTC_IDENT_SEPARATOR_BEGIN, 
				DTC_IDENT_SEPARATOR_END);
	}
	
	private static String appendCons(IDatatypeConstructor cons) throws RodinDBException{
		String result = cons.getIdentifierString();
		IConstructorArgument[] args = cons.getConstructorArguments();
		if (args != null && args.length != 0){
			result += "(";
			for (int i = 0 ; i < args.length ; i++){
				result += args[i].getIdentifierString()+":"+args[i].getType();
				if(i<args.length - 1)
					result+=","+ONE_SPACES;
			}
			result += ")";
		}
		return result;
	}

}
