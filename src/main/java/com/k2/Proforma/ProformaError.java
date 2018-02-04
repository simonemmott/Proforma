package com.k2.Proforma;

public class ProformaError extends Error {

	/**
	 * 
	 */
	private static final long serialVersionUID = -416966006864868014L;

	public ProformaError() {
	}

	public ProformaError(String message) {
		super(message);
	}

	public ProformaError(Throwable cause) {
		super(cause);
	}

	public ProformaError(String message, Throwable cause) {
		super(message, cause);
	}

	public ProformaError(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
