package org.eventb.theory.core.sc.states;

import org.eventb.core.ILabeledElement;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.state.ILabelSymbolInfo;
import org.eventb.core.sc.state.ILabelSymbolTable;
import org.eventb.core.tool.IStateType;
import org.eventb.internal.core.sc.symbolTable.SymbolTable;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.rodinp.core.IInternalElementType;

/**
 * An implementation of a rewrite rule label symbol table to be a place holder for right hand side labels.
 * 
 * @author maamria
 *
 */
@SuppressWarnings("restriction")
public class RewriteRuleLabelSymbolTable extends 
SymbolTable<ILabeledElement, IInternalElementType<? extends ILabeledElement>, ILabelSymbolInfo>
implements ILabelSymbolTable{

	public final static IStateType<RewriteRuleLabelSymbolTable> STATE_TYPE = SCCore
			.getToolStateType(TheoryPlugin.PLUGIN_ID + ".rewriteRuleLabelSymbolTable");
	
	public RewriteRuleLabelSymbolTable(int size) {
		super(size);
	}

	public IStateType<?> getStateType() {
		return STATE_TYPE;
	}

}
