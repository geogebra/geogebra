/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package org.geogebra.common.gui.view.algebra;

import org.geogebra.common.gui.Editing;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.kernel.geos.GeoElement;

/**
 * Algebra view -- shows algebraic representation of the objects either as
 * value, definition or command
 */
public interface AlgebraView extends Editing, SetLabels {
	/**
	 * Returns whether this view is currently visible
	 * 
	 * @return whether this view is currently visible
	 */
	public boolean isVisible();

	/**
	 * Start editing an element
	 * @param geo construction element
	 */
	public void startEditItem(GeoElement geo);

	/**
	 * @return whether any item is being edited
	 */
	public boolean isEditItem();

	/**
	 * (Web only, moved here because of WebSimple)
	 * @return element dragged from AV to graphics
	 */
	public GeoElement getDraggedGeo();

	/**
	 * DEPENDENCY: Tree mode where the objects are categorized by their
	 * dependency (free, dependent, auxiliary) -- default value TYPE: Tree mode
	 * where the objects are categorized by their type (points, circles, ..)
	 * VIEW: Tree mode where the objects are categorized by the view on which
	 * their value is computed (xOyPlane, space, ...) ORDER: Construction
	 * Protocol order
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
		};

		/**
		 * @param mode
		 *            mode XML value
		 * @return mode
		 */
		public static SortMode fromInt(int mode) {
			switch (mode) {
			case 0:
				return SortMode.DEPENDENCY;
			case 1:
				return SortMode.TYPE;
			case 2:
				return SortMode.LAYER;
			case 3:
				return SortMode.ORDER;
			}
			return SortMode.TYPE;
		}

		/**
		 * @return XML value
		 */
		public int toInt() {
			switch (this) {
			case DEPENDENCY:
				return 0;
			case TYPE:
				return 1;
			case LAYER:
				return 2;
			case ORDER:
				return 3;
			}
			return 1;
		}

	}

	/**
	 * Focus or blur the view.
	 * @param b true to focus
	 */
	public void setFocus(boolean b);

	/**
	 * @return last selected element
	 */
	GeoElement getLastSelectedGeo();

	/**
	 * @param geo last selected element
	 */
	void setLastSelectedGeo(GeoElement geo);

	/**
	 * @return whether the view is attached to the kernel
	 */
	boolean isAttachedToKernel();

	/**
	 * @return the sorting mode
	 */
	SortMode getTreeMode();

	/**
	 * Should not be called directly, use AlgebraSettings instead
	 * @param sortMode sort mode
	 */
	void setTreeMode(SortMode sortMode);

	/**
	 * This is just used from Html5/Web, but interface is in Common
	 * 
	 * @param visible
	 *            whether to show AV input
	 */
	void setShowAlgebraInput(boolean visible);

    /**
     * remove the geo (with no check)
     * @param geo geo
     */
    void doRemove(GeoElement geo);

}
