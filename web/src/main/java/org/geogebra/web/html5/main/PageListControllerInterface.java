package org.geogebra.web.html5.main;

import org.geogebra.common.kernel.AppState;
import org.geogebra.common.plugin.EventType;
import org.geogebra.web.html5.gui.RenameCard;

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
	 * Replay an action
	 * 
	 * @param action
	 *            action type
	 * @param state
	 *            state to restore
	 * @param args
	 *            action parameters
	 */
	void executeAction(EventType action, AppState state, String[] args);

	/**
	 * Select a slide
	 * 
	 * @param slideID
	 *            slide identifier
	 */
	void setActiveSlide(String slideID);

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

	/**
	 * export all sliders as PDF
	 * 
	 * @return base64 encoded PDF
	 */
	String exportPDF();

	/**
	 * Renaming a slide
	 * @param card to rename.
	 * @param title the new title.
	 */
	void rename(RenameCard card, String title);
}
