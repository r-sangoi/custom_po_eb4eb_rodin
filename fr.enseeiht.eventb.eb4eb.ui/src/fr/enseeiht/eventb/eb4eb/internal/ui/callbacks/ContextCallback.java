package fr.enseeiht.eventb.eb4eb.internal.ui.callbacks;

import java.util.Collection;

import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;

import fr.enseeiht.eventb.eb4eb.IAnnotation;

public class ContextCallback implements ICallback/*<Predicate>*/ {

	private IAnnotation annotation;
	
	public ContextCallback(IAnnotation annotation) {
		this.annotation = annotation;
	}
	
//	@Override
//	public Predicate call(Object... parameters) {
//		throw new InvalidParametersException("[Framework bug] Invalid Parameters for the callback, expected parameters :\n"
//				+ " - Factory : FormulaFactory\n"
//				+ " - Machine name : String\n"
//				+ " - Events Name : Collection<String>");
//	}
	
	public Predicate call(FormulaFactory factory, String machineName, Collection<String> eventsName) {
		//TODO
		System.out.println(machineName);
		return factory.makeLiteralPredicate(Formula.BTRUE, null);
	}

}
