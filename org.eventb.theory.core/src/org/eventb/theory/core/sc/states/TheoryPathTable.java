/**
 * 
 */
package org.eventb.theory.core.sc.states;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.tool.IStateType;
import org.eventb.internal.core.tool.state.State;
import org.eventb.theory.core.DatabaseUtilitiesTheoryPath;
import org.eventb.theory.core.IAvailableTheory;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.basis.DeployedTheoryDecorator;
import org.rodinp.core.RodinDBException;

/**
 * @author renatosilva
 *
 */
@SuppressWarnings("restriction")
public class TheoryPathTable extends State implements ITheoryPathTable {
	
	private Map<String, IDeployedTheoryRoot> theories;

	/**
	 * 
	 */
	public TheoryPathTable(int size) {
		theories =  new HashMap<String, IDeployedTheoryRoot>(size);
	}

	/* (non-Javadoc)
	 * @see org.eventb.internal.core.tool.types.IState#getStateType()
	 */
	@Override
	public IStateType<?> getStateType() {
		return STATE_TYPE;
	}

	/* (non-Javadoc)
	 * @see org.eventb.theory.core.sc.states.ITheoryPathTable#addTheory(org.eventb.theory.core.ISCAvailableTheory)
	 */
	@Override
	public IDeployedTheoryRoot addTheory(IAvailableTheory theory) throws CoreException {
		assertMutable();
		IDeployedTheoryRoot deployedTheoryRoot = theory.getDeployedTheory();
		IDeployedTheoryRoot conflictingTheory = checkConflict(theory);
		if(conflictingTheory==null)
			theories.put(DatabaseUtilitiesTheoryPath.getFullDescriptionAvailableTheory(theory.getAvailableTheoryProject(), deployedTheoryRoot) ,deployedTheoryRoot);
		
		return conflictingTheory;
	}

	@Override
	public boolean containsTheory(IAvailableTheory theory) throws RodinDBException {
		return theories.containsKey(DatabaseUtilitiesTheoryPath.getFullDescriptionAvailableTheory(theory.getAvailableTheoryProject(), theory.getDeployedTheory()));
	}

	@Override
	public IDeployedTheoryRoot getTheory(String name) {
		return theories.get(name);
	}

	@Override
	public int size() {
		return theories.size();
	}
	
	public Collection<IDeployedTheoryRoot> getAllTheories(){
		return theories.values();
	}

	/**
	 * Checks if a conflicting is found. If yes, return the conflicting theory
	 * 
	 * @param av
	 * @return
	 * @throws CoreException
	 */
	public IDeployedTheoryRoot checkConflict(IAvailableTheory av) throws CoreException{
		IDeployedTheoryRoot deployedTheory = av.getDeployedTheory();
		for (IDeployedTheoryRoot dep : theories.values()){
			DeployedTheoryDecorator dec1 = new DeployedTheoryDecorator(deployedTheory);
			if (dec1.isConflicting(dep)){
				return dep;
			}
		}
		return null;
		
	}
}
