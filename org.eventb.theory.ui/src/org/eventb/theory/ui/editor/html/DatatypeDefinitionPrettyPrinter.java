package org.eventb.theory.ui.editor.html;

import static org.eventb.ui.prettyprint.PrettyPrintUtils.getHTMLBeginForCSSClass;
import static org.eventb.ui.prettyprint.PrettyPrintUtils.getHTMLEndForCSSClass;
import static org.eventb.ui.prettyprint.PrettyPrintUtils.wrapString;

import java.util.ArrayList;
import java.util.List;

import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;
import org.eventb.theory.core.IDatatypeDefinition;
import org.eventb.theory.core.ITypeArgument;
import org.eventb.ui.prettyprint.DefaultPrettyPrinter;
import org.eventb.ui.prettyprint.IElementPrettyPrinter;
import org.eventb.ui.prettyprint.IPrettyPrintStream;
import org.eventb.ui.prettyprint.PrettyPrintAlignments.HorizontalAlignment;
import org.eventb.ui.prettyprint.PrettyPrintAlignments.VerticalAlignement;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;


@SuppressWarnings("restriction")
public class DatatypeDefinitionPrettyPrinter extends DefaultPrettyPrinter implements
	IElementPrettyPrinter{
	
	private static final String STYLE = "eventLabel";
	private static final String DT_IDENT_SEPARATOR_BEGIN = null;
	private static final String DT_IDENT_SEPARATOR_END = null;
	private static final String TWO_SPACES = "  ";
	private static final String ONE_SPACES = " ";
	private static final String DT_SYMBOL = "\u2259";
	
	@Override
	public void prettyPrint(IInternalElement elt, IInternalElement parent,
			IPrettyPrintStream ps) {
		if (elt instanceof IDatatypeDefinition) {
			final IDatatypeDefinition dt = (IDatatypeDefinition) elt;
			try {
				writeDTNameAndTypePars(dt, ps);
			} catch (RodinDBException e) {
				EventBEditorUtils.debugAndLogError(e,
						"Cannot get the details for datatype "
								+ dt.getElementName());
			}
		}
	}

	private void writeDTNameAndTypePars(IDatatypeDefinition dt, IPrettyPrintStream ps)
		throws RodinDBException{
		
		ps.appendString(wrapString(dt.getIdentifierString()+addTypeParameters(dt)+TWO_SPACES), 
				getHTMLBeginForCSSClass(STYLE, //
						HorizontalAlignment.LEFT, //
						VerticalAlignement.MIDDLE), //
				getHTMLEndForCSSClass(STYLE, //
						HorizontalAlignment.LEFT, //
						VerticalAlignement.MIDDLE),  
				DT_IDENT_SEPARATOR_BEGIN, 
				DT_IDENT_SEPARATOR_END);
		ps.appendString(wrapString(DT_SYMBOL+TWO_SPACES), 
						getHTMLBeginForCSSClass("extended", //
								HorizontalAlignment.LEFT, //
								VerticalAlignement.MIDDLE), //
						getHTMLEndForCSSClass(STYLE, //
								HorizontalAlignment.LEFT, //
								VerticalAlignement.MIDDLE), 
						DT_IDENT_SEPARATOR_BEGIN, 
						DT_IDENT_SEPARATOR_END);
		
		
	}
	
	private String writeBetweenBrackets(String str[]){
		if(str.length == 0) return "";
		else if(str.length == 1) return "("+str[0]+")";
		else {
			String retVal = "(";
			for (int i = 0; i < str.length ; i++){
				retVal += str[i];
				if (i<str.length-1){
					retVal += ","+ONE_SPACES;
				}
				
			}
			return retVal+")";
		}
	}
	
	private String addTypeParameters(IDatatypeDefinition dtd)
	throws RodinDBException{
		final ITypeArgument args[] = dtd.getTypeArguments();
		List<String> pars = new ArrayList<String>();
		for (ITypeArgument ta : args){
			if(!ta.hasGivenType()) continue;
			pars.add(ta.getGivenType());
		}
		return writeBetweenBrackets(pars.toArray(new String[pars.size()]));
	}
}
