package org.geogebra.common.io;

import java.io.IOException;

public class XMLParseException extends Exception {

	public XMLParseException(String message) {
		super(message);
	}

	public XMLParseException(IOException ex) {
		super(ex);
	}
}
