/*******************************************************************************
 * Copyright (c) 2011, 2022 University of Southampton and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University of Southampton - initial API and implementation
 *     Systerel - adapt datatypes to Rodin 3.0 API
 *******************************************************************************/
package org.eventb.theory.core.maths.extensions;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.datatype.IConstructorBuilder;
import org.eventb.core.ast.datatype.IDatatype;
import org.eventb.core.ast.datatype.IDatatypeBuilder;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.ast.extension.IOperatorProperties.FormulaType;
import org.eventb.core.ast.extension.IOperatorProperties.Notation;
import org.eventb.core.ast.extensions.maths.AstUtilities;
import org.eventb.core.ast.extensions.maths.AxiomaticDefinition;
import org.eventb.core.ast.extensions.maths.DirectDefinition;
import org.eventb.core.ast.extensions.maths.IAxiomaticTypeOrigin;
import org.eventb.core.ast.extensions.maths.IDatatypeOrigin;
import org.eventb.core.ast.extensions.maths.IOperatorExtension;
import org.eventb.core.ast.extensions.maths.MathExtensionsFactory;
import org.eventb.core.ast.extensions.maths.OperatorExtensionProperties;
import org.eventb.core.ast.extensions.maths.RecursiveDefinition;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.IFormulaExtensionsSource;
import org.eventb.theory.core.ISCAxiomaticDefinitionAxiom;
import org.eventb.theory.core.ISCAxiomaticDefinitionsBlock;
import org.eventb.theory.core.ISCAxiomaticOperatorDefinition;
import org.eventb.theory.core.ISCAxiomaticTypeDefinition;
import org.eventb.theory.core.ISCConstructorArgument;
import org.eventb.theory.core.ISCDatatypeConstructor;
import org.eventb.theory.core.ISCDatatypeDefinition;
import org.eventb.theory.core.ISCNewOperatorDefinition;
import org.eventb.theory.core.ISCOperatorArgument;
import org.eventb.theory.core.ISCRecursiveDefinitionCase;
import org.eventb.theory.core.ISCRecursiveOperatorDefinition;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.ISCTypeArgument;
import org.eventb.theory.core.ISCTypeParameter;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

/**
 * An implementation of formula extensions loader from an extensions source.
 * 
 * <p>
 * Currently, possible sources of extensions include:
 * <li> {@link ISCTheoryRoot}</li>
 * <li> {@link IDeployedTheoryRoot}</li>
 * 
 * </p>
 * 
 * @author maamria
 * 
 */
public class FormulaExtensionsLoader {

	public static final Set<IFormulaExtension> EMPTY_EXT = new LinkedHashSet<IFormulaExtension>();

	private FormulaFactory factory;
	private ITypeEnvironmentBuilder typeEnvironment;
	private IFormulaExtensionsSource source;

	public FormulaExtensionsLoader(IFormulaExtensionsSource source, FormulaFactory factory) {
		this.source = source;
		this.factory = factory;
		this.typeEnvironment = factory.makeTypeEnvironment();
	}

	/**
	 * Creates a datatype origin from a statically checked datatype definition
	 * using the given formula factory.
	 * 
	 * @param definition
	 *            the statically checked datatype definition.
	 * @param factory
	 *            the formula factory to build the datatype origin.
	 * @return the newly created datatype origin.
	 * @throws CoreException
	 *             if some unexpected error occurs.
	 */
	public static IDatatypeOrigin makeDatatypeOrigin(
			ISCDatatypeDefinition definition, FormulaFactory factory)
			throws CoreException {
		String name = definition.getIdentifierString();
		IDatatypeOrigin origin = MathExtensionsFactory
				.makeDatatypeOrigin(name);
		ISCTypeArgument[] typeArguments = definition.getTypeArguments();
		for (ISCTypeArgument typeArgument : typeArguments) {
			origin.addTypeArgument(typeArgument.getElementName(),
					typeArgument.getSCGivenType(factory));
		}
		
		ISCDatatypeConstructor[] constructors = definition.getConstructors();
		for (ISCDatatypeConstructor constructor : constructors) {
			String constructorIdent = constructor.getIdentifierString();
			origin.addConstructor(constructorIdent);
			ISCConstructorArgument[] destructors = constructor.getConstructorArguments();
			for (ISCConstructorArgument destructor : destructors) {
				origin.addDestructor(constructorIdent,
						destructor.getIdentifierString(),
						destructor.getType(factory));
			}
		}
		return origin;
	}


