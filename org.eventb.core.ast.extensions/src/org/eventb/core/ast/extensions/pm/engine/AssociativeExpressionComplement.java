package org.eventb.core.ast.extensions.pm.engine;

import org.eventb.core.ast.Expression;

/**
 * An implementation of a complement to an associative expression.
 * 
 * <p> This class is not intended to be extended by clients.
 * @author maamria
 * @since 1.0
 *
 */
public final class AssociativeExpressionComplement{

	private int tag;
	private Expression toAppend;
	private Expression toPrepend;
	
	public AssociativeExpressionComplement(int tag, Expression toAppend, 
			Expression toPrepend){
		this.tag = tag;
		this.toAppend = toAppend;
		this.toPrepend = toPrepend;
	}
	
	public AssociativeExpressionComplement(AssociativeExpressionComplement complement){
		this.tag = complement.tag;
		this.toAppend = complement.toAppend;
		this.toPrepend = complement.toPrepend;
	}

	public int getTag() {
		return tag;
	}

	public Expression getToAppend() {
		return toAppend;
	}

	public Expression getToPrepend() {
		return toPrepend;
	}
	
	public String toString(){
		StringBuilder builder = new StringBuilder();
		builder.append("Tag : "+ tag +" | ");
		builder.append("Expression to append : "+ toAppend + " | ");
		builder.append("Expression to prepend : "+ toPrepend);
		return builder.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AssociativeExpressionComplement){
			AssociativeExpressionComplement other = (AssociativeExpressionComplement) obj;
			return tag == other.tag && toAppend.equals(other.toAppend) && toPrepend.equals(other.toPrepend);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return tag + 13*toAppend.hashCode() + 17*toPrepend.hashCode();
	}
}
