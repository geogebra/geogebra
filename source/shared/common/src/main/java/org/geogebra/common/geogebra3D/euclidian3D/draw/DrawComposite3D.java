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

package org.geogebra.common.geogebra3D.euclidian3D.draw;

import org.geogebra.common.euclidian.DrawableND;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.Hitting;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer.PickingType;
import org.geogebra.common.geogebra3D.euclidian3D.printer3D.ExportToPrinter3D;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * Class for drawing GeoList (3D part)
 * 
 * @author matthieu
 *
 */
public abstract class DrawComposite3D extends Drawable3D {

	private boolean isVisible;

	private Drawable3DListsForDrawList3D drawable3DLists;

	private DrawList3DArray drawables;
	private int pickOrder = DRAW_PICK_ORDER_MAX;

	/**
	 * common constructor
	 * 
	 * @param view3D
	 *            view
	 * @param geo
	 *            list
	 */
	public DrawComposite3D(EuclidianView3D view3D, GeoElement geo) {
		super(view3D, geo);
		drawables = new DrawList3DArray(view3D, this);
		drawable3DLists = new Drawable3DListsForDrawList3D(view3D);

		setPickingType(PickingType.POINT_OR_CURVE);
	}

	@Override
	synchronized protected boolean updateForItSelf() {

		// Log.debug("LIST -- "+getGeoElement());

		isVisible = getGeoElement().isEuclidianVisible();
		if (!isVisible) {
			return true;
		}

		// getView3D().removeGeoToPick(drawables.size());

		// go through list elements and create and/or update drawables
		int size = size();
		drawables.ensureCapacity(size);
		int oldDrawableSize = drawables.size();

		int drawablePos = 0;
		for (int i = 0; i < size; i++) {
			GeoElement listElement = getElement(i);
			// only new 3D elements are drawn
			if (!listElement.hasDrawable3D()) {
				continue;
			}

			// add drawable for listElement
			if (drawables.addToDrawableList(listElement, drawablePos,
					oldDrawableSize, this)) {
				drawablePos++;
			}

		}

		// remove end of list
		for (int i = drawables.size() - 1; i >= drawablePos; i--) {
			Drawable3D d = (Drawable3D) drawables.get(i);
			if (d.createdByDrawList()) {
				if (d.hasTrace()) {
					d.addLastTrace();
					d.getGeoElement().setUndefined();
					if (shouldBePackedForManager()) {
						d.setGeometriesVisibility(false);
					}
				} else if (!d.hasRecordedTrace()) {
					drawable3DLists.remove(d);
					drawables.remove(i);
					if (shouldBePackedForManager()) {
						d.removeFromGL();
					}
				} else {
					d.getGeoElement().setUndefined();
					if (shouldBePackedForManager()) {
						d.setGeometriesVisibility(false);
					}
				}
			}
		}

		// update for list of lists
		for (int i = 0; i < drawables.size(); i++) {
			Drawable3D d = (Drawable3D) drawables.get(i);
			if (d.createdByDrawList()) {
				if (d.waitForUpdate()) {
					d.update();
				}
			}
		}

		return true;
	}

	protected abstract GeoElement getElement(int i);

	protected abstract int size();

	@Override
	synchronized public void addLastTrace() {
		for (int i = 0; i < drawables.size(); i++) {
			Drawable3D d = (Drawable3D) drawables.get(i);
			d.addLastTrace();
		}
	}

	@Override
	synchronized protected void updateForView() {
		int size = drawables.size();
		for (int i = 0; i < size; i++) {
			Drawable3D d = (Drawable3D) drawables.get(i);
			if (d.createdByDrawList()) {
				d.updateForView();
				if (d.waitForUpdate()) {
					setWaitForUpdate();
				}
			}
		}
	}

	@Override
	synchronized protected void clearTraceForViewChangedByZoomOrTranslate() {
		int size = drawables.size();
		for (int i = 0; i < size; i++) {
			Drawable3D d = (Drawable3D) drawables.get(i);
			if (d.createdByDrawList()) {
				d.clearTraceForViewChangedByZoomOrTranslate();
			}
		}
	}

	@Override
	public void addToDrawable3DLists(Drawable3DLists lists) {
		addToDrawable3DLists(lists, DRAW_TYPE_LISTS);
	}

	@Override
	public void removeFromDrawable3DLists(Drawable3DLists lists) {
		removeFromDrawable3DLists(lists, DRAW_TYPE_LISTS);
	}

	/**
	 * 
	 * @return drawable lists
	 */
	synchronized public Drawable3DListsForDrawList3D getDrawable3DLists() {
		return drawable3DLists;
	}

	@Override
	public void drawOutline(Renderer renderer) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawNotTransparentSurface(Renderer renderer) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawGeometry(Renderer renderer) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawGeometryHidden(Renderer renderer) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawHidden(Renderer renderer) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawHiding(Renderer renderer) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawTransp(Renderer renderer) {
		// TODO Auto-generated method stub

	}

	@Override
	public Drawable3D drawForPicking(Renderer renderer, boolean intersection,
			PickingType type) {

		// not picked as drawable
		return null;

	}

	@Override
	public void drawLabel(Renderer renderer) {
		// no label
	}

	@Override
	public boolean drawLabelForPicking(Renderer renderer) {
		return false;
	}

