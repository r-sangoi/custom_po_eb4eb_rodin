/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.ui.editor.html;


import static org.eventb.ui.prettyprint.PrettyPrintUtils.getHTMLBeginForCSSClass;
import static org.eventb.ui.prettyprint.PrettyPrintUtils.getHTMLEndForCSSClass;
import static org.eventb.ui.prettyprint.PrettyPrintUtils.wrapString;

import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;
import org.eventb.theory.core.ITypeParameter;
import org.eventb.ui.prettyprint.DefaultPrettyPrinter;
import org.eventb.ui.prettyprint.IElementPrettyPrinter;
import org.eventb.ui.prettyprint.IPrettyPrintStream;
import org.eventb.ui.prettyprint.PrettyPrintAlignments.HorizontalAlignment;
import org.eventb.ui.prettyprint.PrettyPrintAlignments.VerticalAlignement;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

/**
 * @author maamria
 *
 */
@SuppressWarnings("restriction")
public class TypeParameterPrettyPrinter extends DefaultPrettyPrinter implements
		IElementPrettyPrinter {

	private static String TYPE_IDENTIFIER = "setIdentifier";
	private static final String TYPE_IDENTIFIER_SEPARATOR_BEGIN = null;
	private static final String TYPE_IDENTIFIER_SEPARATOR_END = null;
	

	@Override
	public void prettyPrint(IInternalElement elt, IInternalElement parent,
			IPrettyPrintStream ps) {
		if (elt instanceof ITypeParameter) {
			final ITypeParameter set = (ITypeParameter) elt;
			try {
				appendTypeIdentifier(ps, //
						wrapString(set.getIdentifierString()));
			} catch (RodinDBException e) {
				EventBEditorUtils.debugAndLogError(e,
						"Cannot get the identifier string for type parameter "
								+ set.getElementName());
			}
		}
	}

	private static void appendTypeIdentifier(IPrettyPrintStream ps,
			String identifier) {
		ps.appendString(identifier, //
				getHTMLBeginForCSSClass(TYPE_IDENTIFIER, //
						HorizontalAlignment.LEFT, //
						VerticalAlignement.MIDDLE), //
				getHTMLEndForCSSClass(TYPE_IDENTIFIER, //
						HorizontalAlignment.LEFT, //
						VerticalAlignement.MIDDLE), //
				TYPE_IDENTIFIER_SEPARATOR_BEGIN, //
				TYPE_IDENTIFIER_SEPARATOR_END);
	}

}

