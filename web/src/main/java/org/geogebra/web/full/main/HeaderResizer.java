package org.geogebra.web.full.main;

import org.gwtproject.dom.client.Element;

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

	/**
	 * reset basic header style and add app specific header classname
	 */
	void resetHeaderStyle(Element header);
}
