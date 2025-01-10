package org.geogebra.common.geogebra3D.euclidian3D.draw;

import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.Hitting;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer.PickingType;
import org.geogebra.common.geogebra3D.euclidian3D.printer3D.ExportToPrinter3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3DLimited;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * Class for drawing quadrics.
 * 
 * @author mathieu
 *
 */
public class DrawQuadric3DLimited extends Drawable3D {

	private DrawConic3D drawBottom;
	private DrawConic3D drawTop;
	private DrawQuadric3DPart drawSide;

	/**
	 * common constructor
	 * 
	 * @param view3d
	 *            3D view
	 * @param geo
	 *            limited quadric
	 */
	public DrawQuadric3DLimited(EuclidianView3D view3d,
			GeoQuadric3DLimited geo) {
		super(view3d, geo);

		drawBottom = new DrawConic3D(view3d, geo.getBottom());
		drawTop = new DrawConic3D(view3d, geo.getTop());
		drawSide = new DrawQuadric3DPart(view3d, geo.getSide());

		// for highlight
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
		// for outline update
		drawBottom.updateForView();
		drawTop.updateForView();
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
			return getAlpha() <= EuclidianController.MAX_TRANSPARENT_ALPHA_VALUE_INT;
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
		double positionOnHitting = Double.NaN;
		PickingType pickingType = PickingType.SURFACE;
		if (drawBottom.hit(hitting)) {
			d = drawBottom.getZPickNear();
			pickingType = drawBottom.getPickingType();
			positionOnHitting = drawBottom.getPositionOnHitting();
		}

		if (drawTop.hit(hitting)) {
			PickingType pickingTypeTop = drawTop.getPickingType();
			if (pickingType == PickingType.SURFACE) {
				if (pickingTypeTop == PickingType.SURFACE) {
					double dTop = drawTop.getZPickNear();
					if (Double.isNaN(d) || dTop > d) {
						d = dTop;
						positionOnHitting = drawTop.getPositionOnHitting();
					}
				} else { // pickingTypeTop == PickingType.POINT_OR_CURVE
					// TODO: opaque bottom
					d = drawTop.getZPickNear();
					pickingType = pickingTypeTop;
					positionOnHitting = drawTop.getPositionOnHitting();
				}
			} else { // pickingType == PickingType.POINT_OR_CURVE
				if (pickingTypeTop == PickingType.SURFACE) {
					// TODO: opaque top
				} else { // pickingTypeTop == PickingType.POINT_OR_CURVE
					double dTop = drawTop.getZPickNear();
					if (Double.isNaN(d) || dTop > d) {
						d = dTop;
						positionOnHitting = drawTop.getPositionOnHitting();
					}
				}
			}
		}

		if (pickingType == PickingType.POINT_OR_CURVE) {
			// TODO opaque side
			setZPick(d, d, hitting.discardPositiveHits(), positionOnHitting);
			setPickingType(PickingType.POINT_OR_CURVE);
			return true;
		}

		if (drawSide.hit(hitting)) {
			double dSide = drawSide.getZPickNear();
			if (Double.isNaN(d) || dSide > d) {
				d = dSide;
			}
			setZPick(d, d, hitting.discardPositiveHits(), positionOnHitting);
			setPickingType(PickingType.SURFACE);
			return true;
		}

		return false;
	}

	@Override
	protected void updateGeometriesVisibility() {
		if (shouldBePackedForManager()) {
			drawBottom.updateGeometriesVisibility();
			drawTop.updateGeometriesVisibility();
			drawSide.updateGeometriesVisibility();
		}
	}

	@Override
	final protected void setGeometriesVisibility(boolean visible) {
		if (shouldBePackedForManager()) {
			drawBottom.setGeometriesVisibility(visible);
			drawTop.setGeometriesVisibility(visible);
			drawSide.setGeometriesVisibility(visible);
		}
	}

	@Override
	final protected void updateGeometriesColor() {
		if (shouldBePackedForManager()) {
			drawBottom.updateGeometriesColor();
			drawTop.updateGeometriesColor();
			drawSide.updateGeometriesColor();
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
			drawBottom.updateForViewNotVisible();
			drawTop.updateForViewNotVisible();
			drawSide.updateForViewNotVisible();
		}
	}

	@Override
	public void removeFromGL() {
		super.removeFromGL();
		if (shouldBePackedForManager()) {
			drawBottom.removeFromGL();
			drawTop.removeFromGL();
			drawSide.removeFromGL();
		}
	}

	@Override
	public boolean shouldBePacked() {
		return true;
	}

	@Override
	public void enlargeBounds(Coords min, Coords max,
			boolean dontExtend) {
		drawBottom.enlargeBounds(min, max, dontExtend);
		drawTop.enlargeBounds(min, max, dontExtend);
		drawSide.enlargeBounds(min, max, dontExtend);
	}

	@Override
	public void exportToPrinter3D(ExportToPrinter3D exportToPrinter3D, boolean exportSurface) {
		if (isVisible()) {
			drawBottom.exportToPrinter3D(exportToPrinter3D, exportSurface);
			drawTop.exportToPrinter3D(exportToPrinter3D, exportSurface);
			drawSide.exportToPrinter3D(exportToPrinter3D, exportSurface);
		}
	}

}
