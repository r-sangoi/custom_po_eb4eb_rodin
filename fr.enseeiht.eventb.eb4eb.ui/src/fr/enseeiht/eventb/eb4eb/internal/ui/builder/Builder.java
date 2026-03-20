package fr.enseeiht.eventb.eb4eb.internal.ui.builder;

import java.util.Collection;
import java.util.LinkedList;

import fr.enseeiht.eventb.eb4eb.internal.ui.callbacks.ICallback;

public abstract class Builder</*O, */C extends ICallback/*<O>*/> implements IBuilder {
	private Collection<C> callbacks;
	
	public Builder() {
		this.callbacks = new LinkedList<C>();
	}
	
	/**
	 * Add callback to the builder. Allows to add behavior to a builder.
	 * @param callback the callback to call
	 */
	public void addCallbacks(C callback) {
		this.callbacks.add(callback);
	}

	/**
	 * Return the added callbacks.
	 * <p>Need to be called by the implemented builder and to call the callbacks returned.</p>
	 * @return the added callbacks
	 */
	protected Collection<C> getCallbacks() {
		return this.callbacks;
	}
	
}
