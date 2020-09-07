package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Manager3DInterface;
import org.geogebra.common.kernel.Path;
import org.geogebra.common.kernel.algos.AlgoDependentNumber;
import org.geogebra.common.kernel.algos.AlgoSurfaceCartesianND;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.VectorArithmetic;
import org.geogebra.common.kernel.arithmetic.VectorNDValue;
import org.geogebra.common.kernel.geos.GeoCurveCartesian;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoly;
import org.geogebra.common.kernel.geos.ParametricCurve;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.Parametrizable;
import org.geogebra.common.main.MyError;

/**
 * Surface[expression, expression, expression, u, umin, umax, v, vmin, vmax]
 * Surface[ point expression, u, umin, umax, v, vmin, vmax]
 */
public class CmdSurfaceCartesian extends CmdCurveCartesian {
	/**
	 * @param kernel
	 *            Kernel
	 */
	public CmdSurfaceCartesian(Kernel kernel) {
		super(kernel);

	}

	@Override
	public GeoElement[] process(Command c) throws MyError {

		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		Manager3DInterface manager3D = kernel.getManager3D();
		if (manager3D == null && n != 7) {
			throw argNumErr(c);
		}
		switch (n) {
		case 2:
			arg = resArgs(c);
			if ((ok[0] = (arg[0] instanceof GeoFunction
					|| arg[0] instanceof GeoCurveCartesian
					|| arg[0] instanceof GeoPoly
					|| arg[0] instanceof GeoConicND))
					&& (ok[1] = arg[1] instanceof GeoNumberValue)) {
				GeoElement[] ret = new GeoElement[1];

				ret[0] = manager3D.surfaceOfRevolution(
						(Path) arg[0], (GeoNumberValue) arg[1],
						null);
				ret[0].setLabel(c.getLabel());
				return ret;
			}
			throw argErr(c, getBadArg(ok, arg));
		case 3:
			arg = resArgs(c);
			if ((ok[0] = (arg[0] instanceof ParametricCurve))
					&& (ok[1] = arg[1] instanceof GeoNumberValue)
					&& (ok[2] = arg[2] instanceof GeoLineND)) {
				GeoElement[] ret = new GeoElement[1];

				ret[0] = manager3D.surfaceOfRevolution(
						(ParametricCurve) arg[0],
						(GeoNumberValue) arg[1], (GeoLineND) arg[2]);
				ret[0].setLabel(c.getLabel());
				return ret;
			}
			if ((ok[0] = (arg[0] instanceof Parametrizable))
					&& (ok[1] = arg[1] instanceof GeoNumberValue)
					&& (ok[2] = arg[2] instanceof GeoLineND)) {
				GeoElement[] ret = new GeoElement[1];

				ret[0] = manager3D.surfaceOfRevolution(
						(Parametrizable) arg[0], (GeoNumberValue) arg[1],
						(GeoLineND) arg[2]);
				ret[0].setLabel(c.getLabel());
				return ret;
			}
			throw argErr(c, getBadArg(ok, arg));
		case 7:
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
				int dim = ((VectorNDValue) arg[0]).getDimension();
				GeoNumberValue[] coords = new GeoNumberValue[dim];
				ExpressionNode exp = c.getArgument(0);
				for (int i = 0; i < dim; i++) {
					kernel.getAlgebraProcessor();
					ExpressionNode cx = VectorArithmetic
							.computeCoord(exp, i);
					AlgoDependentNumber nx = new AlgoDependentNumber(cons, cx,
							false);
					cons.removeFromConstructionList(nx);
					coords[i] = nx.getNumber();
				}
				GeoElement[] ret = new GeoElement[1];
				ret[0] = surfaceCartesian3D(c.getLabel(),
						exp, coords,
						(GeoNumeric) arg[1], (GeoNumberValue) arg[2],
						(GeoNumberValue) arg[3], (GeoNumeric) arg[4],
						(GeoNumberValue) arg[5], (GeoNumberValue) arg[6]);
				return ret;
			}
			throw argErr(c, getBadArg(ok, arg));
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
				GeoNumberValue[] coords = new GeoNumberValue[] {
						(GeoNumberValue) arg[0], (GeoNumberValue) arg[1],
						(GeoNumberValue) arg[2] };
				ret[0] = surfaceCartesian3D(c.getLabel(),
						null, coords, (GeoNumeric) arg[3],
						(GeoNumberValue) arg[4], (GeoNumberValue) arg[5],
						(GeoNumeric) arg[6], (GeoNumberValue) arg[7],
						(GeoNumberValue) arg[8]);
				return ret;
			}

			throw argErr(c, getBadArg(ok, arg));

		default:
			throw argNumErr(c);
		}
	}

	private GeoElement surfaceCartesian3D(String label, ExpressionNode point,
			GeoNumberValue[] coords, GeoNumeric uVar, GeoNumberValue uFrom, GeoNumberValue uTo,
			GeoNumeric vVar, GeoNumberValue vFrom, GeoNumberValue vTo) {
		AlgoSurfaceCartesianND algo = new AlgoSurfaceCartesianND(cons,
				point, coords,
				new GeoNumeric[] { uVar, vVar },
				new GeoNumberValue[] { uFrom, vFrom },
				new GeoNumberValue[] { uTo, vTo });
		algo.getSurface().setLabel(label);
		return algo.getSurface();
	}

	@Override
	protected String[] replaceXYarguments(ExpressionNode[] arg) {
		String[] newXYZ = new String[3];

		if (arg.length == 9 || arg.length == 7) {
			int offset = arg.length - 6;
			// we have to replace "x", "y"
			newXYZ[0] = checkReplaced(arg, offset, "x", "u", offset);
			if (newXYZ[0] == null) {
				newXYZ[0] = checkReplaced(arg, offset + 3, "x", "u", offset);
			}
			newXYZ[1] = checkReplaced(arg, offset + 3, "y", "v", offset);
			if (newXYZ[1] == null) {
				newXYZ[1] = checkReplaced(arg, offset, "y", "v", offset);
			}
		}

		return newXYZ;
	}

}
