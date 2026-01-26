package fr.enseeiht.eventb.eb4eb.sc;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachineRoot;
import org.eventb.core.ISCMachineRoot;
import org.eventb.core.sc.SCProcessorModule;
import org.eventb.core.sc.state.ISCStateRepository;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProblem;

import fr.enseeiht.eventb.eb4eb.EB4EBAttributes;
import fr.enseeiht.eventb.eb4eb.EB4EBPlugin;
import fr.enseeiht.eventb.eb4eb.IAvailableAnnotation;
import fr.enseeiht.eventb.eb4eb.ISCAvailableAnnotation;

public abstract class AbstractAvailableAnnotationModule extends SCProcessorModule {

	public void process(IInternalElement element, IInternalElement target, ISCStateRepository repository, IProgressMonitor monitor) throws CoreException {
		
//		IRodinFile machineFile = (IRodinFile) element;
//		IMachineRoot machineRoot = (IMachineRoot) machineFile.getRoot();
//		
//		IInvariant[] invs = machineRoot.getInvariants();
//		
//		for (IInvariant inv : invs) {
//			IAvailableAnnotation[] availableAnnotations = inv.getChildrenOfType(IAvailableAnnotation.ELEMENT_TYPE);
//		}
//		
//		
//		
//		((ISCMachineRoot) target).getSCInvariant("");
		
		
		IAvailableAnnotation[] availableAnnotations = element.getChildrenOfType(IAvailableAnnotation.ELEMENT_TYPE);
		
		monitor.subTask("ProcessingAnnotations");
		initFilterModules(repository, monitor);

		int index = 0;
		for (IAvailableAnnotation availableAnnotation : availableAnnotations) {
			if(!availableAnnotation.hasAnnotation()) {
				createProblemMarker(element, EB4EBAttributes.AVAILABLE_ANNOTATION_ATTRIBUTE, new IRodinProblem() {
					@Override
					public int getSeverity() {
						return IMarker.SEVERITY_ERROR;
					}
					@Override
					public String getLocalizedMessage(Object[] args) {
						return "No annotation selected";
					}
					@Override
					public String getErrorCode() {
						return EB4EBPlugin.PLUGIN_ID + ".noAnnotationError";
					}
				});
			} else if(filterModules(element, repository, monitor)) {
				ISCAvailableAnnotation scAvailableAnnotation = target.getInternalElement(ISCAvailableAnnotation.ELEMENT_TYPE, "ANN"+index++);
				scAvailableAnnotation.create(null, monitor);
				scAvailableAnnotation.setAnnotation(availableAnnotation.getAnnotation(), monitor);
				scAvailableAnnotation.setSource(availableAnnotation, monitor);
			} else {
				createProblemMarker(element, EB4EBAttributes.AVAILABLE_ANNOTATION_ATTRIBUTE, new IRodinProblem() {
					@Override
					public int getSeverity() {
						return IMarker.SEVERITY_ERROR;
					}
					@Override
					public String getLocalizedMessage(Object[] args) {
						return "Filter Error"; //TODO
					}
					@Override
					public String getErrorCode() {
						return EB4EBPlugin.PLUGIN_ID + ".filterError";
					}
				});
			}
			monitor.worked(1);
		}
		
	}

}
