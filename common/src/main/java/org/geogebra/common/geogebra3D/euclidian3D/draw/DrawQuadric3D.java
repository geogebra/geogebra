package org.geogebra.common.geogebra3D.euclidian3D.draw;

import java.util.ArrayList;

import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.Previewable;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.Hitting;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.PlotterSurface;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer.PickingType;
import org.geogebra.common.geogebra3D.euclidian3D.printer3D.ExportToPrinter3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPlane3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3DPart;
import org.geogebra.common.kernel.PathNormalizer;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoQuadricNDConstants;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * Class for drawing quadrics.
 * 
 * @author mathieu
 *
 */
public class DrawQuadric3D extends Drawable3DSurfaces implements Previewable {
	private Coords project = Coords.createInhomCoorsInD3();
	private Coords p1 = Coords.createInhomCoorsInD3();
	private Coords p2 = Coords.createInhomCoorsInD3();

	private double[] parameters = new double[2];
	private double[] parameters1 = new double[2];
	private double[] parameters2 = new double[2];
	/**
	 * Last longitude used for painting; helps avoiding updates
	 */
	protected int longitude = 0;

	private double scale;

	private double alpha;
	private double beta;
	private Visible visible = Visible.TOTALLY_OUTSIDE;
	private Coords boundsMin = new Coords(3);
	private Coords boundsMax = new Coords(3);
	private double[] uMinMax;
	private double[] vMinMax;

	private DrawLine3D drawLine;
	private DrawPlane3D[] drawPlanes;

	private int surfaceDrawTypeAdded;

	private ArrayList<GeoPointND> selectedPoints;

	/**
	 * common constructor
	 * 
	 * @param a_view3d
	 *            view
	 * @param a_quadric
	 *            quadric
	 */
	public DrawQuadric3D(EuclidianView3D a_view3d, GeoQuadric3D a_quadric) {

		super(a_view3d, a_quadric);

	}

	static private void drawPlane(DrawPlane3D dp, Renderer renderer) {
		if (dp != null) {
			dp.drawGeometry(renderer);
		}
	}

	@Override
	public void drawGeometry(Renderer renderer) {
		switch (((GeoQuadric3D) getGeoElement()).getType()) {
		case GeoQuadricNDConstants.QUADRIC_PARALLEL_PLANES:
		case GeoQuadricNDConstants.QUADRIC_INTERSECTING_PLANES:
			drawPlane(drawPlanes[0], renderer);
			drawPlane(drawPlanes[1], renderer);
			break;
		case GeoQuadricNDConstants.QUADRIC_PLANE:
			drawPlane(drawPlanes[0], renderer);
			break;

		case GeoQuadricNDConstants.QUADRIC_LINE:
			// not used: see drawOutline() and drawGeometryHidden()
			break;

		default:
			renderer.getRendererImpl().setLayer(getLayer());
			renderer.getGeometryManager().draw(getSurfaceIndex());
			renderer.getRendererImpl().setLayer(Renderer.LAYER_DEFAULT);
			break;
		}
	}

	@Override
	protected void drawSurfaceGeometry(Renderer renderer) {
		drawGeometry(renderer);
	}

	@Override
	void drawGeometryHiding(Renderer renderer) {
		drawSurfaceGeometry(renderer);
	}

	@Override
	public void drawGeometryHidden(Renderer renderer) {
		switch (((GeoQuadric3D) getGeoElement()).getType()) {
		default:
			// do nothing
			break;
		case GeoQuadricNDConstants.QUADRIC_LINE:
			initDrawLine((GeoQuadric3D) getGeoElement());
			drawLine.drawGeometryHidden(renderer);
			break;
		}
	}

	@Override
	public void drawOutline(Renderer renderer) {
		if (isVisible()) {
			switch (((GeoQuadric3D) getGeoElement()).getType()) {
			default:
				// do nothing
				break;
			case GeoQuadricNDConstants.QUADRIC_LINE:
				initDrawLine((GeoQuadric3D) getGeoElement());
				drawLine.drawOutline(renderer);
				break;
			}
		}

		drawTracesOutline(renderer, false);

	}

	@Override
	public void drawHidden(Renderer renderer) {
		super.drawHidden(renderer);

		drawTracesOutline(renderer, true);

	}

	@Override
	protected void drawTracesOutline(Renderer renderer, boolean hidden) {

		if ((((GeoQuadric3D) getGeoElement())
				.getType() != GeoQuadricNDConstants.QUADRIC_LINE || hidden)
				&& drawLine != null) {
			drawLine.drawTracesOutline(renderer, hidden);
		}

	}

	@Override
	protected void drawTracesNotTranspSurface(Renderer renderer) {
		if (drawPlanes != null) {
			drawPlanes[0].drawTracesNotTranspSurface(renderer);
			if (drawPlanes[1] != null) {
				drawPlanes[1].drawTracesNotTranspSurface(renderer);
			}
		}

		super.drawTracesNotTranspSurface(renderer);
	}

	@Override
	protected void drawTracesHidingSurface(Renderer renderer) {

		if (drawPlanes != null) {
			drawPlanes[0].drawTracesHidingSurface(renderer);
			if (drawPlanes[1] != null) {
				drawPlanes[1].drawTracesHidingSurface(renderer);
			}
		}

		super.drawTracesHidingSurface(renderer);
	}

	@Override
	protected void drawTracesTranspSurface(Renderer renderer) {
		if (drawPlanes != null) {
			drawPlanes[0].drawTracesTranspSurface(renderer);
			if (drawPlanes[1] != null) {
				drawPlanes[1].drawTracesTranspSurface(renderer);
			}
		}

		super.drawTracesTranspSurface(renderer);

	}

	@Override
	protected void drawGeometryForPicking(Renderer renderer, PickingType type) {
		if (((GeoQuadric3D) getGeoElement())
				.getType() == GeoQuadricNDConstants.QUADRIC_LINE) {
			drawOutline(renderer);
		} else {
			drawGeometry(renderer);
		}
	}

	@Override
	public void updateColors() {
		super.updateColors();

		GeoQuadric3D quadric = (GeoQuadric3D) getGeoElement();
		switch (quadric.getType()) {
		default:
			// do nothing
			break;
		case GeoQuadricNDConstants.QUADRIC_PARALLEL_PLANES:
		case GeoQuadricNDConstants.QUADRIC_INTERSECTING_PLANES:
			initDrawPlanes(quadric);
			drawPlanes[0].updateColors();
			drawPlanes[1].updateColors();
			break;

		case GeoQuadricNDConstants.QUADRIC_PLANE:
			initDrawPlanes(quadric);
			drawPlanes[0].updateColors();
			break;

		case GeoQuadricNDConstants.QUADRIC_LINE:
			initDrawLine(quadric);
			drawLine.updateColors();
			break;
		}
	}

	/**
	 * Visibility flag
	 * 
	 * @author mathieu
	 *
	 */
	private static enum Visible {
		/** the quadric is totally outside */
		TOTALLY_OUTSIDE,
		/** the quadric is totally inside */
		TOTALLY_INSIDE,
		/** the quadric is partly inside, center outside */
		CENTER_OUTSIDE,
		/** the quadric is partly inside, center inside */
		CENTER_INSIDE
	}

