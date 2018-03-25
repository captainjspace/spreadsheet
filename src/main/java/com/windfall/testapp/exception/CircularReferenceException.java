package com.windfall.testapp.exception;

public class CircularReferenceException extends RuntimeException {
	/**
	 * There is a circular reference in the CSV spreadsheet
	 */
	private static final long serialVersionUID = -7173497828761638072L;

	public CircularReferenceException(String message) {
		super(message);
	}
}
