package geogebra.common.geogebra3D.euclidian3D.draw;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.euclidian.Previewable;
import geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import geogebra.common.geogebra3D.euclidian3D.PolygonTriangulation;
import geogebra.common.geogebra3D.euclidian3D.openGL.PlotterBrush;
import geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import geogebra.common.geogebra3D.euclidian3D.openGL.Renderer.PickingType;
import geogebra.common.geogebra3D.kernel3D.algos.AlgoPolyhedronPoints;
import geogebra.common.geogebra3D.kernel3D.algos.AlgoPolyhedronPointsPrism;
import geogebra.common.geogebra3D.kernel3D.algos.AlgoPolyhedronPointsPyramid;
import geogebra.common.geogebra3D.kernel3D.geos.GeoPolyhedron;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.kernelND.GeoSegmentND;

import java.util.ArrayList;

/**
 * Class for drawing 3D polygons.
 * 
 * @author matthieu
 *
 */
public class DrawPolyhedron3D extends Drawable3DSurfaces implements Previewable {

	/**
	 * Common constructor
	 * 
	 * @param a_view3D
	 * @param poly
	 */
	public DrawPolyhedron3D(EuclidianView3D a_view3D, GeoPolyhedron poly) {

		super(a_view3D, poly);

	}

	private boolean isPreview = false;

	private int previewMode;

	private DrawPolygon3D drawPolygon3D;

	private ArrayList<GeoPointND> selectedPoints;
	private ArrayList<GeoPolygon> selectedPolygons;

	public DrawPolyhedron3D(EuclidianView3D a_view3D,
			ArrayList<GeoPointND> selectedPoints,
			ArrayList<GeoPolygon> selectedPolygons, int mode) {

		super(a_view3D);

		drawPolygon3D = new DrawPolygon3D(a_view3D, selectedPoints);

		this.selectedPoints = selectedPoints;
		this.selectedPolygons = selectedPolygons;

		isPreview = true;
		previewMode = mode;

		// updatePreview();

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
		return DRAW_PICK_ORDER_SURFACE;
	}

	@Override
	public void addToDrawable3DLists(Drawable3DLists lists) {

		addToDrawable3DLists(lists, DRAW_TYPE_CLOSED_SURFACES_NOT_CURVED);
		addToDrawable3DLists(lists, DRAW_TYPE_CURVES);

	}

	@Override
	public void removeFromDrawable3DLists(Drawable3DLists lists) {

		removeFromDrawable3DLists(lists, DRAW_TYPE_CLOSED_SURFACES_NOT_CURVED);
		removeFromDrawable3DLists(lists, DRAW_TYPE_CURVES);

	}

	private Coords[] vertices = new Coords[0];

	@Override
	protected boolean updateForItSelf() {

		Renderer renderer = getView3D().getRenderer();

		// outline
		updateOutline(renderer);

		// surface
		int index = renderer.startPolygons(getReusableSurfaceIndex());
		for (GeoPolygon p : ((GeoPolyhedron) getGeoElement())
				.getPolygonsLinked()) {
			drawPolygon(renderer, p);
		}
		for (GeoPolygon p : ((GeoPolyhedron) getGeoElement()).getPolygons()) {
			drawPolygon(renderer, p);
		}
		renderer.endPolygons();

		setSurfaceIndex(index);

		return true;

	}

	private void updateOutline(Renderer renderer) {

		GeoPolyhedron poly = (GeoPolyhedron) getGeoElement();

		PlotterBrush brush = renderer.getGeometryManager().getBrush();
		brush.start(getReusableGeometryIndex());
		brush.setThickness(poly.getLineThickness(), (float) getView3D()
				.getScale());

		for (GeoPolygon p : ((GeoPolyhedron) getGeoElement())
				.getPolygonsLinked()) {
			// draw segments for polygons that have no label
			if (p.isEuclidianVisible() && !p.isLabelSet()) {
				for (GeoSegmentND seg : p.getSegments()) {
					drawSegment(brush, seg);
				}
			}
		}
		for (GeoSegmentND seg : poly.getSegments()) {
			drawSegment(brush, seg);
		}

		setGeometryIndex(brush.end());

	}
	
	

	private static void drawSegment(PlotterBrush brush, GeoSegmentND seg) {

		// draw only segments that have no label
		if (!seg.isEuclidianVisible() || seg.isLabelSet()) {
			return;
		}

		brush.setAffineTexture(0.5f, 0.25f);
		brush.segment(seg.getStartInhomCoords(), seg.getEndInhomCoords());

	}

