package geogebra.common.kernel;

import geogebra.common.kernel.cas.AlgoDependentCasCell;
import geogebra.common.kernel.cas.AlgoTangentCurve;
import geogebra.common.kernel.cas.AlgoTangentFunctionPoint;
import geogebra.common.kernel.geos.GeoCasCell;
import geogebra.common.kernel.geos.GeoCurveCartesian;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoPoint;

public class KernelCAS {

	/**
	 * GeoCasCell dependent on other variables, e.g. m := c + 3
	 * 
	 * @return resulting casCell created using geoCasCell.copy().
	 */
	final public static GeoCasCell DependentCasCell(GeoCasCell geoCasCell) {	
		AlgoDependentCasCell algo = new AlgoDependentCasCell(geoCasCell);
		return algo.getCasCell();
	}

	public static GeoLine Tangent(Construction cons, String label, GeoPoint P,
			GeoFunction f) {
		AlgoTangentFunctionPoint algo = new AlgoTangentFunctionPoint(cons,
				label, P, f);
		GeoLine t = algo.getTangent();
		t.setToExplicit();
		t.update();
		return t;
	}

	public static GeoLine Tangent(Construction cons, String label, GeoPoint P,
			GeoCurveCartesian f) {
		AlgoTangentCurve algo = new AlgoTangentCurve(cons, label, P, f);
		GeoLine t = algo.getTangent();
		t.setToExplicit();
		t.update();
		return t;
	}


}
