package org.geogebra.common.geogebra3D.euclidian3D.draw;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.PlotterBrush;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.PlotterSurface;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoAnglePlanes;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.algos.AlgoAngle;
import org.geogebra.common.kernel.algos.AlgoAnglePointsND;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.plugin.EuclidianStyleConstants;

/**
 * @author ggb3D
 * 
 *         Drawable for GeoConic3D
 *
 */
public class DrawAngle3D extends Drawable3DCurves {

	private Coords labelCenter = new Coords(4);
	private Coords vn2 = new Coords(4);

	/**
	 * @param view3d
	 *            the 3D view where the conic is drawn
	 * @param geo
	 *            the angle to draw
	 */
	public DrawAngle3D(EuclidianView3D view3d, GeoAngle geo) {
		super(view3d, geo);
	}

	@Override
	protected void updateColors() {
		updateAlpha();
		setColorsOutlined();
	}

	@Override
	public void drawGeometry(Renderer renderer) {

		renderer.getGeometryManager().draw(getGeometryIndex());

	}

	// method used only if surface is not transparent
	@Override
	public void drawNotTransparentSurface(Renderer renderer) {

		if (!isVisible()) {
			return;
		}

		if (getAlpha() < 1)
			return;

		setSurfaceHighlightingColor();

		drawSurfaceGeometry(renderer);

	}

	private boolean angleVisible;

	@Override
	protected boolean isLabelVisible() {
		return angleVisible && super.isLabelVisible();
	}

	private Coords[] drawCoords = null;

	private Coords tmpCoords = new Coords(4), tmpCoords2, vn = new Coords(4);

	@Override
	protected boolean updateForItSelf() {

		// update alpha value
		updateColors();

		initCoords();

		Renderer renderer = getView3D().getRenderer();

		GeoAngle angle = (GeoAngle) getGeoElement();
		double a = angle.getDouble();

		if (Kernel.isZero(a)) { // nothing to display
			setGeometryIndex(-1);
			setSurfaceIndex(-1);
			angleVisible = false;
			return true;
		}

		double size = angle.getArcSize() / getView3D().getScale();
		double labelRadius = 1;

		angleVisible = true;

		AlgoElement algo = angle.getDrawAlgorithm();

		if (algo instanceof AlgoAngle) {

			if (!((AlgoAngle) algo).getCoordsInD3(drawCoords)) {
				setGeometryIndex(-1);
				setSurfaceIndex(-1);
				angleVisible = false;
				return true;
			}

			vn.setValues(((AlgoAngle) algo).getVn(), 3);

			Coords center;
			if (algo instanceof AlgoAnglePlanes) { // draw angle at center of
													// the screen
				center = drawCoords[0];
				double[] minmax = getView3D().getIntervalClipped(
						new double[] { Double.NEGATIVE_INFINITY,
								Double.POSITIVE_INFINITY }, center, vn);
				center.setAdd(center,
						tmpCoords.setMul(vn, (minmax[0] + minmax[1]) / 2));
			} else {
				center = drawCoords[0];
			}

			Coords v1 = drawCoords[1];
			v1.calcNorm();
			double l1 = v1.getNorm();
			v1.mulInside3(1 / l1);

			Coords v2 = drawCoords[2];
			v2.calcNorm();
			double l2 = v2.getNorm();
			v2.mulInside3(1 / l2);

			switch (angle.getAngleStyle()) {

			case NOTREFLEX:
				if (angle.getRawAngle() > Math.PI)
					vn.mulInside3(-1);
				break;

			case ISREFLEX:
				if (angle.getRawAngle() < Math.PI)
					vn.mulInside3(-1);
				break;
			}

			vn2.setCrossProduct(vn, v1);
			double a2 = a / 2;
			labelCenter.setAdd(tmpCoords.setMul(v1, Math.cos(a2)),
					labelCenter.setMul(v2, Math.sin(a2)));

			// size < points distances / 2
			if (algo instanceof AlgoAnglePointsND) {
				double l = Math.min(l1, l2) / 2;
				if (size > l)
					size = l;
			}

			labelRadius = size / 1.7;
			labelCenter.mulInside3(labelRadius);
			labelCenter.addInside(center);

			// 90°
			boolean show90degrees = getView3D().getApplication().rightAngleStyle != EuclidianStyleConstants.RIGHT_ANGLE_STYLE_NONE
					&& angle.isEmphasizeRightAngle()
					&& Kernel.isEqual(a, Kernel.PI_HALF);

			// outline
			PlotterBrush brush = renderer.getGeometryManager().getBrush();
			PlotterSurface surface = renderer.getGeometryManager().getSurface();

			brush.start(getReusableGeometryIndex());
			brush.setThickness(getGeoElement().getLineThickness(),
					(float) getView3D().getScale());

			if (show90degrees) {
				switch (getView3D().getRightAngleStyle()) {
				case EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE:
					size *= 0.7071067811865;
					brush.setAffineTexture(0.5f, 0.25f);
					// segments
					if (tmpCoords2 == null) {
						tmpCoords2 = new Coords(4);
					}
					brush.segment(center, tmpCoords.setAdd(center,
							tmpCoords.setMul(v1, size)));
					brush.segment(tmpCoords,
							tmpCoords2.setAdd(tmpCoords, v2.mul(size)));
					brush.segment(tmpCoords.setAdd(center,
							tmpCoords.setMul(v2, size)), tmpCoords2);
					brush.segment(center, tmpCoords);
					setGeometryIndex(brush.end());
					break;

				case EuclidianStyleConstants.RIGHT_ANGLE_STYLE_DOT:
					// arc
					brush.setAffineTexture(0f, 0f);
					brush.arc(center, v1, v2, size, 0, a, 60);
					brush.setAffineTexture(0.5f, 0.25f);
					// segments
					brush.segment(center, tmpCoords.setAdd(center,
							tmpCoords.setMul(v1, size)));
					brush.segment(center, tmpCoords.setAdd(center,
							tmpCoords.setMul(v2, size)));
					// dot (use surface plotter)
					surface.drawSphere(labelCenter, 2.5 * brush.getThickness(),
							16);

					setGeometryIndex(brush.end());
					break;

				case EuclidianStyleConstants.RIGHT_ANGLE_STYLE_L:
					size *= 0.7071067811865;
					double offset = size * 0.4;
					brush.setAffineTexture(0.5f, 0.25f);
					// segments
					if (tmpCoords2 == null) {
						tmpCoords2 = new Coords(4);
					}
					tmpCoords2.setAdd(center, tmpCoords2.setAdd(
							tmpCoords.setMul(v1, offset),
							tmpCoords2.setMul(v2, offset)));
					brush.segment(
							tmpCoords2,
							tmpCoords.setAdd(tmpCoords2,
									tmpCoords.setMul(v1, size)));
					brush.segment(
							tmpCoords2,
							tmpCoords.setAdd(tmpCoords2,
									tmpCoords.setMul(v2, size)));
					setGeometryIndex(brush.end());
					break;
				}
			} else {
				// arc
				brush.setAffineTexture(0f, 0f);
				brush.arc(center, v1, vn2, size, 0, a, 60);
				brush.setAffineTexture(0.5f, 0.25f);
				// segments
				brush.segment(center,
						tmpCoords.setAdd(center, tmpCoords.setMul(v1, size)));
				brush.segment(center,
						tmpCoords.setAdd(center, tmpCoords.setMul(v2, size)));
				setGeometryIndex(brush.end());
			}

			// surface
			if (show90degrees) {
				switch (getView3D().getRightAngleStyle()) {
				case EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE:
					surface.start(getReusableSurfaceIndex());
					surface.parallelogram(center, v1, v2, size, size);
					setSurfaceIndex(surface.end());
					break;
				case EuclidianStyleConstants.RIGHT_ANGLE_STYLE_DOT:
					surface.start(getReusableSurfaceIndex());
					surface.ellipsePart(center, v1, v2, size, size, 0, a);
					setSurfaceIndex(surface.end());
					break;
				case EuclidianStyleConstants.RIGHT_ANGLE_STYLE_L:
					setSurfaceIndex(-1);
					break;
				}
			} else {
				surface.start(getReusableSurfaceIndex());
				surface.ellipsePart(center, v1, vn2, size, size, 0, a);
				setSurfaceIndex(surface.end());
			}

		}

		return true;
	}

