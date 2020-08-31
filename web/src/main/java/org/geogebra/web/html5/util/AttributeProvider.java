package org.geogebra.web.html5.util;

public interface AttributeProvider {

	String getAttribute(String attribute);

	boolean hasAttribute(String attribute);

	void removeAttribute(String attribute);

	void setAttribute(String attribute, String value);
}
