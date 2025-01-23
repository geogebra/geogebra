package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.cas.AlgoTangentFunctionNumber;
import org.geogebra.common.kernel.geos.GeoCurveCartesian;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunctionable;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.implicit.GeoImplicit;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoCurveCartesianND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.MyError;

/**
 * Tangent[ &lt;GeoPoint&gt;, &lt;GeoConic&gt; ] Tangent[ &lt;GeoLine&gt;, &lt;GeoConic&gt;
 * ] Tangent[ &lt;NumberValue&gt;, &lt;GeoFunction&gt; ] Tangent[ &lt;GeoPoint&gt;,
 * &lt;GeoFunction&gt; ] Tangent[ &lt;GeoPoint&gt;, &lt;GeoCurveCartesian&gt; ]
 * Tangent[&lt;GeoPoint&gt;,&lt;GeoImplicitPoly&gt;] Tangent[ &lt;GeoLine&gt;,
 * &lt;GeoImplicitPoly&gt;]
 */
public class CmdTangent extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdTangent(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c);

			// tangents through point
			if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoConic()))) {
				return tangent(c.getLabels(), (GeoPointND) arg[0],
						(GeoConicND) arg[1]);
			} else if ((ok[0] = (arg[0].isGeoConic()))
					&& (ok[1] = (arg[1].isGeoPoint()))) {
				return tangent(c.getLabels(), (GeoPointND) arg[1],
						(GeoConicND) arg[0]);
			} else if ((ok[0] = (arg[0].isGeoLine()))
					&& (ok[1] = (arg[1].isGeoConic()))) {
				return tangent(c.getLabels(), (GeoLineND) arg[0],
						(GeoConicND) arg[1]);
			} else if ((ok[0] = (arg[0] instanceof GeoNumberValue))
					&& (ok[1] = (arg[1].isRealValuedFunction()))) {

				AlgoTangentFunctionNumber algo = new AlgoTangentFunctionNumber(
						cons, c.getLabel(), (GeoNumberValue) arg[0],
						(GeoFunctionable) arg[1]);
				GeoLine t = algo.getTangent();
				t.setToExplicitForm();
				t.update();

				GeoElement[] ret = { t };
				return ret;
			}

			// tangents of function at x = x(Point P)
			else if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isRealValuedFunction()))) {
				GeoElement[] ret = { getAlgoDispatcher().tangent(c.getLabel(),
						(GeoPointND) arg[0],
						(GeoFunctionable) arg[1]) };
				return ret;
			} else if ((ok[0] = (arg[0].isRealValuedFunction()))
					&& (ok[1] = (arg[1].isGeoPoint()))) {
				GeoElement[] ret = { getAlgoDispatcher().tangent(c.getLabel(),
						(GeoPointND) arg[1],
						(GeoFunctionable) arg[0]) };
				return ret;
			}
			// Victor Franco 11-02-2007: for curve's
			else if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoCurveCartesian()))) {

				GeoElement[] ret = { tangentToCurve(c.getLabel(),
						(GeoPointND) arg[0], (GeoCurveCartesianND) arg[1]) };

				return ret;
			}
			// Victor Franco 11-02-2007: end for curve's

			// For Spline

			else if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoImplicitCurve()))) {
				GeoElement[] ret = getAlgoDispatcher().tangent(c.getLabels(),
						(GeoPointND) arg[0], (GeoImplicit) arg[1]);
				return ret;
			} else if ((ok[1] = (arg[1].isGeoPoint()))
					&& (ok[0] = (arg[0].isGeoImplicitCurve()))) {
				GeoElement[] ret = getAlgoDispatcher().tangent(c.getLabels(),
						(GeoPointND) arg[1], (GeoImplicit) arg[0]);
				return ret;

			} else if ((ok[0] = (arg[0].isGeoLine()))
					&& (ok[1] = (arg[1].isGeoImplicitCurve()))) {
				GeoElement[] ret = getAlgoDispatcher().tangent(c.getLabels(),
						(GeoLineND) arg[0], (GeoImplicit) arg[1]);
				return ret;

			} else if ((ok[0] = (arg[0].isGeoConic()))
					&& (ok[1] = (arg[1].isGeoConic()))) {
				return tangent(c.getLabels(), (GeoConicND) arg[0],
						(GeoConicND) arg[1]);
			}

			// syntax error
			else {
				if (!ok[0]) {
					throw argErr(c, arg[0]);
				}
				throw argErr(c, arg[1]);
			}

		default:
			throw argNumErr(c);
		}
	}

	/**
	 * @param label
	 *            output label
	 * @param point
	 *            point
	 * @param curve
	 *            curve
	 * @return tangent
	 */
	protected GeoElement tangentToCurve(String label, GeoPointND point,
			GeoCurveCartesianND curve) {
		return kernel.tangent(label, point, (GeoCurveCartesian) curve);
	}

	/**
	 * 
	 * @param labels
	 *            labels
	 * @param a
	 *            point
	 * @param c
	 *            conic
	 * @return tangent point/conic
	 */
	protected GeoElement[] tangent(String[] labels, GeoPointND a,
			GeoConicND c) {
		return getAlgoDispatcher().tangent(labels, a, c);
	}

	/**
	 * @param labels
	 *            labels
	 * @param l
	 *            line
	 * @param c
	 *            conic
	 * @return tangent line/conic
	 */
	protected GeoElement[] tangent(String[] labels, GeoLineND l, GeoConicND c) {
		return getAlgoDispatcher().tangent(labels, l, c);
	}

	/**
	 * @param labels
	 *            labels
	 * @param c1
	 *            conic
	 * @param c2
	 *            conic
	 * @return tangent conic/conic
	 */
	protected GeoElement[] tangent(String[] labels, GeoConicND c1,
			GeoConicND c2) {
		return getAlgoDispatcher().commonTangents(labels, c1, c2);
	}

}
