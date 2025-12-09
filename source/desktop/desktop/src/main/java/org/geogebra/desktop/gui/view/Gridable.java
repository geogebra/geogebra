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

package org.geogebra.desktop.gui.view;

import java.awt.Component;

import org.geogebra.desktop.main.AppD;

/**
 * This interface is needed by PrintGridable to print an Object on more than one
 * page and divide it's content on those pages such that the "cuts" are only
 * along the given grid.
 *
 */
public interface Gridable {

	/**
	 * @return the widths of all the columns in the grid (in order from left to
	 *         right)
	 */
	public int[] getGridColwidths();

	/**
	 * @return the heights of all the rows in the grid (in order from top to
	 *         bottom)
	 */
	public int[] getGridRowHeights();

	/**
	 * @return application
	 */
	public AppD getApplication();

	/**
	 * 
	 * @return the components on which the print method is used, they will be
	 *         put together according to the placement in the 2D-array
	 */
	public Component[][] getPrintComponents();

}
