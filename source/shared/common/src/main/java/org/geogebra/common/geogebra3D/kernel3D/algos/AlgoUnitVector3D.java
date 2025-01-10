package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoVector3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoUnitVector;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.util.DoubleUtil;

public class AlgoUnitVector3D extends AlgoUnitVector {

	/**
	 * @param cons
	 *            construction
	 * @param line
	 *            line
	 * @param normalize
	 *            whether to return unit vector
	 */
	public AlgoUnitVector3D(Construction cons, GeoDirectionND line,
			boolean normalize) {
		super(cons, (GeoElement) line, normalize);
	}

	@Override
	protected GeoVectorND createVector(Construction cons1) {
		return new GeoVector3D(cons1);
	}

	@Override
	public final void compute() {

		Coords coords = ((GeoDirectionND) inputGeo).getDirectionInD3();
		length = coords.norm();
		if (!normalize) {
			((GeoVector3D) u).setCoords(coords);
		} else if (DoubleUtil.isZero(length)) {
			u.setUndefined();
		} else {
			((GeoVector3D) u).setCoords(coords.mul(1 / length));
		}
	}

	@Override
	protected GeoPointND getInputStartPoint() {

		if (inputGeo.isGeoLine()) {
			return ((GeoLineND) inputGeo).getStartPoint();
		}

		if (inputGeo.isGeoVector()) {
			return ((GeoVectorND) inputGeo).getStartPoint();
		}

		return null; // TODO start point for GeoDirectionND
	}

}
