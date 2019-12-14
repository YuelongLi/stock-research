package database;

public class NoSuchEntryException extends Exception {

	/**
	 * To make eclipse happy
	 */
	private static final long serialVersionUID = 1L;
	public NoSuchEntryException(String message) {
		super(message);
	}
}
