/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core;

import org.eventb.core.IAccuracyElement;
import org.eventb.core.ICommentedElement;
import org.eventb.core.IEventBRoot;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for a deployed theory root.
 * 
 * <p> A deployed theory is a copy of all SC theory elements that do not have errors associated with them.
 * 
 * <p> Validity information are added to children elements where appropriate.
 * 
 * <p> Relationships between deployed theories exist by means of a "use" directive.
 * 
 * <p> This interface is not intended to be implemented by clients.
 * 
 * @see ISCTheoryRoot
 * 
 * @author maamria
 * 
 */
public interface IDeployedTheoryRoot extends IEventBRoot, IAccuracyElement ,
IFormulaExtensionsSource, IExtensionRulesSource, IOutdatedElement, IModificationHashValueElement,
ICommentedElement, ISCTheoryRoot{

	IInternalElementType<IDeployedTheoryRoot> ELEMENT_TYPE = RodinCore
			.getInternalElementType(TheoryPlugin.PLUGIN_ID + ".deployedTheoryRoot");
	
	/**
	 * Returns a handle to an used theory element with the given name.
	 * @param name of used theory
	 * @return the import theory
	 */
	IUseTheory getUsedTheory(String name);
	
	/**
	 * Returns the used theory children elements of this element.
	 * @return all used theories
	 * @throws RodinDBException
	 */
	IUseTheory[] getUsedTheories() throws RodinDBException;
	
	/**
	 * <p>Returns the global type environment of this SC theory.</p>
	 * @param factory
	 * @return the type environment
	 * @throws RodinDBException
	 */
	ITypeEnvironment getTypeEnvironment(FormulaFactory factory) throws RodinDBException;

}
