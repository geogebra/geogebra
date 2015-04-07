package org.geogebra.web.web.helper;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;

/**
 * @author gabor
 *
 *         Factory class for create safeHtml strings
 */
public class SafeHtmlFactory {

	/**
	 * @param imgres
	 *            ImageResource for safehtml
	 * @return The safehtml string of the image
	 */
	public static SafeHtml getImageHtml(final ImageResource imgres) {
		return new SafeHtml() {

			public String asString() {
				return "<img width=\"" + imgres.getWidth() + "\" height=\""
				        + imgres.getHeight() + "\" src=\""
				        + imgres.getSafeUri().asString() + "\" />";
			}
		};
	}

}
