package org.eventb.core.ast.extensions.pm.engine;

import java.util.Map;

import org.eventb.core.ast.DefaultRewriter;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.PredicateVariable;

/**
 * A simple rewriter for predicate variables.
 * @author maamria
 *
 */
public class PredicateVariableSubstituter extends DefaultRewriter {

	private Map<PredicateVariable, Predicate> map;
	
	/**
	 * Creates a predicate variables rewriter.
	 * @param map the map of predicate variables mapped to their substitutes
	 */
	public PredicateVariableSubstituter(
			Map<PredicateVariable, Predicate> map){
		super(true);
		this.map = map;
	}
	
	public Predicate rewrite(PredicateVariable var){
		Predicate pred = map.get(var);
		if (pred != null)
			return pred;
		return var;
	}

}
