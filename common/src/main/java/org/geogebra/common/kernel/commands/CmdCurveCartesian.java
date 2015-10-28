package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoCurveCartesian;
import org.geogebra.common.kernel.algos.AlgoDependentNumber;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.arithmetic.VectorNDValue;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.MyError;

/**
 * Curve[ <x-coord expression>, <y-coord expression>, <number-var>, <from>, <to>
 * ]
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
		boolean[] ok = new boolean[n];

		switch (n) {
		// Curve[ <x-coord expression>, <y-coord expression>, <number-var>,
		// <from>, <to> ]
		// Note: x and y coords are numbers dependent on number-var
		// Curve[t*(1-t)*A+t*t*B+(1-t)*(1-t)*C,t,0,1]
		case 4:
			GeoElement[] arg = resArgsLocalNumVar(c, 1, 2);
			if ((ok[0] = arg[0] instanceof VectorNDValue)
					&& (ok[1] = arg[1].isGeoNumeric())
					&& (ok[2] = arg[2] instanceof GeoNumberValue)
					&& (ok[3] = arg[3] instanceof GeoNumberValue)) {
				ExpressionNode exp = c.getArgument(0);
				int dim = ((VectorNDValue)arg[0]).getDimension();
				NumberValue[] coords = new NumberValue[dim];
				for (int i = 0; i < dim; i++) {
					ExpressionNode cx = kernelA.getAlgebraProcessor()
							.computeCoord(exp, i);
					AlgoDependentNumber nx = new AlgoDependentNumber(cons, cx,
						false);
					coords[i] = nx.getNumber();
				}

				AlgoCurveCartesian algo = getCurveAlgo(exp, coords, arg);
				algo.getCurve().setLabel(c.getLabel());
				GeoElement[] ret = { algo.getCurve() };

				return ret;
			}
		case 5:
			// create local variable at position 2 and resolve arguments
			arg = resArgsLocalNumVar(c, 2, 3);

			if ((ok[0] = arg[0] instanceof GeoNumberValue)
					&& (ok[1] = arg[1] instanceof GeoNumberValue)
					&& (ok[2] = arg[2].isGeoNumeric())
					&& (ok[3] = arg[3] instanceof GeoNumberValue)
					&& (ok[4] = arg[4] instanceof GeoNumberValue)) {

				// make sure Curve[i,i,i,i,i] gives an error
				checkDependency(arg, c.getName(), 3, 2);
				checkDependency(arg, c.getName(), 4, 2);

				AlgoCurveCartesian algo = new AlgoCurveCartesian(cons, null,
						new NumberValue[] {
								(GeoNumberValue) arg[0],
								(GeoNumberValue) arg[1] }, (GeoNumeric) arg[2],
						(GeoNumberValue) arg[3], (GeoNumberValue) arg[4]);
				algo.getCurve().setLabel(c.getLabel());
				GeoElement[] ret = { algo.getCurve() };

				return ret;
			}
			for (int i = 0; i < n; i++) {
				if (!ok[i])
					throw argErr(app, c.getName(), arg[i]);
			}

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

	protected AlgoCurveCartesian getCurveAlgo(ExpressionNode point,
			NumberValue[] coords,
			GeoElement[] arg) {
		return new AlgoCurveCartesian(cons, point, coords, (GeoNumeric) arg[1],
				(GeoNumberValue) arg[2], (GeoNumberValue) arg[3]);
	}
}