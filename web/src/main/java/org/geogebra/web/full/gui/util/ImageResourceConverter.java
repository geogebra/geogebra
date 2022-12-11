package org.geogebra.web.full.gui.util;

import org.gwtproject.resources.client.ImageResource;

public final class ImageResourceConverter {

	private ImageResourceConverter() {
		// TODO: after switching to GWT3 please delete this class
		//  and all the .getSafeUri().asString() hacks
	}

	/**
	 * @param image new style image
	 * @return old style image
	 */
	public static ImageResource
			convertToOldImageResource(ImageResource image) {
		return image;
	}
}
