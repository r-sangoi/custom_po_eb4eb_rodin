package org.eventb.core.ast.extensions.maths;

import java.util.Collections;
import java.util.Map;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FreeIdentifier;

/**
 * A recursive definition
 * 
 * @author maamria
 */
public final class RecursiveDefinition extends Definition {

	private FreeIdentifier operatorArgument;

	private Map<Expression, Formula<?>> recursiveCases;

	public RecursiveDefinition(FreeIdentifier operatorArgument,
			Map<Expression, Formula<?>> recursiveCases) {
		this.operatorArgument = operatorArgument;
		this.recursiveCases = recursiveCases;
	}

	public FreeIdentifier getOperatorArgument() {
		return operatorArgument;
	}

	public Map<Expression, Formula<?>> getRecursiveCases() {
		return Collections.unmodifiableMap(recursiveCases);
	}

}