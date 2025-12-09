/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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
