package de.elnarion.web.wida.ejb.storageservice;

/**
 * This exception is thrown if any container resources are missing.
 */
public class WidaContainerResourcesMissingException extends WidaStorageServiceException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2210136586113496152L;

	/**
	 * Instantiates a new wida container resources missing exception.
	 */
	public WidaContainerResourcesMissingException() {
	}

	/**
	 * Instantiates a new wida container resources missing exception.
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
	public WidaContainerResourcesMissingException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	/**
	 * Instantiates a new wida container resources missing exception.
	 *
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 */
	public WidaContainerResourcesMissingException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Instantiates a new wida container resources missing exception.
	 *
	 * @param message
	 *            the message
	 */
	public WidaContainerResourcesMissingException(String message) {
		super(message);
	}

	/**
	 * Instantiates a new wida container resources missing exception.
	 *
	 * @param cause
	 *            the cause
	 */
	public WidaContainerResourcesMissingException(Throwable cause) {
		super(cause);
	}

}
