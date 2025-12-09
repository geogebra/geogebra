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

import org.geogebra.common.util.FileExtensions;
import org.geogebra.common.util.ImageManager;
import org.geogebra.gwtutil.NavigatorUtil;
import org.geogebra.web.html5.Browser;

/**
 * SVG fix util class
 * @author Laszlo
 */
public class SVGUtil {

	/**
	 * SVG matcher
	 * @param extension to match.
	 * @return if it is an SVG or not
	 */
	public static boolean match(FileExtensions extension) {
		return FileExtensions.SVG.equals(extension);
	}

	/**
	 * Fix and encode SVG content.
	 * @param content of the SVG.
	 * @return the fixed, encoded content.
	 */
	public static String fixAndEncode(String content) {
		if (NavigatorUtil.isFirefox()) {
			return Browser.encodeSVG(ImageManager.fixAndRemoveAspectRatio(content));
		}
		return Browser.encodeSVG(ImageManager.fixSVG(content));
	}
}
