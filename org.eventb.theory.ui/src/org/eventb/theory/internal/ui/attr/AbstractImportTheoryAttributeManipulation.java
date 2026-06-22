/**
 * 
 */
package org.eventb.theory.internal.ui.attr;

import org.eventb.internal.ui.eventbeditor.manipulation.AbstractAttributeManipulation;
import org.eventb.theory.core.IImportTheory;
import org.eventb.theory.core.IImportTheoryProject;
import org.rodinp.core.IRodinElement;

/**
 * @author asiehsalehi
 *
 */
@SuppressWarnings("restriction")
public abstract class AbstractImportTheoryAttributeManipulation extends AbstractAttributeManipulation{
	
	protected final String[] EMPTY_LIST = new String[0];
	
	protected IImportTheory asImportTheoryElement(IRodinElement element) {
		return (IImportTheory) element;
	}
	
	protected IImportTheoryProject asImportTheoryProjectElement(IRodinElement element) {
		return (IImportTheoryProject) element;
	}

}
