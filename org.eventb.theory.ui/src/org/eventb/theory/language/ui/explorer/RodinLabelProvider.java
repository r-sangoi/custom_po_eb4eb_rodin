/**
 * 
 */
package org.eventb.theory.language.ui.explorer;

import org.eclipse.core.resources.IContainer;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eventb.core.IIdentifierElement;
import org.eventb.core.ILabeledElement;
import org.eventb.theory.core.IAvailableTheory;
import org.eventb.theory.core.IAvailableTheoryProject;
import org.eventb.theory.core.ITheoryPathRoot;
import org.eventb.theory.internal.ui.TheoryImage;
import org.eventb.theory.internal.ui.TheoryUIUtils;
import org.eventb.theory.language.internal.ui.ITheoryPathImages;
import org.eventb.theory.ui.explorer.model.TheoryModelPOContainer;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

import fr.systerel.explorer.IElementNode;

/**
 * @author Renato Silva
 *
 */
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
//			if (element instanceof IPSStatus) {
//				IPSStatus status = ((IPSStatus) element);
//				if(status.exists())
//					return TheoryImage.getPRSequentImage(status);
//			}
			if (element instanceof ITheoryPathRoot){
				return TheoryUIUtils.getTheoryImage((ITheoryPathRoot) element);
			}
			if (element instanceof IRodinElement) {
				return TheoryImage.getRodinImage((IRodinElement) element);

			} else if (element instanceof IElementNode) {
				IElementNode node = (IElementNode) element;
				
				if(node.getChildrenType()==IAvailableTheoryProject.ELEMENT_TYPE){
					return TheoryImage.getImage(ITheoryPathImages.IMG_AVAILABLE_THEORY_PROJECT);
				}
				
				if(node.getChildrenType()==IAvailableTheory.ELEMENT_TYPE){
					return TheoryImage.getImage(ITheoryPathImages.IMG_AVAILABLE_THEORY);
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
					TheoryUIUtils.log(e, "when getting label for " + obj);
				}
			} else if (obj instanceof IIdentifierElement) {
				try {
					return ((IIdentifierElement) obj).getIdentifierString();
				} catch (RodinDBException e) {
					TheoryUIUtils.log(e, "when getting identifier for " + obj);
				}
			} else if (obj instanceof IAvailableTheoryProject){
				try {
					return ((IAvailableTheoryProject)obj).getTheoryProject().getElementName();
				} catch (RodinDBException e) {
					TheoryUIUtils.log(e, "when getting IAvailableTheoryProject for " + obj);
				}
			}
			else if (obj instanceof IRodinElement) {
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