	/**
	 * Creates a new axiomatic type origin from a statically checked axiomatic
	 * type definition.
	 * 
	 * @param definition
	 *            the input axiomatic type definition.
	 * @return the newly created axiomatic type definition.
	 * @throws RodinDBException
	 *             if some unexpected error occurs.
	 */
	public static IAxiomaticTypeOrigin makeAxiomaticTypeOrigin(
			ISCAxiomaticTypeDefinition definition) throws RodinDBException {
		String name = definition.getIdentifierString();
		IAxiomaticTypeOrigin origin = MathExtensionsFactory
				.makeAxiomaticTypeOrigin(name);
		return origin;
	}

	public Set<IFormulaExtension> load() throws CoreException {

		if (source == null || !source.exists()) {
			return EMPTY_EXT;
		}
		Set<IFormulaExtension> extensions = new LinkedHashSet<IFormulaExtension>();
		initialise();

		ISCDatatypeDefinition datatypeDefinitions[] = getDatatypes(source);

		for (ISCDatatypeDefinition definition : datatypeDefinitions) {
			DatatypeTransformer transformer = new DatatypeTransformer();
			IDatatype datatype = transformer.transform(definition, factory);
			if (datatype != null) {
				extensions.addAll(datatype.getExtensions());
			}
			factory = factory.withExtensions(extensions);
			typeEnvironment = AstUtilities.getTypeEnvironmentForFactory(typeEnvironment, factory);
		}
		ISCNewOperatorDefinition operatorDefinitions[] = getOperators(source);
		for (ISCNewOperatorDefinition definition : operatorDefinitions) {
			OperatorTransformer transformer = new OperatorTransformer();
			ITypeEnvironmentBuilder localTypeEnvironment = typeEnvironment.makeBuilder();
			IOperatorExtension addedExtensions = transformer.transform(definition, factory, localTypeEnvironment);
			if (addedExtensions == null) {
				continue;
			}
			extensions.add(addedExtensions);
			factory = factory.withExtensions(extensions);
			typeEnvironment = AstUtilities.getTypeEnvironmentForFactory(typeEnvironment, factory);
			localTypeEnvironment = AstUtilities.getTypeEnvironmentForFactory(localTypeEnvironment, factory);
			
			if (definition.getDirectOperatorDefinitions().length == 1){
				Formula<?> scFormula = definition.getDirectOperatorDefinitions()[0].getSCFormula(factory, localTypeEnvironment);
				addedExtensions.setDefinition(new DirectDefinition(scFormula));
			}
			else if (definition.getRecursiveOperatorDefinitions().length == 1){
				ISCRecursiveOperatorDefinition recDef = definition.getRecursiveOperatorDefinitions()[0];
				Map<Expression, Formula<?>> recursiveCases = new LinkedHashMap<Expression, Formula<?>>();
				FreeIdentifier inductiveArg = factory.makeFreeIdentifier(recDef.getInductiveArgument(),
						null, localTypeEnvironment.getType(recDef.getInductiveArgument()));
				for (ISCRecursiveDefinitionCase recCase : recDef.getRecursiveDefinitionCases()){
					ITypeEnvironmentBuilder typeEnv = localTypeEnvironment.makeBuilder();
					Expression caseExp = recCase.getSCCaseExpression(typeEnv, inductiveArg);
					Formula<?> caseDef = recCase.getSCFormula(factory, typeEnv);
					recursiveCases.put(caseExp, caseDef);
				}
				
				addedExtensions.setDefinition(new RecursiveDefinition(inductiveArg, recursiveCases));
			}

		}
		ISCAxiomaticDefinitionsBlock blocks[] = getBlocks(source);
		for(ISCAxiomaticDefinitionsBlock block : blocks){
			for (ISCAxiomaticTypeDefinition def : block.getAxiomaticTypeDefinitions()){
				AxiomaticTypeTransformer trans = new AxiomaticTypeTransformer();
				IFormulaExtension addedExtensions = trans.transform(def, factory, typeEnvironment);
				if (addedExtensions != null) {
					extensions.add(addedExtensions);
				}
				factory = factory.withExtensions(extensions);
				typeEnvironment = AstUtilities.getTypeEnvironmentForFactory(typeEnvironment, factory);
				
			}
			List<IOperatorExtension> axiomExts = new ArrayList<IOperatorExtension>();
			for (ISCAxiomaticOperatorDefinition def : block.getAxiomaticOperatorDefinitions()){
				AxiomaticOperatorTransformer trans = new AxiomaticOperatorTransformer();
				IOperatorExtension addedExtensions = trans.transform(def, factory, typeEnvironment);
				if (addedExtensions != null) {
					extensions.add(addedExtensions);
					axiomExts.add(addedExtensions);
				}
				factory = factory.withExtensions(extensions);
				typeEnvironment = AstUtilities.getTypeEnvironmentForFactory(typeEnvironment, factory);
			}
			List<Predicate> axiomPredicates = new ArrayList<Predicate>();
			for (ISCAxiomaticDefinitionAxiom axiom : block.getAxiomaticDefinitionAxioms()){
				axiomPredicates.add(axiom.getPredicate(typeEnvironment));
			}
			for (IOperatorExtension opExt : axiomExts){
				opExt.setDefinition(new AxiomaticDefinition(axiomPredicates));
			}
		}
		return extensions;
	}

