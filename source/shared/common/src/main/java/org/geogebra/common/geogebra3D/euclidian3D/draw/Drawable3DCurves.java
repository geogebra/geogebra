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
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer.PickingType;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;

/**
 * for "solid" drawables, like lines, segments, etc. these are drawable that are
 * not to become transparent
 * 
 * @author ggb3D
 */

public abstract class Drawable3DCurves extends Drawable3D {

	/**
	 * common constructor
	 * 
	 * @param a_view3d
	 *            view
	 * @param a_geo
	 *            curve
	 */
	public Drawable3DCurves(EuclidianView3D a_view3d, GeoElement a_geo) {
		super(a_view3d, a_geo);
		setPickingType(PickingType.POINT_OR_CURVE);
	}

	/**
	 * constructor for previewables
	 * 
	 * @param a_view3d
	 *            3D view
	 */
	public Drawable3DCurves(EuclidianView3D a_view3d) {
		super(a_view3d);
		setPickingType(PickingType.POINT_OR_CURVE);
	}

	@Override
	public void drawOutline(Renderer renderer) {

		if (isVisible()) {

			setHighlightingColor();

			renderer.getTextures()
					.setDashFromLineType(getGeoElement().getLineType());
			drawGeometry(renderer);
		}

		drawTracesOutline(renderer, false);

	}

	// ///////////////////////////////////////
	// DRAWING GEOMETRIES

	@Override
	public void drawGeometryHidden(Renderer renderer) {

		drawGeometry(renderer);
	}

	@Override
	public void drawHidden(Renderer renderer) {
		super.drawHidden(renderer);

		drawTracesOutline(renderer, true);
	}

	@Override
	public void drawHiding(Renderer renderer) {
		// method not used for solid drawables
	}

	@Override
	public void drawTransp(Renderer renderer) {
		// methods not used for solid drawables
	}

	@Override
	public void drawNotTransparentSurface(Renderer renderer) {
		// methods not used for solid drawables
	}

	@Override
	public boolean isTransparent() {
		return false;
	}

	@Override
	public void addToDrawable3DLists(Drawable3DLists lists) {
		addToDrawable3DLists(lists, DRAW_TYPE_CURVES);
	}

	@Override
	public void removeFromDrawable3DLists(Drawable3DLists lists) {
		removeFromDrawable3DLists(lists, DRAW_TYPE_CURVES);
	}

	@Override
	protected double getColorShift() {
		return COLOR_SHIFT_CURVES;
	}

	@Override
    public void setWaitForUpdateVisualStyle(GProperty prop) {
        if (prop == GProperty.COLOR || prop == GProperty.HIGHLIGHT) {
            setWaitForUpdateColor();
        } else if (prop == GProperty.VISIBLE) {
            setWaitForUpdateVisibility();
        } else {
            setWaitForUpdateOtherStyles(prop);
        }
    }

	private void setWaitForUpdateOtherStyles(GProperty prop) {
		super.setWaitForUpdateVisualStyle(prop);
		if (prop == GProperty.ANGLE_STYLE || prop == GProperty.LINE_STYLE
				|| prop == GProperty.COMBINED
				|| prop == GProperty.POINT_STYLE) {
			// also update for e.g. line width
			setWaitForUpdate();
		}
	}

	@Override
	protected void updateForViewVisible() {
		if (!waitForUpdate()) {
			updateForView();
		}
	}

	@Override
	public void disposePreview() {
		removePreviewFromGL();
		super.disposePreview();
	}

	@Override
	protected void updateGeometriesVisibility() {
		boolean isVisible = isVisible();
		if (geometriesSetVisible != isVisible) {
			setGeometriesVisibility(isVisible);
		}
	}

	@Override
	public int getReusableSurfaceIndex() {
		if (shouldBePackedForManager()) {
			return addToTracesPackingBuffer(getSurfaceIndex());
		}
		return super.getReusableSurfaceIndex();
	}

	@Override
	protected int getReusableGeometryIndex() {
		if (shouldBePackedForManager()) {
			return addToTracesPackingBuffer(getGeometryIndex());
		}
		return super.getReusableGeometryIndex();
	}

	@Override
	protected void recordTrace() {
		if (!shouldBePackedForManager()) {
			super.recordTrace();
		}
	}

	@Override
	protected void clearTraceForViewChangedByZoomOrTranslate() {
		if (shouldBePackedForManager()) {
			if (tracesPackingBuffer != null) {
				while (!tracesPackingBuffer.isEmpty()) {
					doRemoveGeometryIndex(tracesPackingBuffer.pop());
				}
			}
		} else {
			super.clearTraceForViewChangedByZoomOrTranslate();
		}
	}

	@Override
    protected void updateForViewNotVisible() {
        if (getView3D().viewChangedByZoom()) {
            // will be updated if visible again
            setWaitForUpdate();
        }
        updateGeometriesVisibility();
	}

	@Override
	public boolean shouldBePacked() {
		return true;
	}

}
