package com.windfall.testapp.exception;

public class NotACSVException extends Exception {
	
	/**
	 * no commas
	 */
	private static final long serialVersionUID = -8949704224009267372L;

	public NotACSVException(String message) {
		super(message);
	}

}
