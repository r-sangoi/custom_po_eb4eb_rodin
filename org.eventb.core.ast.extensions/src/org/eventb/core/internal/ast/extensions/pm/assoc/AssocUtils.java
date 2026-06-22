package org.eventb.core.internal.ast.extensions.pm.assoc;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.PredicateVariable;

/**
 * Utilities for associative matching.
 * @author maamria
 *
 */
public class AssocUtils {

	/**
	 * Returns an indexed formula list based on the formulae supplied.
	 * @param formulae the formulae array
	 * @param varListToAugment the list to augment with variables, <code>null</code> can be passed
	 * if variable collection is not required
	 * @return list of indexed formulae
	 */
	public static <F extends Formula<F>>  List<IndexedFormula<F>>  getIndexedFormulae(
			F[] formulae, List<IndexedFormula<F>> varListToAugment) {
		if (formulae == null) {
			return null;
		}
		boolean collectVariables = (varListToAugment != null);
		List<IndexedFormula<F>> indexedFormulae = new ArrayList<IndexedFormula<F>>();
		int i = 0;
		for (F formula : formulae) {
			// make sure index as is in the array of formulae
			if(collectVariables && isVariable(formula)){
				varListToAugment.add(new IndexedFormula<F>(i++, formula));
			}
			else {
				indexedFormulae.add(new IndexedFormula<F>(i++, formula));
			}
		}
		return indexedFormulae;
	}
	
	/**
	 * Returns whether the given formula is a {@link FreeIdentifier} or a {@link PredicateVariable}.
	 * @param formula the formula
	 * @return whether <code>formula</code> is a free identifier or a predicate variable
	 */
	public static <F extends Formula<F>>boolean isVariable(F formula){
		return formula instanceof FreeIdentifier || formula instanceof PredicateVariable;
	}
	
	public static <F extends Formula<F>> IndexedFormula<F> containsFormula(
			List<IndexedFormula<F>> indexedFormulae, F formula){
		for (IndexedFormula<F> indexedFormula : indexedFormulae){
			if (indexedFormula.getFormula().equals(formula)){
				return indexedFormula;
			}
		}
		return null;
	}
	
}
