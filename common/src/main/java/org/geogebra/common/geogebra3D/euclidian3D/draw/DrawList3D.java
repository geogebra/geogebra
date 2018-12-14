package org.geogebra.common.geogebra3D.euclidian3D.draw;

import org.geogebra.common.euclidian.DrawableND;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.Hitting;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer.PickingType;
import org.geogebra.common.geogebra3D.euclidian3D.printer3D.ExportToPrinter3D;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;

/**
 * Class for drawing GeoList (3D part)
 * 
 * @author matthieu
 *
 */
public class DrawList3D extends Drawable3D {

	private GeoList geoList;
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
	public DrawList3D(EuclidianView3D view3D, GeoList geo) {
		super(view3D, geo);
		drawables = new DrawList3DArray(view3D, this);
		this.geoList = geo;
		drawable3DLists = new Drawable3DListsForDrawList3D(view3D);

		setPickingType(PickingType.POINT_OR_CURVE);
	}

	@Override
	protected boolean updateForItSelf() {

		// Log.debug("LIST -- "+getGeoElement());

		isVisible = geoList.isEuclidianVisible();
		if (!isVisible) {
			return true;
		}

		// getView3D().removeGeoToPick(drawables.size());

		// go through list elements and create and/or update drawables
		int size = geoList.size();
		drawables.ensureCapacity(size);
		int oldDrawableSize = drawables.size();

		int drawablePos = 0;
		for (int i = 0; i < size; i++) {
			GeoElement listElement = geoList.get(i);
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

		// update for list of lists
		for (int i = 0; i < drawables.size(); i++) {
			Drawable3D d = (Drawable3D) drawables.get(i);
			if (!d.getGeoElement().isLabelSet()) {
				if (d.waitForUpdate()) {
					d.update();
				}
			}
		}

		return true;
	}

	@Override
	public void addLastTrace() {
		for (int i = 0; i < drawables.size(); i++) {
			Drawable3D d = (Drawable3D) drawables.get(i);
			d.addLastTrace();
		}
	}

	@Override
	protected void updateForView() {
		int size = drawables.size();
		for (int i = 0; i < size; i++) {
			Drawable3D d = (Drawable3D) drawables.get(i);
			if (createdByDrawList() || !d.getGeoElement().isLabelSet()) {
				d.updateForView();
				if (d.waitForUpdate()) {
					setWaitForUpdate();
				}
			}
		}
	}

	@Override
	protected void clearTraceForViewChangedByZoomOrTranslate() {
		int size = drawables.size();
		for (int i = 0; i < size; i++) {
			Drawable3D d = (Drawable3D) drawables.get(i);
			if (createdByDrawList() || !d.getGeoElement().isLabelSet()) {
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
	public Drawable3DListsForDrawList3D getDrawable3DLists() {
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
	protected void updateLabel() {
		for (DrawableND d : drawables) {
			if (d instanceof DrawList3D || d instanceof DrawText3D) {
				((Drawable3D) d).updateLabel();
			}
		}
	}

	@Override
	protected void updateLabelPosition() {
		for (DrawableND d : drawables) {
			if (d instanceof DrawList3D || d instanceof DrawText3D) {
				((Drawable3D) d).updateLabelPosition();
			}
		}

	}

	@Override
	protected double getColorShift() {
		return COLOR_SHIFT_NONE; // not needed here
	}

	@Override
	public void setWaitForUpdateVisualStyle(GProperty prop) {

		super.setWaitForUpdateVisualStyle(prop);
		for (DrawableND d : drawables) {
			d.setWaitForUpdateVisualStyle(prop);
		}

		// also update for e.g. line width
		setWaitForUpdate();
	}

	@Override
	public void setWaitForReset() {

		super.setWaitForReset();
		for (DrawableND d : drawables) {
			((Drawable3D) d).setWaitForReset();
		}
	}

	@Override
	protected Drawable3D getDrawablePicked(Drawable3D drawableSource) {

		pickOrder = drawableSource.getPickOrder();
		setPickingType(drawableSource.getPickingType());

		return super.getDrawablePicked(drawableSource);
	}

	@Override
	public int getPickOrder() {
		return pickOrder;
	}

	@Override
	public boolean hit(Hitting hitting) {

		boolean ret = false;

		double listZNear = Double.NEGATIVE_INFINITY;
		double listZFar = Double.NEGATIVE_INFINITY;
		for (DrawableND d : drawables) {
			final Drawable3D d3d = (Drawable3D) d;
			if (d3d.hitForList(hitting)) {
				double zNear = d3d.getZPickNear();
				double zFar = d3d.getZPickFar();
				if (!ret || zNear > listZNear) {
					listZNear = zNear;
					listZFar = zFar;
					setPickingType(d3d.getPickingType());
					pickOrder = d3d.getPickOrder();
					ret = true;
				}
			}
		}

		if (pickOrder == DRAW_PICK_ORDER_POINT) { // list of points are paths
			pickOrder = DRAW_PICK_ORDER_PATH;
		}

		if (ret) {
			setZPick(listZNear, listZFar, hitting.discardPositiveHits());
		}

		return ret;
	}

	@Override
	public void enlargeBounds(Coords min, Coords max, boolean reduceWhenClipped) {
		for (DrawableND d : drawables) {
			((Drawable3D) d).enlargeBounds(min, max, reduceWhenClipped);
		}
	}

	@Override
	public void exportToPrinter3D(ExportToPrinter3D exportToPrinter3D, boolean exportSurface) {
		if (isVisible()) {
			for (DrawableND d : drawables) {
				((Drawable3D) d).exportToPrinter3D(exportToPrinter3D,
						exportSurface);
			}
		}
	}

	@Override
	protected void updateGeometriesVisibility() {
		if (shouldBePackedForManager()) {
			for (DrawableND d : drawables) {
				((Drawable3D) d).updateGeometriesVisibility();
			}
		}
	}

	@Override
	final protected void setGeometriesVisibility(boolean visible) {
		if (shouldBePackedForManager()) {
			for (DrawableND d : drawables) {
				((Drawable3D) d).setGeometriesVisibility(visible);
			}
		}
	}

	@Override
	final protected void updateGeometriesColor() {
		if (shouldBePackedForManager()) {
			for (DrawableND d : drawables) {
				((Drawable3D) d).updateGeometriesColor();
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
	final protected void updateForViewNotVisible() {
		if (shouldBePackedForManager()) {
			for (DrawableND d : drawables) {
				((Drawable3D) d).updateForViewNotVisible();
			}
		}
	}

	@Override
	public void removeFromGL() {
		super.removeFromGL();
		if (shouldBePackedForManager()) {
			for (DrawableND d : drawables) {
				((Drawable3D) d).removeFromGL();
			}
		}
	}

	@Override
	public boolean shouldBePacked() {
		return true;
	}

}
