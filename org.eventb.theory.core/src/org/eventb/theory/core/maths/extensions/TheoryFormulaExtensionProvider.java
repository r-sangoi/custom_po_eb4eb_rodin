/*******************************************************************************
 * Copyright (c) 2011, 2021 University of Southampton and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Soton - initial API and implementation
 *     Systerel - dependency on checked theory path
 *******************************************************************************/
package org.eventb.theory.core.maths.extensions;

import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static org.eventb.theory.core.util.CoreUtilities.log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eventb.core.IEventBRoot;
import org.eventb.core.ILanguage;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.datatype.IDatatype;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.ast.extensions.maths.IAxiomaticTypeOrigin;
import org.eventb.core.ast.extensions.maths.IDatatypeOrigin;
import org.eventb.core.ast.extensions.maths.IOperatorExtension;
import org.eventb.core.extension.IFormulaExtensionProvider;
import org.eventb.theory.core.ISCAxiomaticOperatorDefinition;
import org.eventb.theory.core.ISCAxiomaticTypeDefinition;
import org.eventb.theory.core.ISCConstructorArgument;
import org.eventb.theory.core.ISCDatatypeConstructor;
import org.eventb.theory.core.ISCDatatypeDefinition;
import org.eventb.theory.core.ISCNewOperatorDefinition;
import org.eventb.theory.core.ISCTheoryPathRoot;
import org.eventb.theory.core.ISCTypeArgument;
import org.eventb.theory.core.ITheoryPathRoot;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.util.CoreUtilities;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

/**
 * 
 * @author maamria
 * @author htson - Ensure that COND is added to all loaded formula factory.
 *
 */
public class TheoryFormulaExtensionProvider implements IFormulaExtensionProvider {

	private static final String PROVIDER_ID = TheoryPlugin.PLUGIN_ID
		+ ".theoryExtensionsProvider";
	private FormulaFactory factory;
	private ITypeEnvironmentBuilder typeEnvironment;
	
	@Override
	public String getId() {
		return PROVIDER_ID;
	}

	@Override
	public Set<IFormulaExtension> getFormulaExtensions(IEventBRoot root) {
		try {
			return WorkspaceExtensionsManager.getInstance()
					.getFormulaExtensions(root);
			// else ignore paths
		} catch (CoreException e) {
			CoreUtilities
					.log(e, "Error while computing math extensions for "
							+ root.getPath());
		}
		return FormulaExtensionsLoader.EMPTY_EXT;
	}

	/**
	 * Returns the checked theory path of the given project. We do not search
	 * directly the checked file, as it might not exist yet (e.g., just after a
	 * project clean), but rather the unchecked file that we convert eventually.
	 * 
	 * @param prj
	 *            some Rodin project
	 * @return the checked theory path of the given project
	 */
	private ISCTheoryPathRoot findSCTheoryPathRoot(IRodinProject prj) {
		final ITheoryPathRoot[] paths;
		try {
			paths = prj.getRootElementsOfType(ITheoryPathRoot.ELEMENT_TYPE);
		} catch (RodinDBException e) {
			log(e, "error while getting theory path for project " + prj);
			return null;
		}
		if (paths.length == 0) {
			return null;
		}
		if (paths.length > 1) {
			log(null,
					"Several theory paths in project " + prj + ": "
							+ Arrays.asList(paths));
			return null;
		}
		return paths[0].getSCTheoryPathRoot();
	}

	@Override
	public Set<IRodinFile> getFactoryFiles(IEventBRoot root) {
		final IRodinProject prj = root.getRodinProject();
		final ISCTheoryPathRoot pathRoot = findSCTheoryPathRoot(prj);
		if (pathRoot == null) {
			return emptySet();
		}
		return singleton(pathRoot.getRodinFile());
	}

