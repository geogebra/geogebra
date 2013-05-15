package geogebra.common.kernel;

import geogebra.common.kernel.cas.AlgoDependentCasCell;
import geogebra.common.kernel.cas.AlgoTangentCurve;
import geogebra.common.kernel.cas.AlgoTangentFunctionPoint;
import geogebra.common.kernel.cas.AlgoTangentList;
import geogebra.common.kernel.geos.GeoCasCell;
import geogebra.common.kernel.geos.GeoCurveCartesian;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoPoint;

/**
 * Algo dispatcher for CAS algos
 *
 */
public class KernelCAS {

	/**
	 * GeoCasCell dependent on other variables, e.g. m := c + 3
	 * @param geoCasCell the dependent cell
	 * 
	 * @return resulting casCell created using geoCasCell.copy().
	 */
	final public static GeoCasCell DependentCasCell(GeoCasCell geoCasCell) {	
		AlgoDependentCasCell algo = new AlgoDependentCasCell(geoCasCell);
		return algo.getCasCell();
	}

	/**
	 * @param cons construction
	 * @param label label for output
	 * @param P point
	 * @param f function
	 * @return tangent to function through point
	 */
	public static GeoLine Tangent(Construction cons, String label, GeoPoint P,
			GeoFunction f) {
		AlgoTangentFunctionPoint algo = new AlgoTangentFunctionPoint(cons,
				label, P, f);
		GeoLine t = algo.getTangent();
		t.setToExplicit();
		t.update();
		return t;
	}
	/**
	 * @param cons construction
	 * @param label label for output
	 * @param P point
	 * @param f curve
	 * @return tangent to curve through point
	 */
	public static GeoLine Tangent(Construction cons, String label, GeoPoint P,
			GeoCurveCartesian f) {
		AlgoTangentCurve algo = new AlgoTangentCurve(cons, label, P, f);
		GeoLine t = algo.getTangent();
		t.setToExplicit();
		t.update();
		return t;
	}
	
	/**
	 * @param cons construction
	 * @param label label for output
	 * @param P point
	 * @param list - list of functions
	 * @return tangent to curve through point
	 */
	public static GeoLine Tangent(Construction cons, String label, GeoPoint P,
			GeoList list) {
		AlgoTangentList algo = new AlgoTangentList(cons, label, P, list);
		GeoLine t = algo.getTangent();
		t.setToExplicit();
		t.update();
		return t;
	}

}