	private void initialise() throws CoreException {
		ISCTypeParameter[] typeParameters = getTypeParameters(source);
		for (ISCTypeParameter typeParameter : typeParameters) {
			typeEnvironment.addGivenSet(typeParameter.getIdentifierString());
		}
	}

	private ISCNewOperatorDefinition[] getOperators(IFormulaExtensionsSource source) throws CoreException {
		return source.getSCNewOperatorDefinitions();
	}

	private ISCDatatypeDefinition[] getDatatypes(IFormulaExtensionsSource source) throws CoreException {
		return source.getSCDatatypeDefinitions();
	}

	private ISCTypeParameter[] getTypeParameters(IFormulaExtensionsSource source) throws CoreException {
		return source.getSCTypeParameters();
	}
	
	private ISCAxiomaticDefinitionsBlock[] getBlocks(IFormulaExtensionsSource source) throws CoreException{
		return source.getSCAxiomaticDefinitionsBlocks();
	}
}

/**
 * An implementation of a transformer of a statically checked datatype
 * definition.
 * 
 * @see ISCDatatypeDefinition
 * 
 * @author maamria
 * 
 */
class DatatypeTransformer{

	public IDatatype transform(ISCDatatypeDefinition definition, FormulaFactory factory) throws CoreException {
		if (definition == null || !definition.exists()) {
			return null;
		}
		if (definition.hasHasErrorAttribute() && definition.hasError()) {
			return null;
		}

		final String typeName = definition.getIdentifierString();
		ISCTypeArgument[] scTypeArguments = definition.getTypeArguments();
		final List<String> typeArguments = new ArrayList<String>(scTypeArguments.length);
		for (ISCTypeArgument scTypeArg : scTypeArguments) {
			try {
				typeArguments.add(scTypeArg.getSCGivenType(factory).toString());
			} catch (CoreException e) {
				return null;
			}
			
		}
		IDatatypeOrigin origin = FormulaExtensionsLoader.makeDatatypeOrigin(definition, factory);
		final IDatatypeBuilder dtBuilder = MathExtensionsFactory
				.makeDatatypeBuilder(typeName, typeArguments, factory,
						origin);
		for (ISCDatatypeConstructor cons : definition.getConstructors()) {
			final IConstructorBuilder consBuilder = dtBuilder
					.addConstructor(cons.getIdentifierString());
			for (ISCConstructorArgument dest : cons.getConstructorArguments()) {
				// Note: the datatype type has been serialized as a given type,
				// which is what the IConstructorBuilder expects (instead of
				// a parametric type as one could imagine).
				// Hence not parsing with dtBuilder.parseType(),
				// as this one looks for a parametric type.
				try {
					final Type type = dest.getType(factory);

					consBuilder.addArgument(dest.getIdentifierString(), type);
				} catch (CoreException e) {
					return null;
				}
			}
		}
		return dtBuilder.finalizeDatatype();
	}

}

