package org.eventb.core.ast.extensions.maths;

import java.util.Collections;
import java.util.List;

import org.eventb.core.ast.Predicate;

/**
 * An axiomatic definition
 * 
 * @author maamria
 */
public final class AxiomaticDefinition extends Definition {

	private final List<Predicate> axioms;

	public AxiomaticDefinition(List<Predicate> axioms) {
		this.axioms = axioms;
	}

	public List<Predicate> getAxioms() {
		return Collections.unmodifiableList(axioms);
	}

}