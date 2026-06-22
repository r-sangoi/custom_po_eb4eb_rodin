package org.eventb.theory.rbp.tactics;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.IEventBRoot;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pm.IProofAttempt;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.theory.rbp.reasoning.RewritesSelector;
import org.eventb.theory.rbp.rulebase.IPOContext;
import org.eventb.theory.rbp.rulebase.basis.POContext;
import org.eventb.ui.prover.DefaultTacticProvider;
import org.eventb.ui.prover.ITacticApplication;
import org.eventb.ui.prover.ITacticProvider;

/**
 * The manual tactic for applying interactive rewrite rules.
 * 
 * <p> Conditional, definitional and unconditional rules can be applied interactively.
 * 
 * @since 1.0
 * @author maamria
 *
 */
public class RewritesManualTactic extends DefaultTacticProvider implements ITacticProvider {

	public List<ITacticApplication> getPossibleApplications(
			IProofTreeNode node, Predicate hyp, String globalInput) {
		if (node.getProofTree().getOrigin() instanceof IProofAttempt){
			IProofAttempt attempt = (IProofAttempt) node.getProofTree().getOrigin();
			IPOContext poContext = new POContext(
					(IEventBRoot) attempt.getComponent().getPORoot());
			boolean isGoal = hyp == null;
			Predicate pred = ( isGoal ? node.getSequent().goal() : hyp);
			return pred.inspect(new RewritesSelector(pred, isGoal, poContext));
		}
		// Contextual information needed
		return new ArrayList<ITacticApplication>();
		
	}
}