	private PolygonTriangulation pt = new PolygonTriangulation();

	private void drawPolygon(Renderer renderer, GeoPolygon polygon) {

		// draw only polygons that have no label
		if (!polygon.isEuclidianVisible() || polygon.isLabelSet()) {
			return;
		}

		int pointLength = polygon.getPointsLength();

		if (pointLength < 3) { // no polygon
			return;
		}

		if (vertices.length < pointLength) {
			vertices = new Coords[pointLength];
			for (int i = 0; i < pointLength; i++) {
				vertices[i] = new Coords(3);
			}
		}

		for (int i = 0; i < pointLength; i++) {
			vertices[i].setValues(polygon.getPoint3D(i), 3);
		}

		pt.setPolygon(polygon);
		DrawPolygon3D.drawPolygon(renderer, polygon, pt, vertices,
				polygon.getPointsLength());

	}

	@Override
	protected void updateForView() {

		if (getView3D().viewChangedByZoom()) {

			Renderer renderer = getView3D().getRenderer();

			// outline
			updateOutline(renderer);

			recordTrace();

		}
	}

	public void updatePreview() {

		if (previewBasisIsFinished) {
			getView3D().getCursor3D().updateCascade();
			return;
		}

		if (selectedPolygons.size() == 1) {
			previewBasisIsFinished = true;

			Construction cons = getView3D().getKernel().getConstruction();

			switch (previewMode) {
			case EuclidianConstants.MODE_PYRAMID:
				previewAlgo = new AlgoPolyhedronPointsPyramid(cons, null,
						selectedPolygons.get(0), getView3D().getCursor3D());
				break;
			case EuclidianConstants.MODE_PRISM:
				previewAlgo = new AlgoPolyhedronPointsPrism(cons, null,
						selectedPolygons.get(0), getView3D().getCursor3D());
				break;
			}

			// set visibilities
			previewAlgo.removeOutputFromAlgebraView();
			previewAlgo.removeOutputFromPicking();
			previewAlgo.setOutputPointsEuclidianVisible(false);
			previewAlgo.notifyUpdateOutputPoints();

			// ensure correct drawing of visible parts of the previewable
			previewAlgo.setOutputOtherEuclidianVisible(true);
			previewAlgo.notifyUpdateOutputOther();
		} else {
			drawPolygon3D.updatePreview();
		}

	}

	private AlgoPolyhedronPoints previewAlgo;

	@Override
	public void disposePreview() {
		super.disposePreview();

		if (previewBasisIsFinished) {
			previewBasisIsFinished = false;

			if (previewAlgo != null) {
				previewAlgo.remove();
				previewAlgo = null;
			}
		} else {
			drawPolygon3D.disposePreview();
		}

	}

	// public void hidePreview(){
	// if (previewBasisIsFinished){
	// previewBasisIsFinished = false;
	//
	// if (previewAlgo != null){
	// previewAlgo.remove();
	// previewAlgo = null;
	// }
	// }else{
	// drawPolygon3D.hidePreview();
	// }
	// }

	public void updateMousePos(double x, double y) {
		// TODO Auto-generated method stub
	}

	private boolean previewBasisIsFinished = false;

	/**
	 * tells that the preview basis is done
	 */
	public void previewBasisIsFinished() {
		previewBasisIsFinished = true;

		// dispose polygon preview
		drawPolygon3D.disposePreview();

		// create polyhedron
		GeoPointND[] points = new GeoPointND[selectedPoints.size() + 1];
		for (int i = 0; i < selectedPoints.size(); i++) {
			points[i] = selectedPoints.get(i);
		}
		points[selectedPoints.size()] = getView3D().getCursor3D();

		Construction cons = getView3D().getKernel().getConstruction();

		switch (previewMode) {
		case EuclidianConstants.MODE_PYRAMID:
			previewAlgo = new AlgoPolyhedronPointsPyramid(cons, null, points);
			break;
		case EuclidianConstants.MODE_PRISM:
			previewAlgo = new AlgoPolyhedronPointsPrism(cons, null, points);
			break;
		}

		// set visibilities
		previewAlgo.removeOutputFromAlgebraView();
		previewAlgo.removeOutputFromPicking();
		previewAlgo.setOutputPointsEuclidianVisible(false);
		previewAlgo.notifyUpdateOutputPoints();

		// ensure correct drawing of visible parts of the previewable
		previewAlgo.setOutputOtherEuclidianVisible(true);
		previewAlgo.notifyUpdateOutputOther();

	}

}
