package org.eventb.theory.rbp.rulebase.basis;


/**
 * Common protocol for a filter of proof rules.
 * <p>A filter provides a mechansim to verify whether a given proof rule satisfies a criterion.</p>
 * @author maamria
 *
 */
public interface IProofRuleFilter {
	
	/**
	 * Returns whether the proof rule <code>rule</code> satisfies a specifies criterion.
	 * @param rule to check
	 * @return whether <code>rule</code> satisfies the criterion
	 */
	public boolean filter(IDeployedRule rule);
	
}
