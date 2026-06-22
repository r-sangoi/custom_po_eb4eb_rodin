package org.eventb.core.ast.extensions.maths;

import org.eventb.core.ast.Formula;

/**
 * 
 * A direct definition
 * 
 * @author maamria
 *
 */
public final class DirectDefinition extends Definition {

	private Formula<?> directDefinition;

	public DirectDefinition(Formula<?> directDefinition) {
		this.directDefinition = directDefinition;
	}

	public Formula<?> getDefinition() {
		return directDefinition;
	}

}