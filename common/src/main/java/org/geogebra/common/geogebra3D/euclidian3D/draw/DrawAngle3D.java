package org.geogebra.common.geogebra3D.euclidian3D.draw;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.Hitting;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.PlotterBrush;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.PlotterSurface;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer.PickingType;
import org.geogebra.common.geogebra3D.euclidian3D.printer3D.ExportToPrinter3D;
import org.geogebra.common.geogebra3D.euclidian3D.printer3D.ExportToPrinter3D.Type;
import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoAnglePlanes;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoAngle;
import org.geogebra.common.kernel.algos.AlgoAnglePointsND;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.util.DoubleUtil;

/**
 * @author ggb3D
 * 
 *         Drawable for GeoConic3D
 *
 */
public class DrawAngle3D extends Drawable3DCurves {

	private Coords labelCenter = new Coords(4);
	private Coords vn2 = new Coords(4);
	private Coords[] drawCoords = null;

	private Coords tmpCoords = new Coords(4);
	private Coords tmpCoords2;
	private Coords vn = new Coords(4);
	private Coords v1 = new Coords(4);
	private Coords v2 = new Coords(4);
	private Coords center = Coords.createInhomCoorsInD3();

	private double size;
	private double angleValue;
	private double offset;

	private boolean show90degrees;
	private boolean angleVisible;

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
	public void updateColors() {
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

		if (getAlpha() < 255) {
			return;
		}

		setSurfaceHighlightingColor();

		drawSurfaceGeometry(renderer);
	}

	@Override
	protected boolean isLabelVisible() {
		return angleVisible && super.isLabelVisible();
	}

