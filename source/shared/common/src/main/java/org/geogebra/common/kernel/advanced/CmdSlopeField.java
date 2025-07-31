package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.Evaluate2Var;
import org.geogebra.common.kernel.arithmetic.FunctionalNVar;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLocus;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.MyError;

/**
 * SlopeField[function]
 */
public class CmdSlopeField extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSlopeField(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;
		arg = resArgs(c, info);

		switch (n) {
		case 1:
			if (arg[0] instanceof Evaluate2Var) {
				GeoElement[] ret = {
						slopeField(c.getLabel(), (Evaluate2Var) arg[0], null,
								null, null, null, null, null) };
				return ret;
			}
			throw argErr(c, arg[0]);

		case 2:
			if ((ok[0] = arg[0] instanceof Evaluate2Var)
					&& (ok[1] = arg[1].isGeoNumeric())) {
				GeoElement[] ret = { slopeField(c.getLabel(),
						(Evaluate2Var) arg[0], (GeoNumeric) arg[1], null, null,
						null, null, null) };
				return ret;
			}
			throw argErr(c, getBadArg(ok, arg));

		case 3:
			if ((ok[0] = arg[0] instanceof Evaluate2Var)
					&& (ok[1] = arg[1].isGeoNumeric())
					&& (ok[2] = arg[2].isGeoNumeric())) {
				GeoElement[] ret = { slopeField(c.getLabel(),
						(Evaluate2Var) arg[0], (GeoNumeric) arg[1],
						(GeoNumeric) arg[2], null, null, null, null) };
				return ret;
			}
			throw argErr(c, getBadArg(ok, arg));

		case 7:
			if ((ok[0] = arg[0] instanceof Evaluate2Var)
					&& (ok[1] = arg[1].isGeoNumeric())
					&& (ok[2] = arg[2].isGeoNumeric())
					&& (ok[3] = arg[3].isGeoNumeric())
					&& (ok[4] = arg[4].isGeoNumeric())
					&& (ok[5] = arg[5].isGeoNumeric())
					&& (ok[6] = arg[6].isGeoNumeric())) {
				GeoElement[] ret = {
						slopeField(c.getLabel(), (FunctionalNVar) arg[0],
								(GeoNumeric) arg[1], (GeoNumeric) arg[2],
								(GeoNumeric) arg[3], (GeoNumeric) arg[4],
								(GeoNumeric) arg[5], (GeoNumeric) arg[6]) };
				return ret;
			}
			throw argErr(c, getBadArg(ok, arg));

		default:
			throw argNumErr(c);
		}
	}

	final private GeoLocus slopeField(String label, Evaluate2Var func,
			GeoNumeric n, GeoNumeric lengthRatio, GeoNumeric minX,
			GeoNumeric minY, GeoNumeric maxX, GeoNumeric maxY) {

		AlgoSlopeField algo = new AlgoSlopeField(cons, label, func, n,
				lengthRatio, minX, minY, maxX, maxY);
		return algo.getResult();
	}
}
