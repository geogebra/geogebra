package org.geogebra.web.html5.main;

/**
 * Controller for multipage files
 */
public interface PageListControllerInterface {
	/**
	 * resets the page control panel
	 */
	void resetPageControl();

	GgbFile getSlide(int index);

	String getStructureJSON();

	int getSlideCount();

	void loadSlides(GgbFile archiveContent);

}