	private void initCoords() {
		if (drawCoords != null) {
			return;
		}
		drawCoords = new Coords[] { new Coords(4), new Coords(4), new Coords(4) };
	}

	protected double getStart() {
		return 0;
	}

	protected double getExtent() {
		return 2 * Math.PI;
	}

	@Override
	protected void updateForView() {
		if (getView3D().viewChangedByZoom() // update only if zoom occurred
				|| ((GeoAngle) getGeoElement()).getParentAlgorithm() instanceof AlgoAnglePlanes) {
			updateForItSelf();
			setLabelWaitForUpdate();
		}

	}

	@Override
	public int getPickOrder() {
		return DRAW_PICK_ORDER_PATH;
	}

	@Override
	public void addToDrawable3DLists(Drawable3DLists lists) {
		super.addToDrawable3DLists(lists);
		addToDrawable3DLists(lists, DRAW_TYPE_SURFACES);
	}

	@Override
	public void removeFromDrawable3DLists(Drawable3DLists lists) {
		super.removeFromDrawable3DLists(lists);
		removeFromDrawable3DLists(lists, DRAW_TYPE_SURFACES);

	}

	private void drawSurfaceGeometry(Renderer renderer) {

		renderer.setLayer(getLayer() + 1f); // +1f to avoid z-fighting with
											// planes and polygons
		renderer.getGeometryManager().draw(getSurfaceIndex());
		renderer.setLayer(0);

	}

	@Override
	public void drawTransp(Renderer renderer) {
		if (!isVisible()) {
			return;
		}

		if (!hasTransparentAlpha())
			return;

		setSurfaceHighlightingColor();

		drawSurfaceGeometry(renderer);

	}

	@Override
	public void drawHiding(Renderer renderer) {
		if (!isVisible())
			return;

		if (!hasTransparentAlpha())
			return;

		drawSurfaceGeometry(renderer);

	}

	@Override
	public Coords getLabelPosition() {
		return labelCenter;
	}

	@Override
	protected void updateLabel() {// TODO remove this and implement all angle
									// cases
		if (labelCenter != null)
			super.updateLabel();
	}

	@Override
	protected void updateLabelPosition() {

		if (labelCenter != null)
			super.updateLabelPosition();

	}

	@Override
	protected float getLabelOffsetX() {
		return super.getLabelOffsetX() - 3;
	}

	@Override
	protected float getLabelOffsetY() {
		return super.getLabelOffsetY() + 5;
	}

}
