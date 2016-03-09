package org.geogebra.common.geogebra3D.euclidian3D.draw;

import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.Hitting;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer.PickingType;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3DLimited;
import org.geogebra.common.kernel.geos.GProperty;

/**
 * Class for drawing quadrics.
 * 
 * @author mathieu
 *
 */
public class DrawQuadric3DLimited extends Drawable3D {

	private DrawConic3D drawBottom, drawTop;
	private DrawQuadric3DPart drawSide;

	/**
	 * common constructor
	 * 
	 * @param view3d
	 *            3D view
	 * @param geo
	 *            limited quadric
	 */
	public DrawQuadric3DLimited(EuclidianView3D view3d, GeoQuadric3DLimited geo) {
		super(view3d, geo);

		drawBottom = new DrawConic3D(view3d, geo.getBottom());
		drawTop = new DrawConic3D(view3d, geo.getTop());
		drawSide = new DrawQuadric3DPart(view3d, geo.getSide());

		// for hightlight
		drawBottom.setCreatedByDrawList(this);
		drawTop.setCreatedByDrawList(this);
		drawSide.setCreatedByDrawList(this);

		setPickingType(PickingType.POINT_OR_CURVE);

	}

	// drawing

	@Override
	public void drawGeometry(Renderer renderer) {

		// no need
	}

	@Override
	public void drawOutline(Renderer renderer) {

		drawBottom.drawOutline(renderer);
		drawTop.drawOutline(renderer);

	}

	@Override
	public void drawGeometryHidden(Renderer renderer) {

		// not used
	}

	@Override
	public void drawHidden(Renderer renderer) {
		drawBottom.drawHidden(renderer);
		drawTop.drawHidden(renderer);

	}

	@Override
	protected void drawGeometryForPicking(Renderer renderer, PickingType type) {
		drawBottom.drawGeometryForPicking(renderer, type);
		drawTop.drawGeometryForPicking(renderer, type);
		drawSide.drawGeometryForPicking(renderer, type);
	}

	@Override
	public int getPickOrder() {
		return DRAW_PICK_ORDER_SURFACE;
	}

	@Override
	public void addToDrawable3DLists(Drawable3DLists lists) {

		addToDrawable3DLists(lists, DRAW_TYPE_SURFACES);
		addToDrawable3DLists(lists, DRAW_TYPE_CURVES);

	}

	@Override
	public void removeFromDrawable3DLists(Drawable3DLists lists) {

		removeFromDrawable3DLists(lists, DRAW_TYPE_SURFACES);
		removeFromDrawable3DLists(lists, DRAW_TYPE_CURVES);

	}

	@Override
	protected boolean updateForItSelf() {

		// no need
		return true;

	}

	@Override
	protected void updateForView() {

		// no need
	}

	@Override
	public void update() {

		drawBottom.update();
		drawTop.update();
		drawSide.update();
	}

	@Override
	public void setWaitForUpdateVisualStyle(GProperty prop) {

		drawBottom.setWaitForUpdateVisualStyle(prop);
		drawTop.setWaitForUpdateVisualStyle(prop);
		drawSide.setWaitForUpdateVisualStyle(prop);
	}

	@Override
	public void setWaitForUpdate() {

		drawBottom.setWaitForUpdate();
		drawTop.setWaitForUpdate();
		drawSide.setWaitForUpdate();
	}

	@Override
	public void setWaitForReset() {

		drawBottom.setWaitForReset();
		drawTop.setWaitForReset();
		drawSide.setWaitForReset();
	}

	@Override
	public void drawNotTransparentSurface(Renderer renderer) {

		drawBottom.drawNotTransparentSurface(renderer);
		drawTop.drawNotTransparentSurface(renderer);
		drawSide.drawNotTransparentSurface(renderer);
	}

	@Override
	public void drawTransp(Renderer renderer) {

		drawBottom.drawTransp(renderer);
		drawTop.drawTransp(renderer);
		drawSide.drawTransp(renderer);
	}

	@Override
	public void drawHiding(Renderer renderer) {

		drawBottom.drawHiding(renderer);
		drawTop.drawHiding(renderer);
		drawSide.drawHiding(renderer);

	}

	@Override
	public boolean isTransparent() {
		if (getPickingType() == PickingType.SURFACE) {
			return getAlpha() <= EuclidianController.MAX_TRANSPARENT_ALPHA_VALUE;
		}

		return false;
	}

	@Override
	protected double getColorShift() {
		return COLOR_SHIFT_SURFACE;
	}

	@Override
	public boolean hit(Hitting hitting) {

		if (waitForReset) { // prevent NPE
			return false;
		}

		double d = Double.NaN;
		PickingType pickingType = PickingType.SURFACE;
		if (drawBottom.hit(hitting)) {
			d = drawBottom.getZPickNear();
			pickingType = drawBottom.getPickingType();
		}

		if (drawTop.hit(hitting)) {
			PickingType pickingTypeTop = drawTop.getPickingType();
			if (pickingType == PickingType.SURFACE) {
				if (pickingTypeTop == PickingType.SURFACE) {
					double dTop = drawTop.getZPickNear();
					if (Double.isNaN(d) || dTop > d) {
						d = dTop;
					}
				} else { // pickingTypeTop == PickingType.POINT_OR_CURVE
					// TODO: opaque bottom
					d = drawTop.getZPickNear();
					pickingType = pickingTypeTop;
				}
			} else { // pickingType == PickingType.POINT_OR_CURVE
				if (pickingTypeTop == PickingType.SURFACE) {
					// TODO: opaque top
				} else { // pickingTypeTop == PickingType.POINT_OR_CURVE
					double dTop = drawTop.getZPickNear();
					if (Double.isNaN(d) || dTop > d) {
						d = dTop;
					}
				}
			}
		}


		if (pickingType == PickingType.POINT_OR_CURVE) {
			// TODO opaque side
			setZPick(d, d);
			setPickingType(PickingType.POINT_OR_CURVE);
			return true;
		}

		if (drawSide.hit(hitting)) {
			double dSide = drawSide.getZPickNear();
			if (Double.isNaN(d) || dSide > d) {
				d = dSide;
			}
			setZPick(d, d);
			setPickingType(PickingType.SURFACE);
			return true;
		}


		return false;
	}

}
