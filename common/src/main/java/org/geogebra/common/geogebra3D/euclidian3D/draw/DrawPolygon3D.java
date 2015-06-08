package org.geogebra.common.geogebra3D.euclidian3D.draw;

import java.util.ArrayList;
import java.util.Iterator;

import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.Previewable;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.Hitting;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.PlotterBrush;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer.PickingType;
import org.geogebra.common.geogebra3D.kernel3D.Kernel3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPolygon3D;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.discrete.PolygonTriangulation;
import org.geogebra.common.kernel.discrete.PolygonTriangulation.Convexity;
import org.geogebra.common.kernel.geos.FromMeta;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.App;

/**
 * Class for drawing 3D polygons.
 * 
 * @author matthieu
 *
 */
public class DrawPolygon3D extends Drawable3DSurfaces implements Previewable {

	/**
	 * Common constructor
	 * 
	 * @param a_view3D
	 * @param polygon
	 */
	public DrawPolygon3D(EuclidianView3D a_view3D, GeoPolygon polygon) {

		super(a_view3D, polygon);

		pt.setPolygon(polygon);

		setPickingType(PickingType.SURFACE);

	}

	// drawing

	@Override
	public void drawGeometry(Renderer renderer) {

		renderer.setLayer(getLayer()); // +0f for z-fighting with planes
		renderer.getGeometryManager().draw(getGeometryIndex());
		renderer.setLayer(0);

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

	@Override
	public void drawGeometryHiding(Renderer renderer) {
		drawSurfaceGeometry(renderer);
	}

	@Override
	public void drawGeometryHidden(Renderer renderer) {
		drawGeometry(renderer);
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
	protected void drawSurfaceGeometry(Renderer renderer) {

		renderer.setLayer(getLayer()); // +0f to avoid z-fighting with planes
		renderer.getGeometryManager().draw(getSurfaceIndex());
		renderer.setLayer(0);

	}

	@Override
	public int getPickOrder() {
		/*
		 * Application.debug(alpha<1); if (alpha<1)
		 */
		return DRAW_PICK_ORDER_SURFACE; // when transparent
		/*
		 * else return DRAW_PICK_ORDER_1D; //when not
		 */
	}

	@Override
	public void addToDrawable3DLists(Drawable3DLists lists) {
		if (((GeoPolygon) getGeoElement()).isPartOfClosedSurface())
			addToDrawable3DLists(lists, DRAW_TYPE_CLOSED_SURFACES_NOT_CURVED);
		else
			addToDrawable3DLists(lists, DRAW_TYPE_SURFACES);

		if (!((GeoPolygon) getGeoElement()).wasInitLabelsCalled()) { // no
																		// labels
																		// for
																		// segments
			addToDrawable3DLists(lists, DRAW_TYPE_CURVES);
		}

	}

	@Override
	public void removeFromDrawable3DLists(Drawable3DLists lists) {
		if (((GeoPolygon) getGeoElement()).isPartOfClosedSurface())
			removeFromDrawable3DLists(lists,
					DRAW_TYPE_CLOSED_SURFACES_NOT_CURVED);
		else
			removeFromDrawable3DLists(lists, DRAW_TYPE_SURFACES);

		if (!((GeoPolygon) getGeoElement()).wasInitLabelsCalled()) { // no
																		// labels
																		// for
																		// segments
			removeFromDrawable3DLists(lists, DRAW_TYPE_CURVES);
		}

	}

	private Coords[] vertices = new Coords[0];
	private PolygonTriangulation pt = new PolygonTriangulation();

	private void updateVertices(GeoPolygon polygon, int pointLength) {

		if (vertices.length < pointLength) {
			vertices = new Coords[pointLength];
			for (int i = 0; i < pointLength; i++) {
				vertices[i] = new Coords(3);
			}
		}

		for (int i = 0; i < pointLength; i++) {
			vertices[i].setValues(polygon.getPoint3D(i), 3);
		}
		
		if (pointLength > 0){
			boundsMin.setValues(vertices[0], 3);
			boundsMax.setValues(vertices[0], 3);
			for (int i = 1; i < pointLength; i++) {
				enlargeBounds(boundsMin, boundsMax, vertices[i]);
			}
		}

	}
	
	private Coords boundsMin = new Coords(3), boundsMax = new Coords(3);

	
	@Override
	public void enlargeBounds(Coords min, Coords max) {
		enlargeBounds(min, max, boundsMin, boundsMax);
	}


	@Override
	protected boolean updateForItSelf() {

		// super.updateForItSelf();

		// creates the polygon
		GeoPolygon polygon = (GeoPolygon) getGeoElement();

		int pointLength = polygon.getPointsLength();

		if (pointLength < 3) { // no polygon
			setSurfaceIndex(-1);
			return true;
		}

		Renderer renderer = getView3D().getRenderer();

		updateVertices(polygon, pointLength);

		// outline
		if (!isPreview && !polygon.wasInitLabelsCalled()) { // no labels for
															// segments
			updateOutline(renderer, vertices, pointLength);
		}

		// surface
		int index = renderer.startPolygons(getReusableSurfaceIndex());

		drawPolygon(renderer, polygon, pt, vertices, pointLength);

		renderer.endPolygons();

		setSurfaceIndex(index);

		return true;

	}

	/**
	 * 
	 * @param renderer
	 *            GL renderer
	 * @param polygon
	 *            polygon
	 * @param pt
	 *            polygon triangulation
	 * @param vertices
	 *            vertices of the polygon
	 * @param verticesLength
	 *            vertices length (may <> vertices.length due to cache)
	 */
	static final public void drawPolygon(Renderer renderer, GeoPolygon polygon,
			PolygonTriangulation pt, Coords[] vertices, int verticesLength) {

		Coords n = polygon.getMainDirection();

		pt.clear();

		try {
			// simplify the polygon and check if there are at least 3 points
			// left
			if (pt.updatePoints() > 2) {

				// check if the polygon is convex
				Convexity convexity = pt.checkIsConvex();
				if (convexity != Convexity.NOT) {
					boolean reverse = polygon.getReverseNormalForDrawing()
							^ (convexity == Convexity.CLOCKWISE);
					renderer.getGeometryManager().drawPolygonConvex(n,
							vertices, verticesLength, reverse);
				} else {
					// set intersections (if needed) and divide the polygon into
					// non self-intersecting polygons
					pt.setIntersections();

					// convert the set of polygons to triangle fans
					pt.triangulate();

					// compute 3D coords for intersections
					Coords[] verticesWithIntersections = pt
							.getCompleteVertices(vertices,
									polygon.getCoordSys(), verticesLength);

					// draw the triangle fans
					renderer.getGeometryManager().drawTriangleFans(n,
							verticesWithIntersections, pt.getMaxPointIndex(),
							pt.getTriangleFans());

				}

			}
		} catch (Exception e) {
			App.debug(e.getMessage());
			e.printStackTrace();
		}
	}

	private void updateOutline(Renderer renderer, Coords[] vertices, int length) {

		PlotterBrush brush = renderer.getGeometryManager().getBrush();
		brush.start(getReusableGeometryIndex());
		brush.setThickness(getGeoElement().getLineThickness(),
				(float) getView3D().getScale());
		for (int i = 0; i < length - 1; i++) {
			brush.setAffineTexture(0.5f, 0.25f);
			brush.segment(vertices[i], vertices[i + 1]);
		}
		brush.setAffineTexture(0.5f, 0.25f);
		brush.segment(vertices[length - 1], vertices[0]);
		setGeometryIndex(brush.end());

	}

	@Override
	protected void updateForView() {

		if (getView3D().viewChangedByZoom()) {

			if (!((GeoPolygon) getGeoElement()).wasInitLabelsCalled()) { // no
																			// labels
																			// for
																			// segments

				GeoPolygon polygon = (GeoPolygon) getGeoElement();
				int pointLength = polygon.getPointsLength();
				Renderer renderer = getView3D().getRenderer();

				updateVertices(polygon, pointLength);

				// outline
				updateOutline(renderer, vertices, pointLength);

				recordTrace();
			}
		}
	}

	// //////////////////////////////
	// Previewable interface

	@SuppressWarnings("unchecked")
	private ArrayList<GeoPointND> selectedPoints;

	/** segments of the polygon preview */
	private ArrayList<DrawSegment3D> segments;

	@SuppressWarnings("unchecked")
	private ArrayList<ArrayList> segmentsPoints;

	private boolean isPreview = false;

	/**
	 * Constructor for previewable
	 * 
	 * @param a_view3D
	 * @param selectedPoints
	 */
	@SuppressWarnings("unchecked")
	public DrawPolygon3D(EuclidianView3D a_view3D,
			ArrayList<GeoPointND> selectedPoints) {

		super(a_view3D);

		Kernel3D kernel = getView3D().getKernel();

		setGeoElement(new GeoPolygon3D(kernel.getConstruction(), null));
		getGeoElement().setIsPickable(false);

		pt.setPolygon((GeoPolygon) getGeoElement());

		this.selectedPoints = selectedPoints;

		segments = new ArrayList<DrawSegment3D>();
		segmentsPoints = new ArrayList<ArrayList>();

		setPickingType(PickingType.SURFACE);

		isPreview = true;

		updatePreview();

	}

	public void updateMousePos(double xRW, double yRW) {
		// TODO Auto-generated method stub

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void updatePreview() {

		// intersection curve
		if (segmentsPoints == null) {
			// App.debug(this);
			setWaitForUpdate();
			return;
		}

		int index = 0;
		Iterator<ArrayList> spi = segmentsPoints.iterator();
		Iterator i = selectedPoints.iterator();
		GeoPointND point = null; // current point of the selected points
		ArrayList sp = null; // segment selected points

		// set points to existing segments points
		for (; i.hasNext() && spi.hasNext();) {
			point = (GeoPointND) i.next();
			if (sp != null)
				sp.add(point); // add second point to precedent segment

			sp = spi.next();
			sp.clear();
			sp.add(point); // add first point to current segment
		}

		// clear segments points if there are some more
		for (; spi.hasNext();) {
			sp = spi.next();
			sp.clear();
		}

		// set points to new segments points
		for (; i.hasNext();) {
			if (sp != null && point != null)
				sp.add(point); // add second point to precedent segment

			sp = new ArrayList();
			segmentsPoints.add(sp);
			point = (GeoPointND) i.next();
			sp.add(point);
			DrawSegment3D s = new DrawSegment3D(getView3D(), sp);
			s.getGeoElement().setVisualStyle(getGeoElement());
			segments.add(s);
			getView3D().addToDrawable3DLists(s);
		}

		// update segments
		for (Iterator<DrawSegment3D> s = segments.iterator(); s.hasNext();)
			s.next().updatePreview();

		// Application.debug("DrawList3D:\n"+getView3D().getDrawList3D().toString());

		// polygon itself

		if (selectedPoints.size() < 2) {
			getGeoElement().setEuclidianVisible(false);
			return;
		}

		getGeoElement().setEuclidianVisible(true);

		GeoPointND[] points = new GeoPointND[selectedPoints.size() + 1];

		index = 0;
		for (Iterator p = selectedPoints.iterator(); p.hasNext();) {
			points[index] = (GeoPointND) p.next();
			index++;
		}

		points[index] = getView3D().getCursor3D();

		// sets the points of the polygon
		((GeoPolygon3D) getGeoElement()).setPoints(points, null, false);
		// check if all points are on the same plane
		((GeoPolygon3D) getGeoElement()).updateCoordSys();
		if (getGeoElement().isDefined())
			setWaitForUpdate();

	}

	@Override
	public void disposePreview() {

		// first update preview to ensure segments arrays are correct
		updatePreview();

		super.disposePreview();

		// dispose segments
		if (segments != null) {
			for (DrawSegment3D s : segments) {
				s.disposePreview();
			}
		}

		// we may reuse it
		if (segmentsPoints != null) {
			segmentsPoints.clear();
		}
	}

	@Override
	public boolean doHighlighting() {

		// if the polygon depends on a polyhedron, look at the meta'
		// highlighting

		if (getGeoElement().getMetasLength() > 0) {
			for (GeoElement meta : ((FromMeta) getGeoElement()).getMetas()) {
				if (meta != null && meta.doHighlighting())
					return true;
			}
		}

		return super.doHighlighting();
	}

	private GeoPoint3D hittingPointForOutline;

	@Override
	public boolean hit(Hitting hitting) {

		if (waitForReset) { // prevent NPE
			return false;
		}

		if (getGeoElement().getAlphaValue() < EuclidianController.MIN_VISIBLE_ALPHA_VALUE) {
			return false;
		}

		GeoPolygon poly = (GeoPolygon) getGeoElement();

		if (poly.getCoordSys() == null) {
			App.debug("" + poly);
			return false;
		}

		// project hitting origin on polygon plane
		if (globalCoords == null) {
			globalCoords = new Coords(4);
			inPlaneCoords = new Coords(4);
		}

		if (hitting.isSphere()) {
			hitting.origin.projectPlane(poly.getCoordSys()
					.getMatrixOrthonormal(), globalCoords);

			if (hittingPointForOutline == null) {
				hittingPointForOutline = new GeoPoint3D(poly.getConstruction());
				hittingPointForOutline.setWillingCoordsUndefined();
				hittingPointForOutline.setWillingDirectionUndefined();
			}

			// try outline
			hittingPointForOutline.setCoords(globalCoords);
			poly.pointChanged(hittingPointForOutline);
			Coords p3d = hittingPointForOutline.getInhomCoordsInD3();

			if (project == null) {
				project = Coords.createInhomCoorsInD3();
			}

			double d = p3d.distance(hitting.origin);
			double scale = getView3D().getScale();
			if (d * scale <= poly.getLineThickness() + hitting.getThreshold()) {
				setZPick(-d, -d);
				setPickingType(PickingType.POINT_OR_CURVE);
				return true;
			}

			// try inside
			hittingPointForOutline.setCoords(globalCoords);
			hittingPointForOutline.setRegion(poly);
			poly.pointChangedForRegion(hittingPointForOutline);
			p3d = hittingPointForOutline.getInhomCoordsInD3();
			d = p3d.distance(hitting.origin);
			if (d * scale <= hitting.getThreshold()) {
				setZPick(-d, -d);
				setPickingType(PickingType.SURFACE);
				return true;
			}

		} else {
			hitting.origin.projectPlaneThruVIfPossible(poly.getCoordSys()
					.getMatrixOrthonormal(), hitting.direction, globalCoords,
					inPlaneCoords);

			if (!hitting.isInsideClipping(globalCoords)) {
				return false;
			}

			boolean ret = false;

			// check if hitting projection hits the polygon
			if (poly.isInRegion(inPlaneCoords.getX(), inPlaneCoords.getY())) {
				double parameterOnHitting = inPlaneCoords.getZ();// TODO use
																	// other for
																	// non-parallel
																	// projection
																	// :
																	// -hitting.origin.distance(project[0]);
				setZPick(parameterOnHitting, parameterOnHitting);
				setPickingType(PickingType.SURFACE);
				ret = true;
			}

			// check if hitting is on path
			if (!poly.wasInitLabelsCalled()) {

				if (hittingPointForOutline == null) {
					hittingPointForOutline = new GeoPoint3D(
							poly.getConstruction());
				}
				hittingPointForOutline.setCoords(globalCoords);
				poly.pointChanged(hittingPointForOutline);
				Coords p3d = hittingPointForOutline.getInhomCoordsInD3();

				if (hitting.isInsideClipping(p3d)) {
					if (project == null) {
						project = Coords.createInhomCoorsInD3();
					}
					p3d.projectLine(hitting.origin, hitting.direction, project,
							parameters); // check distance to hitting line
					double d = p3d.distance(project);
					double scale = getView3D().getScale();
					if (d * scale <= poly.getLineThickness()
							+ hitting.getThreshold()) {
						double z = -parameters[0];
						double dz = poly.getLineThickness() / scale;
						setZPick(z + dz, z - dz);
						setPickingType(PickingType.POINT_OR_CURVE);
						return true;
					}
				}
			}

			return ret;

		}

		return false;

	}

	private Coords project, globalCoords, inPlaneCoords;

	private double[] parameters = new double[2];

}
