/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/
package org.geogebra.common.gui.view.algebra;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.kernel.View;
import org.geogebra.common.kernel.geos.GeoElement;

/**
 * Algebra view -- shows algebraic representation of the objects either as value,
 * definition or command
 */
public interface AlgebraView extends View, SetLabels{
	/**
	 * Returns whether this view is currently visible
	 * @return whether this view is currently visible
	 */
	public boolean isVisible();

	public void startEditing(GeoElement geo);
	public void cancelEditing();
	public boolean isEditing();

	// For WebSimple
	public GeoElement getDraggedGeo();
	
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
	public static enum SortMode { 
		DEPENDENCY {
			@Override
			public String toString() {
				return "Dependency";
			}
		},
		TYPE {
			@Override
			public String toString() {
				return "ObjectType";
			}
		},
		VIEW {
			@Override
			public String toString() {
				return "View";
			}
		},
		ORDER {
			@Override
			public String toString() {
				return "ConstructionOrder";
			}
		},
		LAYER {
			@Override
			public String toString() {
				return "Layer";
			}
		}
	}
	
	/**
	 * set tree mode from int value
	 * @param mode int value of the mode
	 */
	public void setTreeMode(int mode);

	public int getTreeModeValue();

	public void setFocus(boolean b);

	public GeoElement getLastSelectedGeo();

	public void setLastSelectedGeo(GeoElement geo);

	public boolean isRenderLaTeX();

	public boolean isAttached();

	public SortMode getTreeMode();

	void setTreeMode(SortMode value);

	/**
	 * This is just used from Html5/Web, but interface is in Common
	 */
	public void setShowAlgebraInput(boolean b);

	public void resetItems();
}
