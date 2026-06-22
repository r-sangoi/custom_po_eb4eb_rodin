/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
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
 * 
 * Common protocol for rule applicability elements.
 * 
 * <p> A rule applicability refers to how it should be used by the provers (automatically/interactively).
 * 
 * @author maamria
 *
 */
public interface IApplicabilityElement extends IInternalElement{
	
	/**
	 * Enumeration for applicability types.
	 * @author maamria
	 *
	 */
	public static enum RuleApplicability{
		AUTOMATIC, INTERACTIVE, AUTOMATIC_AND_INTERACTIVE;
		
		public String toString() {
			switch (this) {
			case AUTOMATIC:
				return "automatic";
			case INTERACTIVE:
				return "interactive";
			case AUTOMATIC_AND_INTERACTIVE:
				return "both";
			}
			return null;
		}
		
		public static String[] getPossibleApplicabilitiesAsStrings(){
			return new String[]{"automatic", "interactive", "both"};
		}
		
		public static RuleApplicability getRuleApplicability(String str) {
			if (str.equalsIgnoreCase("automatic")) {
				return AUTOMATIC;
			} else if (str.equalsIgnoreCase("interactive")) {
				return INTERACTIVE;
			} else if (str.equalsIgnoreCase("both")) {
				return AUTOMATIC_AND_INTERACTIVE;
			}
			return INTERACTIVE;
		}
		
		public static RuleApplicability getRuleApplicability(boolean isAutomatic, boolean isInteractive) {
			if (isAutomatic && isInteractive)
				return AUTOMATIC_AND_INTERACTIVE;
			if (isAutomatic)
				return AUTOMATIC;
			else
				return INTERACTIVE;
		}

		/**
		 * Returns whether this applicability enables automatic application.
		 * @return whether this applicability enables automatic application
		 */
		public boolean isAutomatic() {
			return equals(AUTOMATIC) || equals(AUTOMATIC_AND_INTERACTIVE);
		}

		/**
		 * Returns whether this applicability enables interactive application.
		 * @return whether this applicability enables interactive application
		 */
		public boolean isInteractive() {
			return equals(INTERACTIVE) || equals(AUTOMATIC_AND_INTERACTIVE);
		}
		
	}
	
	/**
	 * Returns whether the applicability attribute is set.
	 * @return whether the applicability attribute is set
	 * @throws RodinDBException
	 */
	public boolean hasApplicabilityAttribute() throws RodinDBException;
	
	/**
	 * Returns the applicability of this element
	 * @return the applicability
	 * @throws RodinDBException
	 */
	public RuleApplicability getApplicability() throws RodinDBException;
	
	/**
	 * Returns whether this element can be used automatically.
	 * @return whether this element can be used automatically
	 * @throws RodinDBException
	 */
	public boolean isAutomatic() throws RodinDBException;
	
	/**
	 * Returns whether this element can be used interactively.
	 * @return whether this element can be used interactively
	 * @throws RodinDBException
	 */
	public boolean isInteractive() throws RodinDBException;
	 
	/**
	 * Sets the applicability of this element to the given value.
	 * @param applicability the applicability
	 * @param monitor the progress monitor
	 * @throws RodinDBException
	 */
	public void setApplicability(RuleApplicability applicability, IProgressMonitor monitor) throws RodinDBException;
	
}