	/**
	 * check if the sphere is (at least partially) visible
	 * 
	 * @param center
	 *            sphere center
	 * @param radius
	 *            sphere radius
	 */
	private void checkSphereVisible(Coords center, double radius) {

		double frustumRadius = getView3D().getFrustumRadius();
		Coords origin = getView3D().getCenter();
		Coords v = origin.sub(center);
		v.calcNorm();
		double centersDistance = v.getNorm();

		if (centersDistance > radius + frustumRadius) { // sphere totally
														// outside the frustum
			visible = Visible.TOTALLY_OUTSIDE;
		} else if (centersDistance + frustumRadius < radius) { // frustum
																// totally
																// inside the
																// sphere
			visible = Visible.TOTALLY_OUTSIDE;
		} else if (centersDistance + radius < frustumRadius) { // totally inside
			visible = Visible.TOTALLY_INSIDE;
		} else if (centersDistance < frustumRadius) { // center inside
			visible = Visible.CENTER_INSIDE;
		} else {
			// calc angles to draw minimum longitudes
			double horizontalDistance = Math
					.sqrt(v.getX() * v.getX() + v.getY() * v.getY());
			if (horizontalDistance > frustumRadius) {
				alpha = Math.asin(frustumRadius / horizontalDistance);
				beta = Math.atan2(v.getY(), v.getX());
				// Log.debug("alpha = "+(alpha*180/Math.PI)+"degrees, beta =
				// "+(beta*180/Math.PI)+"degrees");
				visible = Visible.CENTER_OUTSIDE; // center outside
			} else {
				visible = Visible.CENTER_INSIDE; // do as if center inside
			}
		}

	}

	private void drawSphere(PlotterSurface surface, Coords center,
			double radius) {
		if (visible == Visible.CENTER_OUTSIDE) {
			int longitudeAlpha = 8;
			while (longitudeAlpha * Math.PI < alpha * longitude) {
				longitudeAlpha *= 2;
			}
			// Log.debug(longitudeAlpha+"");
			surface.drawSphere(center, radius, longitude,
					beta - longitudeAlpha * Math.PI / longitude,
					longitudeAlpha);
		} else {
			surface.drawSphere(center, radius, longitude);
		}
	}

	@Override
	public void enlargeBounds(Coords min, Coords max, boolean dontExtend) {
		switch (((GeoQuadric3D) getGeoElement()).getType()) {
		default:
			// do nothing
			break;
		case GeoQuadricNDConstants.QUADRIC_SPHERE:
		case GeoQuadricNDConstants.QUADRIC_SINGLE_POINT:
		case GeoQuadricNDConstants.QUADRIC_CONE:
		case GeoQuadricNDConstants.QUADRIC_CYLINDER:
			enlargeBounds(min, max, boundsMin, boundsMax);
			break;
		}
	}

	@Override
	protected boolean updateForItSelf() {

		Renderer renderer = getView3D().getRenderer();
		GeoQuadric3D quadric = (GeoQuadric3D) getGeoElement();
		PlotterSurface surface;
		int type = quadric.getType();

		switch (type) {
		case GeoQuadricNDConstants.QUADRIC_SPHERE:
			Coords center = quadric.getMidpoint3D();
			double radius = quadric.getHalfAxis(0);
			boundsMin.setValues(center, 3);
			boundsMax.setValues(center, 3);
			boundsMin.addInside(-radius);
			boundsMax.addInside(radius);
			checkSphereVisible(center, radius);
			if (visible != Visible.TOTALLY_OUTSIDE) {
				setPackSurface();
				surface = renderer.getGeometryManager().getSurface();
				surface.start(getReusableSurfaceIndex());
				scale = getView3D().getMaxScale();
				longitude = surface.calcSphereLongitudesNeeded(radius, scale);
				drawSphere(surface, center, radius);
				setSurfaceIndex(surface.end());
				endPacking();
			} else {
				setSurfaceIndexNotVisible();
			}
			hidePlanesIfNotNull();
			hideLineIfNotNull();
			break;
		case GeoQuadricNDConstants.QUADRIC_ELLIPSOID:
			updateEllipsoid(quadric, renderer);
			hidePlanesIfNotNull();
			hideLineIfNotNull();
			break;

		case GeoQuadricNDConstants.QUADRIC_HYPERBOLOID_ONE_SHEET:
			updateHyperboloidOneSheet(quadric, renderer);
			hidePlanesIfNotNull();
			hideLineIfNotNull();
			break;

		case GeoQuadricNDConstants.QUADRIC_HYPERBOLOID_TWO_SHEETS:
			updateHyperboloidTwoSheets(quadric, renderer);
			hidePlanesIfNotNull();
			hideLineIfNotNull();
			break;

		case GeoQuadricNDConstants.QUADRIC_PARABOLOID:
			updateParaboloid(quadric, renderer);
			hidePlanesIfNotNull();
			hideLineIfNotNull();
			break;

		case GeoQuadricNDConstants.QUADRIC_HYPERBOLIC_PARABOLOID:
			updateHyperbolicParaboloid(quadric, renderer);
			hidePlanesIfNotNull();
			hideLineIfNotNull();
			break;

		case GeoQuadricNDConstants.QUADRIC_PARABOLIC_CYLINDER:
			updateParabolicCylinder(quadric, renderer);
			hidePlanesIfNotNull();
			hideLineIfNotNull();
			break;

		case GeoQuadricNDConstants.QUADRIC_HYPERBOLIC_CYLINDER:
			updateHyperbolicCylinder(quadric, renderer);
			hidePlanesIfNotNull();
			hideLineIfNotNull();
			break;

		case GeoQuadricNDConstants.QUADRIC_CONE:
			updateCone(quadric, renderer);
			hidePlanesIfNotNull();
			hideLineIfNotNull();
			break;

		case GeoQuadricNDConstants.QUADRIC_CYLINDER:
			updateCylinder(quadric, renderer);
			hidePlanesIfNotNull();
			hideLineIfNotNull();
			break;

		case GeoQuadricNDConstants.QUADRIC_SINGLE_POINT:
			surface = renderer.getGeometryManager().getSurface();
			setPackSurface();
			surface.start(getReusableSurfaceIndex());
			Coords m = quadric.getMidpoint3D();
			double thickness = quadric.getLineThickness()
					/ getView3D().getScale() * DrawPoint3D.DRAW_POINT_FACTOR;
			surface.drawSphere(quadric.getLineThickness(), m, thickness);
			setSurfaceIndex(surface.end());
			endPacking();

			boundsMin.setValues(m, 3);
			boundsMax.setValues(m, 3);
			boundsMin.addInside(-thickness);
			boundsMax.addInside(thickness);
			hidePlanesIfNotNull();
			hideLineIfNotNull();
			break;

		case GeoQuadricNDConstants.QUADRIC_PARALLEL_PLANES:
		case GeoQuadricNDConstants.QUADRIC_INTERSECTING_PLANES:
			initDrawPlanes(quadric);
			drawPlanes[0].updateForItSelf();
			drawPlanes[1].updateForItSelf();
			hideSurface();
			hideLineIfNotNull();
			break;

		case GeoQuadricNDConstants.QUADRIC_PLANE:
			initDrawPlanes(quadric);
			drawPlanes[0].updateForItSelf();
			if (shouldBePackedForManager()) {
			    if (drawPlanes[1] != null) {
                    drawPlanes[1].setSurfaceIndexNotVisible();
                    drawPlanes[1].setGeometryIndexNotVisible();
                }
			}
			hideSurface();
			hideLineIfNotNull();
			break;

		case GeoQuadricNDConstants.QUADRIC_LINE:
			initDrawLine(quadric);
			drawLine.updateForItSelf();
			hideSurface();
			hidePlanesIfNotNull();
			break;

		default:
			setSurfaceIndexNotVisible();
			hideLineIfNotNull();
			hidePlanesIfNotNull();
		}

		return true;
	}

