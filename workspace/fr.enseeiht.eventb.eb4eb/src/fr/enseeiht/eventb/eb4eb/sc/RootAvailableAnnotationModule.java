package fr.enseeiht.eventb.eb4eb.sc;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IMachineRoot;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;

import fr.enseeiht.eventb.eb4eb.EB4EBPlugin;

public class RootAvailableAnnotationModule extends AbstractAvailableAnnotationModule {

	public static final IModuleType<AbstractAvailableAnnotationModule> MODULE_TYPE = SCCore
			.getModuleType(EB4EBPlugin.PLUGIN_ID + ".rootAvailableAnnotationModule");

	@Override
	public void process(IRodinElement element, IInternalElement target, ISCStateRepository repository, IProgressMonitor monitor) throws CoreException {
		IRodinFile machineFile = (IRodinFile) element;
		super.process((IMachineRoot) machineFile.getRoot(), target, repository, monitor);
	}

	@Override
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

}
