package fr.enseeiht.eventb.eb4eb.internal.ui.builder;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IAxiom;
import org.eventb.core.ICarrierSet;
import org.eventb.core.IConstant;
import org.eventb.core.IContextRoot;
import org.eventb.core.ast.Predicate;
import org.rodinp.core.RodinDBException;

import fr.enseeiht.eventb.eb4eb.internal.ui.callbacks.ContextCallback;

public abstract class ContextBuilder extends Builder</*Predicate, */ContextCallback> {
	
	private IContextRoot root;
	private IProgressMonitor pMonitor;
	private Counter axiomLabelCounter;
	
	public ContextBuilder(IContextRoot root, IProgressMonitor pMonitor) {
		super();
		this.root = root;
		this.pMonitor = pMonitor;
		this.axiomLabelCounter = new Counter("axm%d");
	}

	protected void addCarrierSet(String name) throws RodinDBException {
		ICarrierSet carrierSet = this.root.createChild(ICarrierSet.ELEMENT_TYPE, null, this.pMonitor);
		carrierSet.setIdentifierString(name, this.pMonitor);
	}
	
	protected void addConstant(String name) throws RodinDBException {
		IConstant constant = this.root.createChild(IConstant.ELEMENT_TYPE, null, this.pMonitor);
		constant.setIdentifierString(name, this.pMonitor);
	}

	protected void addAxiom(String label, Predicate predicate, boolean isTheorem) throws RodinDBException {
		IAxiom axiom = this.root.createChild(IAxiom.ELEMENT_TYPE, null, this.pMonitor);
		axiom.setLabel(label, this.pMonitor);
		axiom.setTheorem(isTheorem, this.pMonitor);
		axiom.setPredicateString(predicate.toString(),
				this.pMonitor);
	}
	
	protected void addAxiom(Predicate predicate, boolean isTheorem) throws RodinDBException {
		this.addAxiom(this.axiomLabelCounter.next(), predicate, isTheorem);
	}
	
	private class Counter {
		private String string;
		private int i;
		
		public Counter(String string){
			this.string = string;
		}
		
		public String next() {
			return string.formatted(++i);
		}
	}
}
