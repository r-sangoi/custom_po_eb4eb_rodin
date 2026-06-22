/**
 * 
 */
package org.eventb.theory.language.internal.ui.attr;

import org.eventb.internal.ui.eventbeditor.manipulation.AbstractAttributeManipulation;
import org.eventb.theory.core.IAvailableTheory;
import org.eventb.theory.core.IAvailableTheoryProject;
import org.rodinp.core.IRodinElement;

/**
 * @author RenatoSilva
 *
 */
@SuppressWarnings("restriction")
public abstract class AbstractAvailableTheoryAttributeManipulation extends AbstractAttributeManipulation{
	
	protected final String[] EMPTY_LIST = new String[0];
	
	protected IAvailableTheory asAvailableTheoryElement(IRodinElement element) {
		return (IAvailableTheory) element;
	}
	
	protected IAvailableTheoryProject asAvailableTheoryProjectElement(IRodinElement element) {
		return (IAvailableTheoryProject) element;
	}

}
