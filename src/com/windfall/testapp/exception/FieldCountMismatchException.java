package com.windfall.testapp.exception;

public class FieldCountMismatchException extends Exception {

	/**
	 * Rows in the File contain different field counts, manual editing or additional coding required
	 */
	private static final long serialVersionUID = -4774985040050458882L;

	public FieldCountMismatchException(String message) {
		super(message);
	}
	
}


