package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoCurveCartesian3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoCurveCartesian;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoCurveCartesianND;

public class AlgoCurveCartesian3D extends AlgoCurveCartesian {

	public AlgoCurveCartesian3D(Construction cons, ExpressionNode point,
			GeoNumberValue[] coords, GeoNumeric localVar, GeoNumberValue from,
			GeoNumberValue to) {
		super(cons, point, coords, localVar, from, to);
	}

	@Override
	protected GeoCurveCartesianND createCurve(Construction cons1, Function[] fun,
			ExpressionNode point) {
		return new GeoCurveCartesian3D(cons1, fun, point);
	}

}
