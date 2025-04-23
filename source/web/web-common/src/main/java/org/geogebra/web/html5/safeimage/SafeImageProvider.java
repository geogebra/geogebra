package org.geogebra.web.html5.safeimage;

import org.geogebra.web.html5.util.ArchiveEntry;

/**
 * Safe image consumer.
 * TODO this is implemented by SafeImageFactory, though it's a consumer. Move to ImageManager.
 */
public interface SafeImageProvider {

	/**
	 * Receives the processed image.
	 * @param imageFile image file
	 */
	void onReady(ArchiveEntry imageFile);
}
