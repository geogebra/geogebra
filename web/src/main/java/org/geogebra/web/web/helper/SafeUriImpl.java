package org.geogebra.web.web.helper;

public class SafeUriImpl implements com.google.gwt.safehtml.shared.SafeUri {

	private String url = null;

	public SafeUriImpl(String url) {
		this.url = url;
	}

	public String asString() {
		return url;
	}

}
