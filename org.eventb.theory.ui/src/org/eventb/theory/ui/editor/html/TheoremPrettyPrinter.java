/*******************************************************************************
 * Copyright (c) 2006, 2010 ETH Zurich and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - replaced inherited by extended, event variable by parameter
 *     Systerel - separation of file and root element
 *     Systerel - added implicit children for events
 *     Systerel - added theorem attribute of IDerivedPredicateElement
 * 	   Systerel - fixed bug #2884774 : display guards marked as theorems
 * 	   Systerel - fixed bug #2936324 : Extends clauses in pretty print
 *     Systerel - Extracted and refactored from AstConverter
 ******************************************************************************/
package org.eventb.theory.ui.editor.html;

import static org.eventb.ui.prettyprint.PrettyPrintUtils.getHTMLBeginForCSSClass;
import static org.eventb.ui.prettyprint.PrettyPrintUtils.getHTMLEndForCSSClass;
import static org.eventb.ui.prettyprint.PrettyPrintUtils.wrapString;

import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;
import org.eventb.theory.core.ITheorem;
import org.eventb.ui.prettyprint.DefaultPrettyPrinter;
import org.eventb.ui.prettyprint.IElementPrettyPrinter;
import org.eventb.ui.prettyprint.IPrettyPrintStream;
import org.eventb.ui.prettyprint.PrettyPrintAlignments.HorizontalAlignment;
import org.eventb.ui.prettyprint.PrettyPrintAlignments.VerticalAlignement;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

@SuppressWarnings("restriction")
public class TheoremPrettyPrinter extends DefaultPrettyPrinter implements
		IElementPrettyPrinter {

	private static String THEOREM_LABEL = "eventLabel";
	private static String THEOREM_PREDICATE = "axiomPredicate";

	private static final String THM_LABEL_SEPARATOR_BEGIN = null;
	private static final String THM_LABEL_SEPARATOR_END = ":";
	private static final String THM_PREDICATE_SEPARATOR_BEGIN = null;
	private static final String THM_PREDICATE_SEPARATOR_END = null;

	@Override
	public void prettyPrint(IInternalElement elt, IInternalElement parent,
			IPrettyPrintStream ps) {
		if (elt instanceof ITheorem) {
			final ITheorem thm = (ITheorem) elt;
			try {
				final String label = wrapString(thm.getLabel());
				appendThmLabel(ps, label);
				appendTheoremPredicate(ps, wrapString(thm.getPredicateString()));
			} catch (RodinDBException e) {
				EventBEditorUtils.debugAndLogError(e,
						"Cannot get details for theorem " + thm.getElementName());
			}
		}
	}

	private static void appendThmLabel(IPrettyPrintStream ps, String label) {
		final String cssClass = THEOREM_LABEL;
		
		ps.appendString(label, //
				getHTMLBeginForCSSClass(cssClass,//
						HorizontalAlignment.LEFT, //
						VerticalAlignement.MIDDLE), //
				getHTMLEndForCSSClass(cssClass, //
						HorizontalAlignment.LEFT, //
						VerticalAlignement.MIDDLE), //
				THM_LABEL_SEPARATOR_BEGIN, //
				THM_LABEL_SEPARATOR_END);

	}

	private static void appendTheoremPredicate(IPrettyPrintStream ps,
			String predicate) {
		ps.appendString(predicate, //
				getHTMLBeginForCSSClass(THEOREM_PREDICATE, //
						HorizontalAlignment.LEFT,//
						VerticalAlignement.MIDDLE), //
				getHTMLEndForCSSClass(THEOREM_PREDICATE, //
						HorizontalAlignment.LEFT,//
						VerticalAlignement.MIDDLE), //
				THM_PREDICATE_SEPARATOR_BEGIN, //
				THM_PREDICATE_SEPARATOR_END);
	}

}
