package org.geogebra.common.kernel.geos;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.geogebra3D.kernel3D.algos.ExtrudeConverter;
import org.geogebra.common.kernel.matrix.Coords;

public class PolyhedronNetConverter extends ExtrudeConverter {

	private double lengthDirection;

	@Override
	public double translationToValue(Coords direction, Coords rwTransVec, double startValue,
			EuclidianView view) {
		return direction.dotproduct3(rwTransVec) / lengthDirection + startValue;
	}

	@Override
	public void record(ChangeableParent parent, Coords startPoint) {
		lengthDirection = parent.getDirection().calcNorm();
	}

}