	private void updateEllipsoid(GeoQuadric3D quadric, Renderer renderer) {
		setPackSurface();
		Coords center = quadric.getMidpoint3D();
		double r0 = quadric.getHalfAxis(0);
		double r1 = quadric.getHalfAxis(1);
		double r2 = quadric.getHalfAxis(2);
		PlotterSurface surface = renderer.getGeometryManager().getSurface();
		surface.start(getReusableSurfaceIndex());
		scale = getView3D().getMaxScale();
		double radius = Math.max(r0, Math.max(r1, r2));
		longitude = surface.calcSphereLongitudesNeeded(radius, scale);
		Coords ev0 = quadric.getEigenvec3D(0);
		Coords ev1 = quadric.getEigenvec3D(1);
		Coords ev2 = quadric.getEigenvec3D(2);
		surface.drawEllipsoid(center, ev0, ev1, ev2, r0, r1, r2, longitude);
		setSurfaceIndex(surface.end());
		endPacking();
	}

	private void updateHyperboloidOneSheet(GeoQuadric3D quadric,
			Renderer renderer) {
		double r2 = quadric.getHalfAxis(2);
		if (r2 == Double.POSITIVE_INFINITY) {
			updateCylinder(quadric, renderer);
			return;
		}
		Coords center = quadric.getMidpoint3D();
		double r0 = quadric.getHalfAxis(0);
		double r1 = quadric.getHalfAxis(1);
		PlotterSurface surface = renderer.getGeometryManager().getSurface();
		setPackSurface();
		surface.start(getReusableSurfaceIndex());
		Coords ev0 = quadric.getEigenvec3D(0);
		Coords ev1 = quadric.getEigenvec3D(1);
		Coords ev2 = quadric.getEigenvec3D(2);
		initVminMax();
		getView3D().getMinIntervalOutsideClipping(vMinMax, center, ev2.mul(r2));
		scale = getView3D().getMaxScale();
		// get radius at max
		double radius = Math.max(r0, r1) * Math.max(Math.abs(vMinMax[0]),
				Math.max(Math.abs(vMinMax[1]), 1)) / r2;
		longitude = surface.calcSphereLongitudesNeeded(radius, scale);
		double min = DrawConic3D.asinh(vMinMax[0]);
		double max = DrawConic3D.asinh(vMinMax[1]);
		surface.drawHyperboloidOneSheet(center, ev0, ev1, ev2, r0, r1, r2,
				longitude, min, max, !getView3D().useClippingCube());
		setSurfaceIndex(surface.end());
		endPacking();
	}

	private void initVminMax() {
		if (vMinMax == null) {
			vMinMax = new double[2];
		}
		vMinMax[0] = Double.POSITIVE_INFINITY;
		vMinMax[1] = Double.NEGATIVE_INFINITY;
	}

	private void initUminMax() {
		if (uMinMax == null) {
			uMinMax = new double[2];
		}
		uMinMax[0] = Double.POSITIVE_INFINITY;
		uMinMax[1] = Double.NEGATIVE_INFINITY;
	}

	private void updateHyperbolicParaboloid(GeoQuadric3D quadric,
			Renderer renderer) {
		Coords center = quadric.getMidpoint3D();
		PlotterSurface surface = renderer.getGeometryManager().getSurface();
		setPackSurface();
		surface.start(getReusableSurfaceIndex());
		Coords ev0 = quadric.getEigenvec3D(0);

		initUminMax();
		getView3D().getMinIntervalOutsideClipping(uMinMax, center, ev0);
		initVminMax();
		Coords ev1 = quadric.getEigenvec3D(1);
		Coords ev2 = quadric.getEigenvec3D(2);
		double r0 = quadric.getHalfAxis(0);
		double r1 = quadric.getHalfAxis(1);
		getView3D().getMinIntervalOutsideClipping(vMinMax, center, ev1);
		surface.drawHyperbolicParaboloid(center, ev0, ev1, ev2, r0, r1,
				uMinMax[0], uMinMax[1], vMinMax[0], vMinMax[1],
				!getView3D().useClippingCube());
		setSurfaceIndex(surface.end());
		endPacking();
	}

	private void updateHyperboloidTwoSheets(GeoQuadric3D quadric,
			Renderer renderer) {
		double min, max;
		Coords center = quadric.getMidpoint3D();
		double r0 = quadric.getHalfAxis(0);
		double r1 = quadric.getHalfAxis(1);
		double r2 = quadric.getHalfAxis(2);
		setPackSurface();
		PlotterSurface surface = renderer.getGeometryManager().getSurface();
		surface.start(getReusableSurfaceIndex());
		Coords ev2 = quadric.getEigenvec3D(2);
		initVminMax();
		getView3D().getMinIntervalOutsideClipping(vMinMax, center, ev2.mul(r2));
		scale = getView3D().getMaxScale();
		// get radius at max
		double radius = Math.max(r0, r1)
				* Math.max(Math.abs(vMinMax[0]), Math.abs(vMinMax[1])) / r2;
		longitude = surface.calcSphereLongitudesNeeded(radius, scale);
		if (vMinMax[0] < -1) { // bottom exists
			min = -DrawConic3D.acosh(-vMinMax[0]);
		} else if (vMinMax[0] <= 1) { // top ends at pole
			min = 0;
		} else { // top pole is cut
			min = DrawConic3D.acosh(vMinMax[0]);
		}
		if (vMinMax[1] > 1) { // top exists
			max = DrawConic3D.acosh(vMinMax[1]);
		} else if (vMinMax[1] >= -1) { // bottom ends at pole
			max = 0;
		} else { // bottom pole is cut
			max = -DrawConic3D.acosh(-vMinMax[1]);
		}
		Coords ev0 = quadric.getEigenvec3D(0);
		Coords ev1 = quadric.getEigenvec3D(1);
		surface.drawHyperboloidTwoSheets(center, ev0, ev1, ev2, r0, r1, r2,
				longitude, min, max, !getView3D().useClippingCube());
		setSurfaceIndex(surface.end());
		endPacking();
	}

	private void updateParaboloid(GeoQuadric3D quadric, Renderer renderer) {
		Coords center = quadric.getMidpoint3D();
		double r0 = quadric.getHalfAxis(0);
		double r1 = quadric.getHalfAxis(1);
		PlotterSurface surface = renderer.getGeometryManager().getSurface();
		setPackSurface();
		surface.start(getReusableSurfaceIndex());
		Coords ev0 = quadric.getEigenvec3D(0);
		Coords ev1 = quadric.getEigenvec3D(1);
		Coords ev2 = quadric.getEigenvec3D(2);
		if (quadric.getHalfAxis(2) < 0) {
			ev0 = ev0.mul(-1);
			ev2 = ev2.mul(-1);
		}
		initVminMax();
		getView3D().getMinIntervalOutsideClipping(vMinMax, center, ev2);
		if (vMinMax[1] < 0) {
			// nothing to draw
			setSurfaceIndex(surface.end());
		} else {
			scale = getView3D().getMaxScale();
			// get radius at max
			if (vMinMax[0] <= 0) {
				vMinMax[0] = 0;
			} else {
				vMinMax[0] = Math.sqrt(vMinMax[0]);
			}
			vMinMax[1] = Math.sqrt(vMinMax[1]);
			double radius = Math.max(r0, r1) * vMinMax[1];
			longitude = surface.calcSphereLongitudesNeeded(radius, scale);
			surface.drawParaboloid(center, ev0, ev1, ev2, r0, r1, longitude,
					vMinMax[0], vMinMax[1], !getView3D().useClippingCube());
			setSurfaceIndex(surface.end());
		}
		endPacking();
	}

