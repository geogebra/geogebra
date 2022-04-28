package org.geogebra.web.resources;

import com.google.gwt.safehtml.shared.UriUtils;

public final class ImageResourceConverter {

	private ImageResourceConverter() {
		// TODO: after switching to GWT3 please delete this class
		//  and all the .getSafeUri().asString() hacks
	}

	/**
	 * @param image new style image
	 * @return old style image
	 */
	public static com.google.gwt.resources.client.ImageResource
			convertToOldImageResource(org.gwtproject.resources.client.ImageResource image) {
		return new com.google.gwt.resources.client.impl.ImageResourcePrototype(
				image.getName(),
				UriUtils.fromTrustedString(image.getSafeUri().asString()),
				image.getLeft(),
				image.getTop(),
				image.getWidth(),
				image.getHeight(),
				image.isAnimated(),
				false
		);
	}
}
