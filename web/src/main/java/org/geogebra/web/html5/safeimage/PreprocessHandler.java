package org.geogebra.web.html5.safeimage;

/**
 * Handler to notify if preprocessing has finished
 *
 * @author laszlo
 */
public interface PreprocessHandler {
	void onLoad(ImageFile imageFile);
}
