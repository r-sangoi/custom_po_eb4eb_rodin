/**
 * 
 */
package org.eventb.theory.core.sc.states;

import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.state.ISCState;
import org.eventb.core.tool.IStateType;
import org.eventb.theory.core.IImportTheoryProject;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

/**
 * @author asiehsalehi
 *
 */
public interface IImportProjectTable extends ISCState {
	
	public final IStateType<IImportProjectTable> STATE_TYPE = SCCore.getToolStateType(TheoryPlugin.PLUGIN_ID + ".importProjectTable");
	
	/**
	 * Adds a theory project to the table
	 * 
	 * @param theoryProject
	 * @throws RodinDBException
	 */
	public void addTheoryProject(IImportTheoryProject theoryProject) throws RodinDBException;
	
	/**
	 * Returns whether this closure contains the theory with the specified element name.
	 * 
	 * @param name the element name of the theory
	 * @return whether this closure contains the theory with the specified element name
	 */
	boolean containsTheoryProject(String name);
	
	/**
	 * Returns the theory with the specified element name, or <code>null</code> if it is
	 * not stored in this closure.
	 * 
	 * @param name the element name of the theory
	 * @return the theory with the specified element name, or <code>null</code> if it is
	 * not stored in this closure
	 */
	IRodinProject getTheoryProject(String name);
	
	/**
	 * Returns the size of this theory table.
	 * 
	 * @return the size of this theory table
	 */
	int size();

}
