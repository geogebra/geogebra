package org.geogebra.common.kernel.commands;

/**
 * Asynchronous loading error
 */
public class CommandNotLoadedError extends Error {

	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor
	 */
	public CommandNotLoadedError() {
		super();
	}

	/**
	 * @param message
	 *            error message
	 */
	public CommandNotLoadedError(String message) {
		super(message);
	}

}
