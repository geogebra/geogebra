package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.main.MyError;

/**
 * ContinuedFraction[number], based on FractionText[]
 */
public class CmdContinuedFraction extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdContinuedFraction(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c, info);
		boolean[] ok = new boolean[3];
		switch (n) {
		case 1:

			if (arg[0] instanceof GeoNumberValue) {
				GeoElement[] ret = { continuedFraction(c.getLabel(),
						(GeoNumberValue) arg[0], null, null) };
				return ret;
			}
			throw argErr(c, arg[0]);
		case 2:
			if ((ok[0] = arg[0] instanceof GeoNumberValue)
					&& (ok[1] = arg[1].isGeoBoolean())) {
				GeoElement[] ret = { continuedFraction(c.getLabel(),
						(GeoNumberValue) arg[0], null, (GeoBoolean) arg[1]) };
				return ret;
			}
			if ((ok[0] = arg[0] instanceof GeoNumberValue)
					&& (ok[1] = arg[1] instanceof GeoNumberValue)) {
				GeoElement[] ret = {
						continuedFraction(c.getLabel(), (GeoNumberValue) arg[0],
								(GeoNumberValue) arg[1], null) };
				return ret;
			}

			throw argErr(c, getBadArg(ok, arg));
		case 3:

			if ((ok[0] = arg[0] instanceof GeoNumberValue)
					&& (ok[1] = arg[1] instanceof GeoNumberValue)
					&& (ok[2] = arg[2].isGeoBoolean())) {
				GeoElement[] ret = {
						continuedFraction(c.getLabel(), (GeoNumberValue) arg[0],
								(GeoNumberValue) arg[1], (GeoBoolean) arg[2]) };
				return ret;
			}
			throw argErr(c, getBadArg(ok, arg));

		default:
			throw argNumErr(c);
		}
	}

	final private GeoText continuedFraction(String label, GeoNumberValue num,
			GeoNumberValue level, GeoBoolean shortHand) {
		AlgoContinuedFraction algo = new AlgoContinuedFraction(cons, num,
				level, shortHand);
		GeoText text = algo.getResult();
		text.setLabel(label);
		return text;
	}
}
