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

import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer.PickingType;
import org.geogebra.common.kernel.geos.GeoElement;

/**
 * Class for drawing surfaces
 * 
 * @author matthieu
 *
 */
public abstract class Drawable3DSurfaces extends Drawable3D {

	/**
	 * common constructor
	 * 
	 * @param a_view3d
	 *            view
	 * @param a_geo
	 *            surface
	 */
	public Drawable3DSurfaces(EuclidianView3D a_view3d, GeoElement a_geo) {
		super(a_view3d);
		init(a_geo);

	}

	@Override
	protected void init(GeoElement a_plane3D) {
		super.init(a_plane3D);
		setPickingType(PickingType.SURFACE);
	}

	/**
	 * common constructor for previewable
	 * 
	 * @param a_view3d
	 *            view
	 */
	public Drawable3DSurfaces(EuclidianView3D a_view3d) {
		super(a_view3d);
		setPickingType(PickingType.SURFACE);

	}

	/**
	 * draws the geometry that hides other drawables (for dashed curves)
	 * 
	 * @param renderer
	 *            renderer
	 */
	abstract void drawGeometryHiding(Renderer renderer);

	@Override
	public void drawHiding(Renderer renderer) {

		if (isVisible() && hasTransparentAlpha()) {
			drawSurfaceGeometry(renderer);
		}

		drawTracesHidingSurface(renderer);
	}

	/**
	 * @param renderer
	 *            renderer
	 */
	protected abstract void drawSurfaceGeometry(Renderer renderer);

	@Override
	public void drawTransp(Renderer renderer) {

		if (isVisible() && hasTransparentAlpha()) {
			setSurfaceHighlightingColor();
			drawSurfaceGeometry(renderer);
		}

		drawTracesTranspSurface(renderer);
	}

	@Override
	protected void drawGeometryForPickingIntersection(Renderer renderer) {
		drawOutline(renderer);
	}

	// method used only if surface is not transparent
	@Override
	public void drawNotTransparentSurface(Renderer renderer) {

		if (isVisible() && getAlpha() == 255) {
			setSurfaceHighlightingColor();
			drawSurfaceGeometry(renderer);
		}

		drawTracesNotTranspSurface(renderer);
	}

	@Override
	public void updateColors() {
		updateAlpha();
		setColorsOutlined();
	}

	/*
	 * protected boolean updateForItSelf(){
	 * 
	 * updateColors();
	 * 
	 * return true; }
	 */

	@Override
	protected void updateForView() {
		// overridden in subclasses
	}

	@Override
	public boolean isTransparent() {
		return getAlpha() <= EuclidianController.MAX_TRANSPARENT_ALPHA_VALUE_INT;
	}

	@Override
	public void addToDrawable3DLists(Drawable3DLists lists) {
		addToDrawable3DLists(lists, DRAW_TYPE_SURFACES);
	}

	@Override
	public void removeFromDrawable3DLists(Drawable3DLists lists) {
		removeFromDrawable3DLists(lists, DRAW_TYPE_SURFACES);
	}

	@Override
	protected double getColorShift() {
		return COLOR_SHIFT_SURFACE;
	}

	@Override
	protected void updateForViewVisible() {
		updateGeometriesVisibility();
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
	final protected void setGeometriesVisibility(boolean visible) {
		setGeometriesVisibilityWithSurface(visible);
	}

	@Override
	final protected void updateGeometriesColor() {
		updateGeometriesColor(true);
	}

	@Override
	final public int getReusableSurfaceIndex() {
		if (shouldBePackedForManager()) {
			return addToTracesPackingBuffer(getSurfaceIndex());
		}
		return super.getReusableSurfaceIndex();
	}

	@Override
	final protected int getReusableGeometryIndex() {
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
    final protected void updateForViewNotVisible() {
        if (willNeedUpdateOnVisibleAgain()) {
            setWaitForUpdate();
        }
        updateGeometriesVisibility();
    }

	/**
	 * 
	 * @return true when updated for view and not visible, but will need update
	 *         when visible again
	 */
	protected boolean willNeedUpdateOnVisibleAgain() {
		return getView3D().viewChanged();
	}

	@Override
	public boolean shouldBePacked() {
		return true;
	}
}
