package org.geogebra.common.geogebra3D.euclidian3D.draw;

import java.util.ArrayList;

import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.Previewable;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.Hitting;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.PlotterSurface;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer.PickingType;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPlane3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3DPart;
import org.geogebra.common.kernel.PathNormalizer;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoQuadricNDConstants;

/**
 * Class for drawing quadrics.
 * 
 * @author mathieu
 *
 */
public class DrawQuadric3D extends Drawable3DSurfaces implements Previewable {

	/**
	 * common constructor
	 * 
	 * @param a_view3d
	 * @param a_quadric
	 */
	public DrawQuadric3D(EuclidianView3D a_view3d, GeoQuadric3D a_quadric) {

		super(a_view3d, a_quadric);

	}

	@Override
	public void drawGeometry(Renderer renderer) {
		switch (((GeoQuadric3D) getGeoElement()).getType()) {
		case GeoQuadricNDConstants.QUADRIC_PARALLEL_PLANES:
		case GeoQuadricNDConstants.QUADRIC_INTERSECTING_PLANES:
			drawPlanes[0].drawGeometry(renderer);
			drawPlanes[1].drawGeometry(renderer);
			break;
		case GeoQuadricNDConstants.QUADRIC_PLANE:
			drawPlanes[0].drawGeometry(renderer);
			break;

		case GeoQuadricNDConstants.QUADRIC_LINE:
			// not used: see drawOutline() and drawGeometryHidden()
			break;

		default:
			renderer.setLayer(getLayer());
			renderer.getGeometryManager().draw(getSurfaceIndex());
			renderer.setLayer(0);
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
		case GeoQuadricNDConstants.QUADRIC_LINE:
			drawLine.drawGeometryHidden(renderer);
			break;
		}
	}

