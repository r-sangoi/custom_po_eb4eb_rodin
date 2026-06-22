/*******************************************************************************
 * Copyright (c) 2010, 2014 University of Southampton and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University of Southampton - initial API and implementation
 *     Systerel - convert to free identifier
 *******************************************************************************/
package org.eventb.core.internal.ast.extensions.maths;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Type;

/**
 * Implementation of operator arguments.
 * <p> An operator argument has an index, a name and a type.
 * 
 * <p> A type of an operator argument may contain type parameters. These will be the types on which 
 * the concerned operator definition is polymorphic.
 * 
 * @since 1.0
 * @author maamria
 *
 */
public class OperatorArgument{

	private String argumentName;
	private Type argumentType;
	private int index;
	
	public OperatorArgument(int index, String argumentName, Type argumentType){
		this.index = index;
		this.argumentName = argumentName;
		this.argumentType = argumentType;
	}
	
	public boolean equals(Object o){
		if(o == this)
			return true;
		if(o == null || !(o instanceof OperatorArgument)){
			return false;
		}
		OperatorArgument other = (OperatorArgument) o;
		return index == other.index && argumentName.equals(other.argumentName) &&
			argumentType.equals(other.argumentType);
	}
	
	public int hashCode(){
		return 13*index*argumentType.hashCode() + 23 * argumentName.hashCode();
	}
	
	public String toString(){
		return "("+index +") :"+argumentName +":"+argumentType.toString();
	}
	
	public int getIndex() {
		return index;
	}

	public String getArgumentName() {
		return argumentName;
	}

	public Type getArgumentType() {
		return argumentType;
	}

	public FreeIdentifier asFreeIdentifier() {
		final FormulaFactory ff = argumentType.getFactory();
		return ff.makeFreeIdentifier(argumentName, null, argumentType);
	}

}
