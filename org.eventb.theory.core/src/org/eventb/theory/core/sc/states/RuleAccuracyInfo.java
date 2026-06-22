package org.eventb.theory.core.sc.states;

import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.state.ISCState;
import org.eventb.core.tool.IStateType;
import org.eventb.internal.core.sc.AccuracyInfo;
import org.eventb.theory.core.plugin.TheoryPlugin;

/**
 * An implementation of a repository state that can hold accuracy information of 
 * the static checking process of inference and rewrite rules.
 * @author maamria
 *
 */
@SuppressWarnings("restriction")
public class RuleAccuracyInfo extends AccuracyInfo implements ISCState{

	public final static IStateType<RuleAccuracyInfo> STATE_TYPE = SCCore
		.getToolStateType(TheoryPlugin.PLUGIN_ID + ".ruleAccuracyInfo");
	
	public IStateType<?> getStateType() {
		return STATE_TYPE;
	}
}
