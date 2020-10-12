package org.geogebra.web.html5.main;

import org.geogebra.common.main.undo.ActionExecutor;

/**
 * Controller for multipage files
 */
public interface PageListControllerInterface extends ActionExecutor {
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
	 * Make sure slide content is synced with construction
	 * @param index
	 *            index
	 */
	void refreshSlide(int index);

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

	/**
	 * @return Identifier of current slide, e.g. _slide0
	 */
	String getSlideID();

	/**
	 * Select a slide
	 * 
	 * @param slideID
	 *            slide identifier
	 */
	void setActiveSlide(String slideID);

	/**
	 * load existing page
	 *
	 * @param index
	 *            index of page to load
	 */
	void loadPage(int index);

	/**
	 * Move source slide to target index
	 * 
	 * @param i
	 *            source
	 * @param j
	 *            target
	 */
	void reorder(int i, int j);

	/**
	 * @param idx
	 *            page index
	 * @param selected
	 *            whether to select
	 */
	void clickPage(int idx, boolean selected);

	/**
	 * Persist currently selected page
	 */
	void saveSelected();

	/**
	 * Update preview of selected card
	 */
	void updatePreviewImage();

	void updatePreviewImage(String slideID);

	/**
	 * export all sliders as PDF
	 * 
	 * @return base64 encoded PDF
	 */
	String exportPDF();

	void clickPage(String slideID);

	/**
	 * @param idx
	 *            page index
	 */
	void selectSlide(int idx);
}
