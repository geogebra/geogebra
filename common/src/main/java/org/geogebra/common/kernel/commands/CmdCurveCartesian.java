package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoCurveCartesian;
import org.geogebra.common.kernel.algos.AlgoDependentNumber;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.VectorArithmetic;
import org.geogebra.common.kernel.arithmetic.VectorNDValue;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.MyError;

/**
 * Curve[ &lt;x-coord expression>, &lt;y-coord expression>, &lt;number-var>,
 * &lt;from>, &lt;to> ]
 */
public class CmdCurveCartesian extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdCurveCartesian(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok;
		GeoElement[] arg;

		switch (n) {
		// Curve[ <x-coord expression>, <y-coord expression>, <number-var>,
		// <from>, <to> ]
		// Note: x and y coords are numbers dependent on number-var
		// Curve[t*(1-t)*A+t*t*B+(1-t)*(1-t)*C,t,0,1]
		case 4:
			ok = new boolean[n];
			arg = new GeoElement[n];
			arg[2] = resArgSilent(c, 2, new EvalInfo(false));
			arg[3] = resArgSilent(c, 3, new EvalInfo(false));
			if ((ok[2] = arg[2] instanceof GeoNumberValue)
					&& (ok[3] = arg[3] instanceof GeoNumberValue)) {
				arg = resArgsLocalNumVar(c, 1, 2, 3);
				if ((ok[0] = arg[0] instanceof VectorNDValue)
						&& (ok[1] = arg[1].isGeoNumeric())
						&& (ok[2] = arg[2] instanceof GeoNumberValue)
						&& (ok[3] = arg[3] instanceof GeoNumberValue)) {
					ExpressionNode exp = kernel
							.convertNumberValueToExpressionNode(arg[0]);
					int dim = ((VectorNDValue) arg[0]).getDimension();
					GeoNumberValue[] coords = new GeoNumberValue[dim];
					for (int i = 0; i < dim; i++) {
						ExpressionNode cx = VectorArithmetic.computeCoord(exp, i);
						AlgoDependentNumber nx = new AlgoDependentNumber(cons, cx,
								false);
						cons.removeFromConstructionList(nx);
						coords[i] = nx.getNumber();
					}

					AlgoCurveCartesian algo = getCurveAlgo(exp, coords, arg);
					algo.getCurve().setLabel(c.getLabel());
					GeoElement[] ret = {algo.getCurve()};

					return ret;
				}
			}
			throw argErr(getBadArg(ok, arg), c);
		case 5:
			// create local variable at position 2 and resolve arguments
			ok = new boolean[n];
			arg = resArgsLocalNumVar(c, 2, 3, 4);
			if ((ok[0] = arg[0] instanceof GeoNumberValue)
					&& (ok[1] = arg[1] instanceof GeoNumberValue)
					&& (ok[2] = arg[2].isGeoNumeric())
					&& (ok[3] = arg[3] instanceof GeoNumberValue)
					&& (ok[4] = arg[4] instanceof GeoNumberValue)) {
				AlgoCurveCartesian algo = new AlgoCurveCartesian(cons, null,
						new GeoNumberValue[] { (GeoNumberValue) arg[0],
								(GeoNumberValue) arg[1] },
						(GeoNumeric) arg[2], (GeoNumberValue) arg[3],
						(GeoNumberValue) arg[4]);
				algo.getCurve().setLabel(c.getLabel());
				GeoElement[] ret = { algo.getCurve() };

				return ret;
			}

			throw argErr(getBadArg(ok, arg), c);
		default:
			throw argNumErr(c);
		}
	}

	/**
	 * @param point
	 *            point expression
	 * @param coords
	 *            coordinates
	 * @param arg
	 *            arguments (contains variables)
	 * @return curve algo
	 */
	protected AlgoCurveCartesian getCurveAlgo(ExpressionNode point,
			GeoNumberValue[] coords, GeoElement[] arg) {
		return new AlgoCurveCartesian(cons, point, coords, (GeoNumeric) arg[1],
				(GeoNumberValue) arg[2], (GeoNumberValue) arg[3]);
	}

	@Override
	protected String[] replaceXYarguments(ExpressionNode[] arg) {
		String[] newXYZ = new String[3];

		if (arg.length == 4 || arg.length == 5 || arg.length == 6) {
			int offset = arg.length - 3;
			// we have to replace "x"
			newXYZ[0] = checkReplaced(arg, offset, "x", "u", offset);
			if (newXYZ[0] == null) {
				newXYZ[0] = checkReplaced(arg, offset, "y", "v", offset);
			}

		}

		return newXYZ;
	}
}