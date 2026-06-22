/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core;

import org.eventb.core.IAccuracyElement;
import org.eventb.core.ILabeledElement;
import org.eventb.core.ITraceableElement;

/**
 * Common protocol for statically checked proof rules.
 * 
 * <p> This interface is not intended to be implemented by clients.
 * 
 * @see IRule
 * 
 * @author maamria
 *
 */
public interface ISCRule extends ILabeledElement, IApplicabilityElement,
IDescriptionElement, ITraceableElement, IAccuracyElement, IDefinitionalElement, IGeneralRule
{

}
