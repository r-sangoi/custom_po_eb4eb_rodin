/**
 * 
 */
package org.eventb.theory.core.sc.states;

import java.util.HashMap;
import java.util.Map;

import org.eventb.core.tool.IStateType;
import org.eventb.internal.core.tool.state.State;
import org.eventb.theory.core.IAvailableTheoryProject;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

/**
 * @author renatosilva
 *
 */
@SuppressWarnings("restriction")
public class TheoryPathProjectTable extends State implements
		ITheoryPathProjectTable {
	
	private Map<String, IRodinProject> theoryProjects;

	/**
	 * 
	 */
	public TheoryPathProjectTable(int size) {
		theoryProjects =  new HashMap<String,IRodinProject>(size);
	}

	/* (non-Javadoc)
	 * @see org.eventb.internal.core.tool.types.IState#getStateType()
	 */
	@Override
	public IStateType<?> getStateType() {
		return STATE_TYPE;
	}

	@Override
	public void addTheoryProject(IAvailableTheoryProject theoryProject)
			throws RodinDBException {
		IRodinProject availableTheoryProject = theoryProject.getTheoryProject();
		theoryProjects.put(availableTheoryProject.getElementName(),availableTheoryProject);
	}

	@Override
	public boolean containsTheoryProject(String name) {
		return theoryProjects.containsKey(name);
	}

	@Override
	public IRodinProject getTheoryProject(String name) {
		return theoryProjects.get(name);
	}

	@Override
	public int size() {
		return theoryProjects.size();
	}

}
