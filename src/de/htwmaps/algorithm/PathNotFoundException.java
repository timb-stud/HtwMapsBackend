package de.htwmaps.algorithm;

/**
 * 
 * @author Tim Bartsch
 * 
 */
public class PathNotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PathNotFoundException() {
	}

	public PathNotFoundException(String message) {
		super(message);
	}

	public PathNotFoundException(Throwable cause) {
		super(cause);
	}

	public PathNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

}