	@Override
	public FormulaFactory loadFormulaFactory(ILanguage element,
			IProgressMonitor monitor) throws CoreException {
		// optimisation: if all of extensions of element is syntax and semantic same as the extensions of current ff (getFormulaExtensions), dont need to create new ff
		
		factory = FormulaFactory.getDefault();
 		// @htson: Add COND. This is to ensure that COND is added to all loaded formula factory,
		// since COND is not saved in the language.
		Set<IFormulaExtension> cond = new HashSet<IFormulaExtension>(1);
		cond.add(FormulaFactory.getCond());
		factory = factory.withExtensions(cond);

		List<IRodinElement> extsToLoad = new ArrayList<IRodinElement>(Arrays.asList(element.getChildren()));
		while (!extsToLoad.isEmpty()) {
			boolean progress = false;
			Iterator<IRodinElement> it = extsToLoad.iterator();
			while (it.hasNext()) {
				IRodinElement extensionElement = it.next();
				boolean success = false;
				typeEnvironment = factory.makeTypeEnvironment();
				if (extensionElement instanceof ISCDatatypeDefinition) {
					success = loadSCDatatypeDefinition((ISCDatatypeDefinition) extensionElement);
				} else if (extensionElement instanceof ISCAxiomaticTypeDefinition) {
					success = loadSCAxiomaticTypeDefinition((ISCAxiomaticTypeDefinition) extensionElement);
				} else if (extensionElement instanceof ISCNewOperatorDefinition) {
					success = loadSCNewOperatorDefinition((ISCNewOperatorDefinition) extensionElement);
				} else if (extensionElement instanceof ISCAxiomaticOperatorDefinition) {
					success = loadSCAxiomaticOperatorDefinition((ISCAxiomaticOperatorDefinition) extensionElement);
				} else {
					log(null, "Extension is not supported: " + extensionElement);
				}
				if (success) {
					it.remove();
					progress = true;
				}
			}
			if (!progress) {
				String msg = "Failed to load the formula factory from " + element + "(the following extensions failed: "
						+ extsToLoad + ")";
				log(null, msg);
				throw new CoreException(new Status(IStatus.ERROR, TheoryPlugin.PLUGIN_ID, IStatus.OK, msg, null));
			}
		}
		return factory;
	}


	private boolean loadSCDatatypeDefinition(ISCDatatypeDefinition extensionElement) throws CoreException {
		
		Set<IFormulaExtension> extensions = new LinkedHashSet<IFormulaExtension>();
		DatatypeTransformer transformer = new DatatypeTransformer();
		IDatatype datatype = transformer.transform(extensionElement, factory);
		
		if (datatype != null) {
			extensions.addAll(datatype.getExtensions());
			factory = factory.withExtensions(extensions);
			return true;
		}
		return false;
	}

	private boolean loadSCAxiomaticOperatorDefinition(ISCAxiomaticOperatorDefinition extensionElement) throws CoreException {

		Set<IFormulaExtension> extensions = new LinkedHashSet<IFormulaExtension>();
		AxiomaticOperatorTransformer trans = new AxiomaticOperatorTransformer();
		IOperatorExtension addedExtensions = trans.transform(extensionElement, factory, typeEnvironment);
		if (addedExtensions != null) {
			extensions.add(addedExtensions);
			factory = factory.withExtensions(extensions);
			return true;
		}
		return false;
	}

	private boolean loadSCAxiomaticTypeDefinition(ISCAxiomaticTypeDefinition extensionElement) throws CoreException {
		
		Set<IFormulaExtension> extensions = new LinkedHashSet<IFormulaExtension>();
		AxiomaticTypeTransformer trans = new AxiomaticTypeTransformer();
		IFormulaExtension addedExtensions = trans.transform(extensionElement, factory, typeEnvironment);
		if (addedExtensions != null) {
			extensions.add(addedExtensions);
			factory = factory.withExtensions(extensions);
			return true;
		}
		return false;
		
	}

	private boolean loadSCNewOperatorDefinition(ISCNewOperatorDefinition extensionElement) throws CoreException {
	
		Set<IFormulaExtension> extensions = new LinkedHashSet<IFormulaExtension>();
		OperatorTransformer transformer = new OperatorTransformer();
		ITypeEnvironmentBuilder localTypeEnvironment = typeEnvironment.makeBuilder();
		IOperatorExtension addedExtensions = transformer.transform(extensionElement, factory, localTypeEnvironment);
		if (addedExtensions != null) {
			extensions.add(addedExtensions);
			factory = factory.withExtensions(extensions);
			return true;
		}
		return false;
	}

	@Override
	public void saveFormulaFactory(ILanguage element, FormulaFactory factory,
			IProgressMonitor monitor) throws RodinDBException {
		
		final Set<IFormulaExtension> extensions = factory.getExtensions();
		// The order of the extensions' origins is important, but we do not want
		// any duplication.
		List<Object> extensionElements = new ArrayList<Object>();
		for (IFormulaExtension extension : extensions) {
			Object origin = extension.getOrigin();
			if (origin != null) {
				if (!extensionElements.contains(origin))
					extensionElements.add(origin);
			}
		}
		
		for (Object extensionElement : extensionElements) {
			if (extensionElement instanceof ISCNewOperatorDefinition) {
				ISCNewOperatorDefinition operator = (ISCNewOperatorDefinition) extensionElement;
				String name = operator.getLabel();
				operator.copy(element, null, name, false, monitor);
			}
			else if (extensionElement instanceof IAxiomaticTypeOrigin) {
				IAxiomaticTypeOrigin origin = (IAxiomaticTypeOrigin) extensionElement;
				makeSCAxiomaticTypeDefinition(element, origin, monitor);
//				((ISCAxiomaticTypeDefinition) extensionElement).copy(element, null, null, false, monitor);
			}
			else if (extensionElement instanceof ISCAxiomaticOperatorDefinition) {
				ISCAxiomaticOperatorDefinition operator = (ISCAxiomaticOperatorDefinition) extensionElement;
				String name = operator.getLabel();
				operator.copy(element, null, name, false, monitor);
				
			}
			else if (extensionElement instanceof IDatatype) {
				IDatatypeOrigin origin = (IDatatypeOrigin) ((IDatatype) extensionElement).getOrigin();
				makeSCDatatypeDefinition(element, origin, monitor);
			}
//			else {
//				log(null, "Extension is not supported: " + extensionElement);
//			}
		}
		
	}

