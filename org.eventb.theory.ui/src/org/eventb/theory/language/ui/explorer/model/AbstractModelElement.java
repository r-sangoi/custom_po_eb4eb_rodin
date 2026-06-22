/**
 * 
 */
package org.eventb.theory.language.ui.explorer.model;

import org.rodinp.core.IRodinElement;

import fr.systerel.internal.explorer.model.IModelElement;

/**
 * @author renatosilva
 *
 */
@SuppressWarnings("restriction")
public abstract class AbstractModelElement<E extends IRodinElement> implements IModelElement {
	
	protected IModelElement parent;
	protected E internalElement;
	
	public AbstractModelElement(E element, IModelElement parent){
		this.parent = parent;
		internalElement = element;
	}

	/* (non-Javadoc)
	 * @see fr.systerel.internal.explorer.model.IModelElement#getInternalElement()
	 */
	public IRodinElement getInternalElement() {
		return internalElement;
	}

	/* (non-Javadoc)
	 * @see fr.systerel.internal.explorer.model.IModelElement#getModelParent()
	 */
	public IModelElement getModelParent() {
		return parent;
	}

}
