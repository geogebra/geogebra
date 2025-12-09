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

package org.geogebra.common.kernel.geos;

/**
 * Class to store original screen location of a geo that comes from ggb.
 * 
 * @author Laszlo Gal
 *
 */
public class ScreenLocation {
	private final int x;
	private final int y;

	/**
	 * Constructor from (x, y)
	 * 
	 * @param x
	 *            to set
	 * @param y
	 *            to set
	 */
	public ScreenLocation(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * @return screen x from file
	 */
	public Integer getX() {
		return x;
	}

	/**
	 * @return screen y from file
	 */
	public Integer getY() {
		return y;
	}

}
