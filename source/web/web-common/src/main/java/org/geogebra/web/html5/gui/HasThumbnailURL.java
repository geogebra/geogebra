package org.geogebra.web.html5.gui;

import org.geogebra.common.io.MyXMLio;

public interface HasThumbnailURL {

	/**
	 * @return data URL of PNG export of this view, scaled to {@link MyXMLio#THUMBNAIL_PIXELS_X}
	 */
	String getCanvasBase64WithTypeString();
}
