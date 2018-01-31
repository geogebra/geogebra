package org.geogebra.web.html5.main;


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

	/**
	 * Find card index at given coordinates.
	 * @param x client coordinate.
	 * @param y client coordinate.
	 * @return the card index if any at (x, y), -1 otherwise.
	 */
	int cardIndexAt(int x, int y);

	/**
	 * Starts dragging the given card.
	 * @param pageIndex
	 *            to drag.
	 */
	void startDrag(int pageIndex);

	/**
	 * Starts the drag of card at (x, y) if any.
	 * 
	 * @param x
	 *            coordinate.
	 * @param y
	 *            coordinate.
	 */
	void startDrag(int x, int y);

	/**
	 * Drag the card that was started to.
	 * 
	 * @param x
	 *            coordinate.
	 * @param y
	 *            coordinate.
	 */
	int drag(int x, int y);

	/**
	 * Stops dragging card.
	 */
	void stopDrag();

	/**
	 * Drop the card indexed by srcIdx to position x,y.
	 * @param x coordinate.
	 * @param y coordinate.
	 * @return if the drop was successful or not.
	 */
	boolean dropTo(int x, int y);

	/**
	 * Finds and loads card at (x, y) coordinates.
	 * 
	 * @param x
	 *            coordinate.
	 * @param y
	 *            coordinate.
	 */
	void loadPageAt(int x, int y);
}
