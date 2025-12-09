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

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.Hits3D;
import org.geogebra.common.geogebra3D.euclidian3D.Hitting;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer.PickingType;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.Coords;

public final class DrawText3D extends Drawable3DCurves {

	/**
	 * @param a_view3d
	 *            view
	 * @param text
	 *            text
	 */
	public DrawText3D(EuclidianView3D a_view3d, GeoText text) {
		super(a_view3d, text);
		((DrawLabel3DForText) label).setGeo(text);

		setPickingType(PickingType.POINT_OR_CURVE);
	}

	@Override
	protected DrawLabel3D newDrawLabel3D(EuclidianView3D view3D) {
		return new DrawLabel3DForText(view3D, this);
	}

	@Override
	protected void updateForView() {
		// no change on zoom / rotate
	}

	@Override
	protected boolean updateForItSelf() {
		// setLabelWaitForUpdate();
		return true;
	}

	@Override
	public void drawGeometry(Renderer renderer) {
		// see drawlabel
	}

	@Override
	protected void updateLabel() {
		if (getView3D().drawsLabels()) {
			super.updateLabel();
		}
	}

	@Override
	public CaptionText createStaticCaption3D(GeoElement geo) {
		return new StaticText3D(geo);

	}

	@Override
	protected void updateLabelPosition() {
		label.updatePosition(getView3D().getRenderer());
	}

	// private boolean isLocationDefined;

	@Override
	protected boolean isLabelVisible() {
		return isVisible(); // && isLocationDefined;
	}

	@Override
	public Coords getLabelPosition() {

		// isLocationDefined = true;

		GeoText text = (GeoText) getGeoElement();

		// compute location of text
		if (text.isAbsoluteScreenLocActive()) {
			return new Coords(text.getAbsoluteScreenLocX(),
					text.getAbsoluteScreenLocY(), 0, 1);
		}

		GeoPointND loc = text.getStartPoint();

		if (loc == null) {
			// return new
			// Coords(getView3D().getXZero(),getView3D().getYZero(),0,1);
			return new Coords(0, 0, 0, 1);
		}

		if (!loc.isDefined()) {
			// isLocationDefined = false;
			return null;
		}

		return loc.getInhomCoordsInD3();
	}

	@Override
	public int getPickOrder() {

		return DRAW_PICK_ORDER_TEXT;
	}

	@Override
	public boolean hit(Hitting hitting) {

		if (hitting.hitLabel(label)) {
			setZPick(label.getDrawZ(), label.getDrawZ(),
					hitting.discardPositiveHits(), -label.getDrawZ());
			return true;
		}

		return false;
	}

	@Override
	protected boolean hitLabel(Hitting hitting, Hits3D hits) {
		return false; // no label
	}

	@Override
	public void addToDrawable3DLists(Drawable3DLists lists) {
		addToDrawable3DLists(lists, DRAW_TYPE_TEXTS);
	}

	@Override
	public void removeFromDrawable3DLists(Drawable3DLists lists) {
		removeFromDrawable3DLists(lists, DRAW_TYPE_TEXTS);
	}

	/**
	 * Draw this or not, depending on abs flag.
	 * 
	 * @param renderer
	 *            renderer
	 * @param absolute
	 *            whether only absolute or only relative text should be painted
	 */
	public void drawForAbsolutePosition(Renderer renderer, boolean absolute) {
		GeoText text = (GeoText) getGeoElement();
		if (absolute == text.isAbsoluteScreenLocActive()) {
			drawLabel(renderer);
		}
	}

	@Override
	public boolean isVisible() {
		if  (getGeoElement().isLabelSet() && createdByDrawList()) {
			return false;
		}
		return super.isVisible();
	}
}
