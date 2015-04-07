package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoConic3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPolygon3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Matrix.CoordSys;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.algos.AlgoConicFivePoints;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoPointND;

public class AlgoConicFivePoints3D extends AlgoConicFivePoints {

	private GeoPointND[] inputP;

	public AlgoConicFivePoints3D(Construction cons, String label, GeoPointND[] P) {
		super(cons, label, P);
	}

	@Override
	protected void setInputPoints() {
		input = new GeoElement[5];
		for (int i = 0; i < 5; i++) {
			input[i] = (GeoElement) inputP[i];
		}

	}

	@Override
	protected GeoPoint[] createPoints2D(GeoPointND[] inputP) {

		this.inputP = inputP;

		GeoPoint[] ret = new GeoPoint[5];
		for (int i = 0; i < 5; i++) {
			ret[i] = new GeoPoint(cons);
		}

		return ret;
	}

	@Override
	protected GeoConicND newGeoConic(Construction cons) {
		GeoConic3D ret = new GeoConic3D(cons);
		ret.setCoordSys(new CoordSys(2));
		return ret;
	}

	private Coords tmpCoords;

	@Override
	protected void initCoords() {
		tmpCoords = new Coords(4);
	}

	@Override
	public final void compute() {

		CoordSys cs = conic.getCoordSys();

		if (GeoPolygon3D.updateCoordSys(cs, inputP, P, tmpCoords)) {
			conic.setDefined();
			super.compute();
		} else {
			conic.setUndefined();
		}
	}

}
