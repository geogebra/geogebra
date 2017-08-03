package org.geogebra.web.html5.gui.util;

import org.geogebra.web.resources.SVGResource;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ResourcePrototype;

/**
 * Helper for manipulationg with image resources
 */
public class ImgResourceHelper {
	
	/**
	 * Use icon on given button
	 * 
	 * @param ir
	 *            resource
	 * @param button
	 *            button
	 */
	public static void setIcon(final ResourcePrototype ir,
			final HasSetIcon button) {
	    ImageOrText img = new ImageOrText();
		img.setUrl(safeURI(ir));
	    button.setIcon(img);
	   
	}

	/**
	 * 
	 * @param res
	 *            SVG or PNG resource
	 * @return safe URI
	 */
	public static String safeURI(ResourcePrototype res) {
		if (res instanceof ImageResource) {
			return ((ImageResource) res).getSafeUri().asString();
		}
		if (res instanceof SVGResource) {
			return ((SVGResource) res).getSafeUri().asString();
		}
		return "";
	}


}