	/**
	 * Utility method to create a statically checked axiomatic type definition
	 * from an axiomatic type origin. The new element is created as a child
	 * element of the input language.
	 * 
	 * @param element
	 *            the language element.
	 * @param origin
	 *            the axiomatic type origin.
	 * @param monitor
	 *            the progress monitor to use for reporting progress to the
	 *            user. It is the caller's responsibility to call done() on the
	 *            given monitor. Accepts <code>null</code>, indicating that no
	 *            progress should be reported and that the operation cannot be
	 *            cancelled.
	 * @throws RodinDBException
	 *             if some unexpected error occurs.
	 */
	private void makeSCAxiomaticTypeDefinition(ILanguage element,
			IAxiomaticTypeOrigin origin, IProgressMonitor monitor) throws RodinDBException {
		// Convert the progress monitor to 100%
		SubMonitor subMonitor = SubMonitor.convert(monitor, 100);
		
		// 1. (100%) Create the element
		String name = origin.getName();
		ISCAxiomaticTypeDefinition scAxiomaticTypeDefinition = element.getInternalElement(
				ISCAxiomaticTypeDefinition.ELEMENT_TYPE, name);
		scAxiomaticTypeDefinition.create(null, subMonitor.newChild(100));
	}

	/**
	 * Utility method to create a statically checked datatype definition from a
	 * datatype origin.
	 * 
	 * @param element
	 *            the language element.
	 * @param origin
	 *            the datatype origin.
	 * @param monitor
	 *            the progress monitor to use for reporting progress to the
	 *            user. It is the caller's responsibility to call done() on the
	 *            given monitor. Accepts <code>null</code>, indicating that no
	 *            progress should be reported and that the operation cannot be
	 *            cancelled.
	 * @throws RodinDBException
	 *             if some unexpected error occurs.
	 */
	private void makeSCDatatypeDefinition(ILanguage element,
			IDatatypeOrigin origin, IProgressMonitor monitor)
			throws RodinDBException {
		// Convert the progress monitor to 100%
		SubMonitor subMonitor = SubMonitor.convert(monitor, 100);
		
		// 1. (10%) Create the element
		String name = origin.getName();
		ISCDatatypeDefinition scDatatypeDefinition = element.getInternalElement(
				ISCDatatypeDefinition.ELEMENT_TYPE, name);
		scDatatypeDefinition.create(null, subMonitor.newChild(30));
		
		// 2. (30%) Create the type arguments.
		String[] typeArguments = origin.getTypeArguments();
		SubMonitor typeArgsMonitor = subMonitor.newChild(30).setWorkRemaining(
				typeArguments.length * 2);
		for (String typeArgument : typeArguments) {
			ISCTypeArgument scTypeArgument = scDatatypeDefinition
					.getInternalElement(ISCTypeArgument.ELEMENT_TYPE,
							typeArgument);
			scTypeArgument.create(null, typeArgsMonitor.newChild(1));
			Type type = origin.getGivenType(typeArgument);
			scTypeArgument.setSCGivenType(type, typeArgsMonitor.newChild(1));
		}
		
		// 3. (60%) Create the constructors.
		String[] constructors = origin.getConstructors();
		SubMonitor consMonitor = subMonitor.newChild(60).setWorkRemaining(
				constructors.length * 3);
		for (String constructor : constructors) {
			ISCDatatypeConstructor scDatatypeConstructor = scDatatypeDefinition
					.getInternalElement(ISCDatatypeConstructor.ELEMENT_TYPE,
							constructor);
			scDatatypeConstructor.create(null, consMonitor.newChild(1));
			String[] destructors = origin.getDestructors(constructor);
			Type[] destructorTypes = origin.getDestructorTypes(constructor);
			
			SubMonitor destMonitor = consMonitor.newChild(2).setWorkRemaining(
					destructors.length * 2);
			for (int i = 0; i != destructors.length; ++i) {
				String destructor = destructors[i];
				Type destructorType = destructorTypes[i];
				ISCConstructorArgument scDestructor = scDatatypeConstructor
						.getInternalElement(
								ISCConstructorArgument.ELEMENT_TYPE, destructor);
				scDestructor.create(null, destMonitor.newChild(1));
				scDestructor.setType(destructorType, destMonitor.newChild(1));
			}
		}
	}
	
}
