package org.geogebra.web.html5.safeimage;

import org.geogebra.common.util.FileExtensions;
import org.geogebra.common.util.ImageManager;
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
		return Browser.encodeSVG(ImageManager.fixSVG(content));
	}
}
