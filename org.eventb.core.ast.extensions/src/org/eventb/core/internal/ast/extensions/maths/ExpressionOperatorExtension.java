/*******************************************************************************
 * Copyright (c) 2010, 2014 University of Southampton and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University of Southampton - initial API and implementation
 *     Systerel - setup infix expression operator group
 *******************************************************************************/
package org.eventb.core.internal.ast.extensions.maths;

import static org.eventb.core.ast.extensions.maths.AstUtilities.INFIX_EXTENDED_EXPRESSION_OPERATOR_GROUP;
import static org.eventb.core.internal.ast.extensions.AstExtensionsPlugin.log;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.CycleError;
import org.eventb.core.ast.extension.ExtensionFactory;
import org.eventb.core.ast.extension.ICompatibilityMediator;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.core.ast.extension.IExtensionKind;
import org.eventb.core.ast.extension.IPriorityMediator;
import org.eventb.core.ast.extension.StandardGroup;
import org.eventb.core.ast.extension.IOperatorProperties.Notation;
import org.eventb.core.ast.extensions.maths.Definition;
import org.eventb.core.ast.extensions.maths.OperatorExtensionProperties;
import org.eventb.core.ast.extension.ITypeCheckMediator;
import org.eventb.core.ast.extension.ITypeMediator;
import org.eventb.internal.core.ast.extension.ExtensionKind;

/**
 * An implementation of an expression operator extension.
 * @since 1.0
 * @author maamria
 * 
 */
@SuppressWarnings("restriction")
public class ExpressionOperatorExtension extends OperatorExtension
		implements IExpressionExtension {

	public ExpressionOperatorExtension(OperatorExtensionProperties properties,
			boolean isCommutative, boolean isAssociative,
			OperatorTypingRule typingRule, Definition definition,
			Object source) {

		super(properties, isCommutative, isAssociative, typingRule, definition, source);
	}

	@Override
	public IExtensionKind getKind() {
		if(properties.getNotation().equals(Notation.INFIX) && isAssociative){
			return new ExtensionKind(properties.getNotation(), properties.getFormulaType(), 
					ExtensionFactory.TWO_OR_MORE_EXPRS, true);
		}
		return super.getKind();

	}

	@Override
	public void addCompatibilities(ICompatibilityMediator mediator) {
		if (isAssociative)
			mediator.addAssociativity(getId());
	}

	@Override
	public void addPriorities(IPriorityMediator mediator) {
		super.addPriorities(mediator);
		if (getGroupId().equals(INFIX_EXTENDED_EXPRESSION_OPERATOR_GROUP)) {
			try {
				mediator.addGroupPriority(StandardGroup.PAIR.getId(),
						INFIX_EXTENDED_EXPRESSION_OPERATOR_GROUP);
				mediator.addGroupPriority(
						INFIX_EXTENDED_EXPRESSION_OPERATOR_GROUP,
						StandardGroup.FUNCTIONAL.getId());
			} catch (CycleError e) {
				log(e, "While setting priorities for operator group: "
						+ INFIX_EXTENDED_EXPRESSION_OPERATOR_GROUP);
			}
		}
	}
	
	@Override
	public boolean verifyType(Type proposedType, Expression[] childExprs,
			Predicate[] childPreds) {
		return ((ExpressionOperatorTypingRule) operatorTypingRule).verifyType(
				proposedType, childExprs, childPreds);
	}

	@Override
	public Type typeCheck(ExtendedExpression expression,
			ITypeCheckMediator tcMediator) {
		return ((ExpressionOperatorTypingRule) operatorTypingRule).typeCheck(
				expression, tcMediator);
	}

	@Override
	public Type synthesizeType(Expression[] childExprs, Predicate[] childPreds,
			ITypeMediator mediator) {
		return ((ExpressionOperatorTypingRule) operatorTypingRule)
				.synthesizeType(childExprs, childPreds, mediator);
	}

	@Override
	public boolean isATypeConstructor() {
		return false;
	}
	
}
