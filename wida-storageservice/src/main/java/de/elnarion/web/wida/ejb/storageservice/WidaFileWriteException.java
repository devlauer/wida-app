package de.elnarion.web.wida.ejb.storageservice;

/**
 * The Class WidaFileWriteException.
 */
public class WidaFileWriteException extends WidaStorageServiceException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7146008690543172177L;

	/**
	 * Instantiates a new wida file write exception.
	 */
	public WidaFileWriteException() {
	}

	/**
	 * Instantiates a new wida file write exception.
	 *
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 * @param enableSuppression
	 *            the enable suppression
	 * @param writableStackTrace
	 *            the writable stack trace
	 */
	public WidaFileWriteException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	/**
	 * Instantiates a new wida file write exception.
	 *
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 */
	public WidaFileWriteException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Instantiates a new wida file write exception.
	 *
	 * @param message
	 *            the message
	 */
	public WidaFileWriteException(String message) {
		super(message);
	}

	/**
	 * Instantiates a new wida file write exception.
	 *
	 * @param cause
	 *            the cause
	 */
	public WidaFileWriteException(Throwable cause) {
		super(cause);
	}

}
