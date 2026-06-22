/*******************************************************************************
 * Copyright (c) 2008, 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.theory.ui.explorer;

import org.eclipse.core.resources.IContainer;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eventb.core.IIdentifierElement;
import org.eventb.core.ILabeledElement;
import org.eventb.core.IPSStatus;
import org.eventb.core.seqprover.IConfidence;
import org.eventb.internal.ui.UIUtils;
import org.eventb.theory.core.IAxiomaticDefinitionAxiom;
import org.eventb.theory.core.IAxiomaticDefinitionsBlock;
import org.eventb.theory.core.IAxiomaticOperatorDefinition;
import org.eventb.theory.core.IAxiomaticTypeDefinition;
import org.eventb.theory.core.IDatatypeDefinition;
import org.eventb.theory.core.INewOperatorDefinition;
import org.eventb.theory.core.IProofRulesBlock;
import org.eventb.theory.core.ITheorem;
import org.eventb.theory.core.ITheoryRoot;
import org.eventb.theory.core.ITypeParameter;
import org.eventb.theory.internal.ui.ITheoryImages;
import org.eventb.theory.internal.ui.TheoryImage;
import org.eventb.theory.internal.ui.TheoryUIUtils;
import org.eventb.theory.ui.explorer.model.TheoryModelElementNode;
import org.eventb.theory.ui.explorer.model.TheoryModelPOContainer;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

import fr.systerel.explorer.IElementNode;

/**
 * This class provides labels to all <code>ContentProvider</code> classes.
 */
@SuppressWarnings("restriction")
public class RodinLabelProvider extends DecoratingLabelProvider {


	public RodinLabelProvider() {
		super(new LblProv(), PlatformUI.getWorkbench().getDecoratorManager()
				.getLabelDecorator());
	}

	private static class LblProv implements ILabelProvider {

		public LblProv() {
			// avoid synthetic accessor
		}

		@Override
		public Image getImage(Object element) {
			if (element instanceof IPSStatus) {
				IPSStatus status = ((IPSStatus) element);
				if(status.exists())
					return TheoryImage.getPRSequentImage(status);
			}
			if (element instanceof ITheoryRoot){
				return TheoryUIUtils.getTheoryImage((ITheoryRoot) element);
			}
			if (element instanceof IRodinElement) {
				return TheoryImage.getRodinImage((IRodinElement) element);

			} else if (element instanceof IElementNode) {
				IElementNode node = (IElementNode) element;
				
				if(node.getChildrenType()==ITypeParameter.ELEMENT_TYPE){
					return TheoryImage.getImage(ITheoryImages.IMG_TYPEPAR);
				}
				
				if(node.getChildrenType()==IDatatypeDefinition.ELEMENT_TYPE){
					return TheoryImage.getImage(ITheoryImages.IMG_DATATYPE);
				}
				if (node.getChildrenType() == IAxiomaticDefinitionsBlock.ELEMENT_TYPE){
					return TheoryImage.getImage(ITheoryImages.IMG_RULES_BLOCK);
				}
				if(node.getChildrenType()==INewOperatorDefinition.ELEMENT_TYPE){
					return TheoryImage.getImage(ITheoryImages.IMG_OPERATOR);
				}
				if(node.getChildrenType() == IProofRulesBlock.ELEMENT_TYPE)
					return TheoryImage.getImage(ITheoryImages.IMG_RULES_BLOCK);
				
				if(node.getChildrenType() == ITheorem.ELEMENT_TYPE)
					return TheoryImage.getImage(ITheoryImages.IMG_TTHEOREM);
				
				if(node.getChildrenType() == IAxiomaticOperatorDefinition.ELEMENT_TYPE)
					return TheoryImage.getImage(ITheoryImages.IMG_OPERATOR);
				
				if(node.getChildrenType() == IAxiomaticDefinitionAxiom.ELEMENT_TYPE)
					return TheoryImage.getImage(ITheoryImages.IMG_TTHEOREM);
				
				if(node.getChildrenType() == IAxiomaticTypeDefinition.ELEMENT_TYPE)
					return TheoryImage.getImage(ITheoryImages.IMG_DATATYPE);
				
				if (node.getChildrenType()==IPSStatus.ELEMENT_TYPE) {
					if(node.getParent().getElementType()!=ITheoryRoot.ELEMENT_TYPE)
						return null;
					TheoryModelPOContainer parent = ((TheoryModelElementNode) node)
							.getModelParent();
					boolean discharged = parent.getMinConfidence() > IConfidence.REVIEWED_MAX;
					boolean reviewed = parent.getMinConfidence() > IConfidence.PENDING;
					boolean unattempted = parent.getMinConfidence() == IConfidence.UNATTEMPTED;

					if (discharged) {
						return TheoryImage
								.getImage(ITheoryImages.IMG_DISCHARGED);
					} else if (reviewed) {
						return TheoryImage
								.getImage(ITheoryImages.IMG_REVIEWED);
					} else if (unattempted) {
						return TheoryImage
								.getImage(ITheoryImages.IMG_PENDING_PALE);
					} else {
						return TheoryImage
								.getImage(ITheoryImages.IMG_PENDING);
					}
				}

			} else if (element instanceof IContainer) {
				return PlatformUI.getWorkbench().getSharedImages().getImage(
						ISharedImages.IMG_OBJS_INFO_TSK);
			}
			return null;
		}

		@Override
		public String getText(Object obj) {
			if (obj instanceof ILabeledElement) {
				try {
					return ((ILabeledElement) obj).getLabel();
				} catch (RodinDBException e) {
					UIUtils.log(e, "when getting label for " + obj);
				}
			} else if (obj instanceof IIdentifierElement) {
				try {
					return ((IIdentifierElement) obj).getIdentifierString();
				} catch (RodinDBException e) {
					UIUtils.log(e, "when getting identifier for " + obj);
				}
			} else if (obj instanceof IRodinElement) {
				return ((IRodinElement) obj).getElementName();

			} else if (obj instanceof TheoryModelPOContainer) {
				return TheoryModelPOContainer.DISPLAY_NAME;

			} else if (obj instanceof IElementNode) {
				return ((IElementNode) obj).getLabel();

			} else if (obj instanceof IContainer) {
				return ((IContainer) obj).getName();
			}
			return obj.toString();
		}

		@Override
		public void addListener(ILabelProviderListener listener) {
			// do nothing

		}

		@Override
		public void dispose() {
			// do nothing

		}

		@Override
		public boolean isLabelProperty(Object element, String property) {
			return false;
		}

		@Override
		public void removeListener(ILabelProviderListener listener) {
			// do nothing

		}
	}

}