	@Override
	public boolean isTransparent() {
		return false;
	}

	@Override
	protected boolean isLabelVisible() {
		return isVisible(); // as for texts
	}

	@Override
    synchronized protected void updateLabel() {
		for (DrawableND d : drawables) {
			if (d.createdByDrawList()
					&& (d instanceof DrawList3D || d instanceof DrawText3D)) {
				((Drawable3D) d).updateLabel();
			}
		}
	}

	@Override
    synchronized protected void updateLabelPosition() {
		for (DrawableND d : drawables) {
			if (d.createdByDrawList()
					&& (d instanceof DrawList3D || d instanceof DrawText3D)) {
				((Drawable3D) d).updateLabelPosition();
			}
		}

	}

	@Override
	protected double getColorShift() {
		return COLOR_SHIFT_NONE; // not needed here
	}

	@Override
    synchronized public void setWaitForUpdateVisualStyle(GProperty prop) {

		super.setWaitForUpdateVisualStyle(prop);
		for (DrawableND d : drawables) {
			if (d.createdByDrawList()) {
				d.setWaitForUpdateVisualStyle(prop);
			}
		}

		// also update for e.g. line width
		setWaitForUpdate();
	}

	@Override
    synchronized public void setWaitForReset() {

		super.setWaitForReset();
		for (DrawableND d : drawables) {
			if (d.createdByDrawList()) {
				((Drawable3D) d).setWaitForReset();
			}
		}
	}

	@Override
	synchronized protected Drawable3D getDrawablePicked(
			Drawable3D drawableSource) {

		pickOrder = drawableSource.getPickOrder();
		setPickingType(drawableSource.getPickingType());

		return super.getDrawablePicked(drawableSource);
	}

	@Override
	synchronized public int getPickOrder() {
		return pickOrder;
	}

	@Override
    synchronized public boolean hit(Hitting hitting) {

		boolean ret = false;

		double listZNear = Double.NEGATIVE_INFINITY;
		double listZFar = Double.NEGATIVE_INFINITY;
		double listPositionOnHitting = Double.POSITIVE_INFINITY;
		for (DrawableND d : drawables) {
			if (d.createdByDrawList()) {
				final Drawable3D d3d = (Drawable3D) d;
				if (d3d.hitForList(hitting)) {
					double zNear = d3d.getZPickNear();
					if (!ret || zNear > listZNear) {
						listZNear = zNear;
						listZFar = d3d.getZPickFar();
						listPositionOnHitting = d3d.getPositionOnHitting();
						setPickingType(d3d.getPickingType());
						pickOrder = d3d.getPickOrder();
						ret = true;
					}
				}
			}
		}

		if (pickOrder == DRAW_PICK_ORDER_POINT) { // list of points are paths
			pickOrder = DRAW_PICK_ORDER_PATH;
		}

		if (ret) {
			setZPick(listZNear, listZFar, hitting.discardPositiveHits(),
					listPositionOnHitting);
		}

		return ret;
	}

	@Override
    synchronized public void enlargeBounds(Coords min, Coords max, boolean dontExtend) {
		for (DrawableND d : drawables) {
			if (d.createdByDrawList()) {
				((Drawable3D) d).enlargeBounds(min, max, dontExtend);
			}
		}
	}

	@Override
    synchronized public void exportToPrinter3D(ExportToPrinter3D exportToPrinter3D, boolean
            exportSurface) {
		if (isVisible()) {
			for (DrawableND d : drawables) {
				if (d.createdByDrawList()) {
					((Drawable3D) d).exportToPrinter3D(exportToPrinter3D,
							exportSurface);
				}
			}
		}
	}

	@Override
    synchronized protected void updateGeometriesVisibility() {
		if (shouldBePackedForManager()) {
			for (DrawableND d : drawables) {
				if (d.createdByDrawList()) {
					((Drawable3D) d).updateGeometriesVisibility();
				}
			}
		}
	}

	@Override
    synchronized final protected void setGeometriesVisibility(boolean visible) {
		if (shouldBePackedForManager()) {
			for (DrawableND d : drawables) {
				if (d.createdByDrawList()) {
					((Drawable3D) d).setGeometriesVisibility(visible);
				}
			}
		}
	}

	@Override
    synchronized final protected void updateGeometriesColor() {
		if (shouldBePackedForManager()) {
			for (DrawableND d : drawables) {
				if (d.createdByDrawList()) {
					((Drawable3D) d).updateGeometriesColor();
				}
			}
		}
	}

	@Override
	protected void recordTrace() {
		if (!shouldBePackedForManager()) {
			super.recordTrace();
		}
	}

	@Override
    synchronized final protected void updateForViewNotVisible() {
		if (shouldBePackedForManager()) {
			for (DrawableND d : drawables) {
				if (d.createdByDrawList()) {
					((Drawable3D) d).updateForViewNotVisible();
				}
			}
		}
	}

	@Override
    synchronized public void removeFromGL() {
		super.removeFromGL();
		if (shouldBePackedForManager()) {
			for (DrawableND d : drawables) {
				if (d.createdByDrawList()) {
					((Drawable3D) d).removeFromGL();
				}
			}
		}
	}

	@Override
	public boolean shouldBePacked() {
		return true;
	}

}
