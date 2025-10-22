package org.geogebra.web.html5.main;

import org.geogebra.common.main.undo.ActionExecutor;
import org.geogebra.web.html5.util.StringConsumer;

import jsinterop.base.JsPropertyMap;

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
	 * @return JSON representation of the current multipage file in the format of
	 *         GeoGebra Book
	 */
	String getStructureJSON();

	/**
	 * @return number of slides in the current file
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

	/**
	 * Update preview image.
	 * @param slideID page ID
	 */
	void updatePreviewImage(String slideID);

	/**
	 * export all sliders as PDF
	 * @param scale decides level of detail for rasterized images and patterns
	 * @param consumer gets the base64 encoded PDF
	 */
	void exportPDF(double scale, double dpi, StringConsumer consumer);

	/**
	 * Handle clicking a page card.
	 * @param slideID slide ID
	 */
	void clickPage(String slideID);

	/**
	 * @param pageId
	 *            page ID
	 */
	void selectSlide(String pageId);

	/**
	 * @param eventType event type
	 * @param pageIdx page index
	 * @param appState plain JS object
	 */
	void handlePageAction(String eventType, String pageIdx, JsPropertyMap<?> appState);

	/**
	 * @param pageId page ID
	 * @return plain JS object describing a page (name, file content, thumbnail)
	 */
	PageContent getPageContent(String pageId);

	/**
	 * Set content for a page.
	 * @param pageId page ID
	 * @param content plain JS object describing a page (name, file content, thumbnail)
	 */
	void setPageContent(String pageId, PageContent content);

	/**
	 * @return ID of the active page
	 */
	String getActivePage();

	/**
	 * @return array of all page IDs
	 */
	String[] getPages();
}