	private void updateParabolicCylinder(GeoQuadric3D quadric,
			Renderer renderer) {
		Coords center = quadric.getMidpoint3D();
		double r2 = quadric.getHalfAxis(2);
		PlotterSurface surface = renderer.getGeometryManager().getSurface();
		setPackSurface();
		surface.start(getReusableSurfaceIndex());
		Coords ev0 = quadric.getEigenvec3D(0);
		Coords ev1 = quadric.getEigenvec3D(1);
		Coords ev2 = quadric.getEigenvec3D(2);
		if (vMinMax == null) {
			vMinMax = new double[2];
		}
		vMinMax[0] = Double.POSITIVE_INFINITY;
		vMinMax[1] = Double.NEGATIVE_INFINITY;
		getView3D().getMinIntervalOutsideClipping(vMinMax, center, ev0);
		if (quadric instanceof GeoQuadric3DPart) { // simple cylinder

			if (vMinMax[1] < 0) {
				// nothing to draw

			} else {
				if (vMinMax[0] <= 0) {
					vMinMax[0] = 0;
				} else {
					vMinMax[0] = Math.sqrt(vMinMax[0]);
				}
				vMinMax[1] = Math.sqrt(vMinMax[1]);
				surface.drawParabolicCylinder(center, ev0, ev1, ev2, r2,
						vMinMax[0], vMinMax[1], quadric.getMinParameter(1),
						quadric.getMaxParameter(1), false);

				boundsMin.set(Double.POSITIVE_INFINITY);
				boundsMax.set(Double.NEGATIVE_INFINITY);
			}

		} else {
			if (uMinMax == null) {
				uMinMax = new double[2];
			}
			uMinMax[0] = Double.POSITIVE_INFINITY;
			uMinMax[1] = Double.NEGATIVE_INFINITY;
			if (vMinMax[1] < 0) {
				// nothing to draw

			} else {
				scale = getView3D().getMaxScale();
				// get radius at max
				if (vMinMax[0] <= 0) {
					vMinMax[0] = 0;
				} else {
					vMinMax[0] = Math.sqrt(vMinMax[0]);
				}
				vMinMax[1] = Math.sqrt(vMinMax[1]);
				getView3D().getMinIntervalOutsideClipping(uMinMax, center, ev1);
				surface.drawParabolicCylinder(center, ev0, ev1, ev2, r2,
						vMinMax[0], vMinMax[1], uMinMax[0], uMinMax[1],
						!getView3D().useClippingCube());
			}
		}
		setSurfaceIndex(surface.end());
		endPacking();
	}

	private void updateCone(GeoQuadric3D quadric, Renderer renderer) {
		PlotterSurface surface = renderer.getGeometryManager().getSurface();
		setPackSurface();
		surface.start(getReusableSurfaceIndex());
		if (quadric instanceof GeoQuadric3DPart) { // simple cone
			double height = ((GeoQuadric3DPart) quadric).getBottomParameter()
					- ((GeoQuadric3DPart) quadric).getTopParameter();
			Coords top = quadric.getMidpoint3D();
			Coords ev1 = quadric.getEigenvec3D(0);
			Coords ev2 = quadric.getEigenvec3D(1);
			double radius = quadric.getHalfAxis(0);
			double radius2 = quadric.getHalfAxis(1);
			Coords bottomCenter = surface.cone(top, ev1,
					ev2, quadric.getEigenvec3D(2), radius, radius2, 0,
					2 * Math.PI, height, 1f);

			boundsMin.setValues(top, 3);
			boundsMax.setValues(top, 3);
			radius *= height;
			enlargeBoundsToDiagonal(boundsMin, boundsMax, bottomCenter, ev1,
					ev2, radius, radius2);

		} else { // infinite cone
			if (vMinMax == null) {
				vMinMax = new double[2];
			}
			vMinMax[0] = Double.POSITIVE_INFINITY;
			vMinMax[1] = Double.NEGATIVE_INFINITY;
			getMinMax(vMinMax);
			double min = vMinMax[0];
			double max = vMinMax[1];
			// min -= delta;
			// max += delta;
			// Log.debug(min+","+max);
			Coords center = quadric.getMidpoint3D();
			Coords ev1 = quadric.getEigenvec3D(0);
			Coords ev2 = quadric.getEigenvec3D(1);
			Coords ev3 = quadric.getEigenvec3D(2);
			double r1 = quadric.getHalfAxis(0);
			double r2 = quadric.getHalfAxis(1);

			boundsMin.set(Double.POSITIVE_INFINITY);
			boundsMax.set(Double.NEGATIVE_INFINITY);
			if (min * max < 0) {
				if (getView3D().useClippingCube()) {
					Coords bottomCenter = surface.cone(center, ev1, ev2,
							ev3, r1, r2, 0, 2 * Math.PI, min, 1f);
					enlargeBoundsToDiagonal(boundsMin, boundsMax, bottomCenter,
							ev1, ev2, r1 * min, r2 * min);
					bottomCenter = surface.cone(center, ev1, ev2, ev3, r1,
							r2, 0, 2 * Math.PI, max, 1f);
					enlargeBoundsToDiagonal(boundsMin, boundsMax, bottomCenter,
							ev1, ev2, r1 * max, r2 * max);
				} else {
					Coords bottomCenter = surface.cone(center, ev1, ev2,
							ev3, r1, r2, 0, 2 * Math.PI, min,
							(float) ((-9 * min - max) / (min - max)));
					enlargeBoundsToDiagonal(boundsMin, boundsMax, bottomCenter,
							ev1, ev2, r1 * min, r2 * min);
					bottomCenter = surface.cone(center, ev1, ev2, ev3, r1,
							r2, 0, 2 * Math.PI, max,
							(float) ((-9 * max - min) / (max - min)));
					enlargeBoundsToDiagonal(boundsMin, boundsMax, bottomCenter,
							ev1, ev2, r1 * max, r2 * max);
				}
			} else {
				if (getView3D().useClippingCube()) {
					Coords[] centers = surface.cone(center, ev1, ev2, ev3,
							r1, r2, 0, 2 * Math.PI, min, max, false, false);
					enlargeBoundsToDiagonal(boundsMin, boundsMax, centers[0],
							ev1, ev2, r1 * min, r2 * min);
					enlargeBoundsToDiagonal(boundsMin, boundsMax, centers[1],
							ev1, ev2, r1 * max, r2 * max);
				} else {
					double delta = (max - min) / 10;
					surface.cone(center, ev1, ev2, ev3, r1, r2, 0,
							2 * Math.PI, min + delta, max - delta, false, false);
					Coords[] centers = surface.cone(center, ev1, ev2, ev3,
							r1, r2, 0,
							2 * Math.PI, min, min + delta, true, false);
					enlargeBoundsToDiagonal(boundsMin, boundsMax, centers[0],
							ev1, ev2, r1 * min, r2 * min);
					centers = surface.cone(center, ev1, ev2, ev3, r1, r2,
							0,
							2 * Math.PI, max - delta, max, false, true);
					enlargeBoundsToDiagonal(boundsMin, boundsMax, centers[1],
							ev1, ev2, r1 * max, r2 * max);
				}
			}
		}

		setSurfaceIndex(surface.end());
		endPacking();
	}

