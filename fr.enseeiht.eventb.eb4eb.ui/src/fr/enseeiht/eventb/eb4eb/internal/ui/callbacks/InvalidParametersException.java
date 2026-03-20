package fr.enseeiht.eventb.eb4eb.internal.ui.callbacks;

public class InvalidParametersException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public InvalidParametersException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidParametersException(String message) {
		super(message);
	}
}
