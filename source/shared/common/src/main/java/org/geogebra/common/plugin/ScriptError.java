package org.geogebra.common.plugin;

/**
 * @author Arnaud
 *
 */
@SuppressWarnings("serial")
public class ScriptError extends Exception {
	private String message;

	/**
	 * @param message
	 *            message
	 */
	public ScriptError(String message, Throwable cause) {
		super(cause);
		this.message = message;
	}

	/**
	 * @return error to be displayed (already localized)
	 */
	public String getScriptError() {
		return message;
	}

}