	private void updateHyperbolicCylinder(GeoQuadric3D quadric,
			Renderer renderer) {
		double min, max;
		double radius;
		Coords center = quadric.getMidpoint3D();
		double r0 = quadric.getHalfAxis(0);
		double r1 = quadric.getHalfAxis(1);

		Coords ev0 = quadric.getEigenvec3D(0);
		Coords ev1 = quadric.getEigenvec3D(1);
		Coords ev2 = quadric.getEigenvec3D(2);
		PlotterSurface surface = renderer.getGeometryManager().getSurface();
		setPackSurface();
		surface.start(getReusableSurfaceIndex());
		if (uMinMax == null) {
			uMinMax = new double[2];
		}
		uMinMax[0] = Double.POSITIVE_INFINITY;
		uMinMax[1] = Double.NEGATIVE_INFINITY;
		getView3D().getMinIntervalOutsideClipping(uMinMax, center, ev0.mul(r0));
		if (uMinMax[0] < -1) { // bottom exists
			min = -DrawConic3D.acosh(-uMinMax[0]);
		} else if (uMinMax[0] <= 1) { // top ends at pole
			min = 0;
		} else { // top pole is cut
			min = DrawConic3D.acosh(uMinMax[0]);
		}
		if (uMinMax[1] > 1) { // top exists
			max = DrawConic3D.acosh(uMinMax[1]);
		} else if (uMinMax[1] >= -1) { // bottom ends at pole
			max = 0;
		} else { // bottom pole is cut
			max = -DrawConic3D.acosh(-uMinMax[1]);
		}
		if (quadric instanceof GeoQuadric3DPart) { // simple cylinder
			radius = quadric.getHalfAxis(0);
			double radius2 = quadric.getHalfAxis(1);
			longitude = renderer.getGeometryManager().getLongitude(radius,
					getView3D().getMaxScale());
			if (min < 0) {
				surface.drawHyperbolicCylinder(center, ev0.mul(-1), ev1, ev2,
						radius, radius2, -max, -min, quadric.getMinParameter(1),
						quadric.getMaxParameter(1), false);
			}
			if (max > 0) {
				surface.drawHyperbolicCylinder(center, ev0, ev1, ev2, radius,
						radius2, min, max, quadric.getMinParameter(1),
						quadric.getMaxParameter(1), false);
			}
			boundsMin.set(Double.POSITIVE_INFINITY);
			boundsMax.set(Double.NEGATIVE_INFINITY);
			enlargeBoundsToDiagonal(boundsMin, boundsMax, center, ev1, ev2,
					radius, radius);

		} else {

			scale = getView3D().getMaxScale();

			if (vMinMax == null) {
				vMinMax = new double[2];
			}
			vMinMax[0] = Double.POSITIVE_INFINITY;
			vMinMax[1] = Double.NEGATIVE_INFINITY;
			getView3D().getMinIntervalOutsideClipping(vMinMax, center, ev2);
			if (min < 0) {
				surface.drawHyperbolicCylinder(center, ev0.mul(-1), ev1.mul(-1),
						ev2, r0, r1, -max, -min, vMinMax[0], vMinMax[1],
						!getView3D().useClippingCube());
			}
			if (max > 0) {
				surface.drawHyperbolicCylinder(center, ev0, ev1, ev2, r0, r1,
						min, max, vMinMax[0], vMinMax[1],
						!getView3D().useClippingCube());
			}
			uMinMax[0] = Math.sinh(min);
			uMinMax[1] = Math.sinh(max);
		}
		setSurfaceIndex(surface.end());
		endPacking();
	}

	private void updateCylinder(GeoQuadric3D quadric, Renderer renderer) {
		double min, max;
		double radius, r1, r2;
		Coords center = quadric.getMidpoint3D();
		Coords ev1 = quadric.getEigenvec3D(0);
		Coords ev2 = quadric.getEigenvec3D(1);
		Coords ev3 = quadric.getEigenvec3D(2);

		PlotterSurface surface = renderer.getGeometryManager().getSurface();
		setPackSurface();
		surface.start(getReusableSurfaceIndex());

		if (quadric instanceof GeoQuadric3DPart) { // simple cylinder
			radius = quadric.getHalfAxis(0);
			double radius2 = quadric.getHalfAxis(1);
			longitude = renderer.getGeometryManager().getLongitude(radius,
					getView3D().getMaxScale());
			Coords[] centers = surface.cylinder(center, ev1, ev2,
					ev3, radius, radius2, 0, 2 * Math.PI,
					quadric.getMinParameter(1), quadric.getMaxParameter(1),
					false, false, longitude);

			boundsMin.set(Double.POSITIVE_INFINITY);
			boundsMax.set(Double.NEGATIVE_INFINITY);
			enlargeBoundsToDiagonal(boundsMin, boundsMax, center, ev1, ev2,
					radius, radius);
			enlargeBoundsToDiagonal(boundsMin, boundsMax, centers[1], ev1,
					ev2, radius, radius);

		} else {
			if (vMinMax == null) {
				vMinMax = new double[2];
			}
			vMinMax[0] = Double.POSITIVE_INFINITY;
			vMinMax[1] = Double.NEGATIVE_INFINITY;
			getMinMax(vMinMax);
			min = vMinMax[0];
			max = vMinMax[1];
			r1 = quadric.getHalfAxis(0);
			r2 = quadric.getHalfAxis(1);
			radius = Math.max(r1, r2);

			longitude = renderer.getGeometryManager().getLongitude(radius,
					getView3D().getMaxScale());
			boundsMin.set(Double.POSITIVE_INFINITY);
			boundsMax.set(Double.NEGATIVE_INFINITY);
			if (getView3D().useClippingCube()) {
				Coords[] centers = surface.cylinder(center, ev1, ev2, ev3,
						r1, r2, 0, 2 * Math.PI, min, max, false, false,
						longitude);
				enlargeBoundsToDiagonal(boundsMin, boundsMax, centers[0], ev1,
						ev2, r1, r2);
				enlargeBoundsToDiagonal(boundsMin, boundsMax, centers[1], ev1,
						ev2, r1, r2);
			} else {
				double delta = (max - min) / 10;
				surface.cylinder(center, ev1, ev2, ev3, r1, r2, 0,
						2 * Math.PI, min + delta, max - delta, false, false, longitude);
				Coords[] centers = surface.cylinder(center, ev1, ev2, ev3,
						r1, r2, 0,
						2 * Math.PI, min, min + delta, true, false, longitude);
				enlargeBoundsToDiagonal(boundsMin, boundsMax, centers[0], ev1,
						ev2, r1, r2);
				centers = surface.cylinder(center, ev1, ev2, ev3, r1,
						r2, 0, 2 * Math.PI, max - delta, max, false, true,
						longitude);
				enlargeBoundsToDiagonal(boundsMin, boundsMax, centers[1], ev1,
						ev2, r1, r2);
			}
		}

		setSurfaceIndex(surface.end());
		endPacking();

	}

	private void initDrawPlanes(GeoQuadric3D quadric) {
		if (drawPlanes == null || drawPlanes[0] == null) {
			drawPlanes = new DrawPlane3DForQuadrics[2];
			GeoPlane3D[] planes = quadric.getPlanes();
			drawPlanes[0] = new DrawPlane3DForQuadrics(getView3D(), planes[0],
					quadric);
			drawPlanes[1] = new DrawPlane3DForQuadrics(getView3D(), planes[1],
					quadric);
		}
	}

	private void hideSurface() {
		if (shouldBePackedForManager()) {
			setSurfaceIndex(-1);
		}
	}

	private void hidePlanesIfNotNull() {
		if (shouldBePackedForManager() && drawPlanes != null) {
			drawPlanes[0].setSurfaceIndexNotVisible();
			drawPlanes[0].setGeometryIndexNotVisible();
            if (drawPlanes[1] != null) {
                drawPlanes[1].setSurfaceIndexNotVisible();
                drawPlanes[1].setGeometryIndexNotVisible();
            }
		}
	}

	private void initDrawLine(GeoQuadric3D quadric) {
		if (drawLine == null) {
			drawLine = new DrawLine3DForQuadrics(getView3D(), quadric.getLine(),
					quadric);
		}
	}

