package org.geogebra.common.euclidian;

import java.util.ArrayList;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.kernel.algos.AlgoCircleThreePoints;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.geos.Test;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.App;

public class EuclidianPenFreehand extends EuclidianPen {

	/**
	 * type that is expected to be created
	 */
	public enum ShapeType {
		circleThreePoints, polygon, rigidPolygon, vectorPolygon;
	}

	private ShapeType expected = null;

	public EuclidianPenFreehand(App app, EuclidianView view) {
		super(app, view);
		super.setFreehand(true);
	}

	/**
	 * will be ignored - always freehand
	 */
	@Override
	public void setFreehand(boolean b) {
		// don't do anything
	}

	/**
	 * @param expectedType
	 *            defines the expected shape
	 */
	public void setExpected(ShapeType expectedType) {
		this.expected = expectedType;

		resetParameters();
		switch (expected) {
			case circleThreePoints:
				CIRCLE_MAX_SCORE = 0.15;
				CIRCLE_MIN_DET = 0.9;
				break;
			case polygon:
			case rigidPolygon:
			case vectorPolygon:
				RECTANGLE_LINEAR_TOLERANCE = 0.25;
				POLYGON_LINEAR_TOLERANCE = 0.25;
				RECTANGLE_ANGLE_TOLERANCE = 17 * Math.PI / 180;
				break;
		}
	}

	@Override
	public void handleMouseReleasedForPenMode(boolean right, int x, int y) {
		checkExpectedShape(x, y);
	}

	/**
	 * Creates predicted shape if possible
	 * @param x x-coord of new point
	 * @param y y-coord of new point
	 */
	private void checkExpectedShape(int x, int y) {
		initShapeRecognition(x, y);

		switch (this.expected) {
			case polygon:
			case rigidPolygon:
			case vectorPolygon:
				createPolygon();
				break;
			case circleThreePoints:
				createCircle();
				break;
		}
	}

	/**
	 * creates a circle if possible
	 */
	private void createCircle() {
		GeoElement geoCircle;
		if (tryCircleThroughExistingPoints() != null) {
			return;
		} else if ((geoCircle = getCircleThreePoints()) != null) {

			boolean recreate = false;

			ArrayList<GeoPointND> list = new ArrayList<GeoPointND>();
			for (GeoPointND geo : ((GeoConic) geoCircle).getPointsOnConic()) {
				if (!geo.isLabelSet()) {
					recreate = true;
					geo.setLabel(null);
				}
				list.add(geo);
			}

			// the circle needs to be recreated to prevent errors in the XML
			if (recreate) {
				geoCircle.remove();
				AlgoCircleThreePoints algo = new AlgoCircleThreePoints(app
						.getKernel().getConstruction(), null, list.get(0),
						list.get(1), list.get(2));
				geoCircle = algo.getCircle();
				geoCircle.updateRepaint();
			}
		}
		resetInitialPoint();
	}

	/**
	 * tries to construct a circle through 3 existing points, null otherwise
	 * @return {@link GeoElement circle}
	 */
	private GeoElement tryCircleThroughExistingPoints() {
		GeoElement circle = null;
		ArrayList<GeoPoint> list = new ArrayList<GeoPoint>();
		for (GPoint p : this.penPoints) {
			this.view.setHits(p, this.view.getEuclidianController()
					.getDefaultEventType());
			if (this.view.getHits().containsGeoPoint()) {
				GeoPoint point = (GeoPoint) this.view.getHits()
						.getFirstHit(Test.GEOPOINT);
				if (!list.contains(point)) {
					list.add(point);
				}
			}
		}

		if (list.size() >= 3) {
			circle = this.app.getKernel().getAlgoDispatcher()
					.Circle(null, list.get(0), list.get(1), list.get(2));
		}
		return circle;
	}

	/**
	 * creates a polygon if possible
	 */
	private void createPolygon() {

		GeoElement polygon = null;

		int n = getPolygonal();
		if (n > 1) { // if it's not a line
			polygon = tryPolygon(n);
		}

		//Postprocessing

		if (polygon != null) {
			ArrayList<GeoPoint> list = new ArrayList<GeoPoint>();
			for (GeoPointND point : ((GeoPolygon) polygon).getPoints()) {
				if (point instanceof GeoPoint) {
					list.add((GeoPoint) point);
				}
			}
			if (list.size() == ((GeoPolygon) polygon).getPoints().length) {
				// true if all the points are GeoPoints, otherwise the
				// original Polygon will not be deleted
				polygon.remove();
				polygon = expected == ShapeType.rigidPolygon ?
						this.app.getKernel().RigidPolygon(null, list.toArray(new GeoPoint[0]))[0] :
						this.app.getKernel().VectorPolygon(null, list.toArray(new GeoPoint[0]))[0];
			}
		} else {
			resetInitialPoint();
		}
	}

	private void resetParameters() {
		CIRCLE_MIN_DET = 0.95;
		CIRCLE_MAX_SCORE = 0.10;
		RECTANGLE_LINEAR_TOLERANCE = 0.20;
		POLYGON_LINEAR_TOLERANCE = 0.20;
		RECTANGLE_ANGLE_TOLERANCE = 15 * Math.PI / 180;
	}
}
