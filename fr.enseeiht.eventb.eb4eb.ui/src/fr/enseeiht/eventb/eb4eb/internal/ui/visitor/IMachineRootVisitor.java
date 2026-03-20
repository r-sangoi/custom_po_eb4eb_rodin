package fr.enseeiht.eventb.eb4eb.internal.ui.visitor;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.ISCEvent;
import org.eventb.core.ISCInvariant;
import org.eventb.core.ISCMachineRoot;
import org.eventb.core.ISCVariable;
import org.eventb.core.ISCVariant;

import fr.enseeiht.eventb.eb4eb.internal.ui.builder.IBuilder;

public interface IMachineRootVisitor<Builder extends IBuilder> extends IVisitor<Builder> {
	
	public void visitMachineRoot(Builder builder, ISCMachineRoot machineRoot) throws CoreException;

	public void visitVariable(Builder builder, ISCVariable machineRoot) throws CoreException;

	public void visitInvariant(Builder builder, ISCInvariant machineRoot) throws CoreException;

	public void visitVariant(Builder builder, ISCVariant machineRoot) throws CoreException;

	public void visitEvent(Builder builder, ISCEvent machineRoot) throws CoreException;

}
