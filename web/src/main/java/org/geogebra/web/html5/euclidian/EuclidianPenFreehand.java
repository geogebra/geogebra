package org.geogebra.web.html5.euclidian;

import java.util.ArrayList;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.EuclidianPen;
import org.geogebra.common.euclidian.EuclidianView;
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
		circleThreePoints, circle, polygon, rigidPolygon, vectorPolygon;
	}

	private ShapeType expected = null;
	private GeoElement lastCreated = null;

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
		case circle:
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

	/**
	 * @return the GeoElement that was created the last time, null in case
	 *         creating failed
	 */
	@Override
	public GeoElement getCreatedShape() {
		return lastCreated;
	}

	@Override
	public void handleMouseReleasedForPenMode(boolean right, int x, int y) {
		if (this.expected == ShapeType.circleThreePoints) {
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
				lastCreated = this.app.getKernel().getAlgoDispatcher()
				        .Circle(null, list.get(0), list.get(1), list.get(2));
				return;
			}
		}

		lastCreated = checkShapes(x, y);

		if (lastCreated == null) {
			return;
		}

		switch (this.expected) {
		case circleThreePoints:
		case circle:
			if (lastCreated instanceof GeoConic
			        && ((GeoConic) lastCreated).isCircle()) {
				if (this.initialPoint != null) {
					this.initialPoint.remove();
				}

				boolean recreate = false;

				ArrayList<GeoPointND> list = new ArrayList<GeoPointND>();
				for (GeoPointND geo : ((GeoConic) lastCreated)
				        .getPointsOnConic()) {
					if (!geo.isLabelSet()) {
						recreate = true;
						geo.setLabel(null);
					}
					list.add(geo);
				}

				// the circle needs to be recreated to prevent errors in the XML
				if (recreate && this.expected == ShapeType.circleThreePoints) {
					lastCreated.remove();
					AlgoCircleThreePoints algo = new AlgoCircleThreePoints(app
					        .getKernel().getConstruction(), null, list.get(0),
					        list.get(1), list.get(2));
					GeoConic circle = (GeoConic) algo.getCircle();
					circle.updateRepaint();
					lastCreated = circle;
				}
				if (this.expected == ShapeType.circle) {
					// use the equation of the generated circle and process it
					// as command. required to ensure that the circle is based
					// on the equation and not on the points
					GeoElement freeCopy = lastCreated.copy();
					freeCopy.setLabel(null);

					for (GeoPointND p : list) {
						// the circle is made of three points that have to be
						// deleted (as a consequence the original circle is also
						// deleted)
						if (p instanceof GeoElement) {
							((GeoElement) p).remove();
						}
					}
				}

				return;
			}
			break;
		case polygon:
			if (lastCreated instanceof GeoPolygon) {
				return;
			}
			break;
		case rigidPolygon:
			if (lastCreated instanceof GeoPolygon) {
				ArrayList<GeoPoint> list = new ArrayList<GeoPoint>();
				for (GeoPointND point : ((GeoPolygon) lastCreated).getPoints()) {
					if (point instanceof GeoPoint) {
						list.add((GeoPoint) point);
					}
				}
				if (list.size() == ((GeoPolygon) lastCreated).getPoints().length) {
					// true if all the points are GeoPoints, otherwise the
					// original Polygon will not be deleted
					lastCreated.remove();
					lastCreated = this.app.getKernel().RigidPolygon(null,
					        list.toArray(new GeoPoint[0]))[0];
				}
				return;
			}
			break;
		case vectorPolygon:
			if (lastCreated instanceof GeoPolygon) {
				ArrayList<GeoPoint> list = new ArrayList<GeoPoint>();
				for (GeoPointND point : ((GeoPolygon) lastCreated).getPoints()) {
					if (point instanceof GeoPoint) {
						list.add((GeoPoint) point);
					}
				}
				if (list.size() == ((GeoPolygon) lastCreated).getPoints().length) {
					// true if all the points are GeoPoints, otherwise the
					// original Polygon will not be deleted
					lastCreated.remove();
					lastCreated = this.app.getKernel().VectorPolygon(null,
					        list.toArray(new GeoPoint[0]))[0];
				}
				return;
			}
			break;
		}

		// shape is not of the expected type -> delete it
		if (lastCreated.getParentAlgorithm() != null) {
			for (GeoPointND geo : lastCreated.getParentAlgorithm()
			        .getFreeInputPoints()) {
				if (this.deleteInitialPoint || this.initialPoint == null
				        || !this.initialPoint.equals(geo)) {
					geo.remove();
				}
			}
		}

		if (this.deleteInitialPoint && this.initialPoint != null) {
			this.initialPoint.remove();
		}

		lastCreated.remove();
		lastCreated = null;
	}

	private void resetParameters() {
		CIRCLE_MIN_DET = 0.95;
		CIRCLE_MAX_SCORE = 0.10;
		RECTANGLE_LINEAR_TOLERANCE = 0.20;
		POLYGON_LINEAR_TOLERANCE = 0.20;
		RECTANGLE_ANGLE_TOLERANCE = 15 * Math.PI / 180;
	}

}
