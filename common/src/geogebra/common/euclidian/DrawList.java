/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * DrawPoint.java
 *
 * Created on 11. Oktober 2001, 23:59
 */

package geogebra.common.euclidian;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;

/**
 * Draw a GeoList containing drawable objects
 * 
 * @author Markus Hohenwarter
 */
public final class DrawList extends Drawable implements RemoveNeeded {

	private GeoList geoList;
	// private ArrayList drawables = new ArrayList();
	private DrawListArray drawables;
	private boolean isVisible;

	/**
	 * Creates new drawable list
	 * 
	 * @param view view
	 * @param geoList list
	 */
	public DrawList(AbstractEuclidianView view, GeoList geoList) {
		this.view = view;
		this.geoList = geoList;
		geo = geoList;

		drawables = new DrawListArray(view);

		update();
	}

	@Override
	final public void update() {
		isVisible = geoList.isEuclidianVisible();
		if (!isVisible)
			return;

		// go through list elements and create and/or update drawables
		int size = geoList.size();
		drawables.ensureCapacity(size);
		int oldDrawableSize = drawables.size();

		int drawablePos = 0;
		for (int i = 0; i < size; i++) {
			GeoElement listElement = geoList.get(i);
			if (!listElement.isDrawable())
				continue;

			// new 3D elements are not drawn -- TODO change that
			if (listElement.isGeoElement3D())
				continue;

			// add drawable for listElement
			// if (addToDrawableList(listElement, drawablePos, oldDrawableSize))
			if (drawables.addToDrawableList(listElement, drawablePos,
					oldDrawableSize, this))
				drawablePos++;


		}

		// remove end of list
		for (int i = drawables.size() - 1; i >= drawablePos; i--) {
			view.remove(drawables.get(i).getGeoElement());
			drawables.remove(i);
		}

		// draw trace
		if (geoList.getTrace()) {
			isTracing = true;
			geogebra.common.awt.Graphics2D g2 = view.getBackgroundGraphics();
			if (g2 != null)
				drawTrace(g2);
		} else {
			if (isTracing) {
				isTracing = false;
				//view.updateBackground();
			}
		}

		// G.Sturr 2010-6-28 spreadsheet trace is now handled in
		// GeoElement.update()
		// if (geoList.getSpreadsheetTrace())
		// recordToSpreadsheet(geoList);
	}

	/**
	 * This method is necessary, for example when we set another construction
	 * step, and the sub-drawables of this list should be removed as well
	 */
	final public void remove() {
		for (int i = drawables.size() - 1; i >= 0; i--) {
			GeoElement currentGeo = drawables.get(i).getGeoElement();
			if (!currentGeo.isLabelSet())
				view.remove(currentGeo);
		}
		drawables.clear();
	}

	@Override
	final void drawTrace(geogebra.common.awt.Graphics2D g2) {
		g2.setPaint(geo.getObjectColor());
		g2.setStroke(objStroke);
		if (isVisible) {
			int size = drawables.size();
			for (int i = 0; i < size; i++) {
				Drawable d = (Drawable) drawables.get(i);
				// draw only those drawables that have been created by this
				// list;
				// if d belongs to another object, we don't want to mess with it
				// here
				if (createdByDrawList() || !d.getGeoElement().isLabelSet()) {
					d.draw(g2);
				}
			}
		}
	}


	@Override
	final public void draw(geogebra.common.awt.Graphics2D g2) {
		if (isVisible) {
			boolean doHighlight = geoList.doHighlighting();

			int size = drawables.size();
			for (int i = 0; i < size; i++) {
				Drawable d = (Drawable) drawables.get(i);
				// draw only those drawables that have been created by this
				// list;
				// if d belongs to another object, we don't want to mess with it
				// here
				if (createdByDrawList() || !d.getGeoElement().isLabelSet()) {
					d.getGeoElement().setHighlighted(doHighlight);
					d.draw(g2);
				}
			}
		}
	}

	/**
	 * Returns whether any one of the list items is at the given screen
	 * position.
	 */
	@Override
	final public boolean hit(int x, int y) {
		int size = drawables.size();
		for (int i = 0; i < size; i++) {
			Drawable d = (Drawable) drawables.get(i);
			if (d.hit(x, y))
				return true;
		}
		return false;
	}

	@Override
	final public boolean isInside(geogebra.common.awt.Rectangle rect) {
		int size = drawables.size();
		for (int i = 0; i < size; i++) {
			Drawable d = (Drawable) drawables.get(i);
			if (!d.isInside(rect))
				return false;
		}
		return size > 0;
	}

	/**
	 * Returns the bounding box of this DrawPoint in screen coordinates.
	 */
	@Override
	final public geogebra.common.awt.Rectangle getBounds() {

		if (!geo.isEuclidianVisible())
			return null;

		geogebra.common.awt.Rectangle result = null;

		int size = drawables.size();
		for (int i = 0; i < size; i++) {
			Drawable d = (Drawable) drawables.get(i);
			geogebra.common.awt.Rectangle bb = d.getBounds();
			if (bb != null) {
				if (result == null)
					result = geogebra.common.factories.AwtFactory.prototype.newRectangle(bb); // changed () to (bb) bugfix,
												// otherwise top-left of screen
												// is always included
				// add bounding box of list element
				result.add(bb);
			}
		}

		return result;
	}

	@Override
	final public GeoElement getGeoElement() {
		return geo;
	}

	@Override
	final public void setGeoElement(GeoElement geo) {
		this.geo = geo;
	}

}
