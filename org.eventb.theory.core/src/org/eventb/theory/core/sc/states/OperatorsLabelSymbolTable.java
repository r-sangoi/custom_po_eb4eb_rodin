/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.sc.states;

import org.eventb.core.ILabeledElement;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.state.ILabelSymbolInfo;
import org.eventb.core.sc.state.ILabelSymbolTable;
import org.eventb.core.tool.IStateType;
import org.eventb.internal.core.sc.symbolTable.SymbolTable;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.modules.TheoryModule;
import org.rodinp.core.IInternalElementType;

/**
 * An implementation of an operator label (ID) symbol table.
 * 
 * @author maamria
 * 
 */
@SuppressWarnings("restriction")
public class OperatorsLabelSymbolTable extends 
SymbolTable<ILabeledElement, IInternalElementType<? extends ILabeledElement>, ILabelSymbolInfo>
implements ILabelSymbolTable{

	public final static IStateType<OperatorsLabelSymbolTable> STATE_TYPE = SCCore
			.getToolStateType(TheoryPlugin.PLUGIN_ID
					+ ".operatorsLabelSymbolTable");

	public OperatorsLabelSymbolTable() {
		super(TheoryModule.LABEL_SYMTAB_SIZE);
	}

	public IStateType<?> getStateType() {
		return STATE_TYPE;
	}

}
