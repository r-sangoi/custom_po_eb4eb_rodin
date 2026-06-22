/**
 * 
 */
package org.eventb.theory.language.core.sc;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.sc.StaticChecker;
import org.eventb.theory.core.IAvailableTheory;
import org.eventb.theory.core.IAvailableTheoryProject;
import org.eventb.theory.core.ISCTheoryPathRoot;
import org.eventb.theory.core.ITheoryPathRoot;
import org.eventb.theory.core.sc.Messages;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinCore;
import org.rodinp.core.builder.IGraph;

/**
 * @author RenatoSilva
 *
 */
public class TheoryPathStaticChecker extends StaticChecker{

	public void extract(IFile file, IGraph graph, IProgressMonitor monitor) throws CoreException {
		try {
			monitor.beginTask(Messages.bind(Messages.build_extracting, file.getName()), 1);
			IRodinFile source = RodinCore.valueOf(file);
			ITheoryPathRoot root = (ITheoryPathRoot) source.getRoot();
			final ISCTheoryPathRoot targetRoot = root.getSCTheoryPathRoot();
			IRodinFile target = targetRoot.getRodinFile();

			graph.addTarget(target.getResource());
			graph.addToolDependency(source.getResource(), target.getResource(), true);

			IAvailableTheoryProject[] availableTheoryProjects = root.getAvailableTheoryProjects();
			for(IAvailableTheoryProject theoryProj: availableTheoryProjects){
				List<IRodinFile> files = new ArrayList<IRodinFile>();
				for(IAvailableTheory availableTheory: theoryProj.getTheories()){
					if(availableTheory.hasAvailableTheory()){
						IRodinFile rodinFile = availableTheory.getDeployedTheory().getRodinFile();
						files.add(rodinFile);
						graph.addUserDependency(
								source.getResource(),
								rodinFile.getResource(),
								target.getResource(), false);
					}
				}

				// From Rodin 2.8 when the dependency is correctly added to the builder, no listener is needed.
				//DatabaseUtilitiesTheoryPath.addListener(files,root);
			}

		} finally {
			monitor.done();
		}
	}
}
