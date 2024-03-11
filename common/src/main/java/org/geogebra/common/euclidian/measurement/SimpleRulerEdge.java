package org.geogebra.common.euclidian.measurement;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoPoint;

public class SimpleRulerEdge implements RulerEdge {
	private final int cornerIndex1;
	private final int cornerIndex2;
	private GeoPoint corner1 = null;
	private GeoPoint corner2 = null;

	public SimpleRulerEdge(int cornerIndex1, int cornerIndex2) {
		this.cornerIndex1 = cornerIndex1;
		this.cornerIndex2 = cornerIndex2;
	}

	@Override
	public boolean accept(GPoint p, GPoint projection) {
		return false;
	}

	@Override
	public GeoPoint corner1() {
		return corner1;
	}

	@Override
	public GeoPoint corner2() {
		return corner2;
	}

	@Override
	public void update(GeoImage ruler) {
		if (ruler == null) {
			return;
		}

		Construction cons = ruler.kernel.getConstruction();
		if (corner1 == null) {
			corner1 = new GeoPoint(cons);
			corner2 = new GeoPoint(cons);
		}
		ruler.calculateCornerPoint(corner1, cornerIndex1);
		ruler.calculateCornerPoint(corner2, cornerIndex2);
	}
}
