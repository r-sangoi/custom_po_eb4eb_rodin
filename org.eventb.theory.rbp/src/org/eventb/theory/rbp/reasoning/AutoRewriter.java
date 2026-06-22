package org.eventb.theory.rbp.reasoning;

import org.eventb.core.ast.Formula;
import org.eventb.core.ast.IFormulaRewriter;
import org.eventb.theory.core.IGeneralRule;
import org.eventb.theory.internal.rbp.reasoners.input.IPRMetadata;
import org.eventb.theory.rbp.rulebase.BaseManager;
import org.eventb.theory.rbp.rulebase.IPOContext;

/**
 * <p>An implementation of a rewrite rule automatic rewriter.</p>
 * @author maamria
 * @author htson
 */
public class AutoRewriter extends AbstractRulesApplyer implements IFormulaRewriter{
	
	public AutoRewriter(IPOContext context, IPRMetadata prMetadata){
		super(context, prMetadata);
	}
		
	/**
	 * Returns the list of rewrite rules to apply.
	 * 
	 * <p> Override this method to specify different list of rules.
	 * @param original
	 * @return
	 */
	public IGeneralRule getRule(Formula<?> original){
		BaseManager manager = BaseManager.getDefault();
		String projectName = prMetadata.getProjectName();
		String theoryName = prMetadata.getTheoryName();
		String ruleName = prMetadata.getRuleName();
		return manager.getRewriteRule(true, projectName, ruleName,
				theoryName, context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return "Auto Rewrite " + super.toString();
	}
	
	
}
