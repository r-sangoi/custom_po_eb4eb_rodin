/**
 * 
 */
package org.eventb.theory.core.sc.states;

import java.util.HashMap;
import java.util.Map;

import org.eventb.core.tool.IStateType;
import org.eventb.internal.core.tool.state.State;
import org.eventb.theory.core.IImportTheoryProject;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

/**
 * @author asiehsalehi
 *
 */
@SuppressWarnings("restriction")
public class ImportProjectTable extends State implements
		IImportProjectTable {
	
	private Map<String, IRodinProject> theoryProjects;

	/**
	 * 
	 */
	public ImportProjectTable(int size) {
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
	public void addTheoryProject(IImportTheoryProject theoryProject)
			throws RodinDBException {
		IRodinProject importTheoryProject = theoryProject.getTheoryProject();
		theoryProjects.put(importTheoryProject.getElementName(),importTheoryProject);
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
