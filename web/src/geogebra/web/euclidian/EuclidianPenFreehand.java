package geogebra.web.euclidian;

import geogebra.common.euclidian.EuclidianPen;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.App;

public class EuclidianPenFreehand extends EuclidianPen {

	/**
	 * type that is expected to be created
	 */
	public enum ShapeType {
		circle, polygon;
	}

	private ShapeType expected = null;
	private GeoElement lastCreated = null;

	public EuclidianPenFreehand(App app, EuclidianView view) {
		super(app, view);
		super.setFreehand(true);

		CIRCLE_MAX_SCORE = 0.15;
		CIRCLE_MIN_DET = 0.9;
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
