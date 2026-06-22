/*******************************************************************************
 * Copyright (c) 2010, 2014 University of Southampton and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University of Southampton - initial API and implementation
 *     Systerel - adapt datatypes to Rodin 3.0 API
 *******************************************************************************/
package org.eventb.theory.core.sc.states;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.datatype.IConstructorBuilder;
import org.eventb.core.ast.datatype.IDatatype;
import org.eventb.core.ast.datatype.IDatatypeBuilder;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.ast.extensions.maths.IDatatypeOrigin;
import org.eventb.core.ast.extensions.maths.MathExtensionsFactory;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.state.ISCState;
import org.eventb.core.tool.IStateType;
import org.eventb.internal.core.tool.state.State;
import org.eventb.theory.core.plugin.TheoryPlugin;

/**
 * @author maamria
 *
 */
@SuppressWarnings("restriction")
public class DatatypeTable extends State implements ISCState {
	
	public static final IStateType<DatatypeTable> STATE_TYPE = SCCore.getToolStateType(TheoryPlugin.PLUGIN_ID + ".datatypeTable");
	
	private Map<String, DatatypeEntry> datatypes;
	private String currentDatatype = null;
	
	private FormulaFactory initialFactory;

	public DatatypeTable(FormulaFactory initialFactory){
		this.initialFactory = initialFactory;
		datatypes = new LinkedHashMap<String, DatatypeTable.DatatypeEntry>();
	}
	
	@Override
	public IStateType<?> getStateType() {
		return STATE_TYPE;
	}
	
	public FormulaFactory augmentFormulaFactory() throws CoreException{
		assertMutable();
		DatatypeEntry entry = datatypes.get(currentDatatype);
		
		final IDatatype datatype = entry.finalizeDatatype();
		
		Set<IFormulaExtension> extensions  = datatype.getExtensions();
		initialFactory = initialFactory.withExtensions(extensions);
		return initialFactory;
	}
	
	public FormulaFactory reset() throws CoreException{
		assertMutable();
		currentDatatype = null;
		return initialFactory;
	}
	
	public void setErrorProne() throws CoreException{
		assertMutable();
		DatatypeEntry entry = datatypes.get(currentDatatype);
		if(entry != null)
			entry.setErrorProne();
	}
	
	public boolean isErrorProne() throws CoreException{
		assertMutable();
		DatatypeEntry entry = datatypes.get(currentDatatype);
		if(entry != null)
			return entry.isErrorProne();
		return true;
	}
	
	public void addDatatype(String name, List<String> typeArgs, IDatatypeOrigin origin) throws CoreException{
		assertMutable();
		datatypes.put(name, new DatatypeEntry(name, typeArgs, origin));
		currentDatatype = name;
	}
	
	public boolean datatypeHasBaseConstructor() throws CoreException{
		assertMutable();
		return datatypes.get(currentDatatype).hasBaseConstructor();
	}
	
	public boolean isAllowedGivenType(GivenType givenType) throws CoreException {
		assertMutable();
		return datatypes.get(currentDatatype).isAllowedGivenType(givenType);
	}
	
	@Override
	public void makeImmutable() {
		datatypes = Collections.unmodifiableMap(datatypes);
		super.makeImmutable();
	}
	
	public boolean checkName(String name) throws CoreException{
		assertMutable();
		return initialFactory.isValidIdentifierName(name);
	}

	public void addConstructor(String consName) throws CoreException {
		assertMutable();
		datatypes.get(currentDatatype).addConstructor(consName);
	}
	
	public IParseResult parseType(String strType) throws CoreException {
		assertMutable();
		return datatypes.get(currentDatatype).parseType(strType);
	}

	public void addDestructor(String destName, Type type) throws CoreException{
		assertMutable();
		datatypes.get(currentDatatype).addDestructor(destName, type);
	}
	
	private class DatatypeEntry{
		
		private final IDatatypeBuilder dtBuilder;
		private final String identifier;
		private final List<String> typeArguments;
		private final List<String> constructors = new ArrayList<String>();
		private final List<String> destructors = new ArrayList<String>();
		private IConstructorBuilder currentConstructor = null;

		boolean isErrorProne = false;
		
		public DatatypeEntry(String identifier, List<String> typeArgs, IDatatypeOrigin origin){
			this.dtBuilder = MathExtensionsFactory.makeDatatypeBuilder(identifier, typeArgs, initialFactory, origin);
			this.identifier = identifier;
			this.typeArguments = new ArrayList<String>(typeArgs);
		}
		
		public boolean isAllowedGivenType(GivenType givenType) throws CoreException {
			final String name = givenType.getName();
			return identifier.equals(name) || typeArguments.contains(name);
		}
		
		public void setErrorProne(){
			isErrorProne = true;
		}
		
		public boolean isErrorProne(){
			return isErrorProne;
		}
		
		public boolean hasBaseConstructor() {
			return dtBuilder.hasBasicConstructor();
		}
		
		public void addConstructor(String name){
			currentConstructor = dtBuilder.addConstructor(name);
			constructors.add(name);
		}

		public IParseResult parseType(String strType) {
			return dtBuilder.parseType(strType);
		}
		
		public void addDestructor(String destructor, Type type){
			currentConstructor.addArgument(destructor, type);
			destructors.add(destructor);
		}
		
		public IDatatype finalizeDatatype() {
			return dtBuilder.finalizeDatatype();
		}
	}
}
