package fr.enseeiht.eventb.eb4eb.internal.ui.acceptor;

import org.eclipse.core.runtime.CoreException;

import fr.enseeiht.eventb.eb4eb.internal.ui.builder.IBuilder;
import fr.enseeiht.eventb.eb4eb.internal.ui.visitor.IVisitor;

public interface IAcceptor<B extends IBuilder, V extends IVisitor<B>> {
	/**
	 * {@link Visitor}
	 * {@code <Builder>}
	 * @param <V> The Visitor
	 * @param <B> The Builder
	 * @param visitor The 
	 * @param builder
	 * @throws CoreException
	 */
	public void accept(V visitor, B builder) throws CoreException;
}
