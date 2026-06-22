/*******************************************************************************
 * Copyright (c) 2020 CentraleSupélec.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.ast.extensions.wd;

import org.eventb.core.ast.Formula;
import org.eventb.core.ast.Predicate;
import org.eventb.core.internal.ast.extensions.wd.DComputer;
import org.eventb.core.internal.ast.extensions.wd.YComputer;

/**
 * Compute well-definedness conditions.
 *
 * This exposes a public API to compute well-definedness conditions using
 * various operators, such as the Y operator and the D operator.
 *
 * @author Guillaume Verdier
 *
 * @noextend This class is not intended to be subclassed by clients.
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public class WDComputer {

	/**
	 * Compute the well-definedness condition of a formula with the Y operator.
	 *
	 * @param formula the formula on which the well-definedness condition is computed
	 * @return the well-definedness condition
	 * @see YComputer
	 */
	public static Predicate getYLemma(Formula<?> formula) {
		YComputer computer = new YComputer(formula.getFactory());
		return computer.getWDLemma(formula);
	}

	/**
	 * Compute the well-definedness condition of a formula with the D operator.
	 *
	 * @param formula the formula on which the well-definedness condition is computed
	 * @return the well-definedness condition
	 * @see DComputer
	 */
	public static Predicate getDLemma(Formula<?> formula) {
		DComputer computer = new DComputer(formula.getFactory());
		return computer.getWDLemma(formula);
	}

}
