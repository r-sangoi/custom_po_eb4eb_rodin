/*******************************************************************************
 * Copyright (c) 2010, 2020 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *     University of Southampton - Adaptation for the D-library
 *******************************************************************************/
package org.eventb.core.internal.ast.extensions.wd;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.extension.IWDMediator;

/**
 * An implementation of a well-definedness mediator for Y.
 * 
 * @author Nicolas Beauger, maamria
 * 
 */
public class YMediator implements IWDMediator {

	private final FormulaBuilder fb;

	public YMediator(FormulaBuilder formulaBuilder) {
		this.fb = formulaBuilder;
	}

	@Override
	public Predicate makeTrueWD() {
		return fb.btrue;
	}

	@Override
	public FormulaFactory getFormulaFactory() {
		return fb.ff;
	}
}
