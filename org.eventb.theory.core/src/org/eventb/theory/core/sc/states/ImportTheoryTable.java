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
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.IImportTheory;
import org.eventb.theory.core.basis.DeployedTheoryDecorator;
import org.rodinp.core.RodinDBException;

/**
 * @author asiehsalehi
 *
 */
@SuppressWarnings("restriction")
public class ImportTheoryTable extends State implements IImportTheoryTable {
	
	private Map<String, IDeployedTheoryRoot> theories;

	/**
	 * 
	 */
	public ImportTheoryTable(int size) {
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
	 * @see org.eventb.theory.core.sc.states.ITheoryPathTable#addTheory(org.eventb.theory.core.ISCImportTheory)
	 */
	@Override
	public IDeployedTheoryRoot addTheory(IImportTheory theory) throws CoreException {
		assertMutable();
		IDeployedTheoryRoot deployedTheoryRoot = theory.getImportTheory();
		IDeployedTheoryRoot conflictingTheory = checkConflict(theory);
		if(conflictingTheory==null)
			theories.put(DatabaseUtilitiesTheoryPath.getFullDescriptionAvailableTheory(theory.getImportTheoryProject(), deployedTheoryRoot) ,deployedTheoryRoot);
		
		return conflictingTheory;
	}

	@Override
	public boolean containsTheory(IImportTheory theory) throws RodinDBException {
		return theories.containsKey(DatabaseUtilitiesTheoryPath.getFullDescriptionAvailableTheory(theory.getImportTheoryProject(), theory.getImportTheory()));
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
	public IDeployedTheoryRoot checkConflict(IImportTheory av) throws CoreException{
		IDeployedTheoryRoot deployedTheory = av.getImportTheory();
		for (IDeployedTheoryRoot dep : theories.values()){
			DeployedTheoryDecorator dec1 = new DeployedTheoryDecorator(deployedTheory);
			if (dec1.isConflicting(dep)){
				return dep;
			}
		}
		return null;
		
	}
}
