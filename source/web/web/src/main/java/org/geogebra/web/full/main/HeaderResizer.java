package org.geogebra.web.full.main;

import org.gwtproject.dom.client.Element;

/**
 * Header resizer.
 */
public interface HeaderResizer {

	/**
	 * Updates header according to screen size.
	 */
	void resizeHeader();

	/**
	 *
	 * @return header height on  small screen.
	 */
	int getSmallScreenHeight();

	/**
	 *
	 * @return header height
	 */
	int getHeaderHeight();

	/**
	 * reset basic header style and add app specific header classname
	 */
	void reset(Element header);
}
