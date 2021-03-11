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

package org.geogebra.common.euclidian.draw;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.RemoveNeeded;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.arithmetic.FunctionalNVar;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;

/**
 * Draw a GeoList containing drawable objects
 * 
 * @author Markus Hohenwarter
 */
public final class DrawList extends Drawable implements RemoveNeeded {
	/** coresponding list as geo */
	GeoList geoList;
	private DrawListArray drawables;
	/** whether this is visible */
	boolean isVisible;

	/**
	 * Creates new drawable list
	 * 
	 * @param view
	 *            view
	 * @param geoList
	 *            list
	 */
	public DrawList(EuclidianView view, GeoList geoList) {
		this.view = view;
		this.geoList = geoList;
		geo = geoList;

		reset();

		update();
	}

	private void reset() {

		if (drawables == null) {
			drawables = new DrawListArray(view);
		}

	}

	@Override
	public void update() {

		isVisible = geoList.isEuclidianVisible();
		if (!isVisible) {
			return;
		}

		// go through list elements and create and/or update drawables
		int size = geoList.size();
		drawables.ensureCapacity(size);
		int oldDrawableSize = drawables.size();

		int drawablePos = 0;
		for (int i = 0; i < size; i++) {
			GeoElement listElement = geoList.get(i);
			if (!listElement.isDrawable()) {
				continue;
			}

			// add drawable for listElement
			// if (addToDrawableList(listElement, drawablePos,
			// oldDrawableSize))
			if (drawables.addToDrawableList(listElement, drawablePos,
					oldDrawableSize, this)) {
				drawablePos++;
			}

		}

		// remove end of list
		for (int i = drawables.size() - 1; i >= drawablePos; i--) {
			view.remove(drawables.get(i).getGeoElement());
			drawables.remove(i);
		}

		// draw trace
		if (geoList.getTrace()) {
			isTracing = true;
			GGraphics2D g2 = view.getBackgroundGraphics();
			if (g2 != null) {
				drawTrace(g2);
			}
		} else {
			if (isTracing) {
				isTracing = false;
				// view.updateBackground();
			}
		}

	}

	/**
	 * This method is necessary, for example when we set another construction
	 * step, and the sub-drawables of this list should be removed as well
	 */
	@Override
	public void remove() {
		for (int i = drawables.size() - 1; i >= 0; i--) {
			GeoElement currentGeo = drawables.get(i).getGeoElement();
			if (!currentGeo.isLabelSet()) {
				view.remove(currentGeo);
			}
		}
		drawables.clear();
	}

	@Override
	protected void drawTrace(GGraphics2D g2) {

		g2.setPaint(geo.getObjectColor());
		g2.setStroke(objStroke);
		if (isVisible) {
			int size = drawables.size();
			for (int i = 0; i < size; i++) {
				Drawable d = (Drawable) drawables.get(i);
				// draw only those drawables that have been created by this
				// list;
				// if d belongs to another object, we don't want to mess
				// with it
				// here
				if (createdByDrawList() || !d.getGeoElement().isLabelSet()) {
					d.draw(g2);
				}
			}
		}
	}

	@Override
	public void draw(GGraphics2D g2) {

		if (isVisible) {
			boolean doHighlight = isHighlighted();

			int size = drawables.size();
			for (int i = 0; i < size; i++) {
				Drawable d = (Drawable) drawables.get(i);
				// draw only those drawables that have been created by this
				// list;
				// if d belongs to another object, we don't want to mess
				// with it
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
	public boolean hit(int x, int y, int hitThreshold) {

		int size = drawables.size();
		for (int i = 0; i < size; i++) {
			Drawable d = (Drawable) drawables.get(i);
			if (d.hit(x, y, hitThreshold)) {
				return true;
			}
		}
		return false;

	}

	@Override
	public boolean isInside(GRectangle rect) {
		int size = drawables.size();
		for (int i = 0; i < size; i++) {
			Drawable d = (Drawable) drawables.get(i);
			if (!d.isInside(rect)) {
				return false;
			}
		}
		return size > 0;

	}

	@Override
	public boolean intersectsRectangle(GRectangle rect) {
		int size = drawables.size();
		for (int i = 0; i < size; i++) {
			Drawable d = (Drawable) drawables.get(i);
			if (d.intersectsRectangle(rect)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the bounding box of this DrawPoint in screen coordinates.
	 */
	@Override
	public GRectangle getBounds() {
		if (!geo.isEuclidianVisible()) {
			return null;
		}

		GRectangle result = null;

		int size = drawables.size();
		for (int i = 0; i < size; i++) {
			Drawable d = (Drawable) drawables.get(i);
			GRectangle bb = d.getBounds();
			if (bb != null) {
				if (result == null) {
					result = AwtFactory.getPrototype().newRectangle(bb);
				}
				// changed () to (bb) bugfix,
				// otherwise top-left of screen
				// is always included
				// add bounding box of list element
				result.add(bb);
			}
		}

		return result;
	}

	/**
	 * @param geoItem
	 *            geo
	 * @return whether it should be painted in LaTeX
	 */
	static boolean needsLatex(GeoElement geoItem) {
		return geoItem instanceof FunctionalNVar
				|| (geoItem.isGeoText() && geoItem.isLaTeXDrawableGeo());
	}

}