	private void hideLineIfNotNull() {
		if (shouldBePackedForManager() && drawLine != null) {
			drawLine.setGeometryIndexNotVisible();
		}
	}

	/**
	 * 
	 * @return min and max value along the axis of the quadric
	 */
	protected double[] getMinMax() {

		GeoQuadric3D quadric = (GeoQuadric3D) getGeoElement();

		double[] minmax = { Double.POSITIVE_INFINITY,
				Double.NEGATIVE_INFINITY };

		getView3D().getMinIntervalOutsideClipping(minmax,
				quadric.getMidpoint3D(), quadric.getEigenvec3D(2));

		// Log.debug(minmax[0]+","+minmax[1]);

		return minmax;
	}

	/**
	 * set min and max value along the axis of the quadric
	 * 
	 * @param minmax
	 *            min/max values
	 */
	protected void getMinMax(double[] minmax) {

		GeoQuadric3D quadric = (GeoQuadric3D) getGeoElement();

		getView3D().getMinIntervalOutsideClipping(minmax,
				quadric.getMidpoint3D(), quadric.getEigenvec3D(2));

	}

	@Override
	protected void updateForView() {

		GeoQuadric3D quadric = (GeoQuadric3D) getGeoElement();
		int type = quadric.getType();
		switch (type) {
		case GeoQuadricNDConstants.QUADRIC_SPHERE:
			if (getView3D().viewChangedByZoom()) {
				Renderer renderer = getView3D().getRenderer();
				PlotterSurface surface = renderer.getGeometryManager()
						.getSurface();
				scale = getView3D().getMaxScale();
				// check if longitude length changes
				double radius = quadric.getHalfAxis(0);
				int l = surface.calcSphereLongitudesNeeded(radius, scale);
				Coords center = quadric.getMidpoint3D();
				checkSphereVisible(center, radius);
				if (visible != Visible.TOTALLY_OUTSIDE) {
					longitude = l;
					setPackSurface();
					surface.start(getReusableSurfaceIndex());
					drawSphere(surface, center, radius);
					setSurfaceIndex(surface.end());
					endPacking();
					recordTrace();
				} else {
					setSurfaceIndex(-1);
				}
			} else if (visible != Visible.TOTALLY_INSIDE
					&& getView3D().viewChangedByTranslate()) {
				Renderer renderer = getView3D().getRenderer();
				PlotterSurface surface = renderer.getGeometryManager()
						.getSurface();

				Coords center = quadric.getMidpoint3D();
				double radius = quadric.getHalfAxis(0);
				checkSphereVisible(center, radius);
				if (visible != Visible.TOTALLY_OUTSIDE) {
					setPackSurface();
					surface.start(getReusableSurfaceIndex());
					drawSphere(surface, center, radius);
					setSurfaceIndex(surface.end());
					endPacking();
					recordTrace();
				} else {
					setSurfaceIndex(-1);
				}

			}
			break;
		case GeoQuadricNDConstants.QUADRIC_ELLIPSOID:
		case GeoQuadricNDConstants.QUADRIC_CONE:
		case GeoQuadricNDConstants.QUADRIC_CYLINDER:
		case GeoQuadricNDConstants.QUADRIC_HYPERBOLOID_ONE_SHEET:
		case GeoQuadricNDConstants.QUADRIC_HYPERBOLOID_TWO_SHEETS:
		case GeoQuadricNDConstants.QUADRIC_PARABOLOID:
		case GeoQuadricNDConstants.QUADRIC_HYPERBOLIC_PARABOLOID:
		case GeoQuadricNDConstants.QUADRIC_PARABOLIC_CYLINDER:
		case GeoQuadricNDConstants.QUADRIC_HYPERBOLIC_CYLINDER:
		case GeoQuadricNDConstants.QUADRIC_SINGLE_POINT:
			if (getView3D().viewChangedByZoom()
					|| getView3D().viewChangedByTranslate()) {
				updateForItSelf();
			}
			break;
		case GeoQuadricNDConstants.QUADRIC_PARALLEL_PLANES:
		case GeoQuadricNDConstants.QUADRIC_INTERSECTING_PLANES:
			if (getView3D().viewChanged()) {
				initDrawPlanes(quadric);
				drawPlanes[0].updateForView();
				drawPlanes[1].updateForView();
				super.setWaitForUpdate();
			}
			break;

		case GeoQuadricNDConstants.QUADRIC_PLANE:
			if (getView3D().viewChanged()) {
				initDrawPlanes(quadric);
				drawPlanes[0].updateForView();
				super.setWaitForUpdate();
			}
			break;

		case GeoQuadricNDConstants.QUADRIC_LINE:
			if (getView3D().viewChanged()) {
				initDrawLine(quadric);
				drawLine.updateForView();
				super.setWaitForUpdate();
			}
			break;

		default:
			// do nothing
			break;

		}
	}

	@Override
	protected void recordTrace() {
		GeoQuadric3D quadric = (GeoQuadric3D) getGeoElement();
		switch (quadric.getType()) {
		case GeoQuadricNDConstants.QUADRIC_PARALLEL_PLANES:
		case GeoQuadricNDConstants.QUADRIC_INTERSECTING_PLANES:
			initDrawPlanes(quadric);
			drawPlanes[0].recordTrace();
			drawPlanes[1].recordTrace();
			break;

		case GeoQuadricNDConstants.QUADRIC_PLANE:
			initDrawPlanes(quadric);
			drawPlanes[0].recordTrace();
			break;

		case GeoQuadricNDConstants.QUADRIC_LINE:
			initDrawLine(quadric);
			drawLine.recordTrace();
			break;

		default:
			super.recordTrace();
			break;
		}
	}

	@Override
	protected void clearTraceForViewChangedByZoomOrTranslate() {

		if (drawPlanes != null) {
			drawPlanes[0].clearTraceForViewChanged();
			if (drawPlanes[1] != null) {
				drawPlanes[1].clearTraceForViewChanged();
			}
		}

		if (drawLine != null) {
			drawLine.clearTraceForViewChanged();
		}

		super.clearTraceForViewChangedByZoomOrTranslate();

	}

	@Override
	public void setWaitForUpdate() {

		super.setWaitForUpdate();
		GeoQuadric3D quadric = (GeoQuadric3D) getGeoElement();
		int type = quadric.getType();

		switch (type) {
		default:
			// do nothing
			break;
		case GeoQuadricNDConstants.QUADRIC_PARALLEL_PLANES:
		case GeoQuadricNDConstants.QUADRIC_INTERSECTING_PLANES:
			initDrawPlanes(quadric);
			drawPlanes[0].setWaitForUpdate();
			drawPlanes[1].setWaitForUpdate();
			break;
		case GeoQuadricNDConstants.QUADRIC_PLANE:
			initDrawPlanes(quadric);
			drawPlanes[0].setWaitForUpdate();
			break;
		case GeoQuadricNDConstants.QUADRIC_LINE:
			initDrawLine(quadric);
			drawLine.setWaitForUpdate();
			break;
		}
	}

	@Override
	public int getPickOrder() {
		if (getPickingType() == PickingType.POINT_OR_CURVE) {
			return DRAW_PICK_ORDER_POINT;
		}

		return DRAW_PICK_ORDER_SURFACE;
	}

	@Override
	public boolean isTransparent() {
		if (getPickingType() == PickingType.SURFACE) {
			return getAlpha() <= EuclidianController.MAX_TRANSPARENT_ALPHA_VALUE_INT;
		}

		return false;
	}

