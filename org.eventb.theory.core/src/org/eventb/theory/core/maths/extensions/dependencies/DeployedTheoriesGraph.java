/*******************************************************************************
 * Copyright (c) 2011, 2013 University of Southampton and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University of Southampton - initial API and implementation
 *     Systerel -  refactored after imports only concern deployed theories
 *******************************************************************************/
package org.eventb.theory.core.maths.extensions.dependencies;

import java.util.Collection;

import org.eclipse.core.runtime.CoreException;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.TheoryHierarchyHelper;

public class DeployedTheoriesGraph extends
		DependenciesGraph<IDeployedTheoryRoot> {

	@Override
	protected Collection<IDeployedTheoryRoot> getEdgesOut(
			IDeployedTheoryRoot element) throws CoreException {
		return TheoryHierarchyHelper.getImportedTheories(element);
	}

}