class AxiomaticTypeTransformer{

	public IFormulaExtension transform(ISCAxiomaticTypeDefinition definition, FormulaFactory factory,
			ITypeEnvironment typeEnvironment) {
		try{
			IAxiomaticTypeOrigin origin = FormulaExtensionsLoader.makeAxiomaticTypeOrigin(definition);
		return (IFormulaExtension)
				MathExtensionsFactory.getAxiomaticTypeExtension(definition.getIdentifierString(), 
						definition.getIdentifierString() + " Axiomatic Type", origin);
		} catch(CoreException exception){
			return null;
		}
	}
	
}

class AxiomaticOperatorTransformer extends DefinitionTransformer<ISCAxiomaticOperatorDefinition>{
	
	@Override
	public IOperatorExtension transform(ISCAxiomaticOperatorDefinition definitionElmnt, FormulaFactory factory,
			ITypeEnvironmentBuilder typeEnvironment) throws RodinDBException  {
		if (definitionElmnt == null || !definitionElmnt.exists()) {
			return null;
		}
		if (definitionElmnt.hasHasErrorAttribute() && definitionElmnt.hasError()) {
			return null;
		}
		String theoryName = definitionElmnt.getParent().getParent().getElementName();
		String syntax = definitionElmnt.getLabel();
		String operatorID = AstUtilities.makeOperatorID(theoryName, syntax);
		FormulaType formulaType = definitionElmnt.getFormulaType();
		Notation notation = definitionElmnt.getNotationType();
		String groupID = definitionElmnt.getOperatorGroup();
		boolean isAssociative = definitionElmnt.isAssociative();
		boolean isCommutative = definitionElmnt.isCommutative();

		ISCOperatorArgument[] scOperatorArguments = definitionElmnt.getOperatorArguments();
		Map<String, Type> operatorArguments = new LinkedHashMap<String, Type>();
		List<GivenType> typeParameters = new ArrayList<GivenType>();
		for (ISCOperatorArgument arg : scOperatorArguments) {
			Type type;
			try {
				type = arg.getType(factory);
			} catch (CoreException e) {
				return null;
			}
			operatorArguments.put(arg.getIdentifierString(), type);
			for (GivenType t : AstUtilities.getGivenTypes(type)) {
				if (!typeParameters.contains(t)) {
					typeParameters.add(t);
				}
			}
		}
		ITypeEnvironmentBuilder tempTypeEnvironment = AstUtilities.getTypeEnvironmentForFactory(
				typeEnvironment, factory);
		for (String arg : operatorArguments.keySet()) {
			tempTypeEnvironment.addName(arg, operatorArguments.get(arg));
		}
		Predicate wdCondition;
		Predicate dWdCondition;
		try {
			wdCondition = definitionElmnt
					.getPredicate(tempTypeEnvironment);
			dWdCondition = definitionElmnt.getWDCondition(factory,
					tempTypeEnvironment);
		} catch (CoreException e) {
			return null;
		}
		IOperatorExtension extension = null;
		OperatorExtensionProperties properties = new OperatorExtensionProperties(operatorID, syntax, formulaType,
				notation, groupID);
		if (AstUtilities.isExpressionOperator(definitionElmnt.getFormulaType())) {
			Type type;
			try {
				type = definitionElmnt.getType(factory);
			} catch (CoreException e) {
				return null;
			}
			extension = (IOperatorExtension)MathExtensionsFactory.getExpressionExtension(properties, isCommutative, isAssociative, 
					operatorArguments, type, wdCondition, dWdCondition, null,definitionElmnt);
		} else {
			extension = (IOperatorExtension)MathExtensionsFactory.getPredicateExtension(properties, isCommutative, operatorArguments,
					wdCondition, dWdCondition, null, definitionElmnt);
		}
		return extension;
	}
	
}

/**
 * An implementation of a transformer for statically checked operator
 * definitions.
 * 
 * @see ISCNewOperatorDefinition
 * 
 * @author maamria
 * 
 */
class OperatorTransformer extends DefinitionTransformer<ISCNewOperatorDefinition> {

