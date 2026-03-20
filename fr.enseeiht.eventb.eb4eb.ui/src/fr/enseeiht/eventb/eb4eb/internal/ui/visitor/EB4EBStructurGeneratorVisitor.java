package fr.enseeiht.eventb.eb4eb.internal.ui.visitor;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IEvent;
import org.eventb.core.IMachineRoot;
import org.eventb.core.ISCAction;
import org.eventb.core.ISCEvent;
import org.eventb.core.ISCGuard;
import org.eventb.core.ISCInvariant;
import org.eventb.core.ISCMachineRoot;
import org.eventb.core.ISCVariable;
import org.eventb.core.ISCVariant;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.rodinp.core.RodinDBException;

import fr.enseeiht.eventb.eb4eb.ISCAvailableAnnotation;
import fr.enseeiht.eventb.eb4eb.internal.ui.builder.EB4EBContextBuilder;
import fr.enseeiht.eventb.eb4eb.internal.ui.callbacks.ContextCallback;

public class EB4EBStructurGeneratorVisitor implements IMachineRootVisitor<EB4EBContextBuilder> {
	
	@Override
	public void visitMachineRoot(EB4EBContextBuilder builder, ISCMachineRoot machineRoot) throws CoreException {
		builder.translate(machineRoot.getTypeEnvironment().getFormulaFactory());
		// TODO : edit
//		builder.addCallbacks(new ContextCallback());
//		builder.addCallbacks(new ContextCallback());
//		builder.addCallbacks(new ContextCallback());
		ISCAvailableAnnotation[] avAnnotations = machineRoot.getChildrenOfType(ISCAvailableAnnotation.ELEMENT_TYPE);
		for (ISCAvailableAnnotation avAnnotation : avAnnotations) {
			System.out.println("avAnnotation : " + avAnnotation.getAnnotation().getIdentifierString());
			builder.addCallbacks(new ContextCallback(avAnnotation.getAnnotation()));
		}
	}

	@Override
	public void visitVariable(EB4EBContextBuilder builder, ISCVariable var) throws CoreException {
		FormulaFactory factory = ((ISCMachineRoot) var.getRoot()).getTypeEnvironment().getFormulaFactory();
		builder.addVariableIdentifier(var.getIdentifier(factory));
		builder.addVariableType(var.getType(factory));
	}

	@Override
	public void visitInvariant(EB4EBContextBuilder builder, ISCInvariant inv) throws CoreException {
		ITypeEnvironment env = ((ISCMachineRoot) inv.getRoot()).getTypeEnvironment();
		if(inv.isTheorem())
			builder.addTheoremPredicate(inv.getPredicate(env));
		else
			builder.addInvariantPredicate(inv.getPredicate(env));
		
		ISCAvailableAnnotation[] avAnnotations = inv.getChildrenOfType(ISCAvailableAnnotation.ELEMENT_TYPE);
		for (ISCAvailableAnnotation avAnnotation : avAnnotations) {
			System.out.println("avAnnotation : " + avAnnotation.getAnnotation().getIdentifierString());
		}
	}

	@Override
	public void visitVariant(EB4EBContextBuilder builder, ISCVariant variant) throws CoreException {
		ITypeEnvironment env = ((ISCMachineRoot) variant.getRoot()).getTypeEnvironment();
		builder.setVariant(variant.getExpression(env));
	}

	@Override
	public void visitEvent(EB4EBContextBuilder builder, ISCEvent event) throws CoreException {
		ITypeEnvironment env = ((ISCMachineRoot) event.getRoot()).getTypeEnvironment();
		builder.addEventName(event.getLabel());
		if(isInitialisation(event)) {
			builder.setInitEvent(event.getLabel());
			for (ISCAction action : event.getSCActions()) {
				builder.addInitAction(action.getAssignment(env));
			}
		} else {
			builder.addProgressEvent(event.getLabel());
			for (ISCGuard guard : event.getSCGuards()) {
				builder.addEventGuard(event.getLabel(), guard.getPredicate(env));
			}
			for (ISCAction action : event.getSCActions()) {
				builder.addEventBAP(event.getLabel(), action.getAssignment(env));
			}
			builder.fillUnassignedBAP(event.getLabel());
		}
		switch (event.getConvergence()) {
		case ANTICIPATED:
			builder.addAnticipatedEvent(event.getLabel());
			break;
		case CONVERGENT:
			builder.addConvergentEvent(event.getLabel());
			break;
		case ORDINARY:
			builder.addOrdinaryEvent(event.getLabel());
			break;
		}
	}
	
	private static boolean isInitialisation(ISCEvent scEv) throws RodinDBException {
		IEvent[] nonSCEvents = ((IMachineRoot) EventBPlugin.asMachineFile(scEv.getRodinFile()).getRoot()).getChildrenOfType(IEvent.ELEMENT_TYPE);
		IEvent ev = null;
		int i = 0;
		while(ev==null) {
			if(nonSCEvents[i].getLabel().equals(scEv.getLabel()))
				ev = nonSCEvents[i];
			i++;
		}
		return ev.isInitialisation();
	}

}