	@Override
	public void addToDrawable3DLists(Drawable3DLists lists) {
		switch (((GeoQuadric3D) getGeoElement()).getType()) {
		case GeoQuadricNDConstants.QUADRIC_SPHERE:
		case GeoQuadricNDConstants.QUADRIC_ELLIPSOID:
		case GeoQuadricNDConstants.QUADRIC_CONE:
		case GeoQuadricNDConstants.QUADRIC_CYLINDER:
		case GeoQuadricNDConstants.QUADRIC_HYPERBOLOID_ONE_SHEET:
			addToDrawable3DLists(lists, DRAW_TYPE_CLOSED_SURFACES_CURVED);
			surfaceDrawTypeAdded = DRAW_TYPE_CLOSED_SURFACES_CURVED;
			break;
		default:
			addToDrawable3DLists(lists, DRAW_TYPE_SURFACES);
			surfaceDrawTypeAdded = DRAW_TYPE_SURFACES;
			break;
		}
		addToDrawable3DLists(lists, DRAW_TYPE_CURVES);
	}

	@Override
	public void removeFromDrawable3DLists(Drawable3DLists lists) {
		removeFromDrawable3DLists(lists, surfaceDrawTypeAdded);
		removeFromDrawable3DLists(lists, DRAW_TYPE_CURVES);
	}

	// //////////////////////////////
	// Previewable interface

	/**
	 * constructor for previewable
	 * 
	 * @param view3D
	 *            view
	 * @param selectedPoints
	 *            points defining preview
	 * @param type
	 *            quadric type
	 */
	public DrawQuadric3D(EuclidianView3D view3D,
			ArrayList<GeoPointND> selectedPoints, int type) {

		super(view3D);

		GeoQuadric3D q = new GeoQuadric3D(view3D.getKernel().getConstruction());
		setGeoElement(q);
		q.setIsPickable(false);
		q.setType(type);
		// setGeoElement(q);

		setPickingType(PickingType.SURFACE);

		this.selectedPoints = selectedPoints;

		updatePreview();

	}

	@Override
	public void updateMousePos(double xRW, double yRW) {
		// not needed
	}

	@Override
	public void updatePreview() {

		GeoPointND firstPoint = null;
		GeoPointND secondPoint = null;
		if (selectedPoints.size() >= 1) {
			firstPoint = selectedPoints.get(0);
			if (selectedPoints.size() == 2) {
				secondPoint = selectedPoints.get(1);
			} else {
				secondPoint = getView3D().getCursor3D();
			}
		}

		if (selectedPoints.size() >= 1) {
			((GeoQuadric3D) getGeoElement()).setSphereND(firstPoint,
					secondPoint);
			getGeoElement().setEuclidianVisible(true);
			setWaitForUpdate();
		} else {
			getGeoElement().setEuclidianVisible(false);
		}

	}

	@Override
	public boolean hit(Hitting hitting) {
		if (waitForReset) { // prevent NPE
			return false;
		}

		GeoQuadric3D quadric = (GeoQuadric3D) getGeoElement();

		quadric.resetLastHitParameters();

		if (quadric.getType() == GeoQuadricNDConstants.QUADRIC_NOT_CLASSIFIED) {
			return false;
		}

		if (quadric.getType() == GeoQuadricNDConstants.QUADRIC_SINGLE_POINT) {
			if (DrawPoint3D.hit(hitting, quadric.getMidpoint3D(), this,
					quadric.getLineThickness(), project, parameters, false)) {
				setPickingType(PickingType.POINT_OR_CURVE);
				return true;
			}
			return false;
		}

		if (quadric.getType() == GeoQuadricNDConstants.QUADRIC_LINE) {
			initDrawLine(quadric);
			if (drawLine.hit(hitting)) {
				setZPick(drawLine.getZPickNear(), drawLine.getZPickFar(),
						hitting.discardPositiveHits(),
						drawLine.getPositionOnHitting());
				setPickingType(PickingType.POINT_OR_CURVE);
				return true;
			}
			return false;
		}

		if (getGeoElement()
				.getAlphaValue() < EuclidianController.MIN_VISIBLE_ALPHA_VALUE) {
			return false;
		}

		if (quadric.getType() == GeoQuadricNDConstants.QUADRIC_PARALLEL_PLANES
				|| quadric
						.getType() == GeoQuadricNDConstants.QUADRIC_INTERSECTING_PLANES) {
			double z1 = Double.NEGATIVE_INFINITY, z2 = Double.NEGATIVE_INFINITY;
			if (drawPlanes[0].hit(hitting, p1, project)) {
				z1 = drawPlanes[0].getZPickNear();
			}
			if (drawPlanes[1].hit(hitting, p2, project)) {
				z2 = drawPlanes[1].getZPickNear();
			}

			int planeIndex = 0;

			// keep highest value (closest to eye)
			if (z1 < z2) {
				z1 = z2;
				planeIndex = 1;
			}

			// if both negative infinity : not hitted
			if (Double.isInfinite(z1)) {
				quadric.resetLastHitParameters();
				return false;
			}

			// project with ortho matrix to get correct parameters
			hitting.origin.projectPlaneThruVIfPossible(
					quadric.getPlanes()[planeIndex].getCoordSys()
							.getMatrixOrthonormal(),
					hitting.direction, p1, project);

			parameters1[0] = PathNormalizer.inverseInfFunction(project.getX())
					+ 2 * planeIndex;
			parameters1[1] = project.getY();
			quadric.setLastHitParameters(parameters1);

			// hitted
			setZPick(z1, z1, hitting.discardPositiveHits(),
					drawPlanes[planeIndex].getPositionOnHitting());
			setPickingType(PickingType.SURFACE);
			return true;

		}

		if (quadric.getType() == GeoQuadricNDConstants.QUADRIC_PLANE) {
			if (drawPlanes[0].hit(hitting)) {
				setZPick(drawPlanes[0].getZPickNear(),
						drawPlanes[0].getZPickFar(),
						hitting.discardPositiveHits(),
						drawPlanes[0].getPositionOnHitting());
				setPickingType(PickingType.SURFACE);
				return true;
			}
			return false;
		}

		quadric.getProjections(hitting.origin, hitting.direction, p1,
				parameters1, p2, parameters2);

		double z1 = Double.NEGATIVE_INFINITY, z2 = Double.NEGATIVE_INFINITY;

		// check first point
		if (hitting.isInsideClipping(p1)
				&& arePossibleParameters(parameters1[0], parameters1[1])) {
			// check distance to hitting line
			p1.projectLine(hitting.origin, hitting.direction, project,
					parameters);

			double d = getView3D().getScaledDistance(p1, project);
			if (d <= hitting.getThreshold()) {
				z1 = -parameters[0];
			}
		}

		// check second point (if defined)
		if (p2.isDefined() && hitting.isInsideClipping(p2)
				&& arePossibleParameters(parameters2[0], parameters2[1])) {
			// check distance to hitting line
			p2.projectLine(hitting.origin, hitting.direction, project,
					parameters);

			double d = getView3D().getScaledDistance(p2, project);
			if (d <= hitting.getThreshold()) {
				z2 = -parameters[0];
			}
		}

		// keep highest value (closest to eye)
		if (z1 < z2 || (hitting.discardPositiveHits() && z1 > 0)) {
			z1 = z2;
			quadric.setLastHitParameters(parameters2);
		} else {
			quadric.setLastHitParameters(parameters1);
		}

		// if both negative infinity : not hitted
		if (Double.isInfinite(z1)) {
			quadric.resetLastHitParameters();
			return false;
		}

		// hitted
		setZPick(z1, z1, hitting.discardPositiveHits(), -z1);
		setPickingType(PickingType.SURFACE);
		return true;

	}