	@Override
	public void drawOutline(Renderer renderer) {
		if (isVisible()) {
			switch (((GeoQuadric3D) getGeoElement()).getType()) {
			case GeoQuadricNDConstants.QUADRIC_LINE:
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

		if ((((GeoQuadric3D) getGeoElement()).getType() != GeoQuadricNDConstants.QUADRIC_LINE || hidden)
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
		if (((GeoQuadric3D) getGeoElement()).getType() == GeoQuadricNDConstants.QUADRIC_LINE) {
			drawOutline(renderer);
		} else {
			drawGeometry(renderer);
		}
	}

	@Override
	protected void updateColors() {
		super.updateColors();

		GeoQuadric3D quadric = (GeoQuadric3D) getGeoElement();
		switch (quadric.getType()) {
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

	protected int longitude = 0;

	private double scale;

	private double alpha, beta;

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

	private Visible visible = Visible.TOTALLY_OUTSIDE;

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
			double horizontalDistance = Math.sqrt(v.getX() * v.getX()
					+ v.getY() * v.getY());
			if (horizontalDistance > frustumRadius) {
				alpha = Math.asin(frustumRadius / horizontalDistance);
				beta = Math.atan2(v.getY(), v.getX());
				// App.debug("alpha = "+(alpha*180/Math.PI)+"degrees, beta = "+(beta*180/Math.PI)+"degrees");
				visible = Visible.CENTER_OUTSIDE; // center outside
			} else {
				visible = Visible.CENTER_INSIDE; // do as if center inside
			}
		}

	}

	private void drawSphere(PlotterSurface surface, Coords center, double radius) {
		if (visible == Visible.CENTER_OUTSIDE) {
			int longitudeAlpha = 8;
			while (longitudeAlpha * Math.PI < alpha * longitude) {
				longitudeAlpha *= 2;
			}
			// App.debug(longitudeAlpha+"");
			surface.drawSphere(center, radius, longitude, beta - longitudeAlpha
					* Math.PI / longitude, longitudeAlpha);
		} else {
			surface.drawSphere(center, radius, longitude);
		}
	}
	

	
	private Coords boundsMin = new Coords(3), boundsMax = new Coords(3);

	
	@Override
	public void enlargeBounds(Coords min, Coords max) {
		switch (((GeoQuadric3D) getGeoElement()).getType()) {
		case GeoQuadricNDConstants.QUADRIC_SPHERE:
		case GeoQuadricNDConstants.QUADRIC_SINGLE_POINT:
			enlargeBounds(min, max, boundsMin, boundsMax);
			break;
		case GeoQuadricNDConstants.QUADRIC_CONE:
		case GeoQuadricNDConstants.QUADRIC_CYLINDER:
			if (getGeoElement() instanceof GeoQuadric3DPart) {
				enlargeBounds(min, max, boundsMin, boundsMax);
			}
			break;
		}
	}

	private double[] uMinMax, vMinMax;


	@Override
	protected boolean updateForItSelf() {
		
		Renderer renderer = getView3D().getRenderer();
		GeoQuadric3D quadric = (GeoQuadric3D) getGeoElement();
		PlotterSurface surface;
		int type = quadric.getType();
		
		double min, max;

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
				surface = renderer.getGeometryManager().getSurface();
				surface.start(getReusableSurfaceIndex());
				scale = getView3D().getScale();
				longitude = surface.calcSphereLongitudesNeeded(radius, scale);
				drawSphere(surface, center, radius);
				setSurfaceIndex(surface.end());
			} else {
				setSurfaceIndex(-1);
			}
			break;
		case GeoQuadricNDConstants.QUADRIC_ELLIPSOID:
			center = quadric.getMidpoint3D();
			double r0 = quadric.getHalfAxis(0);
			double r1 = quadric.getHalfAxis(1);
			double r2 = quadric.getHalfAxis(2);
			surface = renderer.getGeometryManager().getSurface();
			surface.start(getReusableSurfaceIndex());
			scale = getView3D().getScale();
			radius = Math.max(r0, Math.max(r1, r2));
			longitude = surface.calcSphereLongitudesNeeded(radius, scale);
			Coords ev0 = quadric.getEigenvec3D(0);
			Coords ev1 = quadric.getEigenvec3D(1);
			Coords ev2 = quadric.getEigenvec3D(2);
			surface.drawEllipsoid(center, ev0, ev1, ev2, r0, r1, r2, longitude);
			setSurfaceIndex(surface.end());
			break;

		case GeoQuadricNDConstants.QUADRIC_HYPERBOLOID_ONE_SHEET:
			center = quadric.getMidpoint3D();
			r0 = quadric.getHalfAxis(0);
			r1 = quadric.getHalfAxis(1);
			r2 = quadric.getHalfAxis(2);
			surface = renderer.getGeometryManager().getSurface();
			surface.start(getReusableSurfaceIndex());
			ev0 = quadric.getEigenvec3D(0);
			ev1 = quadric.getEigenvec3D(1);
			ev2 = quadric.getEigenvec3D(2);
			if (vMinMax == null) {
				vMinMax = new double[2];
			}
			vMinMax[0] = Double.POSITIVE_INFINITY;
			vMinMax[1] = Double.NEGATIVE_INFINITY;
			getView3D().getMinIntervalOutsideClipping(vMinMax, center,
					ev2.mul(r2));
			scale = getView3D().getScale();
			// get radius at max
			radius = Math.max(r0, r1)
					* Math.max(Math.abs(vMinMax[0]),
							Math.max(Math.abs(vMinMax[1]), 1)) / r2;
			longitude = surface.calcSphereLongitudesNeeded(radius, scale);
			min = DrawConic3D.asinh(vMinMax[0]);
			max = DrawConic3D.asinh(vMinMax[1]);
			surface.drawHyperboloidOneSheet(center, ev0, ev1, ev2, r0, r1, r2,
					longitude, min, max, !getView3D()
							.useClippingCube());
			setSurfaceIndex(surface.end());
			break;

		case GeoQuadricNDConstants.QUADRIC_HYPERBOLOID_TWO_SHEETS:
			center = quadric.getMidpoint3D();
			r0 = quadric.getHalfAxis(0);
			r1 = quadric.getHalfAxis(1);
			r2 = quadric.getHalfAxis(2);
			surface = renderer.getGeometryManager().getSurface();
			surface.start(getReusableSurfaceIndex());
			ev0 = quadric.getEigenvec3D(0);
			ev1 = quadric.getEigenvec3D(1);
			ev2 = quadric.getEigenvec3D(2);
			if (vMinMax == null) {
				vMinMax = new double[2];
			}
			vMinMax[0] = Double.POSITIVE_INFINITY;
			vMinMax[1] = Double.NEGATIVE_INFINITY;
			getView3D().getMinIntervalOutsideClipping(vMinMax, center,
					ev2.mul(r2));
			scale = getView3D().getScale();
			// get radius at max
			radius = Math.max(r0, r1)
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
			surface.drawHyperboloidTwoSheets(center, ev0, ev1, ev2, r0, r1, r2,
					longitude, min, max, !getView3D().useClippingCube());
			setSurfaceIndex(surface.end());
			break;

		case GeoQuadricNDConstants.QUADRIC_PARABOLOID:
			center = quadric.getMidpoint3D();
			r0 = quadric.getHalfAxis(0);
			r1 = quadric.getHalfAxis(1);
			surface = renderer.getGeometryManager().getSurface();
			surface.start(getReusableSurfaceIndex());
			ev0 = quadric.getEigenvec3D(0);
			ev1 = quadric.getEigenvec3D(1);
			ev2 = quadric.getEigenvec3D(2);
			if (vMinMax == null) {
				vMinMax = new double[2];
			}
			vMinMax[0] = Double.POSITIVE_INFINITY;
			vMinMax[1] = Double.NEGATIVE_INFINITY;
			getView3D().getMinIntervalOutsideClipping(vMinMax, center, ev2);
			if (vMinMax[1] < 0) {
				// nothing to draw
				setSurfaceIndex(surface.end());
			} else {
				scale = getView3D().getScale();
				// get radius at max
				if (vMinMax[0] <= 0) {
					vMinMax[0] = 0;
				} else {
					vMinMax[0] = Math.sqrt(vMinMax[0]);
				}
				vMinMax[1] = Math.sqrt(vMinMax[1]);
				radius = Math.max(r0, r1) * vMinMax[1];
				longitude = surface.calcSphereLongitudesNeeded(radius, scale);
				surface.drawParaboloid(center, ev0, ev1, ev2, r0, r1,
						longitude, vMinMax[0], vMinMax[1], !getView3D()
								.useClippingCube());
				setSurfaceIndex(surface.end());
			}
			break;

		case GeoQuadricNDConstants.QUADRIC_HYPERBOLIC_PARABOLOID:
			center = quadric.getMidpoint3D();
			r0 = quadric.getHalfAxis(0);
			r1 = quadric.getHalfAxis(1);
			surface = renderer.getGeometryManager().getSurface();
			surface.start(getReusableSurfaceIndex());
			ev0 = quadric.getEigenvec3D(0);
			ev1 = quadric.getEigenvec3D(1);
			ev2 = quadric.getEigenvec3D(2);
			if (uMinMax == null) {
				uMinMax = new double[2];
			}
			uMinMax[0] = Double.POSITIVE_INFINITY;
			uMinMax[1] = Double.NEGATIVE_INFINITY;
			getView3D().getMinIntervalOutsideClipping(uMinMax, center, ev0);
			if (vMinMax == null) {
				vMinMax = new double[2];
			}
			vMinMax[0] = Double.POSITIVE_INFINITY;
			vMinMax[1] = Double.NEGATIVE_INFINITY;
			getView3D().getMinIntervalOutsideClipping(vMinMax, center, ev1);
			surface.drawHyperbolicParaboloid(center, ev0, ev1, ev2, r0, r1,
					uMinMax[0], uMinMax[1], vMinMax[0], vMinMax[1],
					!getView3D().useClippingCube());
			setSurfaceIndex(surface.end());
			break;

		case GeoQuadricNDConstants.QUADRIC_PARABOLIC_CYLINDER:
			center = quadric.getMidpoint3D();
			r2 = quadric.getHalfAxis(2);
			surface = renderer.getGeometryManager().getSurface();
			surface.start(getReusableSurfaceIndex());
			ev0 = quadric.getEigenvec3D(0);
			ev1 = quadric.getEigenvec3D(1);
			ev2 = quadric.getEigenvec3D(2);
			if (vMinMax == null) {
				vMinMax = new double[2];
			}
			vMinMax[0] = Double.POSITIVE_INFINITY;
			vMinMax[1] = Double.NEGATIVE_INFINITY;
			getView3D().getMinIntervalOutsideClipping(vMinMax, center, ev0);
			if (vMinMax[1] < 0) {
				// nothing to draw
				setSurfaceIndex(surface.end());
			} else {
				scale = getView3D().getScale();
				// get radius at max
				if (vMinMax[0] <= 0) {
					vMinMax[0] = 0;
				} else {
					vMinMax[0] = Math.sqrt(vMinMax[0]);
				}
				vMinMax[1] = Math.sqrt(vMinMax[1]);
				if (uMinMax == null) {
					uMinMax = new double[2];
				}
				uMinMax[0] = Double.POSITIVE_INFINITY;
				uMinMax[1] = Double.NEGATIVE_INFINITY;
				getView3D().getMinIntervalOutsideClipping(uMinMax, center, ev1);
				surface.drawParabolicCylinder(center, ev0, ev1, ev2, r2,
						vMinMax[0], vMinMax[1], uMinMax[0], uMinMax[1],
						!getView3D()
								.useClippingCube());
				setSurfaceIndex(surface.end());
			}
			break;

		case GeoQuadricNDConstants.QUADRIC_HYPERBOLIC_CYLINDER:
			center = quadric.getMidpoint3D();
			r0 = quadric.getHalfAxis(0);
			r1 = quadric.getHalfAxis(1);
			surface = renderer.getGeometryManager().getSurface();
			surface.start(getReusableSurfaceIndex());
			ev0 = quadric.getEigenvec3D(0);
			ev1 = quadric.getEigenvec3D(1);
			ev2 = quadric.getEigenvec3D(2);
			if (uMinMax == null) {
				uMinMax = new double[2];
			}
			uMinMax[0] = Double.POSITIVE_INFINITY;
			uMinMax[1] = Double.NEGATIVE_INFINITY;
			getView3D().getMinIntervalOutsideClipping(uMinMax, center,
					ev0.mul(r0));
			scale = getView3D().getScale();
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
			if (vMinMax == null) {
				vMinMax = new double[2];
			}
			vMinMax[0] = Double.POSITIVE_INFINITY;
			vMinMax[1] = Double.NEGATIVE_INFINITY;
			getView3D().getMinIntervalOutsideClipping(vMinMax, center, ev2);
			if (min < 0) {
				surface.drawHyperbolicCylinder(center, ev0.mul(-1),
						ev1.mul(-1), ev2, r0, r1, -max, -min, vMinMax[0],
						vMinMax[1], !getView3D()
								.useClippingCube());
			}
			if (max > 0) {
				surface.drawHyperbolicCylinder(center, ev0, ev1, ev2, r0, r1,
						min, max, vMinMax[0], vMinMax[1], !getView3D()
								.useClippingCube());
			}
			uMinMax[0] = Math.sinh(min);
			uMinMax[1] = Math.sinh(max);
			setSurfaceIndex(surface.end());

			break;

		case GeoQuadricNDConstants.QUADRIC_CONE:
			surface = renderer.getGeometryManager().getSurface();
			surface.start(getReusableSurfaceIndex());
			if (quadric instanceof GeoQuadric3DPart) { // simple cone
				double height = ((GeoQuadric3DPart) quadric)
						.getBottomParameter()
						- ((GeoQuadric3DPart) quadric).getTopParameter();
				Coords top = quadric.getMidpoint3D();
				ev1 = quadric.getEigenvec3D(0);
				ev2 = quadric.getEigenvec3D(1);
				radius = quadric.getHalfAxis(0);
				Coords bottomCenter = surface.cone(top, ev1, ev2, quadric.getEigenvec3D(2),
						radius, 0, 2 * Math.PI, height, 1f);
				
				boundsMin.setValues(top, 3);
				boundsMax.setValues(top, 3);
				radius *= height;
				enlargeBoundsToDiagonal(boundsMin, boundsMax, bottomCenter, ev1, ev2, radius, radius);
				
			} else { // infinite cone
				if (vMinMax == null) {
					vMinMax = new double[2];
				}
				vMinMax[0] = Double.POSITIVE_INFINITY;
				vMinMax[1] = Double.NEGATIVE_INFINITY;
				getMinMax(vMinMax);
				min = vMinMax[0];
				max = vMinMax[1];
				// min -= delta;
				// max += delta;
				// App.debug(min+","+max);
				center = quadric.getMidpoint3D();
				ev1 = quadric.getEigenvec3D(0);
				ev2 = quadric.getEigenvec3D(1);
				Coords ev3 = quadric.getEigenvec3D(2);
				r1 = quadric.getHalfAxis(0);
				r2 = quadric.getHalfAxis(1);

				if (min * max < 0) {
					if (getView3D().useClippingCube()) {
						surface.cone(center, ev1, ev2, ev3, r1, r2, 0,
								2 * Math.PI, min, 1f);
						surface.cone(center, ev1, ev2, ev3, r1, r2, 0,
								2 * Math.PI, max, 1f);
					} else {
						surface.cone(center, ev1, ev2, ev3, r1, r2, 0,
								2 * Math.PI, min,
								(float) ((-9 * min - max) / (min - max)));
						surface.cone(center, ev1, ev2, ev3, r1, r2, 0,
								2 * Math.PI, max,
								(float) ((-9 * max - min) / (max - min)));
					}
				} else {
					if (getView3D().useClippingCube()) {
						surface.cone(center, ev1, ev2, ev3, r1, r2, 0,
								2 * Math.PI, min, max, false, false);
					} else {
						double delta = (max - min) / 10;
						surface.cone(center, ev1, ev2, ev3, r1, r2, 0,
								2 * Math.PI, min + delta, max - delta, false,
								false);
						surface.cone(center, ev1, ev2, ev3, r1, r2, 0,
								2 * Math.PI, min, min + delta, true, false);
						surface.cone(center, ev1, ev2, ev3, r1, r2, 0,
								2 * Math.PI, max - delta, max, false, true);
					}
				}
			}

			setSurfaceIndex(surface.end());
			break;

		case GeoQuadricNDConstants.QUADRIC_CYLINDER:

			center = quadric.getMidpoint3D();
			ev1 = quadric.getEigenvec3D(0);
			ev2 = quadric.getEigenvec3D(1);
			Coords ev3 = quadric.getEigenvec3D(2);

			surface = renderer.getGeometryManager().getSurface();
			surface.start(getReusableSurfaceIndex());

			if (quadric instanceof GeoQuadric3DPart) { // simple cylinder
				radius = quadric.getHalfAxis(0);
				longitude = renderer.getGeometryManager().getLongitude(radius, getView3D().getScale());
				Coords bottomCenter = surface.cylinder(center, ev1, ev2, ev3, radius, 0, 2 * Math.PI,
						quadric.getMinParameter(1), quadric.getMaxParameter(1),
						false, false, longitude);				
				
				boundsMin.set(Double.POSITIVE_INFINITY);
				boundsMax.set(Double.NEGATIVE_INFINITY);
				enlargeBoundsToDiagonal(boundsMin, boundsMax, center, ev1, ev2, radius, radius);
				enlargeBoundsToDiagonal(boundsMin, boundsMax, bottomCenter, ev1, ev2, radius, radius);
				
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

				longitude = renderer.getGeometryManager().getLongitude(radius, getView3D().getScale());
				if (getView3D().useClippingCube()) {
					surface.cylinder(center, ev1, ev2, ev3, r1, r2, 0,
							2 * Math.PI, min, max, false, false, longitude);
				} else {
					double delta = (max - min) / 10;
					surface.cylinder(center, ev1, ev2, ev3, r1, r2, 0,
							2 * Math.PI, min + delta, max - delta, false, false, longitude);
					surface.cylinder(center, ev1, ev2, ev3, r1, r2, 0,
							2 * Math.PI, min, min + delta, true, false, longitude);
					surface.cylinder(center, ev1, ev2, ev3, r1, r2, 0,
							2 * Math.PI, max - delta, max, false, true, longitude);
				}

			}

			setSurfaceIndex(surface.end());

			break;

		case GeoQuadricNDConstants.QUADRIC_SINGLE_POINT:
			surface = renderer.getGeometryManager().getSurface();
			surface.start(getReusableSurfaceIndex());
			Coords m = quadric.getMidpoint3D();
			double thickness = quadric.getLineThickness()
					/ getView3D().getScale()
					* DrawPoint3D.DRAW_POINT_FACTOR;
			surface.drawSphere(quadric.getLineThickness(),
					m, thickness);
			setSurfaceIndex(surface.end());
			
			boundsMin.setValues(m, 3);
			boundsMax.setValues(m, 3);
			boundsMin.addInside(-thickness);
			boundsMax.addInside(thickness);
			break;

		case GeoQuadricNDConstants.QUADRIC_PARALLEL_PLANES:
		case GeoQuadricNDConstants.QUADRIC_INTERSECTING_PLANES:
			initDrawPlanes(quadric);
			drawPlanes[0].updateForItSelf();
			drawPlanes[1].updateForItSelf();
			break;

		case GeoQuadricNDConstants.QUADRIC_PLANE:
			initDrawPlanes(quadric);
			drawPlanes[0].updateForItSelf();
			break;

		case GeoQuadricNDConstants.QUADRIC_LINE:
			initDrawLine(quadric);
			drawLine.updateForItSelf();
			break;

		default:
			setSurfaceIndex(-1);
		}


		return true;
	}

	private DrawPlane3D[] drawPlanes;

	private void initDrawPlanes(GeoQuadric3D quadric) {
		if (drawPlanes == null) {
			drawPlanes = new DrawPlane3DForQuadrics[2];
			GeoPlane3D[] planes = quadric.getPlanes();
			drawPlanes[0] = new DrawPlane3DForQuadrics(getView3D(), planes[0],
					quadric);
			drawPlanes[1] = new DrawPlane3DForQuadrics(getView3D(), planes[1],
					quadric);
		}
	}

	private DrawLine3D drawLine;

	private void initDrawLine(GeoQuadric3D quadric) {
		if (drawLine == null){
			drawLine = new DrawLine3DForQuadrics(getView3D(),
					quadric.getLine(), quadric);
		}
	}

	/**
	 * 
	 * @return min and max value along the axis of the quadric
	 */
	protected double[] getMinMax() {

		GeoQuadric3D quadric = (GeoQuadric3D) getGeoElement();

		double[] minmax = { Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY };

		getView3D().getMinIntervalOutsideClipping(minmax,
				quadric.getMidpoint3D(), quadric.getEigenvec3D(2));

		// App.debug(minmax[0]+","+minmax[1]);

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

	protected void setSurfaceV(float min, float max, PlotterSurface surface) {
		float fade = (max - min) / 10f;

		switch (((GeoQuadric3D) getGeoElement()).getType()) {
		case GeoQuadricNDConstants.QUADRIC_CYLINDER:
			surface.setV(min, max);
			surface.setNbV(3);
			surface.setVFading(fade, fade);
			break;

		case GeoQuadricNDConstants.QUADRIC_CONE:
			if (min * max < 0) {
				surface.setV(min, 0);
				surface.setNbV(2);
				surface.setVFading(fade, 0);
				surface.draw();
				surface.setV(0, max);
				surface.setNbV(2);
				surface.setVFading(0, fade);
				surface.draw();
			} else {
				surface.setV(min, max);
				surface.setNbV(3);
				surface.setVFading(fade, fade);
				surface.draw();
			}
			break;
		}

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

				double s = scale;
				scale = getView3D().getScale();
				// check if longitude length changes
				double radius = quadric.getHalfAxis(0);
				int l = surface.calcSphereLongitudesNeeded(radius, scale);
				// redraw if sphere was not visible, or if new longitude length,
				// or if negative zoom occured
				if (visible == Visible.TOTALLY_OUTSIDE || l != longitude
						|| scale < s) {
					Coords center = quadric.getMidpoint3D();
					checkSphereVisible(center, radius);
					if (visible != Visible.TOTALLY_OUTSIDE) {
						// App.debug(l+","+longitude);
						longitude = l;
						surface.start(getReusableSurfaceIndex());
						drawSphere(surface, center, radius);
						setSurfaceIndex(surface.end());
						recordTrace();
					} else {
						setSurfaceIndex(-1);
					}
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
					surface.start(getReusableSurfaceIndex());
					drawSphere(surface, center, radius);
					setSurfaceIndex(surface.end());
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

		}

	}

	@Override
	public void setWaitForUpdateVisualStyle() {

		GeoQuadric3D quadric = (GeoQuadric3D) getGeoElement();
		switch (quadric.getType()) {
		case GeoQuadricNDConstants.QUADRIC_SINGLE_POINT:
			super.setWaitForUpdate();
			break;
		case GeoQuadricNDConstants.QUADRIC_PARALLEL_PLANES:
		case GeoQuadricNDConstants.QUADRIC_INTERSECTING_PLANES:
			initDrawPlanes(quadric);
			drawPlanes[0].setWaitForUpdateVisualStyle();
			drawPlanes[1].setWaitForUpdateVisualStyle();
			super.setWaitForUpdate();
			break;

		case GeoQuadricNDConstants.QUADRIC_PLANE:
			initDrawPlanes(quadric);
			drawPlanes[0].setWaitForUpdateVisualStyle();
			super.setWaitForUpdate();
			break;

		case GeoQuadricNDConstants.QUADRIC_LINE:
			initDrawLine(quadric);
			drawLine.setWaitForUpdateVisualStyle();
			super.setWaitForUpdate();
			break;
		}

		super.setWaitForUpdateVisualStyle();

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
	protected void clearTraceForViewChanged() {

		if (drawPlanes != null) {
			drawPlanes[0].clearTraceForViewChanged();
			if (drawPlanes[1] != null) {
				drawPlanes[1].clearTraceForViewChanged();
			}
		}

		if (drawLine != null) {
			drawLine.clearTraceForViewChanged();
		}

		super.clearTraceForViewChanged();
	}

	@Override
	public void setWaitForUpdate() {

		super.setWaitForUpdate();
		GeoQuadric3D quadric = (GeoQuadric3D) getGeoElement();
		int type = quadric.getType();

		switch (type) {
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
			return getAlpha() <= EuclidianController.MAX_TRANSPARENT_ALPHA_VALUE;
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
			break;
		default:
			addToDrawable3DLists(lists, DRAW_TYPE_SURFACES);
			break;
		}
		addToDrawable3DLists(lists, DRAW_TYPE_CURVES);
	}

	@Override
	public void removeFromDrawable3DLists(Drawable3DLists lists) {
		switch (((GeoQuadric3D) getGeoElement()).getType()) {
		case GeoQuadricNDConstants.QUADRIC_SPHERE:
		case GeoQuadricNDConstants.QUADRIC_ELLIPSOID:
		case GeoQuadricNDConstants.QUADRIC_CONE:
		case GeoQuadricNDConstants.QUADRIC_CYLINDER:
		case GeoQuadricNDConstants.QUADRIC_HYPERBOLOID_ONE_SHEET:
			removeFromDrawable3DLists(lists, DRAW_TYPE_CLOSED_SURFACES_CURVED);
			break;
		default:
			removeFromDrawable3DLists(lists, DRAW_TYPE_SURFACES);
			break;
		}
		removeFromDrawable3DLists(lists, DRAW_TYPE_CURVES);
	}

	// //////////////////////////////
	// Previewable interface

	@SuppressWarnings("rawtypes")
	private ArrayList selectedPoints;

	/**
	 * constructor for previewable
	 * 
	 * @param view3D
	 * @param selectedPoints
	 * @param cs1D
	 */
	@SuppressWarnings("unchecked")
	public DrawQuadric3D(EuclidianView3D view3D, ArrayList selectedPoints,
			int type) {

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

	public void updateMousePos(double xRW, double yRW) {

	}

	public void updatePreview() {

		GeoPointND firstPoint = null;
		GeoPointND secondPoint = null;
		if (selectedPoints.size() >= 1) {
			firstPoint = (GeoPointND) selectedPoints.get(0);
			if (selectedPoints.size() == 2)
				secondPoint = (GeoPointND) selectedPoints.get(1);
			else
				secondPoint = getView3D().getCursor3D();
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
		
		quadric.resetLastHittedParameters();

		if (quadric.getType() == GeoQuadricNDConstants.QUADRIC_NOT_CLASSIFIED
				|| quadric.getType() == GeoQuadricNDConstants.QUADRIC_NOT_SET) {
			return false;
		}

		if (quadric.getType() == GeoQuadricNDConstants.QUADRIC_SINGLE_POINT) {
			if (DrawPoint3D.hit(hitting, quadric.getMidpoint3D(), this, quadric.getLineThickness(), project,
					parameters, false)){
				setPickingType(PickingType.POINT_OR_CURVE);
				return true;
			}
			return false;
		}	
		

		if (quadric.getType() == GeoQuadricNDConstants.QUADRIC_LINE) {
			if (drawLine.hit(hitting)) {
				setZPick(drawLine.getZPickNear(), drawLine.getZPickFar());
				setPickingType(PickingType.POINT_OR_CURVE);
				return true;
			}
			return false;
		}

		if (getGeoElement().getAlphaValue() < EuclidianController.MIN_VISIBLE_ALPHA_VALUE) {
			return false;
		}
		
		if (quadric.getType() == GeoQuadricNDConstants.QUADRIC_PARALLEL_PLANES
				|| quadric.getType() == GeoQuadricNDConstants.QUADRIC_INTERSECTING_PLANES) {
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
				quadric.resetLastHittedParameters();
				return false;
			}

			// project with ortho matrix to get correct parameters
			hitting.origin.projectPlaneThruVIfPossible(
					quadric.getPlanes()[planeIndex].getCoordSys()
							.getMatrixOrthonormal(), hitting.direction, p1,
					project);

			parameters1[0] = PathNormalizer.inverseInfFunction(project.getX())
					+ 2 * planeIndex;
			parameters1[1] = project.getY();
			quadric.setLastHittedParameters(parameters1);

			// hitted
			setZPick(z1, z1);
			setPickingType(PickingType.SURFACE);
			return true;

		}

		if (quadric.getType() == GeoQuadricNDConstants.QUADRIC_PLANE) {
			if (drawPlanes[0].hit(hitting)) {
				setZPick(drawPlanes[0].getZPickNear(),
						drawPlanes[0].getZPickFar());
				setPickingType(PickingType.SURFACE);
				return true;
			}
			return false;
		}


		quadric.getProjections(null, hitting.origin, hitting.direction, p1,
				parameters1, p2, parameters2);
		
		double z1 = Double.NEGATIVE_INFINITY, z2 = Double.NEGATIVE_INFINITY;
		
		// check first point
		if (hitting.isInsideClipping(p1)
				&& arePossibleParameters(parameters1[0], parameters1[1])) {
			// check distance to hitting line
			p1.projectLine(hitting.origin, hitting.direction, project, parameters); 

			double d = p1.distance(project);
			if (d * getView3D().getScale() <= hitting.getThreshold()) {
				z1 = -parameters[0];
			}
		}
		
		// check second point (if defined)
		if (p2.isDefined() && hitting.isInsideClipping(p2)
				&& arePossibleParameters(parameters2[0], parameters2[1])) {
			// check distance to hitting line
			p2.projectLine(hitting.origin, hitting.direction, project, parameters); 

			double d = p2.distance(project);
			if (d * getView3D().getScale() <= hitting.getThreshold()) {
				z2 = -parameters[0];
			}
		}
		
		// keep highest value (closest to eye)
		if (z1 < z2){
			z1 = z2;
			quadric.setLastHittedParameters(parameters2);
		} else {
			quadric.setLastHittedParameters(parameters1);
		}
		
		// if both negative infinity : not hitted
		if (Double.isInfinite(z1)){
			quadric.resetLastHittedParameters();
			return false;
		}
		
		// hitted
		setZPick(z1, z1);
		setPickingType(PickingType.SURFACE);
		return true;


	}

	private boolean arePossibleParameters(double u, double v) {
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

		if (u < uMinMax[0]) {
			return false;
		}
		if (u > uMinMax[1]) {
			return false;
		}
		return true;
	}

	private boolean isPossibleV(double v) {

		if (v < vMinMax[0]) {
			return false;
		}
		if (v > vMinMax[1]) {
			return false;
		}
		return true;
	}

	private Coords project = Coords.createInhomCoorsInD3(), p1 = Coords.createInhomCoorsInD3(), p2 = Coords.createInhomCoorsInD3();

	private double[] parameters = new double[2];
	private double[] parameters1 = new double[2];
	private double[] parameters2 = new double[2];


	@Override
	public Drawable3D drawForPicking(Renderer renderer, boolean intersection,
			PickingType type) {

		switch (type) {
		case POINT_OR_CURVE:
			int quadricType = ((GeoQuadric3D) getGeoElement()).getType();
			if (quadricType == GeoQuadricNDConstants.QUADRIC_SINGLE_POINT
					&& quadricType == GeoQuadricNDConstants.QUADRIC_LINE) {
				return super.drawForPicking(renderer, intersection, type);
			}
			return null;
		case SURFACE:
			quadricType = ((GeoQuadric3D) getGeoElement()).getType();
			if (quadricType == GeoQuadricNDConstants.QUADRIC_SINGLE_POINT
					&& quadricType == GeoQuadricNDConstants.QUADRIC_LINE) {
				return null;
			}
			return super.drawForPicking(renderer, intersection, type);
		case LABEL:
			return super.drawForPicking(renderer, intersection, type);
		default:
			return null;
		}

	}

}
