package org.geogebra.common.geogebra3D.euclidian3D.draw;

import java.util.ArrayList;

import org.geogebra.common.euclidian.Previewable;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.Hitting;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer.PickingType;
import org.geogebra.common.kernel.geos.GeoElement;

/**
 * Class for drawing multiple polygons within intersection curve.
 * 
 * @author matthieu
 *
 */
public class DrawIntersectionCurve3D extends Drawable3DCurves implements
		Previewable {

	private ArrayList<Drawable3D> drawables;

	/**
	 * Common constructor
	 * 
	 * @param a_view3D
	 *            3D view
	 * @param geo
	 *            first geo
	 */
	public DrawIntersectionCurve3D(EuclidianView3D a_view3D, GeoElement geo) {

		super(a_view3D, geo);

		drawables = new ArrayList<Drawable3D>();

		setPickingType(PickingType.POINT_OR_CURVE);

	}

	/**
	 * add a polygon to draw
	 * 
	 * @param d
	 *            drawable
	 */
	public void add(Drawable3D d) {
		drawables.add(d);
	}

	// drawing

	@Override
	public void drawGeometry(Renderer renderer) {

		for (Drawable3D d : drawables) {
			d.drawGeometry(renderer);
		}

	}

	@Override
	public int getPickOrder() {

		return DRAW_PICK_ORDER_PATH;

	}

	@Override
	protected boolean updateForItSelf() {

		for (Drawable3D d : drawables) {
			d.updateForItSelf();
		}

		return true;

	}

	@Override
	protected void updateForView() {

		for (Drawable3D d : drawables) {
			d.updateForView();
		}
	}

	// //////////////////////////////
	// Previewable interface

	public void updateMousePos(double xRW, double yRW) {
		// TODO Auto-generated method stub

	}

	public void updatePreview() {

		setWaitForUpdate();

	}

	@Override
	public void disposePreview() {
		super.disposePreview();

		for (Drawable3D d : drawables) {
			d.disposePreview();
		}

	}

	@Override
	public boolean hit(Hitting hitting) {

		if (waitForReset) { // prevent NPE
			return false;
		}

		boolean ret = false;

		setZPick(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);

		for (Drawable3D d : drawables) {
			if (d.hit(hitting)) {
				if (d.getZPickNear() > getZPickNear()) {
					setPickingType(d.getPickingType());
					setZPick(d.getZPickNear(), d.getZPickFar());
				}
				ret = true;
			}
		}

		return ret;

	}

	@Override
	public void addToDrawable3DLists(Drawable3DLists lists) {
		for (Drawable3D d : drawables) {
			d.addToDrawable3DLists(lists);
		}
	}

}