	@Override
	protected boolean updateForItSelf() {

		// update alpha value
		updateColors();

		initCoords();

		Renderer renderer = getView3D().getRenderer();

		GeoAngle angle = (GeoAngle) getGeoElement();
		angleValue = angle.getDouble();

		if (DoubleUtil.isZero(angleValue)) { // nothing to display
			setGeometryIndex(-1);
			setSurfaceIndex(-1);
			angleVisible = false;
			return true;
		}

		size = angle.getArcSize() / getView3D().getScale();
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

			center.setValues(drawCoords[0], 3);
			center.setW(1);
			if (algo instanceof AlgoAnglePlanes) { // draw angle at center of
													// the screen
				double[] minmax = getView3D().getIntervalClippedLarge(
						new double[] { Double.NEGATIVE_INFINITY,
								Double.POSITIVE_INFINITY },
						center, vn);
				center.setAdd(center,
						tmpCoords.setMul(vn, (minmax[0] + minmax[1]) / 2));
			}

			v1.setValues(drawCoords[1], 3);
			v1.calcNorm();
			double l1 = v1.getNorm();
			v1.mulInside3(1 / l1);

			v2.setValues(drawCoords[2], 3);
			v2.calcNorm();
			double l2 = v2.getNorm();
			v2.mulInside3(1 / l2);

			switch (angle.getAngleStyle()) {

			default:
			case ANTICLOCKWISE:
				//no adjustment
				break;
			case NOTREFLEX:
				if (angle.getRawAngle() > Math.PI) {
					vn.mulInside3(-1);
				}
				break;

			case ISREFLEX:
				if (angle.getRawAngle() < Math.PI) {
					vn.mulInside3(-1);
				}
				break;
			}

			vn2.setCrossProduct4(vn, v1);
			double a2 = angleValue / 2;
			labelCenter.setAdd(tmpCoords.setMul(v1, Math.cos(a2)),
					labelCenter.setMul(v2, Math.sin(a2)));

			// size < points distances / 2
			if (algo instanceof AlgoAnglePointsND) {
				double l = Math.min(l1, l2) / 2;
				if (size > l) {
					size = l;
				}
			}

			labelRadius = size / 1.7;
			labelCenter.mulInside3(labelRadius);
			labelCenter.addInside(center);

			// 90degrees
			show90degrees = getView3D()
					.getRightAngleStyle() != EuclidianStyleConstants.RIGHT_ANGLE_STYLE_NONE
					&& angle.isEmphasizeRightAngle()
					&& DoubleUtil.isEqual(angleValue, Kernel.PI_HALF);

			// outline
			PlotterBrush brush = renderer.getGeometryManager().getBrush();
			PlotterSurface surface = renderer.getGeometryManager().getSurface();

			if (show90degrees) {
				switch (getView3D().getRightAngleStyle()) {
				default:
				case EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE:
					startBrush(brush);
					size *= 0.7071067811865;
					offset = 0;
					brush.setAffineTexture(0.5f, 0.25f);
					// segments
					if (tmpCoords2 == null) {
						tmpCoords2 = new Coords(4);
					}
					brush.segment(center, tmpCoords.setAdd(center,
							tmpCoords.setMul(v1, size)));
					tmpCoords2.set(tmpCoords);
					brush.segment(tmpCoords,
							tmpCoords2.addInsideMul(v2, size));
					brush.segment(tmpCoords.setAdd(center,
							tmpCoords.setMul(v2, size)), tmpCoords2);
					brush.segment(center, tmpCoords);
					setGeometryIndex(brush.end());
					break;

				case EuclidianStyleConstants.RIGHT_ANGLE_STYLE_DOT:
					// create point template if needed
					float pointSize = getGeoElement().getLineThickness()
                            * PlotterBrush.LINE3D_THICKNESS;
                    renderer.getGeometryManager()
                            .createPointTemplateIfNeeded((int) pointSize);
                    // create angle outline + dot
					startBrush(brush);
					// arc
					brush.setAffineTexture(0f, 0f);
					brush.arc(center, v1, v2, size, 0, angleValue, 60);
					brush.setAffineTexture(0.5f, 0.25f);
					// segments
					brush.segment(center, tmpCoords.setAdd(center,
							tmpCoords.setMul(v1, size)));
					brush.segment(center, tmpCoords.setAdd(center,
							tmpCoords.setMul(v2, size)));
					// dot (use surface plotter)

					tmpCoords.set3(labelCenter);
					renderer.getGeometryManager().drawPoint(this, pointSize,
							tmpCoords);

					setGeometryIndex(brush.end());
					break;

				case EuclidianStyleConstants.RIGHT_ANGLE_STYLE_L:
					startBrush(brush);
					size *= 0.7071067811865;
					offset = size * 0.4;
					brush.setAffineTexture(0.5f, 0.25f);
					// segments
					if (tmpCoords2 == null) {
						tmpCoords2 = new Coords(4);
					}
					tmpCoords2.setAdd(center,
							tmpCoords2.setAdd(tmpCoords.setMul(v1, offset),
									tmpCoords2.setMul(v2, offset)));
					brush.segment(tmpCoords2, tmpCoords.setAdd(tmpCoords2,
							tmpCoords.setMul(v1, size)));
					brush.segment(tmpCoords2, tmpCoords.setAdd(tmpCoords2,
							tmpCoords.setMul(v2, size)));
					setGeometryIndex(brush.end());
					break;
				}
			} else {
				startBrush(brush);
				// arc
				brush.setAffineTexture(0f, 0f);
				brush.arc(center, v1, vn2, size, 0, angleValue, 60);
				brush.setAffineTexture(0.5f, 0.25f);
				// segments
				brush.segment(center,
						tmpCoords.setAdd(center, tmpCoords.setMul(v1, size)));
				brush.segment(center,
						tmpCoords.setAdd(center, tmpCoords.setMul(v2, size)));
				setGeometryIndex(brush.end());
			}
			endPacking();

			// surface
			setPackSurface();
			if (show90degrees) {
				switch (getView3D().getRightAngleStyle()) {
				default:
				case EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE:
					surface.start(getReusableSurfaceIndex());
					surface.parallelogram(this, center, v1, v2, size, size);
					setSurfaceIndex(surface.end());
					break;
				case EuclidianStyleConstants.RIGHT_ANGLE_STYLE_DOT:
					surface.start(getReusableSurfaceIndex());
					surface.ellipsePart(this, center, v1, v2, size, size, 0,
							angleValue);
					setSurfaceIndex(surface.end());
					break;
				case EuclidianStyleConstants.RIGHT_ANGLE_STYLE_L:
					setSurfaceIndex(-1);
					break;
				}
			} else {
				surface.start(getReusableSurfaceIndex());
				surface.ellipsePart(this, center, v1, vn2, size, size, 0,
						angleValue);
				setSurfaceIndex(surface.end());
			}
			endPacking();

		}

