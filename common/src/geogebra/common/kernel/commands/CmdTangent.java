package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.cas.AlgoTangentFunctionNumber;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoCurveCartesian;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunctionable;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.implicit.GeoImplicitPoly;
import geogebra.common.main.MyError;

/**
 * Tangent[ <GeoPoint>, <GeoConic> ] Tangent[ <GeoLine>, <GeoConic> ] Tangent[
 * <NumberValue>, <GeoFunction> ] Tangent[ <GeoPoint>, <GeoFunction> ] Tangent[
 * <GeoPoint>, <GeoCurveCartesian> ] Tangent[<GeoPoint>,<GeoImplicitPoly>]
 * Tangent[ <GeoLine>, <GeoImplicitPoly>]
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
	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c);

			// tangents through point
			if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoConic())))
				return getAlgoDispatcher().Tangent(c.getLabels(), (GeoPoint) arg[0],
						(GeoConic) arg[1]);
			else if ((ok[0] = (arg[0].isGeoConic()))
					&& (ok[1] = (arg[1].isGeoPoint())))
				return getAlgoDispatcher().Tangent(c.getLabels(), (GeoPoint) arg[1],
						(GeoConic) arg[0]);
			else if ((ok[0] = (arg[0].isGeoLine()))
					&& (ok[1] = (arg[1].isGeoConic())))
				return getAlgoDispatcher().Tangent(c.getLabels(), (GeoLine) arg[0],
						(GeoConic) arg[1]);
			else if ((ok[0] = (arg[0].isNumberValue()))
					&& (ok[1] = (arg[1].isGeoFunctionable()))) {
				
				AlgoTangentFunctionNumber algo = new AlgoTangentFunctionNumber(cons,
						c.getLabel(),
						(NumberValue) arg[0], ((GeoFunctionable) arg[1])
								.getGeoFunction());
				GeoLine t = algo.getTangent();
				t.setToExplicit();
				t.update();

				GeoElement[] ret = {t};
				return ret;
			}

			// tangents of function at x = x(Point P)
			else if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoFunctionable()))) {
				GeoElement[] ret = { getAlgoDispatcher().Tangent(c.getLabel(),
						(GeoPoint) arg[0], ((GeoFunctionable) arg[1])
								.getGeoFunction()) };
				return ret;
			} else if ((ok[0] = (arg[0].isGeoFunctionable()))
					&& (ok[1] = (arg[1].isGeoPoint()))) {
				GeoElement[] ret = { getAlgoDispatcher().Tangent(c.getLabel(),
						(GeoPoint) arg[1], ((GeoFunctionable) arg[0])
								.getGeoFunction()) };
				return ret;
			}
			// Victor Franco 11-02-2007: for curve's
			else if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoCurveCartesian()))) {

				GeoElement[] ret = { kernelA.Tangent(c.getLabel(),
						(GeoPoint) arg[0], (GeoCurveCartesian) arg[1]) };
				return ret;
			}
			// Victor Franco 11-02-2007: end for curve's
			
			// For Spline
			else if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoList())) && areCurve((GeoList)arg[1])) {

				GeoElement[] ret = { kernelA.Tangent(c.getLabel(),
						(GeoPoint) arg[0], (GeoList) arg[1]) };
				return ret;
			}
			
			else if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoImplicitPoly()))) {
				GeoElement[] ret = getAlgoDispatcher().Tangent(c.getLabels(),
						(GeoPoint) arg[0], (GeoImplicitPoly) arg[1]);
				return ret;
			} else if ((ok[1] = (arg[1].isGeoPoint()))
					&& (ok[0] = (arg[0].isGeoImplicitPoly()))) {
				GeoElement[] ret = getAlgoDispatcher().Tangent(c.getLabels(),
						(GeoPoint) arg[1], (GeoImplicitPoly) arg[0]);
				return ret;
			} else if ((ok[0] = (arg[0].isGeoLine()))
					&& (ok[1] = (arg[1].isGeoImplicitPoly()))) {
				GeoElement[] ret = getAlgoDispatcher().Tangent(c.getLabels(),
						(GeoLine) arg[0], (GeoImplicitPoly) arg[1]);
				return ret;
			} else if ((ok[0] = (arg[0].isGeoConic()))
					&& (ok[1] = (arg[1].isGeoConic()))) {
				return getAlgoDispatcher().CommonTangents(c.getLabels(), (GeoConic) arg[0], (GeoConic) arg[1]);
			}

			// syntax error
			else {
				if (!ok[0])
					throw argErr(app, c.getName(), arg[0]);
				throw argErr(app, c.getName(), arg[1]);
			}

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

	private static boolean areCurve(GeoList geoList) {
		for (int i=0;i<geoList.size();i++){
			if(!geoList.get(i).isGeoCurveCartesian()){
				return false;
			}
		}
		return true;
	}
}
