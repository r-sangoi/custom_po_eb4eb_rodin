package org.eventb.theory.core.sc.states;

import static java.util.Arrays.asList;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.datatype.IDatatype;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.state.ISCState;
import org.eventb.core.tool.IStateType;
import org.eventb.internal.core.tool.state.State;
import org.eventb.theory.core.IRecursiveDefinitionCase;
import org.eventb.theory.core.plugin.TheoryPlugin;

/**
 * 
 * @author maamria
 * 
 */
@SuppressWarnings("restriction")
public class RecursiveDefinitionInfo extends State implements ISCState{

	public final static IStateType<RecursiveDefinitionInfo> STATE_TYPE = SCCore
			.getToolStateType(TheoryPlugin.PLUGIN_ID + ".recursiveDefinitionInfo");
	
	private Map<IRecursiveDefinitionCase, CaseEntry> baseEntries;
	private Map<IRecursiveDefinitionCase, CaseEntry> inductiveEntries;
	private FreeIdentifier inductiveArgument;
	private boolean accurate = true;
	private IDatatype datatype;
	private Set<IExpressionExtension> coveredConstructors;
	
	public RecursiveDefinitionInfo(){
		baseEntries = new LinkedHashMap<IRecursiveDefinitionCase, CaseEntry>();
		inductiveEntries = new LinkedHashMap<IRecursiveDefinitionCase, CaseEntry>();
		coveredConstructors = new LinkedHashSet<IExpressionExtension>();
	}
	
	public void setInductiveArgument(FreeIdentifier ident, FormulaFactory factory) throws CoreException{
		assertMutable();
		this.inductiveArgument = ident;
		Type type = ident.getType();
		
		if (type == null || !( type.toExpression() instanceof ExtendedExpression) ||
				!(((ExtendedExpression)type.toExpression()).getExtension().getOrigin() instanceof IDatatype)){
			throw new IllegalStateException("Illegal inductive argument");
		}
		IExpressionExtension extension = ((ExtendedExpression)type.toExpression()).getExtension();
		datatype = (IDatatype) extension.getOrigin();
	}
	
	public boolean isConstructor(IExpressionExtension extension) throws CoreException{
		assertMutable();
		return datatype.getConstructor(extension.getSyntaxSymbol()) != null;
	}
	
	public boolean isCoveredConstuctor(IExpressionExtension extension) throws CoreException{
		assertMutable();
		return coveredConstructors.contains(extension);
	}
	
	public boolean coveredAllConstructors() throws CoreException{
		assertImmutable();
		return coveredConstructors.containsAll(asList(datatype.getConstructors()));
	}
	
	public FreeIdentifier getInductiveArgument() throws CoreException{
		return inductiveArgument;
	}
	
	public void addEntry(IRecursiveDefinitionCase defCase, 
			ExtendedExpression exp, ITypeEnvironment typeEnvironment) throws CoreException{
		assertMutable();
		RecursiveDefinitionInfo.CaseEntry caseEntry = new CaseEntry(exp, typeEnvironment);
		if (caseEntry.isBaseCase()){
			baseEntries.put(defCase, caseEntry);
		}
		else {
			inductiveEntries.put(defCase, caseEntry);
		}
		coveredConstructors.add(exp.getExtension());
	}
	
	public Map<IRecursiveDefinitionCase, CaseEntry> getBaseEntries() throws CoreException{
		assertImmutable();
		return baseEntries;
	}
	
	public Map<IRecursiveDefinitionCase, CaseEntry> getInductiveEntries() throws CoreException{
		assertImmutable();
		return inductiveEntries;
	}
	
	public boolean isAccurate() throws CoreException{
		assertImmutable();
		return accurate;
	}

	public void setNotAccurate() throws CoreException{
		assertMutable();
		this.accurate = false;
	}

	@Override
	public void makeImmutable() {
		baseEntries = Collections.unmodifiableMap(baseEntries);
		inductiveEntries = Collections.unmodifiableMap(inductiveEntries);
		super.makeImmutable();
	}
	
	@Override
	public IStateType<?> getStateType() {
		return STATE_TYPE;
	}
	
	/**
	 * A simple implementation for a recursive definition case.
	 * 
	 * <p> Example : cons(x, l0) => 1 + listSize(l0)
	 * @author maamria
	 *
	 */
	public static class CaseEntry {
		ExtendedExpression caseExpression;
		ITypeEnvironment localTypeEnvironment;
		private boolean erroneous;
	
		// expression must be type checked
		public CaseEntry(ExtendedExpression caseExpression,
				ITypeEnvironment localTypeEnvironment) {
			this.caseExpression = caseExpression;
			this.localTypeEnvironment = localTypeEnvironment;
		}
	
		public ExtendedExpression getCaseExpression() {
			return caseExpression;
		}
	
		public ITypeEnvironment getLocalTypeEnvironment() {
			return localTypeEnvironment;
		}
		
		public boolean isErroneous() {
			return erroneous;
		}
	
		public void setErroneous() {
			this.erroneous = true;
		}
	
		public boolean isBaseCase(){
			boolean isBase = true;
			for (FreeIdentifier ident : caseExpression.getFreeIdentifiers()) {
				if (ident.getType().equals(caseExpression.getType())) {
					isBase = false;
					break;
				}
			}
			return isBase;
		}
	}

}

