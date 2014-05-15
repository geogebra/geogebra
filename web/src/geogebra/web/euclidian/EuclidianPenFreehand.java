package geogebra.web.euclidian;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.euclidian.EuclidianPen;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.App;

import java.util.ArrayList;

public class EuclidianPenFreehand extends EuclidianPen {

	/**
	 * type that is expected to be created
	 */
	public enum ShapeType {
		circle, polygon, regularPolygon, rigidPolygon, vectorPolygon;
	}

	private ShapeType expected = null;
	private GeoElement lastCreated = null;

	public EuclidianPenFreehand(App app, EuclidianView view) {
		super(app, view);
		super.setFreehand(true);

		CIRCLE_MAX_SCORE = 0.20;
		CIRCLE_MIN_DET = 0.85;
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
	}

	/**
	 * @return the GeoElement that was created the last time, null in case
	 *         creating failed
	 */
	public GeoElement getCreatedShape() {
		return lastCreated;
	}

	@Override
	public void handleMouseReleasedForPenMode(boolean right, int x, int y) {
		lastCreated = checkShapes(x, y);

		if (lastCreated == null) {
			return;
		}

		switch (this.expected) {
		case circle:
			if (lastCreated instanceof GeoConic) {
				if (this.initialPoint != null) {
					this.initialPoint.remove();
				}
				return;
			}
			break;
		case polygon:
			if (lastCreated instanceof GeoPolygon) {
				return;
			}
			break;
		case regularPolygon:
			if (lastCreated instanceof GeoLine) {
				this.app.getDialogManager()
				        .showNumberInputDialogRegularPolygon(
				                this.app.getLocalization()
				                        .getMenu(
				                                this.app.getKernel()
				                                        .getModeText(
				                                                EuclidianConstants.MODE_REGULAR_POLYGON)),
				                this.view.getEuclidianController(),
				                ((GeoLine) lastCreated).startPoint,
				                ((GeoLine) lastCreated).endPoint);
				lastCreated.remove();
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
					this.app.getKernel().RigidPolygon(null,
					        list.toArray(new GeoPoint[0]));					
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
					this.app.getKernel().VectorPolygon(null,
					        list.toArray(new GeoPoint[0]));					
				}
				return;
			}
			break;
		}

		// shape is not of the expected type -> delete it
		for (GeoPointND geo : lastCreated.getParentAlgorithm()
		        .getFreeInputPoints()) {
			geo.remove();
		}
		lastCreated.remove();
		lastCreated = null;
	}
}
