package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoVector3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.algos.AlgoUnitVector;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;

public class AlgoUnitVector3D extends AlgoUnitVector {


	public AlgoUnitVector3D(Construction cons, GeoDirectionND line,
			boolean normalize) {
		super(cons, (GeoElement) line, normalize);
	}

	@Override
	protected GeoVectorND createVector(Construction cons) {
		GeoVector3D ret = new GeoVector3D(cons);
		return ret;
	}

	@Override
	public final void compute() {

		Coords coords = ((GeoDirectionND) inputGeo).getDirectionInD3();
		length = coords.norm();
		if (!normalize) {
			((GeoVector3D) u).setCoords(coords);
		} else if (Kernel.isZero(length)) {
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
