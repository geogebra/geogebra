package org.geogebra.common.geogebra3D.euclidian3D.draw;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer.PickingType;
import org.geogebra.common.kernel.geos.GeoElement;

/**
 * 
 * @author ggb3D
 *
 *         for "solid" drawables, like lines, segments, etc. these are drawable
 *         that are not to become transparent
 *
 */

public abstract class Drawable3DCurves extends Drawable3D {

	/**
	 * common constructor
	 * 
	 * @param a_view3d
	 * @param a_geo
	 */
	public Drawable3DCurves(EuclidianView3D a_view3d, GeoElement a_geo) {
		super(a_view3d, a_geo);
		setPickingType(PickingType.POINT_OR_CURVE);
	}

	/**
	 * constructor for previewables
	 * 
	 * @param a_view3d
	 */
	public Drawable3DCurves(EuclidianView3D a_view3d) {
		super(a_view3d);
		setPickingType(PickingType.POINT_OR_CURVE);
	}

	@Override
	public void drawOutline(Renderer renderer) {

		if (isVisible()) {

			setHighlightingColor();

			renderer.getTextures().setDashFromLineType(
					getGeoElement().getLineType());
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

	// methods not used for solid drawables
	@Override
	public void drawHiding(Renderer renderer) {
	}

	@Override
	public void drawTransp(Renderer renderer) {
	}

	@Override
	public void drawNotTransparentSurface(Renderer renderer) {
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
	public void setWaitForUpdateVisualStyle() {
		super.setWaitForUpdateVisualStyle();

		// also update for e.g. line width
		setWaitForUpdate();
	}

}
