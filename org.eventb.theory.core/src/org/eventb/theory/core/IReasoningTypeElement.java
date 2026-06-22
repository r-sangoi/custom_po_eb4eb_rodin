/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for a reasoning type element.
 * 
 * <p> Inference rules can be used for forward reasoning (i.e., new hypothesis generation) and/or backward reasoning
 * (i.e., goal-directed inference). This interface provides an abstract layer for attributing inference rules according
 * to their applicability.
 * 
 * <p> This interface is not intended to be implemented by clients.
 * 
 * @see IReasoningTypeElement.ReasoningType
 * 
 * @author maamria
 *
 */
public interface IReasoningTypeElement extends IInternalElement{

	/**
	 * Reasoning type enumeration.
	 * @author maamria
	 *
	 */
	public static enum ReasoningType {BACKWARD, FORWARD, BACKWARD_AND_FORWARD;
	
		public String toString() {
			switch (this) {
			case BACKWARD:
				return "backward";
			case FORWARD:
				return "forward";
			default:
				return "both";
			}
		};
	
		public static ReasoningType getReasoningType(String typeString){
			if ("backward".equals(typeString)){
				return BACKWARD;
			}
			if ("forward".equals(typeString)){
				return FORWARD;
			}
			if ("both".equals(typeString)){
				return BACKWARD_AND_FORWARD;
			}
			throw new IllegalArgumentException("unknown reasoning type " + typeString);
		}
		
	}
	
	/**
	 * Returns whether the attribute is present.
	 * @return whether the attribute is present
	 * @throws RodinDBException
	 */
	boolean hasReasoningAttribute() throws RodinDBException;
	
	/**
	 * Return whether this inference rule is suitable for backward reasoning.
	 * @return whether this inference rule is suitable for backward reasoning
	 * @throws RodinDBException
	 */
	boolean isSuitableForBackwardReasoning() throws RodinDBException;
	
	/**
	 * Return whether this inference rule is suitable for forward reasoning.
	 * @return whether this inference rule is suitable for forward reasoning
	 * @throws RodinDBException
	 */
	boolean isSuitableForForwardReasoning() throws RodinDBException;
	
	/**
	 * Return whether this inference rule is suitable for backward and forward reasoning.
	 * @return whether this inference rule is suitable for backward and forward reasoning
	 * @throws RodinDBException
	 */
	boolean isSuitableForAllReasoning() throws RodinDBException;
	
	/**
	 * Sets the reasoning type of this inference rule to the given value.
	 * @param type the reasoning type
	 * @param monitor the progress monitor
	 * @throws RodinDBException
	 */
	void setReasoningType(ReasoningType type, IProgressMonitor monitor) throws RodinDBException;
	
}
