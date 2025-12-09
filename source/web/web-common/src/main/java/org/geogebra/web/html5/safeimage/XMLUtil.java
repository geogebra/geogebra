/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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
