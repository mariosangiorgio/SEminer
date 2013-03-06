package it.polimi.masAPI.exceptions;

public class CrawlingException extends Exception {
	private static final long serialVersionUID = 1703091669722560239L;

	public CrawlingException(String message, Throwable cause) {
		super(message, cause);
	}
}
