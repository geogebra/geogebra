package org.geogebra.web.full.main;

public interface HeaderResizer {

	/**
	 * Updates heeader according to screen size.
	 */
	void resizeHeader();

	/**
	 *
	 * @return header height on  small screen.
	 */
	int getSmallScreenHeight();
}
