package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoVector3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoUnitVector;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.MyMath;

public class AlgoUnitVectorPoint3D extends AlgoUnitVector {

	/**
	 * @param cons
	 *            construction
	 * @param point
	 *            point
	 * @param normalize
	 *            whether to return unit vector
	 */
	public AlgoUnitVectorPoint3D(Construction cons, GeoPoint3D point,
			boolean normalize) {
		super(cons, point, normalize);
	}

	@Override
	protected GeoVectorND createVector(Construction cons1) {
		return new GeoVector3D(cons1);
	}

	@Override
	public final void compute() {
		Coords coords = ((GeoPoint3D) inputGeo).getInhomCoordsInD3();
		length = MyMath.length(coords.getX(), coords.getY(), coords.getZ());
		if (!normalize) {
			((GeoVector3D) u).setCoords(coords);
		} else if (DoubleUtil.isZero(length)) {
			u.setUndefined();
		} else {
			Coords divided = coords.mul(1 / length);
			divided.setW(0);
			((GeoVector3D) u).setCoords(divided);
		}
	}

	@Override
	protected GeoPointND getInputStartPoint() {
		return null;
	}

}
