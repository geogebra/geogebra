package geogebra.common.geogebra3D.euclidian3D.draw;

import geogebra.common.euclidian.EuclidianController;
import geogebra.common.euclidian.Previewable;
import geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import geogebra.common.geogebra3D.euclidian3D.Hitting;
import geogebra.common.geogebra3D.euclidian3D.openGL.PlotterBrush;
import geogebra.common.geogebra3D.euclidian3D.openGL.PlotterSurface;
import geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import geogebra.common.geogebra3D.euclidian3D.openGL.Renderer.PickingType;
import geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3D;
import geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3DPart;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.kernelND.GeoQuadricNDConstants;
import geogebra.common.main.App;

import java.util.ArrayList;

/**
 * Class for drawing quadrics.
 * 
 * @author matthieu
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
		renderer.setLayer(getLayer());
		renderer.getGeometryManager().draw(getSurfaceIndex());
		renderer.setLayer(0);
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
		// TODO Auto-generated method stub

	}

	@Override
	public void drawOutline(Renderer renderer) {
		// no outline
	}

	private int longitude = 0;

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
				// App.debug("alpha = "+(alpha*180/Math.PI)+"°, beta = "+(beta*180/Math.PI)+"°");
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

	@Override
	protected boolean updateForItSelf() {

		Renderer renderer = getView3D().getRenderer();
		GeoQuadric3D quadric = (GeoQuadric3D) getGeoElement();
		PlotterSurface surface;

		switch (quadric.getType()) {
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

		case GeoQuadricNDConstants.QUADRIC_CONE:
			surface = renderer.getGeometryManager().getSurface();
			surface.start(getReusableSurfaceIndex());
			if (quadric instanceof GeoQuadric3DPart) { // simple cone
				double height = ((GeoQuadric3DPart) quadric)
						.getBottomParameter()
						- ((GeoQuadric3DPart) quadric).getTopParameter();
				Coords top = quadric.getMidpoint3D();
				Coords ev1 = quadric.getEigenvec3D(0);
				Coords ev2 = quadric.getEigenvec3D(1);
				radius = quadric.getHalfAxis(0);
				Coords bottomCenter = surface.cone(top, ev1, ev2, quadric.getEigenvec3D(2),
						radius, 0, 2 * Math.PI, height, 1f);
				
				boundsMin.setValues(top, 3);
				boundsMax.setValues(top, 3);
				radius *= height;
				enlargeBoundsToDiagonal(boundsMin, boundsMax, bottomCenter, ev1, ev2, radius, radius);
				
			} else { // infinite cone
				double[] minmax = getMinMax();
				double min = minmax[0];
				double max = minmax[1];
				// min -= delta;
				// max += delta;
				// App.debug(min+","+max);
				center = quadric.getMidpoint3D();
				Coords ev1 = quadric.getEigenvec3D(0);
				Coords ev2 = quadric.getEigenvec3D(1);
				Coords ev3 = quadric.getEigenvec3D(2);
				radius = quadric.getHalfAxis(0);
				if (min * max < 0) {
					if (getView3D().useClippingCube()) {
						surface.cone(center, ev1, ev2, ev3, radius, 0,
								2 * Math.PI, min, 1f);
						surface.cone(center, ev1, ev2, ev3, radius, 0,
								2 * Math.PI, max, 1f);
					} else {
						surface.cone(center, ev1, ev2, ev3, radius, 0,
								2 * Math.PI, min,
								(float) ((-9 * min - max) / (min - max)));
						surface.cone(center, ev1, ev2, ev3, radius, 0,
								2 * Math.PI, max,
								(float) ((-9 * max - min) / (max - min)));
					}
				} else {
					if (getView3D().useClippingCube()) {
						surface.cone(center, ev1, ev2, ev3, radius, 0,
								2 * Math.PI, min, max, false, false);
					} else {
						double delta = (max - min) / 10;
						surface.cone(center, ev1, ev2, ev3, radius, 0,
								2 * Math.PI, min + delta, max - delta, false,
								false);
						surface.cone(center, ev1, ev2, ev3, radius, 0,
								2 * Math.PI, min, min + delta, true, false);
						surface.cone(center, ev1, ev2, ev3, radius, 0,
								2 * Math.PI, max - delta, max, false, true);
					}
				}
			}

			setSurfaceIndex(surface.end());
			break;

		case GeoQuadricNDConstants.QUADRIC_CYLINDER:

			center = quadric.getMidpoint3D();
			Coords ev1 = quadric.getEigenvec3D(0);
			Coords ev2 = quadric.getEigenvec3D(1);
			Coords ev3 = quadric.getEigenvec3D(2);
			radius = quadric.getHalfAxis(0);

			surface = renderer.getGeometryManager().getSurface();
			surface.start(getReusableSurfaceIndex());

			if (quadric instanceof GeoQuadric3DPart) { // simple cylinder
				Coords bottomCenter = surface.cylinder(center, ev1, ev2, ev3, radius, 0, 2 * Math.PI,
						quadric.getMinParameter(1), quadric.getMaxParameter(1),
						false, false);
				
				boundsMin.set(Double.POSITIVE_INFINITY);
				boundsMax.set(Double.NEGATIVE_INFINITY);
				enlargeBoundsToDiagonal(boundsMin, boundsMax, center, ev1, ev2, radius, radius);
				enlargeBoundsToDiagonal(boundsMin, boundsMax, bottomCenter, ev1, ev2, radius, radius);
				
			} else {
				double[] minmax = getMinMax();
				double min = minmax[0];
				double max = minmax[1];
				if (getView3D().useClippingCube()) {
					surface.cylinder(center, ev1, ev2, ev3, radius, 0,
							2 * Math.PI, min, max, false, false);
				} else {
					double delta = (max - min) / 10;
					surface.cylinder(center, ev1, ev2, ev3, radius, 0,
							2 * Math.PI, min + delta, max - delta, false, false);
					surface.cylinder(center, ev1, ev2, ev3, radius, 0,
							2 * Math.PI, min, min + delta, true, false);
					surface.cylinder(center, ev1, ev2, ev3, radius, 0,
							2 * Math.PI, max - delta, max, false, true);
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

		default:
			setSurfaceIndex(-1);
		}

		return true;
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

		switch (quadric.getType()) {
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
		case GeoQuadricNDConstants.QUADRIC_CONE:
		case GeoQuadricNDConstants.QUADRIC_CYLINDER:
		case GeoQuadricNDConstants.QUADRIC_SINGLE_POINT:
			if (getView3D().viewChangedByZoom()
					|| getView3D().viewChangedByTranslate()) {
				updateForItSelf();
			}
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
		case GeoQuadricNDConstants.QUADRIC_CONE:
		case GeoQuadricNDConstants.QUADRIC_CYLINDER:
			addToDrawable3DLists(lists, DRAW_TYPE_CLOSED_SURFACES_CURVED);
			break;
		default:
			addToDrawable3DLists(lists, DRAW_TYPE_SURFACES);
		}
	}

	@Override
	public void removeFromDrawable3DLists(Drawable3DLists lists) {
		switch (((GeoQuadric3D) getGeoElement()).getType()) {
		case GeoQuadricNDConstants.QUADRIC_SPHERE:
		case GeoQuadricNDConstants.QUADRIC_CONE:
		case GeoQuadricNDConstants.QUADRIC_CYLINDER:
			removeFromDrawable3DLists(lists, DRAW_TYPE_CLOSED_SURFACES_CURVED);
			break;
		default:
			removeFromDrawable3DLists(lists, DRAW_TYPE_SURFACES);
		}
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
		
		if (quadric.getType() == GeoQuadricNDConstants.QUADRIC_SINGLE_POINT) {
			if (DrawPoint3D.hit(hitting, quadric.getMidpoint3D(), this, quadric.getLineThickness(), project,
					parameters, false)){
				setPickingType(PickingType.POINT_OR_CURVE);
				return true;
			}
		}	
		

		if (getGeoElement().getAlphaValue() < EuclidianController.MIN_VISIBLE_ALPHA_VALUE) {
			return false;
		}
		

		// TODO remove this
		if (quadric.getType() != GeoQuadricNDConstants.QUADRIC_SPHERE
				&& quadric.getType() != GeoQuadricNDConstants.QUADRIC_CYLINDER
				&& quadric.getType() != GeoQuadricNDConstants.QUADRIC_CONE) {
			return false;
		}

		Coords p3d = quadric.getProjection(null, hitting.origin,
				hitting.direction)[0];

		if (!hitting.isInsideClipping(p3d)) {
			return false;
		}

		p3d.projectLine(hitting.origin, hitting.direction, project, parameters); // check
																					// distance
																					// to
																					// hitting
																					// line

		double d = p3d.distance(project);
		if (d * getView3D().getScale() <= hitting.getThreshold()) {
			double z = -parameters[0];
			setZPick(z, z);
			setPickingType(PickingType.SURFACE);
			return true;
		}

		return false;

	}

	private Coords project = Coords.createInhomCoorsInD3();

	private double[] parameters = new double[2];

}
