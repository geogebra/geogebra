package org.geogebra.common.io;

import java.io.IOException;

public class XMLParseException extends Exception {

	public XMLParseException(String message) {
		super(message);
	}

	public XMLParseException(String message, Throwable cause) {
		super(message, cause);
	}

	public XMLParseException(IOException ex) {
		super(ex);
	}
}
