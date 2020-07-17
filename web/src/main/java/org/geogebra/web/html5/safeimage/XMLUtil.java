package org.geogebra.web.html5.safeimage;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

/**
 * Util class to parse xm natively.
 *
 * @author laszlo
 */
@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public class XMLUtil {

	/**
	 * Sets xml content
	 * @param content to set.
	 */
	public native void setContent(String content);

	/**
	 * Removes specified tag from xml.
	 * @param tag to remove
	 */
	public native void removeTag(String tag);

	/**
	 *
	 * @return the xml string.
	 */
	public native String getContent();
}
