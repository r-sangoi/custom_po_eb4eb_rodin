package org.eventb.theory.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

/**
 * 
 * Common protocol for hypothesis elements.
 * 
 * <p> Givens of inference rules can be set to be 'hyp' which triggers a special treatment by the prover.
 * 
 * @author maamria
 *
 */
public interface IHypElement extends IInternalElement{

	/**
	 * Returns whether the hyp attribute is present for this element.
	 * @return whether the hyp attribute is present for this element
	 * @throws RodinDBException
	 */
	public boolean hasHypAttribute() throws RodinDBException;
	
	/**
	 * Returns whether this element is tagged as hyp.
	 * @return whether this element is tagged as hyp
	 * @throws RodinDBException
	 */
	public boolean isHyp() throws RodinDBException;
	
	/**
	 * Set this element to be hyp is <code>isHyp</code> is set to <code>true</code>.
	 * @param isHyp whether to set this element to hyp
	 * @param monitor the progress monitor
	 * @throws RodinDBException
	 */
	public void setHyp(boolean isHyp, IProgressMonitor monitor) throws RodinDBException;
	
}
