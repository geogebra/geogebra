package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoConic3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPolygon3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoConicFivePoints;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.CoordSys;

/**
 * 3D version of conic through 5 points.
 *
 */
public class AlgoConicFivePoints3D extends AlgoConicFivePoints {

	private GeoPointND[] inputP;
	private double[] tmpCoords;

	/**
	 * @param cons
	 *            construction
	 * @param pts
	 *            points
	 */
	public AlgoConicFivePoints3D(Construction cons, GeoPointND[] pts) {
		super(cons, pts);
	}

	@Override
	protected void setInputPoints() {
		input = new GeoElement[5];
		for (int i = 0; i < 5; i++) {
			input[i] = (GeoElement) inputP[i];
		}
	}

	@Override
	protected GeoPoint[] createPoints2D(GeoPointND[] inputPoints) {

		this.inputP = inputPoints;

		GeoPoint[] ret = new GeoPoint[5];
		for (int i = 0; i < 5; i++) {
			ret[i] = new GeoPoint(cons);
		}

		return ret;
	}

	@Override
	protected GeoConicND newGeoConic(Construction cons0) {
		GeoConic3D ret = new GeoConic3D(cons0);
		ret.setCoordSys(new CoordSys(2));
		return ret;
	}

	@Override
	protected void initCoords() {
		tmpCoords = new double[4];
	}

	@Override
	public final void compute() {

		CoordSys cs = conic.getCoordSys();

		if (GeoPolygon3D.updateCoordSys(cs, inputP, P, tmpCoords, Kernel.STANDARD_PRECISION)) {
			conic.setDefined();
			super.compute();
		} else {
			conic.setUndefined();
		}
	}

}
