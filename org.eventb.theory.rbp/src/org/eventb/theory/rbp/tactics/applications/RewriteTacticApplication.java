/*******************************************************************************
 * Copyright (c) 2011,2016 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.tactics.applications;

import org.eclipse.swt.graphics.Point;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.ExtendedPredicate;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.extensions.maths.AstUtilities;
import org.eventb.core.ast.extensions.maths.AstUtilities.PositionPoint;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.tactics.BasicTactics;
import org.eventb.theory.core.IGeneralRule;
import org.eventb.theory.core.ISCRewriteRule;
import org.eventb.theory.internal.rbp.reasoners.input.IPRMetadata;
import org.eventb.theory.internal.rbp.reasoners.input.RewriteInput;
import org.eventb.theory.rbp.plugin.RbPPlugin;
import org.eventb.theory.rbp.reasoners.ManualRewriteReasoner;
import org.eventb.theory.rbp.rulebase.BaseManager;
import org.eventb.theory.rbp.rulebase.IPOContext;
import org.eventb.theory.rbp.rulebase.basis.IDeployedRule;
import org.eventb.ui.prover.DefaultTacticProvider.DefaultPositionApplication;
import org.eventb.ui.prover.IPositionApplication;
import org.rodinp.core.RodinDBException;

/**
 * 
 * @author maamria
 * @author htson - Minor fixes
 * @version 1.1
 * @see RewriteInput
 * @since 1.0
 */
public class RewriteTacticApplication extends DefaultPositionApplication implements IPositionApplication {

	private static final String TACTIC_ID = RbPPlugin.PLUGIN_ID + ".RbP0";
	
	private RewriteInput input;
	
	private IPOContext context;
	
	public RewriteTacticApplication(RewriteInput input, IPOContext context) {
		super(input.getPredicate(), input.getPosition());
		this.input = input;
		this.context = context;
	}

	/* (non-Javadoc)
	 * @see DefaultPositionApplication#getHyperlinkBounds(String, Predicate)
	 */
	public Point getHyperlinkBounds(String parsedString,
			Predicate parsedPredicate) {
		return getOperatorPosition(parsedPredicate,
				parsedString);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see DefaultPositionApplication#getHyperlinkLabel()
	 */
	public String getHyperlinkLabel() {
		IPRMetadata prMetadata = input.getPRMetadata();
		String projectName = prMetadata.getProjectName();
		String theoryName = prMetadata.getTheoryName();
		String ruleName = prMetadata.getRuleName();
		// Get the inference rule (given the meta-data) from the current context
		BaseManager manager = BaseManager.getDefault();
		IGeneralRule rule = manager.getRewriteRule(false, projectName, ruleName,
				theoryName, context);
		if (rule == null) { // Definitional rule
			return "Expand definition";
		}
		if (rule instanceof IDeployedRule) {
			String description = ((IDeployedRule) rule).getDescription();
			return description + " (rewrite)";
		} else { // ISCRewriteRule
			try {
				return ((ISCRewriteRule) rule).getDescription() + " (rewrite)";
			} catch (RodinDBException e) {
				return "Rewrite (failed to get description)";
			}
		}
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see DefaultPositionApplication#getTactic(String[], String)
	 */
	public ITactic getTactic(String[] inputs, String globalInput) {
		ManualRewriteReasoner reasoner = new ManualRewriteReasoner();
		return BasicTactics.reasonerTac(reasoner, input);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see DefaultPositionApplication#getTacticID()
	 */
	public String getTacticID() {
		return TACTIC_ID;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see DefaultPositionApplication#getOperatorPosition(Predicate, String)
	 */
	public Point getOperatorPosition(Predicate predicate, String predStr) {
		Formula<?> subFormula = predicate.getSubFormula(position);
		if (subFormula instanceof ExtendedExpression) {
			ExtendedExpression exp = (ExtendedExpression) subFormula;
			PositionPoint point = AstUtilities.getPositionOfOperator(exp,
					predStr);
			return new Point(point.getX(), point.getY());
		}
		if (subFormula instanceof ExtendedPredicate) {
			ExtendedPredicate pred = (ExtendedPredicate) subFormula;
			PositionPoint point = AstUtilities.getPositionOfOperator(pred,
					predStr);
			return new Point(point.getX(), point.getY());
		}
		return super.getOperatorPosition(predicate, predStr);
	}
}
