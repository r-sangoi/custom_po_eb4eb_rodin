/*******************************************************************************
 * Copyright (c) 2011, 2020 University of Southampton and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.ui.explorer;

import java.text.Collator;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eventb.core.IPSStatus;
import org.eventb.theory.core.IAxiomaticDefinitionAxiom;
import org.eventb.theory.core.IAxiomaticDefinitionsBlock;
import org.eventb.theory.core.IAxiomaticOperatorDefinition;
import org.eventb.theory.core.IAxiomaticTypeDefinition;
import org.eventb.theory.core.IDatatypeConstructor;
import org.eventb.theory.core.IDatatypeDefinition;
import org.eventb.theory.core.IInferenceRule;
import org.eventb.theory.core.INewOperatorDefinition;
import org.eventb.theory.core.IProofRulesBlock;
import org.eventb.theory.core.IRewriteRule;
import org.eventb.theory.core.ITheorem;
import org.eventb.theory.core.ITheoryRoot;
import org.eventb.theory.core.ITypeParameter;
import org.eventb.theory.ui.explorer.model.ModelTheory;
import org.eventb.theory.ui.explorer.model.TheoryModelController;
import org.rodinp.core.IInternalElementType;

import fr.systerel.explorer.IElementNode;
import fr.systerel.internal.explorer.model.IModelElement;

/**
 * Grouper class for content providers.
 * 
 * @author im06r
 * 
 */
@SuppressWarnings("restriction")
public class TheoryChildrenContentProviders {
	
	public static class AntiSorter extends ViewerComparator {

		public AntiSorter() {
		}

		public AntiSorter(Collator collator) {
			super(collator);
		}

		@Override
		public int compare(Viewer viewer, Object e1, Object e2) {
			return 0;
		}

	}

	public static class POContentProvider extends AbstractContentProvider {

		public POContentProvider() {
			super(IPSStatus.ELEMENT_TYPE);
		}
	}
	
	public static class DatatypeConstructorContentProvider extends AbstractContentProvider {

		public DatatypeConstructorContentProvider() {
			super(IDatatypeConstructor.ELEMENT_TYPE);
		}
	}

	public static class DatatypeContentProvider extends AbstractContentProvider {

		public DatatypeContentProvider() {
			super(IDatatypeDefinition.ELEMENT_TYPE);
		}

		@Override
		public Object getParent(Object element) {

			if (element instanceof IDatatypeDefinition) {
				IDatatypeDefinition carr = (IDatatypeDefinition) element;
				ITheoryRoot root = (ITheoryRoot) carr.getRoot();
				ModelTheory thy = TheoryModelController.getTheory(root);
				if (thy != null) {
					return thy.datatype_node;
				}
			}
			if (element instanceof IElementNode) {
				return ((IElementNode) element).getParent();
			}
			return null;
		} 
	}

	public static class InferenceRuleContentProvider extends AbstractContentProvider {

		public InferenceRuleContentProvider() {
			super(IInferenceRule.ELEMENT_TYPE);
		}
	}

	public static class AxiomaticBlockContentProvider extends AbstractContentProvider {

		public AxiomaticBlockContentProvider() {
			super(IAxiomaticDefinitionsBlock.ELEMENT_TYPE);
		}

		@Override
		public Object getParent(Object element) {

			if (element instanceof IAxiomaticDefinitionsBlock) {
				IAxiomaticDefinitionsBlock carr = (IAxiomaticDefinitionsBlock) element;
				ITheoryRoot root = (ITheoryRoot) carr.getRoot();
				ModelTheory thy = TheoryModelController.getTheory(root);
				if (thy != null) {
					return thy.axb_node;
				}
			}
			if (element instanceof IElementNode) {
				return ((IElementNode) element).getParent();
			}
			return null;
		}

	}
	
	public static class OperatorContentProvider extends AbstractContentProvider {

		public OperatorContentProvider() {
			super(INewOperatorDefinition.ELEMENT_TYPE);
		}

