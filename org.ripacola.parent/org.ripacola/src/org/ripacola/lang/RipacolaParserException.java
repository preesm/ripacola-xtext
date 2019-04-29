package org.ripacola.lang;

public class RipacolaParserException extends RuntimeException {
	private static final long serialVersionUID = 3271350602555442619L;

	public RipacolaParserException(final Throwable cause) {
		super(cause);
	}

	public RipacolaParserException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
