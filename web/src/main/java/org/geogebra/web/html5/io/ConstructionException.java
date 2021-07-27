package org.geogebra.web.html5.io;

@SuppressWarnings("serial")
public class ConstructionException extends Exception {

	public ConstructionException(String message) {
		super(message);
	}

	public ConstructionException(Exception cause) {
		super(cause.getLocalizedMessage(), cause);
	}

}
