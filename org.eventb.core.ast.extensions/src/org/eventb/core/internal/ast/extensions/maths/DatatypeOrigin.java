/*******************************************************************************
 * Copyright (c) 2016, 2021 University of Southampton and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University of Southampton - initial API and implementation
 *******************************************************************************/

package org.eventb.core.internal.ast.extensions.maths;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eventb.core.ast.Type;
import org.eventb.core.ast.datatype.IDatatype;
import org.eventb.core.ast.extensions.maths.IDatatypeOrigin;

/**
 * <p>
 * An implementation of datatype origin. 
 * </p>
 *
 * @author htson
 * @version 0.1
 * @see IDatatype
 * @since 3.1.0
 * @noextend This class is not intended to be subclassed by clients.
 */
public class DatatypeOrigin implements IDatatypeOrigin {
	// The name of the datatype.
	private String name;

	// The map of type arguments to their corresponding type.
	private Map<String, Type> typeArguments;
	
	// The list of constructors.
	private List<String> constructors;
	
	// The map of constructors' destructors
	private Map<String, List<String>> destructors;
	
	// The map of constructors' destructor types.
	private Map<String, List<Type>> destructorTypes;
	
	/**
	 * Construct an instance of the datatype origin for datatype with a given
	 * name.
	 * 
	 * @param name
	 *            the name of the datatype as defined by this origin.
	 */
	public DatatypeOrigin(String name) {
		this.name = name;
		this.typeArguments = new LinkedHashMap<String, Type>();
		this.constructors = new ArrayList<String>();
		this.destructors = new HashMap<String, List<String>>();
		this.destructorTypes = new HashMap<String, List<Type>>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IDatatypeDefinition#addTypeArgument(String, Type)
	 */
	@Override
	public void addTypeArgument(String typeArgument, Type type) {
		typeArguments.put(typeArgument, type);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IDatatypeDefinition#addConstructor(String[])
	 */
	@Override
	public void addConstructor(String... constructors) {
		Collections.addAll(this.constructors, constructors);
		for (String constructor : constructors) {
			destructors.put(constructor, new ArrayList<String>());
			destructorTypes.put(constructor, new ArrayList<Type>());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IDatatypeDefinition#addDestructor(String, String, Type)
	 */
	@Override
	public void addDestructor(String constructor, String destructor, Type type) {
		// Assert PRECONDITION
		assert constructors.contains(constructor);

		destructors.get(constructor).add(destructor);
		destructorTypes.get(constructor).add(type);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IDatatypeOrigin#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/* (non-Javadoc)
	 * @see IDatatypeOrigin#getTypeArguments()
	 */
	@Override
	public String[] getTypeArguments() {
		Set<String> keySet = typeArguments.keySet();
		return keySet.toArray(new String[keySet.size()]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IDatatypeOrigin#getGivenType(String)
	 */
	@Override
	public Type getGivenType(String typeArgument) {
		return typeArguments.get(typeArgument);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IDatatypeOrigin#getConstructors()
	 */
	@Override
	public String[] getConstructors() {
		return constructors.toArray(new String[constructors.size()]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IDatatypeOrigin#getDestructors(String)
	 */
	@Override
	public String[] getDestructors(String constructor) {
		List<String> d = destructors.get(constructor);
		return d.toArray(new String[d.size()]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IDatatypeOrigin#getDestructorTypes(String)
	 */
	@Override
	public Type[] getDestructorTypes(String constructor) {
		List<Type> t = destructorTypes.get(constructor);
		return t.toArray(new Type[t.size()]);
	}

	/**
	 * Equality for datatype origin by comparing the contents.
	 * 
	 * @see Object#equals(Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || this.getClass() != obj.getClass()) {
			return false;
		}
		
		DatatypeOrigin other = (DatatypeOrigin) obj;
		
		// Compare the datatype name.
		String thisName = this.getName();
		String otherName = other.getName();
		if (thisName == null && otherName != null)
			return false;
		if (!thisName.equals(otherName))
			return false;
		if (!thisName.equals(otherName))
			return false;
		
		// Compare the type arguments.
		if (!hasSameTypeArguments(this, other)) {
			return false;
		}
		
		// Compare the constructors.
		if (!hasSameConstructors(this, other)) {
			return false;
		}
		
		// Compare the destructors.
		if (!hasSameDestructors(this, other)) {
			return false;
		}
		
		// Compare the destructor types.
		if (!hasSameDestructorTypes(this, other))
			return false;

		return true;
	}

	/**
	 * Utility method for comparing the type arguments.
	 * 
	 * @param mine
	 *            mine datatype origin
	 * @param other
	 *            the other's datatype origin
	 * @return <code>true</code> if the two input origins have the same type
	 *         arguments including their types.
	 * @see #equals(Object)
	 */
	private boolean hasSameTypeArguments(DatatypeOrigin mine,
			DatatypeOrigin other) {
		String[] mineTypeArguments = mine.getTypeArguments();
		String[] otherTypeArguments = other.getTypeArguments();
		
		if (!Arrays.equals(mineTypeArguments, otherTypeArguments)) {
			return false;
		}
		for (String typeArg : mineTypeArguments) {
			Type mineType = mine.getGivenType(typeArg);
			Type otherType = other.getGivenType(typeArg);
			if (mineType == null && otherType != null)
				return false;
			if (!mineType.equals(otherType))
				return false;
		}
		return true;
	}

	/**
	 * Utility method for comparing the constructors of the datatype origins.
	 * 
	 * @param mine
	 *            mine datatype origin.
	 * @param other
	 *            the other's datatype origin.
	 * @return <code>true</code> if the two input datatype origins have the same
	 *         SET of constructors.
	 * @see #equals(Object)
	 */
	private boolean hasSameConstructors(DatatypeOrigin mine,
			DatatypeOrigin other) {
		Set<String> mineConstructors = new HashSet<String>(Arrays.asList(mine
				.getConstructors()));
		Set<String> otherConstructors = new HashSet<String>(Arrays.asList(other
				.getConstructors()));
		return mineConstructors.equals(otherConstructors);
	}

	/**
	 * Utility method for comparing the destructors of datatype origins.
	 * 
	 * @param mine
	 *            mine datatype origin.
	 * @param other
	 *            the other's datatype origin.
	 * @return <code>true</code> if the two input datatypes have the same arrays
	 *         of destructors for each constructor.
	 * @see #equals(Object)
	 * @see #hasSameConstructors(DatatypeOrigin, DatatypeOrigin)
	 * @precondition the two origins have the same set of constructors.
	 */
	private boolean hasSameDestructors(DatatypeOrigin mine,
			DatatypeOrigin other) {
		// Assume the same constructors
		String[] mineConstructors = mine.getConstructors();
		for (String constructor : mineConstructors) {
			String[] mineDestructors = mine.getDestructors(constructor);
			String[] otherDestructors = other.getDestructors(constructor);
			if (!Arrays.equals(mineDestructors, otherDestructors))
				return false;
		}
		return true;
	}

	/**
	 * Utility method for comparing the destructor types of datatype origins.
	 * 
	 * @param mine
	 *            mine datatype origin.
	 * @param other
	 *            the other's datatype origin.
	 * @return <code>true</code> if the two input datatypes have the same arrays
	 *         of destructor types for each constructor.
	 * @see #equals(Object)
	 * @see #hasSameConstructors(DatatypeOrigin, DatatypeOrigin)
	 * @precondition the two origins have the same set of constructors.
	 */
	private boolean hasSameDestructorTypes(DatatypeOrigin mine,
			DatatypeOrigin other) {
		String[] mineConstructors = mine.getConstructors();
		for (String constructor : mineConstructors) {
			Type[] mineDestructorTypes = mine.getDestructorTypes(constructor);
			Type[] otherDestructorTypes = other.getDestructorTypes(constructor);
			if (!Arrays.equals(mineDestructorTypes, otherDestructorTypes))
				return false;
		}
		return true;
	}

	/**
	 * The hash code is computed from the datatype origin's type arguments and
	 * constructors.
	 */
	@Override
	public int hashCode() {
		final int prime = 19;
		int result = this.getName().hashCode();
		String[] typeArgs = this.getTypeArguments();
		for (String typeArg : typeArgs) {
			result = prime*result + typeArg.hashCode();
		}
		result = prime*result;
		String[] constrs = this.getConstructors();
		for (String constr : constrs) {
			result += constr.hashCode();
		}
		result = prime * result + Arrays.hashCode(constrs);
		return result;
	}

	
}
