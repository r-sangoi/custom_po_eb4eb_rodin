package org.eventb.theory.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

/**
 * Common interface for a complete rewrite rule.
 * 
 * <p> A rewrite rule is complete if the disjunction of its rhs's conditions is provable.</p>
 * 
 * <p>This interface is not intended to be implemented by clients.</p>
 * @author maamria
 *
 */
public interface ICompleteElement extends IInternalElement{

	/**
	 * <p>To check whether the completeness attribute is set.</p>
	 * @return whether the attribute is set
	 * @throws RodinDBException
	 */
	boolean hasComplete() throws RodinDBException;
	/**
	 * <p>Returns whether the element is complete or incomplete.</p>
	 * @return whether the element is complete
	 * @throws RodinDBException
	 */
	boolean isComplete() throws RodinDBException;
	
	/**
	 * <p>Sets the completeness attribute to the new value <code>isComplete</code>.</p>
	 * @param isComplete the new value
	 * @param pm the progress monitor
	 * @throws RodinDBException
	 */
	void setComplete(boolean isComplete, IProgressMonitor pm)
		throws RodinDBException;
	
}
