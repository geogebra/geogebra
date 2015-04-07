/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * Gridable.java
 *
 * Created on 18.08.2011, 17:20
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

	public AppD getApplication();

	/**
	 * 
	 * @return the components on which the print method is used, they will be
	 *         put together according to the placement in the 2D-array
	 */
	public Component[][] getPrintComponents();

}
