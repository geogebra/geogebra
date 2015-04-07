package org.geogebra.web.web.helper;

public class SafeUri implements com.google.gwt.safehtml.shared.SafeUri {

	private String url = null;

	public SafeUri(String url) {
		this.url = url;
	}

	public String asString() {
		return url;
	}

}
