package org.geogebra.common.euclidian.measurement;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoPoint;

public class LegEdge implements RulerEdge {
	private GeoPoint endpoint1;
	private GeoPoint endpoint2;
	private GeoPoint corner0;
	private GeoPoint corner3;
	private GeoPoint corner4;
	enum Legs {
		A(1),
		B(2);

		Legs(int index) {
			this.index = index;
		}

		private int index;

		public int index() {
			return index;
		}
	}

	private Legs leg;

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
		ensureCorners(image.getApp().getActiveEuclidianView());
		image.calculateCornerPoint(corner3, 3);
		image.calculateCornerPoint(corner4, 4);
		endpoint1.x = (corner3.x + corner4.x) / 2;
		endpoint1.y = (corner3.y + corner4.y) / 2;
		endpoint1.z = 1;
		image.calculateCornerPoint(endpoint2, leg.index());
		endpoint1.updateCoords();
		endpoint2.updateCoords();
	}

	private void ensureCorners(EuclidianView view) {
		if (endpoint1 == null) {
			endpoint1 = new GeoPoint(view.getKernel().getConstruction(), true);
			endpoint2 = new GeoPoint(view.getKernel().getConstruction(), true);
			corner0 = new GeoPoint(view.getKernel().getConstruction(), true);
			corner3 = new GeoPoint(view.getKernel().getConstruction(), true);
			corner4 = new GeoPoint(view.getKernel().getConstruction(), true);
		}
	}

}
