/**
 * 
 */
package org.eventb.theory.core.sc.states;

import java.util.Collection;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.state.ISCState;
import org.eventb.core.tool.IStateType;
import org.eventb.theory.core.IAvailableTheory;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.rodinp.core.RodinDBException;

/**
 * @author renatosilva
 *
 */
public interface ITheoryPathTable extends ISCState {
	
	public final IStateType<ITheoryPathTable> STATE_TYPE = SCCore.getToolStateType(TheoryPlugin.PLUGIN_ID + ".theoryPathTable");
	
	/**
	 * Adds a theory to the table and checks for conflicts. If conflicts are found, return the conflicting theory
	 * 
	 * @param theory
	 * @throws CoreException 
	 */
	public IDeployedTheoryRoot addTheory(IAvailableTheory theory) throws CoreException;
	
	/**
	 * Returns whether this closure contains the theory with the specified element name.
	 * 
	 * @param name the element name of the theory
	 * @return whether this closure contains the theory with the specified element name
	 * @throws RodinDBException 
	 */
	boolean containsTheory(IAvailableTheory deployedTheory) throws RodinDBException;
	
	/**
	 * Returns the theory with the specified element name, or <code>null</code> if it is
	 * not stored in this closure.
	 * 
	 * @param name the element name of the theory
	 * @return the theory with the specified element name, or <code>null</code> if it is
	 * not stored in this closure
	 */
	IDeployedTheoryRoot getTheory(String name);
	
	/**
	 * Returns the size of this theory table.
	 * 
	 * @return the size of this theory table
	 */
	int size();
	
	/**
	 * Returns all the deployed theories stored in the table
	 * @return all deployed theories in the table
	 */
	public Collection<IDeployedTheoryRoot> getAllTheories();
	
	
}