	@Override
	public IOperatorExtension transform(ISCNewOperatorDefinition definitionElmnt, FormulaFactory factory,
			ITypeEnvironmentBuilder typeEnvironment) throws RodinDBException {
		if (definitionElmnt == null || !definitionElmnt.exists()) {
			return null;
		}
		if (definitionElmnt.hasHasErrorAttribute() && definitionElmnt.hasError()) {
			return null;
		}
		// (htson) Bug fix: Get the theory name from the source element's root
		// (should the un-checked theory). Originally, getting the name of the
		// parent is incorrect in the case for the formula factory from the proofs (getting the language "L").
		// String theoryName = definitionElmnt.getParent().getElementName();
		IInternalElement source = (IInternalElement) definitionElmnt.getSource();
		String theoryName = source.getRoot().getElementName();
		String syntax = definitionElmnt.getLabel();
		String operatorID = AstUtilities.makeOperatorID(theoryName, syntax);
		FormulaType formulaType = definitionElmnt.getFormulaType();
		Notation notation = definitionElmnt.getNotationType();
		String groupID = definitionElmnt.getOperatorGroup();
		boolean isAssociative = definitionElmnt.isAssociative();
		boolean isCommutative = definitionElmnt.isCommutative();

		ISCOperatorArgument[] scOperatorArguments = definitionElmnt.getOperatorArguments();
		Map<String, Type> operatorArguments = new LinkedHashMap<String, Type>();
		List<GivenType> typeParameters = new ArrayList<GivenType>();
		for (ISCOperatorArgument arg : scOperatorArguments) {
			Type type;
			try {
				type = arg.getType(factory);
			} catch (CoreException e) {
				return null;
			}
			operatorArguments.put(arg.getIdentifierString(), type);
			for (GivenType t : AstUtilities.getGivenTypes(type)) {
				if (!typeParameters.contains(t)) {
					typeParameters.add(t);
				}
			}
		}
		for (String arg : operatorArguments.keySet()) {
			typeEnvironment.addName(arg, operatorArguments.get(arg));
		}
		Predicate wdCondition;
		Predicate dWdCondition;
		try {
			wdCondition = definitionElmnt
					.getPredicate(typeEnvironment);
			dWdCondition = definitionElmnt.getWDCondition(factory,
					typeEnvironment);
		} catch (CoreException e) {
			return null;
		}
		final IOperatorExtension extension;
		OperatorExtensionProperties properties = new OperatorExtensionProperties(operatorID, syntax, formulaType,
				notation, groupID);
		if (AstUtilities.isExpressionOperator(definitionElmnt.getFormulaType())) {
			Type type;
			try {
				type = definitionElmnt.getType(factory);
			} catch (CoreException e) {
				return null;
			}
			extension = (IOperatorExtension) MathExtensionsFactory.getExpressionExtension(properties, isCommutative, isAssociative, 
					operatorArguments, type, wdCondition, dWdCondition, null, definitionElmnt);
		} else {
			extension = (IOperatorExtension)MathExtensionsFactory.getPredicateExtension(properties, isCommutative, operatorArguments,
					wdCondition, dWdCondition, null,definitionElmnt);
		}
		return extension;
	}

}

/**
 * Common protocol for a definition element transformer.
 * 
 * <p>
 * Typically, a definition element contains information to construct a
 * mathematical formula extension as per AST requirements.
 * 
 * <p>
 * Currently, the available definition elements are :
 * 
 * <li>
 * <code> {@link ISCNewOperatorDefinition} </code></li>
 * <li>
 * <code> {@link ISCAxiomaticOperatorDefinition} </code></li>
 * </p>
 * 
 * @author maamria
 * 
 */
abstract class DefinitionTransformer<E extends IInternalElement> {

	/**
	 * Returns the mathematical extension contained in the definition
	 * element.
	 * 
	 * @param definition
	 *            the definitional element
	 * @param factory
	 *            the formula factory
	 * @param typeEnvironment
	 *            the type environment
	 * @return the formula extension
	 * @throws RodinDBException 
	 * @throws CoreException
	 */
	public abstract IOperatorExtension transform(E definition, final FormulaFactory factory,
			ITypeEnvironmentBuilder typeEnvironment) throws CoreException;

}