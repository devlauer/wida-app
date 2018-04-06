package de.elnarion.web.wida.ejb.storageservice;

/**
 * The Class WidaStorageServiceException represents the base exception type of this service.
 * All other exceptions of this service are derived from this class.
 */
public class WidaStorageServiceException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5064912791984011714L;

	/**
	 * Instantiates a new wida storage service exception.
	 */
	public WidaStorageServiceException() {
	}

	/**
	 * Instantiates a new wida storage service exception.
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
	public WidaStorageServiceException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	/**
	 * Instantiates a new wida storage service exception.
	 *
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 */
	public WidaStorageServiceException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Instantiates a new wida storage service exception.
	 *
	 * @param message
	 *            the message
	 */
	public WidaStorageServiceException(String message) {
		super(message);
	}

	/**
	 * Instantiates a new wida storage service exception.
	 *
	 * @param cause
	 *            the cause
	 */
	public WidaStorageServiceException(Throwable cause) {
		super(cause);
	}
	
	

}