		return true;
	}

	private void startBrush(PlotterBrush brush) {
		setPackCurve();
		brush.start(getReusableGeometryIndex());
		brush.setThickness(getGeoElement().getLineThickness(),
				(float) getView3D().getScale());
	}

	private void initCoords() {
		if (drawCoords != null) {
			return;
		}
		drawCoords = new Coords[] { new Coords(4), new Coords(4),
				new Coords(4) };
	}

	@Override
	protected void updateForView() {
		if (getView3D().viewChangedByZoom() // update only if zoom occurred
				|| ((GeoAngle) getGeoElement())
						.getParentAlgorithm() instanceof AlgoAnglePlanes) {
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
		renderer.getRendererImpl().setLayer(getLayer());
		renderer.getGeometryManager().draw(getSurfaceIndex());
		renderer.getRendererImpl().setLayer(Renderer.LAYER_DEFAULT);

	}

	@Override
	public int getLayer() {
		// +1 shift to avoid z-fighting with planes and polygons
		return super.getLayer() + Renderer.LAYER_ANGLE_SHIFT;
	}

	@Override
	public void drawTransp(Renderer renderer) {
		if (!isVisible()) {
			return;
		}

		if (!hasTransparentAlpha()) {
			return;
		}

		setSurfaceHighlightingColor();

		drawSurfaceGeometry(renderer);

	}

	@Override
	public void drawHiding(Renderer renderer) {
		if (!isVisible()) {
			return;
		}

		if (!hasTransparentAlpha()) {
			return;
		}

		drawSurfaceGeometry(renderer);

	}

	@Override
	public Coords getLabelPosition() {
		return labelCenter;
	}

	@Override
	protected void updateLabel() { // TODO remove this and implement all angle
									// cases
		if (labelCenter != null) {
			super.updateLabel();
		}
	}

	@Override
	protected void updateLabelPosition() {

		if (labelCenter != null) {
			super.updateLabelPosition();
		}

	}

	@Override
	protected float getLabelOffsetX() {
		return super.getLabelOffsetX() - 3;
	}

	@Override
	protected float getLabelOffsetY() {
		return super.getLabelOffsetY() + 5;
	}

	@Override
	public boolean hit(Hitting hitting) {

		if (waitForReset) { // prevent NPE
			return false;
		}

		if (!angleVisible) {
			return false;
		}

		hitting.origin.projectPlaneInPlaneCoords(v1, vn2, hitting.direction,
				center, tmpCoords);

		if (DoubleUtil.isZero(tmpCoords.getW())) {
			return false;
		}

		double x = tmpCoords.getX();
		double y = tmpCoords.getY();

		if (show90degrees) {
			switch (getView3D().getRightAngleStyle()) {
			case EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE:
			case EuclidianStyleConstants.RIGHT_ANGLE_STYLE_L:
				if (x < offset || x > size + offset || y < offset
						|| y > size + offset) {
					return false;
				}
				double z = tmpCoords.getZ();
				setZPick(z, z, hitting.discardPositiveHits(), -z);
				setPickingType(PickingType.POINT_OR_CURVE);
				return true;
			}
		}

		double d = Math.sqrt(x * x + y * y);
		if (d > size) {
			return false;
		}

		double a = Math.atan2(y, x);
		if (a < 0) {
			a += Math.PI * 2;
		}
		if (a > angleValue) {
			return false;
		}

		double z = tmpCoords.getZ();

		setZPick(z, z, hitting.discardPositiveHits(), -z);
		setPickingType(PickingType.POINT_OR_CURVE);

		return true;
	}

	@Override
	protected void updateGeometriesColor() {
		updateGeometriesColor(true);
	}

	@Override
	protected void setGeometriesVisibility(boolean visible) {
		setGeometriesVisibilityWithSurface(visible);
	}

	@Override
	public void exportToPrinter3D(ExportToPrinter3D exportToPrinter3D,
			boolean exportSurface) {
		if (isVisible()) {
			if (exportSurface) {
				exportToPrinter3D.exportSurface(this, true, false);
			} else {
				exportToPrinter3D.exportCurve(this, Type.CURVE);
			}
		}
	}

}
