/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/
package geogebra.common.gui.view.algebra;

import geogebra.common.kernel.View;
import geogebra.common.kernel.geos.GeoElement;

/**
 * Algebra view -- shows algebraic representation of the objects either as value,
 * definition or command
 */
public interface AlgebraView extends View{
	/**
	 * Returns whether this view is currently visible
	 * @return whether this view is currently visible
	 */
	public boolean isVisible();

	public GeoElement getGeoElementForPath(Object tp);//Object=TreePath
	public GeoElement getGeoElementForLocation(Object tree, int x, int y);//Object=JTree

	public void startEditing(GeoElement geo, boolean shiftDown);
	public void cancelEditing();
	public boolean isEditing();

	public Object getPathForLocation(int x, int y);//Object=TreePath
	public Object getPathBounds(Object tp);//Object=Rectangle;Object=TreePath
	
	/**DEPENDENCY:
	 * Tree mode where the objects are categorized by their dependency (free,
	 * dependent, auxiliary) -- default value
	 * TYPE:
	 * Tree mode where the objects are categorized by their type (points,
	 * circles, ..)
	 * VIEW:
	 * Tree mode where the objects are categorized by the view on which their
	 * value is computed (xOyPlane, space, ...)
	 * ORDER:
	 * Construction Protocol order
	 */
	public static enum SortMode { DEPENDENCY, TYPE, VIEW, ORDER, LAYER }
	
	/**
	 * set tree mode from int value
	 * @param mode int value of the mode
	 */
	public void setTreeMode(int mode);

	public void setFocus(boolean b);

	public GeoElement getLastSelectedGeo();

	public void setLastSelectedGeo(GeoElement geo);

	public boolean isRenderLaTeX();
}
