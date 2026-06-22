/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.ast.extensions.maths;

import org.eventb.core.ast.extension.IOperatorProperties.FormulaType;
import org.eventb.core.ast.extension.IOperatorProperties.Notation;

/**
 * Convenient class for grouping operator properties.
 * @author maamria
 *
 */
public class OperatorExtensionProperties {

	private String operatorID;
	private String syntax;
	private FormulaType formulaType; 
	private Notation notation;
	private String groupID;
	
	public OperatorExtensionProperties(String operatorID, String syntax, 
			FormulaType formulaType, Notation notation, String groupID){
		this.operatorID = operatorID;
		this.syntax = syntax;
		this.formulaType = formulaType;
		this.notation = notation;
		this.groupID = groupID;
	}

	public String getOperatorID() {
		return operatorID;
	}

	public String getSyntax() {
		return syntax;
	}

	public FormulaType getFormulaType() {
		return formulaType;
	}

	public Notation getNotation() {
		return notation;
	}

	public String getGroupID() {
		return groupID;
	}
	
	public boolean equals(Object o){
		if (o == null){
			return false;
		}
		if (o == this){
			return true;
		}
		if (!(o instanceof OperatorExtensionProperties)){
			return false;
		}
		OperatorExtensionProperties other = (OperatorExtensionProperties) o;
		return 
//				operatorID.equals(other.getOperatorID()) &&
			syntax.equals(other.getSyntax()) &&
			formulaType.equals(other.getFormulaType()) &&
			notation.equals(other.getNotation()) &&
			(groupID == null ? other.getGroupID() == null : groupID.equals(other.getGroupID()));
	}
	
	public int hashCode(){
		final int prime = 17;
		return 
//				prime * operatorID.hashCode() + 
				prime * syntax.hashCode() + 
				prime * formulaType.hashCode() + prime * notation.hashCode()+ (groupID == null ? 0 : groupID.hashCode());
	}
	
}