		@Override
		public Object getParent(Object element) {

			if (element instanceof INewOperatorDefinition) {
				INewOperatorDefinition carr = (INewOperatorDefinition) element;
				ITheoryRoot root = (ITheoryRoot) carr.getRoot();
				ModelTheory thy = TheoryModelController.getTheory(root);
				if (thy != null) {
					return thy.op_node;
				}
			}
			if (element instanceof IElementNode) {
				return ((IElementNode) element).getParent();
			}
			return null;
		}

	}
	
	public static class RewriteRuleContentProvider extends AbstractContentProvider {

		public RewriteRuleContentProvider() {
			super(IRewriteRule.ELEMENT_TYPE);
		}
	}
	
	public static class AxiomaticOperatorContentProvider extends AbstractContentProvider{

		public AxiomaticOperatorContentProvider() {
			super(IAxiomaticOperatorDefinition.ELEMENT_TYPE);
		}
	}
	
	public static class AxiomaticAxiomContentProvider extends AbstractContentProvider{
		
		public AxiomaticAxiomContentProvider() {
			super(IAxiomaticDefinitionAxiom.ELEMENT_TYPE);
		}
	}
	
	public static class AxiomaticTypeContentProvider extends AbstractContentProvider{
		
		public AxiomaticTypeContentProvider() {
			super(IAxiomaticTypeDefinition.ELEMENT_TYPE);
		}
		
	}
	
	public static class RuleBlockContentProvider extends AbstractContentProvider {

		public RuleBlockContentProvider() {
			super(IProofRulesBlock.ELEMENT_TYPE);
		}

		@Override
		public Object getParent(Object element) {

			if (element instanceof IProofRulesBlock) {
				IProofRulesBlock carr = (IProofRulesBlock) element;
				ITheoryRoot root = (ITheoryRoot) carr.getRoot();
				ModelTheory thy = TheoryModelController.getTheory(root);
				if (thy != null) {
					return thy.pb_node;
				}
			}
			if (element instanceof IElementNode) {
				return ((IElementNode) element).getParent();
			}
			return null;
		}
	}
	
	public static class TheoremContentProvider extends AbstractContentProvider {

		public TheoremContentProvider() {
			super(ITheorem.ELEMENT_TYPE);
		}

		@Override
		public Object getParent(Object element) {

			if (element instanceof ITheorem) {
				ITheorem carr = (ITheorem) element;
				ITheoryRoot root = (ITheoryRoot) carr.getRoot();
				ModelTheory thy = TheoryModelController.getTheory(root);
				if (thy != null) {
					return thy.thm_node;
				}
			}
			if (element instanceof IElementNode) {
				return ((IElementNode) element).getParent();
			}
			return null;
		}
	}
	
	public static class TypeParameterContentProvider extends AbstractContentProvider {

		public TypeParameterContentProvider() {
			super(ITypeParameter.ELEMENT_TYPE);
		}

		@Override
		public Object getParent(Object element) {
			if (element instanceof ITypeParameter) {
				ITypeParameter carr = (ITypeParameter) element;
				ITheoryRoot root = (ITheoryRoot) carr.getRoot();
				ModelTheory thy = TheoryModelController.getTheory(root);
				if (thy != null) {
					return thy.typepar_node;
				}
			}
			if (element instanceof IElementNode) {
				return ((IElementNode) element).getParent();
			}
			return null;
		}
	}
}

@SuppressWarnings("restriction")
abstract class AbstractContentProvider implements ITreeContentProvider {

	protected static final Object[] NO_OBJECT = new Object[0];
	
	protected final IInternalElementType<?> type;

	public AbstractContentProvider(IInternalElementType<?> type) {
		this.type = type;
	}
	
	@Override
	public Object[] getChildren(Object element) {
		IModelElement model = TheoryModelController.getModelElement(element);
		if (model != null) {
			return model.getChildren(type, false);
		}
		return NO_OBJECT;
	}

	@Override
	public boolean hasChildren(Object element) {
		return getChildren(element).length > 0;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}
	
	/**
	 * Override this to change the parent behaviour.
	 */
	@Override
	public Object getParent(Object element) {

		IModelElement model = TheoryModelController.getModelElement(element);
		if (model != null) {
			return model.getParent(true);
		}
		return null;
	}

	@Override
	public void dispose() {
		// ignore
	
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// ignore
	}

}
