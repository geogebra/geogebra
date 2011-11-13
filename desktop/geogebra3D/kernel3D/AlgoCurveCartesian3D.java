package geogebra3D.kernel3D;

import geogebra.kernel.AlgoCurveCartesian;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoCurveCartesian;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.arithmetic.Function;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.kernelND.GeoCurveCartesianND;


public class AlgoCurveCartesian3D extends AlgoCurveCartesian {

	public AlgoCurveCartesian3D(Construction cons, String label,
			NumberValue[] coords, GeoNumeric localVar, NumberValue from,
			NumberValue to) {
		super(cons, label, coords, localVar, from, to);
	}
	
	protected GeoCurveCartesianND createCurve(Construction cons, Function[] fun){
    	return new GeoCurveCartesian3D(cons, fun);
    }

}
