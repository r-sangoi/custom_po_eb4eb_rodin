/*******************************************************************************
 * Copyright (c) 2011, 2020 University of Southampton and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.internal.core.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.util.CoreUtilities;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;

/**
 * @author renatosilva
 *
 */
public class DeployElementRegistry{
	
	private final static String DEPLOYED_ELEMENTS_ID = TheoryPlugin.PLUGIN_ID + ".deployedElements";
	private static DeployElementRegistry SINGLETON_INSTANCE;
	public static boolean DEBUG;

	private List<IInternalElementType<IInternalElement>> rootDeployedElements;
	private Map<IInternalElementType<IInternalElement>, List<IInternalElementType<IInternalElement>>> deployedElements;
	private final String ATTRIBUTE_TYPE_ID = "typeId";
	private final String ATTRIBUTE_CHILD_TYPE_ID = "childTypeId";

	/**
	 * Private default constructor enforces that only one instance of this class
	 * is present.
	 */
	private DeployElementRegistry() {
		// Singleton implementation
		if (deployedElements == null) {
			loadDeployedElements();
		}
	}

	public static DeployElementRegistry getDeployedElementsRegistry() {
		if(SINGLETON_INSTANCE == null){
			SINGLETON_INSTANCE = new DeployElementRegistry();
		}
		return SINGLETON_INSTANCE;
	}

	/**
	 * Initialises the provider using extensions to the formula extension
	 * provider extension point. It shall be only one extension provider.
	 */
	private synchronized void loadDeployedElements() {
		if (deployedElements != null) {
			// Prevents loading by two thread in parallel
			return;
		}
		rootDeployedElements = new ArrayList<IInternalElementType<IInternalElement>>();
		deployedElements = new LinkedHashMap<IInternalElementType<IInternalElement>, List<IInternalElementType<IInternalElement>>>();
		final IExtensionRegistry xRegistry = Platform.getExtensionRegistry();
		final IExtensionPoint xPoint = xRegistry.getExtensionPoint(DEPLOYED_ELEMENTS_ID);
		for (IConfigurationElement element : xPoint.getConfigurationElements()) {
			String attribute = element.getAttribute(ATTRIBUTE_TYPE_ID);
			IInternalElementType<IInternalElement> elementType = RodinCore.getInternalElementType(attribute);
			if(null!=elementType){
				rootDeployedElements.add(elementType);
				if(element.getChildren().length>0)
					loadDeployedElementsChildren((IInternalElementType<IInternalElement>)elementType, element);
			}
			else{
				CoreUtilities.log(null, "TypeId "+ attribute + " not found or not declared yet when reading DeployElementRegistry");
			}
		}
	}
	
	private synchronized void loadDeployedElementsChildren(IInternalElementType<IInternalElement> parentElementType, IConfigurationElement element){
		IConfigurationElement[] children = element.getChildren();
		List<IInternalElementType<IInternalElement>> childrenType = new ArrayList<IInternalElementType<IInternalElement>>();
		for (IConfigurationElement child : children) {
			String attribute = child.getAttribute(ATTRIBUTE_TYPE_ID);
			if(null!=attribute){
				IInternalElementType<IInternalElement> elementType = RodinCore.getInternalElementType(attribute);
				if(null!=elementType){
					childrenType.add(elementType);
					loadDeployedElementsChildren(elementType, child);
				}
			} else{
				attribute = child.getAttribute(ATTRIBUTE_CHILD_TYPE_ID);
				IInternalElementType<IInternalElement> childElementType = RodinCore.getInternalElementType(attribute);
				if(null!=childElementType){
					childrenType.add(childElementType);
				}
				else{
					CoreUtilities.log(null, "TypeId/ChildTypeId "+ attribute + " not found or not declared yet when reading DeployElementRegistry");
				}
			}
		}
		deployedElements.put(parentElementType,childrenType);
	}
	
	public List<IInternalElementType<IInternalElement>> getRootDeployedElements(){
		return rootDeployedElements;
	}
	
	public List<IInternalElementType<IInternalElement>> getDeployedElementChildren(IInternalElementType<?> elementType){
		List<IInternalElementType<IInternalElement>> children = new ArrayList<IInternalElementType<IInternalElement>>();
		if(deployedElements.containsKey(elementType))
			children = deployedElements.get(elementType);
		return children;
	}
	
	public boolean hasDeployedElementChildren(IInternalElementType<?> elementType){
		return deployedElements.containsKey(elementType);
	}
	
	public List<IInternalElementType<IInternalElement>> getDeployedElement(IInternalElementType<?> elementType){
		List<IInternalElementType<IInternalElement>> children = new ArrayList<IInternalElementType<IInternalElement>>();
		if(deployedElements.containsKey(elementType))
			children = deployedElements.get(elementType);
		return children;
	}
}
