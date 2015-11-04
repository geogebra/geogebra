package org.geogebra.common.geogebra3D.kernel3D.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoDependentNumber;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.arithmetic.VectorNDValue;
import org.geogebra.common.kernel.commands.CmdCurveCartesian;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.MyError;

/*
 * Line[ <GeoPoint3D>, <GeoPoint3D> ] or CmdLine
 */
public class CmdSurfaceCartesian3D extends CmdCurveCartesian {

	public CmdSurfaceCartesian3D(Kernel kernel) {
		super(kernel);

	}

	public GeoElement[] process(Command c) throws MyError {

		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];

		switch (n) {
		case 7:
			GeoElement[] arg;
			// create local variables and resolve arguments
			// Surface[(1;a;b),a,0,pi,b,0,pi]
			arg = resArgsLocalNumVar(c, new int[] { 1, 4 }, new int[] { 2, 5 });
			if ((ok[0] = arg[0] instanceof VectorNDValue)

			&& (ok[1] = arg[1] instanceof GeoNumeric)
					&& (ok[2] = arg[2] instanceof GeoNumberValue)
					&& (ok[3] = arg[3] instanceof GeoNumberValue)
					&& (ok[4] = arg[4] instanceof GeoNumeric)
					&& (ok[5] = arg[5] instanceof GeoNumberValue)
					&& (ok[6] = arg[6] instanceof GeoNumberValue)) {
				NumberValue[] coords = new NumberValue[3];
				ExpressionNode exp = c.getArgument(0);
				for (int i = 0; i < 3; i++) {
					ExpressionNode cx = kernelA.getAlgebraProcessor()
							.computeCoord(exp, i);
					AlgoDependentNumber nx = new AlgoDependentNumber(cons, cx,
							false);
					coords[i] = nx.getNumber();
				}
				GeoElement[] ret = new GeoElement[1];
				ret[0] = kernelA.getManager3D().SurfaceCartesian3D(
						c.getLabel(), exp, coords[0], coords[1], coords[2],
						(GeoNumeric) arg[1], (GeoNumberValue) arg[2],
						(GeoNumberValue) arg[3], (GeoNumeric) arg[4],
						(GeoNumberValue) arg[5], (GeoNumberValue) arg[6]);
				return ret;
			}
			throw argErr(app, c.getName(), getBadArg(ok, arg));
		case 9:
			// create local variables and resolve arguments
			arg = resArgsLocalNumVar(c, new int[] { 3, 6 }, new int[] { 4, 7 });
			if ((ok[0] = arg[0] instanceof GeoNumberValue)
					&& (ok[1] = arg[1] instanceof GeoNumberValue)
					&& (ok[2] = arg[2] instanceof GeoNumberValue)
					&& (ok[3] = arg[3] instanceof GeoNumeric)
					&& (ok[4] = arg[4] instanceof GeoNumberValue)
					&& (ok[5] = arg[5] instanceof GeoNumberValue)
					&& (ok[6] = arg[6] instanceof GeoNumeric)
					&& (ok[7] = arg[7] instanceof GeoNumberValue)
					&& (ok[8] = arg[8] instanceof GeoNumberValue)) {
				GeoElement[] ret = new GeoElement[1];
				ret[0] = kernelA.getManager3D().SurfaceCartesian3D(
						c.getLabel(), null, (GeoNumberValue) arg[0],
						(GeoNumberValue) arg[1], (GeoNumberValue) arg[2],
						(GeoNumeric) arg[3], (GeoNumberValue) arg[4],
						(GeoNumberValue) arg[5], (GeoNumeric) arg[6],
						(GeoNumberValue) arg[7], (GeoNumberValue) arg[8]);
				return ret;
			}

			throw argErr(app, c.getName(), getBadArg(ok, arg));

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}
