package org.geogebra.web.full.helper;

import org.gwtproject.resources.client.ImageResource;

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
	@SuppressWarnings("serial")
	public static SafeHtml getImageHtml(final ImageResource imgres) {
		return () -> "<img width=\"" + imgres.getWidth() + "\" height=\""
				+ imgres.getHeight() + "\" src=\""
				+ imgres.getSafeUri().asString() + "\" />";
	}

}
