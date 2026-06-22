/**
 * 
 */
package org.eventb.theory.core.sc.states;

import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.state.IAccuracyInfo;
import org.eventb.core.tool.IStateType;
import org.eventb.internal.core.tool.state.State;
import org.eventb.theory.core.plugin.TheoryPlugin;

/**
 * @author renatosilva
 *
 */
@SuppressWarnings("restriction")
public class TheoryPathAccuracyInfo extends State implements IAccuracyInfo {
	
	public final static IStateType<TheoryPathAccuracyInfo> STATE_TYPE = SCCore
			.getToolStateType(TheoryPlugin.PLUGIN_ID + ".theoryPathAccuracyInfo");
	
	private boolean accurate;

	/**
	 * 
	 */
	public TheoryPathAccuracyInfo() {
		accurate = true;
	}

	/* (non-Javadoc)
	 * @see org.eventb.internal.core.tool.types.IState#getStateType()
	 */
	@Override
	public IStateType<?> getStateType() {
		return STATE_TYPE;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.state.IAccuracyInfo#isAccurate()
	 */
	@Override
	public boolean isAccurate() {
		return accurate;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.state.IAccuracyInfo#setNotAccurate()
	 */
	@Override
	public void setNotAccurate() {
		accurate = false;
	}

}
