package org.geogebra.common.geogebra3D.euclidian3D.draw;

import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.Previewable;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.Hitting;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.PlotterBrush;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.PlotterSurface;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer.PickingType;
import org.geogebra.common.geogebra3D.euclidian3D.printer3D.ExportToPrinter3D;
import org.geogebra.common.geogebra3D.euclidian3D.printer3D.ExportToPrinter3D.Type;
import org.geogebra.common.kernel.PathParameter;
import org.geogebra.common.kernel.arithmetic.Functional2Var;
import org.geogebra.common.kernel.geos.FromMeta;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoConicNDConstants;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.util.DoubleUtil;

/**
 * @author ggb3D
 * 
 *         Drawable for GeoConic3D
 *
 */
public class DrawConic3D extends Drawable3DCurves
		implements Functional2Var, Previewable {
	/* used for update */
	protected Coords m;
	protected Coords d;
	protected Coords[] points = new Coords[4];
	protected double[] minmax;
	protected GeoConicND conic;
	protected Coords ev1;
	protected Coords ev2;
	protected double e1;
	protected double e2;
	private Coords boundsMin = new Coords(3);
	protected Coords boundsMax = new Coords(3);

	private Coords tmpCoords1;
	private Coords tmpCoords2;
	private PathParameter hittingPathParameter = new PathParameter();
	private double alpha;
	private double beta;

	protected int longitude = 60;
	private Coords project;
	protected Coords globalCoords;
	protected Coords inPlaneCoords;

	private double[] parameters = new double[2];
	private int drawTypeAdded;
	private Visible visible = Visible.TOTALLY_OUTSIDE;

	/**
	 * @param view3d
	 *            the 3D view where the conic is drawn
	 * @param conic
	 *            the conic to draw
	 */
	public DrawConic3D(EuclidianView3D view3d, GeoConicND conic) {
		super(view3d, conic);
		setPickingType(PickingType.POINT_OR_CURVE);

	}

	@Override
	public void setGeoElement(GeoElement a_geo) {
		super.setGeoElement(a_geo);
		conic = (GeoConicND) a_geo;
	}

	@Override
	public void updateColors() {
		updateAlpha();
		setColorsOutlined();
	}

	@Override
	public void drawGeometry(Renderer renderer) {

		GeoConicND conic1 = (GeoConicND) getGeoElement();

		switch (conic1.getType()) {
		case GeoConicNDConstants.CONIC_CIRCLE:
		case GeoConicNDConstants.CONIC_ELLIPSE:
		case GeoConicNDConstants.CONIC_HYPERBOLA:
		case GeoConicNDConstants.CONIC_PARABOLA:
		case GeoConicNDConstants.CONIC_DOUBLE_LINE:
		case GeoConicNDConstants.CONIC_INTERSECTING_LINES:
		case GeoConicNDConstants.CONIC_PARALLEL_LINES:
		case GeoConicNDConstants.CONIC_SINGLE_POINT:
			renderer.getGeometryManager().draw(getGeometryIndex());
			break;
		default:
			break;

		}

	}

	@Override
	public void exportToPrinter3D(ExportToPrinter3D exportToPrinter3D, boolean exportSurface) {
		if (isVisible()) {
			if (exportSurface) {
				exportToPrinter3D.exportSurface(this, true, true);
			} else {
				if (getGeoElement().getLineThickness() > 0) {
					Type exportType;
					switch (conic.getType()) {
					case GeoConicNDConstants.CONIC_CIRCLE:
					case GeoConicNDConstants.CONIC_ELLIPSE:
						exportType = Type.CURVE_CLOSED;
						break;
					case GeoConicNDConstants.CONIC_HYPERBOLA:
					case GeoConicNDConstants.CONIC_PARABOLA:
					case GeoConicNDConstants.CONIC_DOUBLE_LINE:
					case GeoConicNDConstants.CONIC_INTERSECTING_LINES:
					case GeoConicNDConstants.CONIC_PARALLEL_LINES:
					default:
						exportType = Type.CURVE;
						break;
					}
					exportToPrinter3D.exportCurve(this, exportType);
				}
			}
		}
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

	/**
	 * 
	 * @param x
	 *            value
	 * @return acosh(x) if x>=1; 0 otherwise
	 */
	public static double acosh(double x) {
		if (x <= 1) {
			return 0;
		}
		return Math.log(x + Math.sqrt(x * x - 1));
	}

	/**
	 * 
	 * @param x
	 *            value
	 * @return asinh(x)
	 */
	public static double asinh(double x) {
		return Math.log(x + Math.sqrt(1 + x * x));
	}

	private void createTmpCoordsIfNeeded() {
		if (tmpCoords1 == null) {
			tmpCoords1 = new Coords(3);
			tmpCoords2 = new Coords(3);
		}
	}

	@Override
	protected boolean updateForItSelf() {

		// update alpha value
		updateColors();

		Renderer renderer = getView3D().getRenderer();

		// check is visible (and update values)
		checkVisibleAndSetBoundingBox();
		if (visible == Visible.TOTALLY_OUTSIDE) {
			setGeometryIndex(-1);
			setSurfaceIndex(-1);
			return true;
		}

		if (conic.getType() == GeoConicNDConstants.CONIC_SINGLE_POINT) {

			PlotterSurface surface;

			surface = renderer.getGeometryManager().getSurface();
			updateSinglePoint(surface);

		} else {

			if (visible != Visible.FRUSTUM_INSIDE) { // no outline when frustum
														// inside
				setPackCurve();
				PlotterBrush brush = renderer.getGeometryManager().getBrush();
				brush.start(getReusableGeometryIndex());

				brush.setThickness(getGeoElement().getLineThickness(),
						(float) getView3D().getScale());

				brush.setAffineTexture(0f, 0f);
				switch (conic.getType()) {
				case GeoConicNDConstants.CONIC_CIRCLE:
					updateEllipse(brush);
					break;
				case GeoConicNDConstants.CONIC_ELLIPSE:
					updateEllipse(brush);
					break;
				case GeoConicNDConstants.CONIC_HYPERBOLA:
					updateHyperbola(brush);
					break;
				case GeoConicNDConstants.CONIC_PARABOLA:
					updateParabola(brush);
					break;
				case GeoConicNDConstants.CONIC_DOUBLE_LINE:
					createTmpCoordsIfNeeded();
					brush.segment(tmpCoords1.setAdd3(m, tmpCoords1.setMul3(d, minmax[0])),
							tmpCoords2.setAdd3(m, tmpCoords2.setMul3(d, minmax[1])));
					break;
				case GeoConicNDConstants.CONIC_INTERSECTING_LINES:
				case GeoConicNDConstants.CONIC_PARALLEL_LINES:
					updateLines(brush);
					break;
				default:
					break;

				}

				setGeometryIndex(brush.end());
				endPacking();
			}

			// surface
			setPackSurface();
			PlotterSurface surface = renderer.getGeometryManager().getSurface();
			surface.start(getReusableSurfaceIndex());

			switch (conic.getType()) {
			case GeoConicNDConstants.CONIC_CIRCLE:
			case GeoConicNDConstants.CONIC_ELLIPSE:
				updateEllipse(surface);
				break;
			case GeoConicNDConstants.CONIC_INTERSECTING_LINES:
				updateIntersectingLines(surface);
				break;
			case GeoConicNDConstants.CONIC_PARALLEL_LINES:
				updateParallelLines(surface);
				break;
			case GeoConicNDConstants.CONIC_HYPERBOLA:
				updateHyperbola(surface);
				break;
			case GeoConicNDConstants.CONIC_PARABOLA:
				updateParabola(surface);
				break;

			default:
				break;

			}

			setSurfaceIndex(surface.end());
			endPacking();
		}

		return true;
	}

	/**
	 * 
	 * @param i
	 *            index for the line
	 * @return min, max parameters on the i-th line
	 */
	protected double[] getLineMinMax(int i) {
		return getView3D().getIntervalClippedLarge(new double[] {
				Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY }, m, d);
	}
	
	/**
	 * initiate #points array
	 */
	protected void createPointsIfNeeded() {
		if (points[0] == null) {
			for (int i = 0; i < 4; i++) {
				points[i] = new Coords(3);
			}
		}
	}

	/**
	 * update outline for parallel lines
	 * 
	 * @param brush
	 *            brush plotter
	 */
	protected void updateLines(PlotterBrush brush) {

		createPointsIfNeeded();
		
		m = conic.getOrigin3D(0);
		d = conic.getDirection3D(0);
		if (d.isDefined()) {
			minmax = getLineMinMax(0);
			Coords p = points[0];
			p.setAdd3(m, p.setMul3(d, minmax[0]));
			p = points[1];
			p.setAdd3(m, p.setMul3(d, minmax[1]));

			brush.segment(points[0], points[1]);
		} else { // tells the surface that second line is infinite
			points[0].setUndefined();
		}

		m = conic.getOrigin3D(1);
		d = conic.getDirection3D(1);
		if (d.isDefined()) {
			minmax = getLineMinMax(1);
			Coords p = points[3];
			p.setAdd3(m, p.setMul3(d, minmax[0]));
			p = points[2];
			p.setAdd3(m, p.setMul3(d, minmax[1]));

			brush.segment(points[2], points[3]);
		} else { // tells the surface that second line is infinite
			points[0].setUndefined();
		}

	}

	/**
	 * update surface drawing for parallel lines case
	 * 
	 * @param surface
	 *            surface plotter
	 */
	protected void updateParallelLines(PlotterSurface surface) {
		if (points[0].isDefined()) { // in case second line is infinite
			surface.drawQuad(this, points[0], points[1], points[2], points[3]);
		}
	}

	/**
	 * update surface drawing for intersecting lines case
	 * 
	 * @param surface
	 *            surface plotter
	 */
	protected void updateIntersectingLines(PlotterSurface surface) {
		surface.drawTriangle(this, points[0], points[2], conic.getMidpoint3D());
		surface.drawTriangle(this, points[1], points[3], conic.getMidpoint3D());
	}

	/**
	 * update outline drawing of hyperbola
	 * 
	 * @param brush
	 *            brush plotter
	 */
	protected void updateHyperbola(PlotterBrush brush) {

		double[] minmax1 = { Double.POSITIVE_INFINITY,
				Double.NEGATIVE_INFINITY };

		getView3D().getMinIntervalOutsideClipping(minmax1, m, ev1.mul(e1));

		minmax = new double[4];
		minmax[1] = acosh(minmax1[1]);
		minmax[3] = acosh(-minmax1[0]);
		minmax[0] = -minmax[1];
		minmax[2] = -minmax[3];

		brush.hyperbolaBranch(m, ev1, ev2, e1, e2, minmax[0], minmax[1]);
		brush.hyperbolaBranch(m, ev1.mul(-1), ev2, e1, e2, minmax[2],
				minmax[3]);

	}

	/**
	 * update outline drawing of hyperbola
	 * 
	 * @param brush
	 *            brush plotter
	 */
	protected void updateParabola(PlotterBrush brush) {
		minmax = getParabolaMinMax();
		brush.parabola(m, ev1, ev2, conic.p, minmax[0], minmax[1], points[0],
				points[1]);
	}

	/**
	 * update surface drawing for hypebola case
	 * 
	 * @param surface
	 *            surface plotter
	 */
	protected void updateParabola(PlotterSurface surface) {
		surface.parabola(this, m, ev1, ev2, conic.p, minmax[0], minmax[1]);
	}

	/**
	 * 
	 * @return min/max for parabola
	 */
	protected double[] getParabolaMinMax() {

		double[] minmax1 = { Double.POSITIVE_INFINITY,
				Double.NEGATIVE_INFINITY };

		getView3D().getMinIntervalOutsideClipping(minmax1, m, ev1);
		double tMax = Math.sqrt(2 * minmax1[1] / conic.p);
		return new double[] { -tMax, tMax };
	}

	/**
	 * update surface drawing for hypebola case
	 * 
	 * @param surface
	 *            surface plotter
	 */
	protected void updateHyperbola(PlotterSurface surface) {
		surface.hyperbolaPart(this, m, ev1, ev2, e1, e2, minmax[0], minmax[1]);
		surface.hyperbolaPart(this, m, ev1.mul(-1), ev2, e1, e2, minmax[2],
				minmax[3]);
	}

	/**
	 * update surface drawing for ellipse case
	 * 
	 * @param surface
	 *            surface plotter
	 */
	protected void updateEllipse(PlotterSurface surface) {
		surface.ellipsePart(this, m, ev1, ev2, e1, e2, getEllipseSurfaceStart(),
				getEllipseSurfaceExtent(), isSector());
	}

	/**
	 * update surface drawing for single point case
	 * 
	 * @param surface
	 *            surface plotter
	 */
	protected void updateSinglePoint(PlotterSurface surface) {
		setPackCurve();
		surface.start(this, getReusableGeometryIndex());
		// number of vertices depends on point size
		int nb = 2 + conic.getLineThickness();
		surface.setU((float) getMinParameter(0), (float) getMaxParameter(0));
		surface.setNbU(2 * nb);
		surface.setV((float) getMinParameter(1), (float) getMaxParameter(1));
		surface.setNbV(nb);
		surface.draw(shouldBePackedForManager());
		setGeometryIndex(surface.end());
		endPacking();

		setSurfaceIndex(-1);
	}

	/**
	 * 
	 * @return true if is a sector (for surface drawing)
	 */
	protected boolean isSector() {
		return true;
	}

	/**
	 * 
	 * @return start angle value for drawing ellipse surface
	 */
	protected double getEllipseSurfaceStart() {
		if (visible == Visible.CENTER_OUTSIDE
				|| visible == Visible.FRUSTUM_INSIDE) {
			return beta - alpha;
		}
		return 0;
	}

	/**
	 * 
	 * @return extent angle value for drawing ellipse surface
	 */
	protected double getEllipseSurfaceExtent() {

		if (visible == Visible.CENTER_OUTSIDE
				|| visible == Visible.FRUSTUM_INSIDE) {
			return 2 * alpha;
		}

		return 2 * Math.PI;
	}

	/**
	 * draws outline for circle
	 * 
	 * @param brush
	 *            brush plotter
	 */
	protected void updateCircle(PlotterBrush brush) {

		if (visible == Visible.CENTER_OUTSIDE) {
			longitude = brush.calcArcLongitudesNeeded(e1, alpha,
					getView3D().getScale());
			brush.arc(m, ev1, ev2, e1, beta - alpha, 2 * alpha, longitude);
		} else {
			longitude = brush.calcArcLongitudesNeeded(e1, Math.PI,
					getView3D().getScale());
			brush.circle(m, ev1, ev2, e1, longitude);
		}

	}

	/**
	 * draws outline for ellipse
	 * 
	 * @param brush
	 *            brush plotter
	 */
	protected void updateEllipse(PlotterBrush brush) {

		if (visible == Visible.CENTER_OUTSIDE) {
			brush.arcEllipse(m, ev1, ev2, e1, e2, beta - alpha, 2 * alpha);
		} else {
			brush.arcEllipse(m, ev1, ev2, e1, e2, 0, 2 * Math.PI);
		}

	}

	@Override
	protected void updateForView() {
		if (getView3D().viewChanged()) {
			switch (((GeoConicND) getGeoElement()).getType()) {
			case GeoConicNDConstants.CONIC_DOUBLE_LINE:
			case GeoConicNDConstants.CONIC_HYPERBOLA:
			case GeoConicNDConstants.CONIC_INTERSECTING_LINES:
			case GeoConicNDConstants.CONIC_LINE:
			case GeoConicNDConstants.CONIC_PARABOLA:
			case GeoConicNDConstants.CONIC_PARALLEL_LINES:
				if (getView3D().viewChangedByZoom()
						|| getView3D().viewChangedByTranslate()) {
					updateForItSelf();
				}
				break;
			case GeoConicNDConstants.CONIC_CIRCLE:
			case GeoConicNDConstants.CONIC_ELLIPSE:
				if (getView3D().viewChangedByZoom() // update only if zoom
													// occurred
						|| (visible != Visible.TOTALLY_INSIDE
								&& getView3D().viewChangedByTranslate())) {
					// if
																			// translate
																			// with
																			// not
																			// totally
																			// visible
																			// ellipse
					updateForItSelf();
				}
				break;

			case GeoConicNDConstants.CONIC_SINGLE_POINT:
				if (getView3D().viewChangedByZoom()) {
					// occurred
					updateForItSelf();
				}
				break;

			default:
				// do nothing
				break;
			}
		}

	}

	@Override
	public int getPickOrder() {
		if (getPickingType() == PickingType.POINT_OR_CURVE) {
			return DRAW_PICK_ORDER_PATH;
		}

		return DRAW_PICK_ORDER_SURFACE;
	}

	@Override
	public void addToDrawable3DLists(Drawable3DLists lists) {
		super.addToDrawable3DLists(lists);
		if (((GeoConicND) getGeoElement()).isEndOfQuadric()) {
			drawTypeAdded = DRAW_TYPE_CLOSED_SURFACES_NOT_CURVED;
		} else {
			drawTypeAdded = DRAW_TYPE_SURFACES;
		}

		addToDrawable3DLists(lists, drawTypeAdded);
	}

	@Override
	public void removeFromDrawable3DLists(Drawable3DLists lists) {
		super.removeFromDrawable3DLists(lists);
		removeFromDrawable3DLists(lists, drawTypeAdded);

	}

	private void drawSurfaceGeometry(Renderer renderer) {

		switch (((GeoConicND) getGeoElement()).getType()) {
		default:
			// do nothing
			break;
		case GeoConicNDConstants.CONIC_CIRCLE:
		case GeoConicNDConstants.CONIC_ELLIPSE:
		case GeoConicNDConstants.CONIC_PARALLEL_LINES:
		case GeoConicNDConstants.CONIC_INTERSECTING_LINES:
		case GeoConicNDConstants.CONIC_HYPERBOLA:
		case GeoConicNDConstants.CONIC_PARABOLA:
			renderer.getRendererImpl().setLayer(getLayer());
			renderer.getGeometryManager().draw(getSurfaceIndex());
			renderer.getRendererImpl().setLayer(Renderer.LAYER_DEFAULT);
			break;
		}

	}

	@Override
	protected void drawGeometryForPicking(Renderer renderer, PickingType type) {
		if (type == PickingType.POINT_OR_CURVE) {
			drawGeometry(renderer);
		} else {
			if (getAlpha() > 0) { // surface is pickable only if not totally
									// transparent
				drawSurfaceGeometry(renderer);
			}
		}
	}

	@Override
	public void drawTransp(Renderer renderer) {

		if (isVisible() && hasTransparentAlpha()) {
			setSurfaceHighlightingColor();
			drawSurfaceGeometry(renderer);
		}

		drawTracesTranspSurface(renderer);

	}

	@Override
	public void drawHiding(Renderer renderer) {

		if (isVisible() && hasTransparentAlpha()) {
			drawSurfaceGeometry(renderer);
		}

		drawTracesHidingSurface(renderer);

	}

	// /////////////////////////////////
	// FUNCTION2VAR INTERFACE
	// /////////////////////////////////

	@Override
	public void evaluatePoint(double u, double v, Coords point) {
		GeoConicND conic1 = (GeoConicND) getGeoElement();
		double r = conic1.getLineThickness() / getView3D().getScale() * 1.5;
		point.set(Math.cos(u) * Math.cos(v) * r, Math.sin(u) * Math.cos(v) * r,
				Math.sin(v) * r, 1);
		point.setAdd3(point, conic1.getMidpoint3D());
	}

	@Override
	public Coords evaluateNormal(double u, double v) {
		return new Coords(new double[] { Math.cos(u) * Math.cos(v),
				Math.sin(u) * Math.cos(v), Math.sin(v) });
	}

	@Override
	public double getMinParameter(int index) {
		switch (index) {
		case 0: // u
		default:
			return 0;
		case 1: // v
			return -Math.PI / 2;
		}
	}

	@Override
	public double getMaxParameter(int index) {
		switch (index) {
		case 0: // u
		default:
			return 2 * Math.PI;
		case 1: // v
			return Math.PI / 2;
		}

	}

	@Override
	public void updatePreview() {
		// setWaitForUpdate();
	}

	@Override
	public void updateMousePos(double x, double y) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean isTransparent() {
		if (getPickingType() == PickingType.SURFACE) {
			return getAlpha() <= EuclidianController.MAX_TRANSPARENT_ALPHA_VALUE_INT;
		}

		return false;
	}

	/**
	 * Visibility flag
	 * 
	 * @author mathieu
	 *
	 */
	private static enum Visible {
		/** the conic is totally outside */
		TOTALLY_OUTSIDE,
		/** the frustum is inside the conic */
		FRUSTUM_INSIDE,
		/** the conic is totally inside */
		TOTALLY_INSIDE,
		/** the conic is partly inside, center outside */
		CENTER_OUTSIDE,
		/** the conic is partly inside, center inside */
		CENTER_INSIDE
	}

	/**
	 * check if the ellipse is (at least partially) visible
	 * 
	 * @param center
	 *            ellipse center
	 * @param rMin
	 *            min ellipse radius
	 * @param rMax
	 *            max ellipse radius
	 */
	private void checkEllipseVisible(Coords center, double rMin, double rMax) {

		double frustumRadius = getView3D().getFrustumRadius();
		Coords origin = getView3D().getCenter();
		Coords v = origin.sub(center);
		v.calcNorm();
		double centersDistance = v.getNorm();

		if (centersDistance > rMax + frustumRadius) { // circle totally outside
														// the frustum
			visible = Visible.TOTALLY_OUTSIDE;
		} else if (centersDistance < frustumRadius) { // center inside
			visible = Visible.CENTER_INSIDE;
		} else if (centersDistance + frustumRadius < rMin) { // frustum totally
																// inside the
																// circle
			visible = Visible.FRUSTUM_INSIDE;
			calcVisibleAngles(v, frustumRadius);
		} else if (centersDistance + rMax < frustumRadius) { // totally inside
			visible = Visible.TOTALLY_INSIDE;
		} else {
			visible = calcVisibleAngles(v, frustumRadius);
		}

	}

	/**
	 * calc angles to draw minimum longitudes
	 */
	private Visible calcVisibleAngles(Coords v, double frustumRadius) {
		// calc angles to draw minimum longitudes
		double x = v.dotproduct(ev1);
		double y = v.dotproduct(ev2);
		double horizontalDistance = Math.sqrt(x * x + y * y);
		if (horizontalDistance > frustumRadius) {
			alpha = Math.asin(frustumRadius / horizontalDistance);
			beta = Math.atan2(y * e1, x * e2);
			// Log.debug("alpha = "+(alpha*180/Math.PI)+"degrees, beta =
			// "+(beta*180/Math.PI)+"degrees");
			return Visible.CENTER_OUTSIDE; // center outside
		}

		return Visible.CENTER_INSIDE; // do as if center inside
	}

	@Override
	public void enlargeBounds(Coords min, Coords max, boolean dontExtend) {
		switch (conic.getType()) {
		default:
			// do nothing
			break;
		case GeoConicNDConstants.CONIC_SINGLE_POINT:
		case GeoConicNDConstants.CONIC_CIRCLE:
		case GeoConicNDConstants.CONIC_ELLIPSE:
			enlargeBounds(min, max, boundsMin, boundsMax);
			break;
		}
	}

	final private void setBoundsEllipse() {
		boundsMin.set(Double.POSITIVE_INFINITY);
		boundsMax.set(Double.NEGATIVE_INFINITY);
		enlargeBoundsToDiagonal(boundsMin, boundsMax, m, ev1, ev2, e1, e2);
		double radius = conic.getLineThickness() * PlotterBrush.LINE3D_THICKNESS
				/ getView3D().getScale();
		boundsMin.addInside(-radius);
		boundsMax.addInside(radius);
	}

	/**
	 * check if conic is visible. Note that midpoint, etc. are updated here
	 */
	protected void checkVisibleAndSetBoundingBox() {
		switch (conic.getType()) {
		case GeoConicNDConstants.CONIC_SINGLE_POINT:
			m = conic.getMidpoint3D();

			boundsMin.setValues(m, 3);
			boundsMax.setValues(m, 3);
			double radius = conic.getLineThickness() / getView3D().getScale()
					* DrawPoint3D.DRAW_POINT_FACTOR;
			boundsMin.addInside(-radius);
			boundsMax.addInside(radius);

			double frustumRadius = getView3D().getFrustumRadius();
			Coords origin = getView3D().getCenter();
			Coords v = origin.sub(m);
			v.calcNorm();
			double centersDistance = v.getNorm();
			if (DoubleUtil.isGreater(centersDistance, frustumRadius)) {
				visible = Visible.TOTALLY_OUTSIDE;
			} else {
				visible = Visible.TOTALLY_INSIDE;
			}
			break;
		case GeoConicNDConstants.CONIC_CIRCLE:
			m = conic.getMidpoint3D();
			ev1 = conic.getEigenvec3D(0);
			ev2 = conic.getEigenvec3D(1);
			e1 = conic.getHalfAxis(0);
			e2 = e1;

			setBoundsEllipse();

			checkEllipseVisible(m, e1, e2);
			break;
		case GeoConicNDConstants.CONIC_ELLIPSE:
			m = conic.getMidpoint3D();
			ev1 = conic.getEigenvec3D(0);
			ev2 = conic.getEigenvec3D(1);
			e1 = conic.getHalfAxis(0);
			e2 = conic.getHalfAxis(1);
			setBoundsEllipse();
			double eMin, eMax;
			if (e1 > e2) {
				eMax = e1;
				eMin = e2;
			} else {
				eMax = e2;
				eMin = e1;
			}
			checkEllipseVisible(m, eMin, eMax);

			// dilate angle
			if (alpha * eMax >= Math.PI * eMin) {
				alpha = Math.PI;
			} else {
				alpha *= eMax / eMin;
			}

			break;
		case GeoConicNDConstants.CONIC_HYPERBOLA:
			m = conic.getMidpoint3D();
			ev1 = conic.getEigenvec3D(0);
			ev2 = conic.getEigenvec3D(1);
			e1 = conic.getHalfAxis(0);
			e2 = conic.getHalfAxis(1);
			visible = Visible.TOTALLY_INSIDE; // TODO
			break;
		case GeoConicNDConstants.CONIC_PARABOLA:
			m = conic.getMidpoint3D();
			ev1 = conic.getEigenvec3D(0);
			ev2 = conic.getEigenvec3D(1);
			visible = Visible.TOTALLY_INSIDE; // TODO
			break;
		case GeoConicNDConstants.CONIC_DOUBLE_LINE:
			m = conic.getOrigin3D(0);
			d = conic.getDirection3D(0);
			minmax = getLineMinMax(0);
			visible = Visible.TOTALLY_INSIDE; // TODO
			break;
		case GeoConicNDConstants.CONIC_INTERSECTING_LINES:
		case GeoConicNDConstants.CONIC_PARALLEL_LINES:
		default:
			visible = Visible.TOTALLY_INSIDE; // TODO
			break;

		}
	}

	@Override
	public boolean hit(Hitting hitting) {
		return hit(hitting, false);
	}

	@Override
	public boolean hitForList(Hitting hitting) {
		if (hasGeoElementVisible() && getGeoElement().isPickable()) {
			return hit(hitting, true);
		}

		return false;
	}

	/**
	 * 
	 * @param hitting
	 *            e.g. ray
	 * @param checkRealPointSize
	 *            true if we check point size (and not threshold)
	 * @return true if hitted
	 */
	private boolean hit(Hitting hitting, boolean checkRealPointSize) {

		if (waitForReset) { // prevent NPE
			return false;
		}

		switch (((GeoConicND) getGeoElement()).getType()) {
		case GeoConicNDConstants.CONIC_EMPTY:
			return false;

		case GeoConicNDConstants.CONIC_SINGLE_POINT:
			if (project == null) {
				project = Coords.createInhomCoorsInD3();
			}
			if (DrawPoint3D.hit(hitting, conic.getMidpoint3D(), this,
					conic.getLineThickness(), project, parameters,
					checkRealPointSize)) {
				setPickingType(PickingType.POINT_OR_CURVE);
				return true;
			}
			return false;

		case GeoConicNDConstants.CONIC_CIRCLE:
		case GeoConicNDConstants.CONIC_ELLIPSE:
		default: // TODO check other cases

			boolean ret = false;

			// project hitting origin on polygon plane
			if (globalCoords == null) {
				globalCoords = new Coords(4);
				inPlaneCoords = new Coords(4);
			}
			hitting.origin.projectPlaneThruVIfPossible(
					conic.getCoordSys().getMatrixOrthonormal(),
					hitting.direction, globalCoords, inPlaneCoords);

			// try conic surface
			if (getGeoElement()
					.getAlphaValue() > EuclidianController.MIN_VISIBLE_ALPHA_VALUE
					&& hitting.isInsideClipping(globalCoords)
					&& conic.isInRegion(inPlaneCoords.getX(),
							inPlaneCoords.getY())) {
				// TODO use other for non-parallel projection:
				// -hitting.origin.distance(project[0]);
				double parameterOnHitting = inPlaneCoords.getZ();
				setZPick(parameterOnHitting, parameterOnHitting,
						hitting.discardPositiveHits(), -parameterOnHitting);
				setPickingType(PickingType.SURFACE);
				ret = true;
			}

			// try outline
			inPlaneCoords.setZ(1.0);
			conic.pointChanged(inPlaneCoords, hittingPathParameter);
			Coords p3d = conic.getCoordSys().getPoint(inPlaneCoords.getX(),
					inPlaneCoords.getY()); // get nearest point on conic
			// Log.debug("\n"+p2d+"\n3d:\n"+p3d);

			if (hitting.isInsideClipping(p3d)) {
				if (project == null) {
					project = Coords.createInhomCoorsInD3();
				}
				p3d.projectLine(hitting.origin, hitting.direction, project,
						parameters); // check distance to hitting line
				double d1 = getView3D().getScaledDistance(p3d, project);
				if (d1 <= conic.getLineThickness() + hitting.getThreshold()) {
					double z = -parameters[0];
					double dz = conic.getLineThickness()
							/ getView3D().getScale();
					setZPick(z + dz, z - dz, hitting.discardPositiveHits(),
							parameters[0]);
					setPickingType(PickingType.POINT_OR_CURVE);
					return true;
				}
			}

			return ret;
		}
	}

	@Override
	public boolean doHighlighting() {

		// if it depends on a limited quadric, look at the meta' highlighting

		if (getGeoElement().getMetasLength() > 0) {
			for (GeoElement meta : ((FromMeta) getGeoElement()).getMetas()) {
				if (meta != null && meta.doHighlighting()) {
					return true;
				}
			}
		}

		return super.doHighlighting();
	}

    @Override
    protected void updateForViewNotVisible() {
        switch (((GeoConicND) getGeoElement()).getType()) {
            case GeoConicNDConstants.CONIC_DOUBLE_LINE:
            case GeoConicNDConstants.CONIC_HYPERBOLA:
            case GeoConicNDConstants.CONIC_INTERSECTING_LINES:
            case GeoConicNDConstants.CONIC_LINE:
            case GeoConicNDConstants.CONIC_PARABOLA:
            case GeoConicNDConstants.CONIC_PARALLEL_LINES:
                if (getView3D().viewChangedByZoom()
                        || getView3D().viewChangedByTranslate()) {
                    setWaitForUpdate();
                }
                break;
            case GeoConicNDConstants.CONIC_CIRCLE:
            case GeoConicNDConstants.CONIC_ELLIPSE:
                if (getView3D().viewChangedByZoom()
                        || (visible != Visible.TOTALLY_INSIDE
                        && getView3D().viewChangedByTranslate())) {
                    setWaitForUpdate();
                }
                break;
            default:
                if (getView3D().viewChangedByZoom()) {
                    // will be updated if visible again
                    setWaitForUpdate();
                }
                break;
        }
        updateGeometriesVisibility();
	}

	@Override
	protected void updateGeometriesColor() {
		updateGeometriesColor(true);
	}

	@Override
	protected void setGeometriesVisibility(boolean visible) {
		setGeometriesVisibilityWithSurface(visible);
	}

}
