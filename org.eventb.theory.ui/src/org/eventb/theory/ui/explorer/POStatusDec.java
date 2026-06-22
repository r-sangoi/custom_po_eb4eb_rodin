/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.theory.ui.explorer;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eventb.core.IPSStatus;
import org.eventb.theory.core.IDatatypeDefinition;
import org.eventb.theory.core.ITypeParameter;
import org.eventb.theory.internal.ui.TheoryImage;
import org.eventb.theory.ui.explorer.model.TheoryModelController;
import org.eventb.ui.IEventBSharedImages;
import org.rodinp.core.IElementType;
import org.rodinp.core.IRodinElement;

import fr.systerel.explorer.IElementNode;
import fr.systerel.internal.explorer.model.IModelElement;
import fr.systerel.internal.explorer.statistics.IStatistics;

/**
 * @author Nicolas Beauger
 * 
 */
@SuppressWarnings("restriction")
public class POStatusDec implements ILightweightLabelDecorator {

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

	private static String getOverlayIcon(Object obj) {
		if (!hasStatistics(obj))
			return null;
		// Proof status doesn't need overlay icon
		if (obj instanceof IElementNode) {
			final IElementNode node = (IElementNode) obj;
			if (node.getChildrenType().equals(IPSStatus.ELEMENT_TYPE))
				return null;
		}

		final IModelElement model = TheoryModelController.getModelElement(obj);
		if (model == null)
			return null;

		final IStatistics s = new Statistics(model);
		if (s.getUndischargedRest() > 0) {
			return IEventBSharedImages.IMG_PENDING_OVERLAY_PATH;
		} else if (s.getReviewed() > 0) {
			return IEventBSharedImages.IMG_REVIEWED_OVERLAY_PATH;
		} else {
			return null;
		}
	}

	private static boolean hasStatistics(Object obj) {
		final IElementType<?> type = getElementType(obj);
		return type != ITypeParameter.ELEMENT_TYPE
				&& type != IDatatypeDefinition.ELEMENT_TYPE;
	}

	private static IElementType<?> getElementType(Object obj) {
		if (obj instanceof IRodinElement) {
			return ((IRodinElement) obj).getElementType();
		}
		if (obj instanceof IElementNode) {
			return ((IElementNode) obj).getChildrenType();
		}
		return null;
	}

	@Override
	public void decorate(Object element, IDecoration decoration) {
		final String ovrName = getOverlayIcon(element);
		if (ovrName == null)
			return;
		final ImageDescriptor overlay = TheoryImage.getImageDescriptor(ovrName);
		decoration.addOverlay(overlay, IDecoration.BOTTOM_RIGHT);
	}

}
