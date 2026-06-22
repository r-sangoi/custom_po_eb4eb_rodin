/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core;

import org.eventb.core.ICommentedElement;
import org.eventb.core.IPredicateElement;

/**
 * Common protocol for an inference rule clause (either infer or given). 
 * 
 * <p>An inference clause must have a predicate.
 * 
 * <p> This interface is not intended to be implemented by clients.
 * 
 * @author maamria
 *
 */
public interface IInferenceClause extends IPredicateElement, ICommentedElement{
	
}
