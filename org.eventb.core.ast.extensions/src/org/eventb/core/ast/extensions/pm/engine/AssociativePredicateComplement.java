package org.eventb.core.ast.extensions.pm.engine;

import org.eventb.core.ast.Predicate;

/**
 * An implementation of a complement to an associative predicate.
 * 
 * <p> This class is not intended to be extended by clients.
 * @author maamria
 * @since 1.0
 *
 */
public final class AssociativePredicateComplement{

	private int tag;
	private Predicate toAppend;
	private Predicate toPrepend;
	
	public AssociativePredicateComplement(int tag, Predicate toAppend, 
			Predicate toPrepend){
		this.tag = tag;
		this.toAppend = toAppend;
		this.toPrepend = toPrepend;
	}
	
	public AssociativePredicateComplement(AssociativePredicateComplement complement){
		this.tag = complement.tag;
		this.toAppend = complement.toAppend;
		this.toPrepend = complement.toPrepend;
	}
	
	public int getTag() {
		return tag;
	}

	public Predicate getToAppend() {
		return toAppend;
	}

	public Predicate getToPrepend() {
		return toPrepend;
	}
	
	public String toString(){
		StringBuilder builder = new StringBuilder();
		builder.append("Tag : "+ tag +" | ");
		builder.append("Predicate to append : "+ toAppend + " | ");
		builder.append("Predicate to prepend : "+ toPrepend);
		return builder.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AssociativePredicateComplement){
			AssociativePredicateComplement other = (AssociativePredicateComplement) obj;
			return tag == other.tag && toAppend.equals(other.toAppend) && toPrepend.equals(other.toPrepend);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return tag + 13*toAppend.hashCode() + 17*toPrepend.hashCode();
	}

}