	private boolean arePossibleParameters(double u, double v) {

		if (getGeoElement() instanceof GeoQuadric3DPart) {
			return true; // no limitation in parameters
		}

		switch (((GeoQuadric3D) getGeoElement()).getType()) {
		case GeoQuadricNDConstants.QUADRIC_HYPERBOLIC_PARABOLOID:
			return isPossibleU(u) && isPossibleV(v);
		case GeoQuadricNDConstants.QUADRIC_PARABOLIC_CYLINDER:
			return isPossibleU(u) && isPossibleV(Math.abs(v));
		case GeoQuadricNDConstants.QUADRIC_HYPERBOLIC_CYLINDER:
			double u0;
			if (u > 1) {
				u0 = Math.abs(PathNormalizer.infFunction(u - 2));
			} else {
				u0 = -Math.abs(PathNormalizer.infFunction(u));
			}
			return isPossibleU(u0) && isPossibleV(v);
		case GeoQuadricNDConstants.QUADRIC_HYPERBOLOID_ONE_SHEET:
		case GeoQuadricNDConstants.QUADRIC_HYPERBOLOID_TWO_SHEETS:
		case GeoQuadricNDConstants.QUADRIC_PARABOLOID:
		case GeoQuadricNDConstants.QUADRIC_CONE:
		case GeoQuadricNDConstants.QUADRIC_CYLINDER:
			return isPossibleV(v);
		default:
			return true;
		}
	}

	private boolean isPossibleU(double u) {
        return isPossible(u, uMinMax);
	}

	private boolean isPossibleV(double v) {
        return isPossible(v, vMinMax);
	}

    static private boolean isPossible(double value, double[] minmax) {
        if (minmax == null) {
            return false;
        }
        if (value < minmax[0]) {
            return false;
        }
        if (value > minmax[1]) {
            return false;
        }
        return true;
    }

	@Override
	public Drawable3D drawForPicking(Renderer renderer, boolean intersection,
			PickingType type) {

		switch (type) {
		case POINT_OR_CURVE:
			int quadricType = ((GeoQuadric3D) getGeoElement()).getType();
			if (quadricType == GeoQuadricNDConstants.QUADRIC_SINGLE_POINT
					|| quadricType == GeoQuadricNDConstants.QUADRIC_LINE) {
				return super.drawForPicking(renderer, intersection, type);
			}
			return null;
		case SURFACE:
			quadricType = ((GeoQuadric3D) getGeoElement()).getType();
			if (quadricType == GeoQuadricNDConstants.QUADRIC_SINGLE_POINT
					|| quadricType == GeoQuadricNDConstants.QUADRIC_LINE) {
				return null;
			}
			return super.drawForPicking(renderer, intersection, type);
		case LABEL:
			return super.drawForPicking(renderer, intersection, type);
		default:
			return null;
		}

	}

	@Override
	public void exportToPrinter3D(ExportToPrinter3D exportToPrinter3D, boolean exportSurface) {
		if (isVisible()) {
			GeoQuadric3D quadric = (GeoQuadric3D) getGeoElement();
			switch (quadric.getType()) {
			case GeoQuadricNDConstants.QUADRIC_PARALLEL_PLANES:
			case GeoQuadricNDConstants.QUADRIC_INTERSECTING_PLANES:
				drawPlanes[0].exportToPrinter3D(exportToPrinter3D,
						exportSurface);
                if (drawPlanes[1] != null) {
                    drawPlanes[1].exportToPrinter3D(exportToPrinter3D,
                            exportSurface);
                }
				break;
			case GeoQuadricNDConstants.QUADRIC_PLANE:
				drawPlanes[0].exportToPrinter3D(exportToPrinter3D,
						exportSurface);
				break;
			case GeoQuadricNDConstants.QUADRIC_LINE:
				drawLine.exportToPrinter3D(exportToPrinter3D, exportSurface);
				break;
			default:
				if (exportSurface) {
					exportToPrinter3D.exportSurface(this, false,
							this instanceof DrawQuadric3DPart);
				}
				break;
			}
		}
	}

	@Override
	protected void updateForViewVisible() {
		super.updateForViewVisible();
		GeoQuadric3D quadric = (GeoQuadric3D) getGeoElement();
		switch (quadric.getType()) {
		default:
			// do nothing
			break;
		case GeoQuadricNDConstants.QUADRIC_PARALLEL_PLANES:
		case GeoQuadricNDConstants.QUADRIC_INTERSECTING_PLANES:
			initDrawPlanes(quadric);
			drawPlanes[0].updateForViewVisible();
			drawPlanes[1].updateForViewVisible();
			break;

		case GeoQuadricNDConstants.QUADRIC_PLANE:
			initDrawPlanes(quadric);
			drawPlanes[0].updateForViewVisible();
			break;

		case GeoQuadricNDConstants.QUADRIC_LINE:
			initDrawLine(quadric);
			drawLine.updateForViewVisible();
			break;
		}
	}

	@Override
    public void disposePreview() {
        if (drawPlanes != null) {
            drawPlanes[0].removePreviewFromGL();
            if (drawPlanes[1] != null) {
                drawPlanes[1].removePreviewFromGL();
            }
        }
        if (drawLine != null) {
            drawLine.removePreviewFromGL();
        }
        super.disposePreview();
	}

	@Override
	public void setWaitForUpdateVisualStyle(GProperty prop) {

		GeoQuadric3D quadric = (GeoQuadric3D) getGeoElement();
		switch (quadric.getType()) {
		case GeoQuadricNDConstants.QUADRIC_SINGLE_POINT:
			super.setWaitForUpdate();
			break;
		case GeoQuadricNDConstants.QUADRIC_PARALLEL_PLANES:
		case GeoQuadricNDConstants.QUADRIC_INTERSECTING_PLANES:
			initDrawPlanes(quadric);
			drawPlanes[0].setWaitForUpdateVisualStyle(prop);
            if (drawPlanes[1] != null) {
                drawPlanes[1].setWaitForUpdateVisualStyle(prop);
            }
			super.setWaitForUpdate();
			break;

		case GeoQuadricNDConstants.QUADRIC_PLANE:
			initDrawPlanes(quadric);
			drawPlanes[0].setWaitForUpdateVisualStyle(prop);
			super.setWaitForUpdate();
			break;

		case GeoQuadricNDConstants.QUADRIC_LINE:
			initDrawLine(quadric);
			drawLine.setWaitForUpdateVisualStyle(prop);
			super.setWaitForUpdate();
			break;

		default:
			// do nothing
			break;
		}

        super.setWaitForUpdateVisualStyle(prop);
        if (prop == GProperty.COLOR || prop == GProperty.HIGHLIGHT) {
            setWaitForUpdateColor();
        } else if (prop == GProperty.VISIBLE) {
            setWaitForUpdateVisibility();
        }
    }

	@Override
	protected void updateGeometriesVisibility() {
		super.updateGeometriesVisibility();
		if (drawPlanes != null) {
			drawPlanes[0].updateGeometriesVisibility();
			if (drawPlanes[1] != null) {
                drawPlanes[1].updateGeometriesVisibility();
            }
		}
		if (drawLine != null) {
			drawLine.updateGeometriesVisibility();
		}
	}

	@Override
	public boolean addedFromClosedSurface() {
		return true;
	}

	@Override
    public void removeFromGL() {
        super.removeFromGL();
        if (drawPlanes != null) {
            drawPlanes[0].removeFromGL();
            drawPlanes[1].removeFromGL();
        }
        if (drawLine != null) {
            drawLine.removeFromGL();
        }
	}
}
