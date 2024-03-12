package org.geogebra.common.euclidian.measurement;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoPoint;

/**
 * Represents the leg edges of a right triangle.
 */
public final class LegEdge implements MeasurementToolEdge {
	private GeoPoint endpoint1;
	private GeoPoint endpoint2;

	// GeoImage corners 3 and 4.
	private GeoPoint corner3;
	private GeoPoint corner4;
	private Legs leg;

	/**
	 *
	 * @param leg to specify which leg it is.
	 */
	public LegEdge(Legs leg) {
		this.leg = leg;
	}

	@Override
	public GeoPoint endpoint1() {
		return endpoint1;
	}

	@Override
	public GeoPoint endpoint2() {
		return endpoint2;
	}

	@Override
	public void update(GeoImage image) {
		ensureCorners(image.getKernel().getConstruction());
		image.calculateCornerPoint(corner3, 3);
		image.calculateCornerPoint(corner4, 4);
		endpoint1.x = (corner3.x + corner4.x) / 2;
		endpoint1.y = (corner3.y + corner4.y) / 2;
		endpoint1.z = 1;
		image.calculateCornerPoint(endpoint2, leg.index());
		endpoint1.updateCoords();
		endpoint2.updateCoords();
	}

	private void ensureCorners(Construction cons) {
		if (endpoint1 == null) {
			endpoint1 = new GeoPoint(cons, true);
			endpoint2 = new GeoPoint(cons, true);
			corner3 = new GeoPoint(cons, true);
			corner4 = new GeoPoint(cons, true);
		}
	}
}
