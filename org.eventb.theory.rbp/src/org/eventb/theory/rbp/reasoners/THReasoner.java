/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.reasoners;

import static org.eventb.core.seqprover.ProverFactory.makeAntecedent;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerInputReader;
import org.eventb.core.seqprover.IReasonerInputWriter;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.SerializeException;
import org.eventb.theory.internal.rbp.reasoners.input.MultipleStringInput;
import org.eventb.theory.rbp.plugin.RbPPlugin;

/**
 * 
 * @author maamria
 * 
 */
public class THReasoner implements IReasoner {

	public static final String REASONER_ID = RbPPlugin.PLUGIN_ID + ".instantiateTheoremReasoner";

	private static final String DISPLAY_NAME = "Instantiate Theorem(s)";

	private static final String STR_KEY = "string";

	@Override
	public IReasonerOutput apply(IProverSequent seq, IReasonerInput input, IProofMonitor pm) {
		MultipleStringInput stringInput = (MultipleStringInput) input;
		List<String> theoremsStrs = stringInput.strings;
		if (theoremsStrs.isEmpty()) {
			return ProverFactory.reasonerFailure(this, stringInput, "No theorem to add");
		}
		FormulaFactory factory = seq.getFormulaFactory();
		ITypeEnvironment typeEnvironment = seq.typeEnvironment();
		Set<Predicate> addedHyps = new LinkedHashSet<Predicate>();
		Set<Predicate> addedWDHyps = new LinkedHashSet<Predicate>();
		for (String theoremStr : theoremsStrs) {
			// parse the string
			IParseResult parseResult = factory.parsePredicate(theoremStr, null);
			if (parseResult.hasProblem()) {
				return ProverFactory.reasonerFailure(this, stringInput, "Cannot parse '" + theoremStr + "'");
			}
			// check the free identifiers
			Predicate theoremPredicate = parseResult.getParsedPredicate();
			for (FreeIdentifier identifier : theoremPredicate.getFreeIdentifiers()) {
				if (!typeEnvironment.contains(identifier.getName())) {
					return ProverFactory.reasonerFailure(this, stringInput, "Cannot type check '" + theoremStr + "'");
				}
			}
			// type check the predicate
			ITypeCheckResult tcResult = theoremPredicate.typeCheck(typeEnvironment);
			if (tcResult.hasProblem()) {
				return ProverFactory.reasonerFailure(this, stringInput, "Cannot type check '" + theoremStr + "'");
			}
			// check given types
			for (GivenType givenType : theoremPredicate.getGivenTypes()) {
				if (!typeEnvironment.contains(givenType.getName())) {
					return ProverFactory.reasonerFailure(this, stringInput, "Cannot type check '" + theoremStr + "'");
				}
			}
			// make the forward inference step

			addedHyps.add(theoremPredicate);
			Predicate wdPredicate = theoremPredicate.getWDPredicate();
			if (!wdPredicate.equals(factory.makeLiteralPredicate(
					Predicate.BTRUE, null))) {
				addedHyps.add(wdPredicate);
				addedWDHyps.add(wdPredicate);
			}
		}
		// make the antecedent : no free idents / no hyp actions
		IAntecedent antecedent = makeAntecedent(null, addedHyps, addedWDHyps, null, null);
		return ProverFactory.makeProofRule(this, stringInput, null, DISPLAY_NAME, antecedent);
	}

	@Override
	public String getReasonerID() {
		return REASONER_ID;
	}

	@Override
	public void serializeInput(IReasonerInput input, IReasonerInputWriter writer) throws SerializeException {
		MultipleStringInput multipleStringInput = (MultipleStringInput) input;
		int k = 0;
		for (String str : multipleStringInput.strings) {
			writer.putString(STR_KEY + k++, str);
		}
	}

	@Override
	public IReasonerInput deserializeInput(IReasonerInputReader reader) throws SerializeException {
		List<String> strings = new ArrayList<String>();
		int k = 0;
		String firstStr = reader.getString(STR_KEY + k++);
		if (firstStr == null) {
			throw new SerializeException(new IllegalStateException("Multiple strings were not serialised properly!"));
		}
		strings.add(firstStr);
		try {
			while ((firstStr = reader.getString(STR_KEY + k++)) != null) {
				strings.add(firstStr);
			}
		} catch (Exception e) {
			// do nothing
		}
		return new MultipleStringInput(strings);
	}

}
