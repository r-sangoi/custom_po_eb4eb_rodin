package fr.enseeiht.eventb.eb4eb.internal.ui.acceptor;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.ISCEvent;
import org.eventb.core.ISCInvariant;
import org.eventb.core.ISCMachineRoot;
import org.eventb.core.ISCVariable;
import org.eventb.core.ISCVariant;

import fr.enseeiht.eventb.eb4eb.internal.ui.builder.IBuilder;
import fr.enseeiht.eventb.eb4eb.internal.ui.visitor.IMachineRootVisitor;

public class MachineRootAcceptor<Builder extends IBuilder> implements IAcceptor<Builder, IMachineRootVisitor<Builder>> {
	
	ISCMachineRoot mch;
	
	public MachineRootAcceptor(ISCMachineRoot mch) {
		this.mch = mch;
	}
	
	@Override
	public void accept(IMachineRootVisitor<Builder> visitor, Builder builder) throws CoreException {
		visitor.visitMachineRoot(builder, this.mch);
		for (ISCVariable var : this.mch.getSCVariables()) {
			visitor.visitVariable(builder, var);
		}
		for (ISCInvariant inv : this.mch.getSCInvariants()) {
			visitor.visitInvariant(builder, inv);
		}
		ISCVariant[] variants = this.mch.getSCVariants();
		if(variants.length > 1) {
			//TODO
		}
		if(variants.length == 1)
			visitor.visitVariant(builder, variants[0]);
		for (ISCEvent ev : this.mch.getSCEvents()) {
			visitor.visitEvent(builder, ev);
		}
	}
}
