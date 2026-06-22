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

import org.eventb.ui.prettyprint.DefaultPrettyPrinter;
import org.eventb.ui.prettyprint.IElementPrettyPrinter;
import org.eventb.ui.prettyprint.IPrettyPrintStream;
import org.eventb.ui.prettyprint.PrettyPrintAlignments.HorizontalAlignment;
import org.eventb.ui.prettyprint.PrettyPrintAlignments.VerticalAlignement;
import org.rodinp.core.IInternalElement;

public class ComponentPrettyPrinter extends DefaultPrettyPrinter implements
		IElementPrettyPrinter {

	private static final String STYLE = "componentName";
	private static final String COMPONENT_NAME_SEPARATOR_BEGIN = null;
	private static final String COMPONENT_NAME_SEPARATOR_END = null;

	@Override
	public void prettyPrint(IInternalElement elt, IInternalElement parent,
			IPrettyPrintStream ps) {
		final String bareName = elt.getRodinFile().getBareName();
		appendComponentName(ps, wrapString(bareName));
	}

	protected static void appendComponentName(IPrettyPrintStream ps, String label) {
		ps.appendString(label, 
				getHTMLBeginForCSSClass(STYLE, HorizontalAlignment.LEFT, VerticalAlignement.MIDDLE), 
				getHTMLEndForCSSClass(STYLE, HorizontalAlignment.LEFT, VerticalAlignement.MIDDLE), 
				COMPONENT_NAME_SEPARATOR_BEGIN, 
				COMPONENT_NAME_SEPARATOR_END);
	}

}

