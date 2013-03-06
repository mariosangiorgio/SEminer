package it.polimi.crawler.exceptions;

public class PersistenceException extends Exception {

	private static final long serialVersionUID = 8252532582320966155L;

	public PersistenceException(String message) {
		super(message);
	}

	public PersistenceException(String message, Exception cause) {
		this(message);
		initCause(cause);
	}
}
