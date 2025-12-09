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
public class DrawIntersectionCurve3D extends Drawable3DCurves
		implements Previewable {

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

		drawables = new ArrayList<>();

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
	public void updateIntersectionCurveVisibility() {
        for (Drawable3D d : drawables) {
            d.setGeometriesVisibility(intersectionCurveVisibility);
        }
    }

	@Override
	protected void updateForView() {

		for (Drawable3D d : drawables) {
			d.updateForView();
		}
	}

	// //////////////////////////////
	// Previewable interface

	@Override
	public void updateMousePos(double xRW, double yRW) {
		// TODO Auto-generated method stub

	}

	@Override
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

		setZPick(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY,
				hitting.discardPositiveHits(), Double.POSITIVE_INFINITY);

		for (Drawable3D d : drawables) {
			if (d.hit(hitting)) {
				if (d.getZPickNear() > getZPickNear()) {
					setPickingType(d.getPickingType());
					setZPick(d.getZPickNear(), d.getZPickFar(),
							hitting.discardPositiveHits(),
							d.getPositionOnHitting());
				}
				ret = true;
			}
		}

		return ret;

	}

	@Override
	public void addToDrawable3DLists(Drawable3DLists lists) {
		// not needed with packing
	}

	@Override
	public void removeFromDrawable3DLists(Drawable3DLists lists) {
        // not needed with packing
	}

}
