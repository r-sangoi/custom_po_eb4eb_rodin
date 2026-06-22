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
 * Symbol tables for axiomatic definitions labels
 * @author maamria
 *
 */
@SuppressWarnings("restriction")
public class AxiomaticDefinitionsLabelSymbolTable extends 
SymbolTable<ILabeledElement, IInternalElementType<? extends ILabeledElement>, ILabelSymbolInfo>
implements ILabelSymbolTable{

	public final static IStateType<AxiomaticDefinitionsLabelSymbolTable> STATE_TYPE = SCCore.getToolStateType(TheoryPlugin.PLUGIN_ID
			+ ".axiomaticDefinitionsLabelSymbolTable");
	
	public AxiomaticDefinitionsLabelSymbolTable() {
		super(TheoryModule.LABEL_SYMTAB_SIZE);
	}

	@Override
	public IStateType<?> getStateType() {
		return STATE_TYPE;
	}

}
