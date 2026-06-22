/*******************************************************************************
 * Copyright (c) 2016 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University of Southampton - initial API and implementation
 *******************************************************************************/

package org.eventb.core.ast.extensions.maths;

import org.eventb.core.ast.Type;
import org.eventb.core.ast.datatype.IDatatype;
import org.eventb.core.internal.ast.extensions.maths.DatatypeOrigin;

/**
 * <p>
 * A common protocol for datatype origins. Instances of this interface are use
 * as origin element for datatype {@link IDatatype}.
 * </p>
 *
 * @author htson
 * @version 0.1
 * @see DatatypeOrigin
 * @since 4.0
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface IDatatypeOrigin {

	/**
	 * Add a type argument with the given input name and type.
	 * 
	 * @param name
	 *            the name of the type argument.
	 * @param type
	 *            the type of the type argument.
	 */
	public void addTypeArgument(String name, Type type);

	/**
	 * Add a list of constructors for the datatype.
	 * 
	 * @param constructors
	 *            the list of constructors.
	 */
	public void addConstructor(String... constructors);

	/**
	 * Add a destructor of the input type for the a constructor.
	 * 
	 * @param constructor
	 *            the constructor.
	 * @param destructor
	 *            the destructor.
	 * @param type
	 *            the type of the destructor.
	 * @precondtion the constructor must exist, i.e., be added to the datatype
	 *              origin using {@link #addConstructor(String...)}.
	 */
	public void addDestructor(String constructor, String destructor, Type type);

	/**
	 * Returns the name of the datatype as defined by this origin.
	 * 
	 * @return the name of the datatype as defined by this origin.
	 */
	public String getName();

	/**
	 * Returns the list of type arguments for the datatype as defined by this
	 * origin.
	 * 
	 * @return the list of type arguments for the datatype as defined by this
	 *         origin.
	 * @postcondition the return value is guaranteed to be NOT <code>null</code>
	 *                .
	 */
	public String[] getTypeArguments();

	/**
	 * Returns the given type for the input type argument.
	 * 
	 * @param typeArgument
	 *            the input type argument.
	 * @return the given type for the input type argument.
	 */
	public Type getGivenType(String typeArgument);

	/**
	 * Returns the array of constructors for the datatype as defined by the
	 * origin.
	 * 
	 * @return the array of constructors for the datatype as defined by the
	 *         origin.
	 * @postcondition the returned value is guaranteed to be NOT
	 *                <code>null</code>.
	 */
	public String[] getConstructors();

	/**
	 * Returns the array of destructors for constructor as defined by the data
	 * type origin.
	 * 
	 * @param constructor
	 *            the input constructor.
	 * @return the array of destructors for constructor as defined by the data
	 *         type origin.
	 * @postcondition the returned value is guaranteed to be NOT
	 *                <code>null</code>. The length of the returned array must
	 *                be the same as the array of destructor types as returned
	 *                by {@link #getDestructorTypes(String)}.
	 */
	public String[] getDestructors(String constructor);

	/**
	 * Returns the array of destructor types for the constructor as defined by
	 * the datatype origin.
	 * 
	 * @param constructor
	 *            the input constructor.
	 * @return the array of destructor types for the constructor as defined by
	 *         the datatype origin.
	 * @postcondition the returned value is guaranteed to be NOT
	 *                <code>null</code>. The length of the returned array must
	 *                be the same as the array of destructors as returned by
	 *                {@link #getDestructors(String)}.
	 */
	public Type[] getDestructorTypes(String constructor);

}
