package org.eventb.theory.core.pog.modules;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.pog.POGCore;
import org.eventb.core.pog.state.IPOGStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.internal.core.pog.modules.BaseModule;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.rodinp.core.IRodinElement;

/**
 * 
 * @author maamria
 *
 */
@SuppressWarnings("restriction")
public class TheoryPOGModule extends BaseModule {

	private final IModuleType<TheoryPOGModule> MODULE_TYPE = 
		POGCore.getModuleType(TheoryPlugin.PLUGIN_ID + ".theoryPOGModule"); 
	
	@Override
	public void process(
			IRodinElement element, 
			IPOGStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		processModules(element, repository, monitor);
	}
	
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

}
