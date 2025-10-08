package org.geogebra.common.kernel;

public class MacroException extends Exception {
	public MacroException(String s) {
		super(s);
	}

	public MacroException(String message, Throwable cause) {
		super(message, cause);
	}
}
