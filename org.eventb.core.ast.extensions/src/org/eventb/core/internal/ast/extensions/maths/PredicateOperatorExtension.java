/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.internal.ast.extensions.maths;

import org.eventb.core.ast.ExtendedPredicate;
import org.eventb.core.ast.extension.IPredicateExtension;
import org.eventb.core.ast.extension.ITypeCheckMediator;
import org.eventb.core.ast.extensions.maths.Definition;
import org.eventb.core.ast.extensions.maths.OperatorExtensionProperties;

/**
 * An implementation of a predicate operator extension.
 * @since 1.0
 * 
 * @author maamria
 * 
 */
public class PredicateOperatorExtension extends OperatorExtension
		implements IPredicateExtension {
	
	public PredicateOperatorExtension(OperatorExtensionProperties properties,
			boolean isCommutative, OperatorTypingRule typingRule, Definition definition,
			Object source){
		
		super(properties, isCommutative, false, typingRule, definition, source);
	}

	@Override
	public void typeCheck(ExtendedPredicate predicate,
			ITypeCheckMediator tcMediator) {
		((PredicateOperatorTypingRule)operatorTypingRule).typeCheck(predicate, tcMediator);
	}
}
