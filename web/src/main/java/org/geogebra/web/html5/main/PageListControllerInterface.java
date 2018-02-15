package org.geogebra.web.html5.main;

import org.geogebra.common.plugin.EventType;

/**
 * Controller for multipage files
 */
public interface PageListControllerInterface {
	/**
	 * resets the page control panel
	 */
	void resetPageControl();

	/**
	 * @param index
	 *            index
	 * @return slide at given index
	 */
	GgbFile getSlide(int index);

	/**
	 * @return JSON representation of current multislide file in the format of
	 *         GeoGebra Book
	 */
	String getStructureJSON();

	/**
	 * @return number of slides in current file
	 */
	int getSlideCount();

	/**
	 * Load file that contains multiple slides; if it's a simple file do nothing
	 * 
	 * @param archiveContent
	 *            file to open
	 * @return whether it was a multislide file
	 */
	boolean loadSlides(GgbFile archiveContent);


	String getSlideID();


	void executeAction(EventType action, String[] args);
}
