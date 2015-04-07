package org.geogebra.common.geogebra3D.kernel3D.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CmdFunction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.MyError;

/**
 * Command for 2 var functions
 */
public class CmdFunction2Var extends CmdFunction {

	/**
	 * @param kernel
	 *            kernel
	 */
	public CmdFunction2Var(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {

		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];

		GeoElement[] arg;

		switch (n) {
		case 7:
			// create local variable at position 3 and resolve arguments
			arg = resArgsLocalNumVar(c, new int[] { 1, 4 }, new int[] { 2, 5 });

			if ((ok[0] = arg[0] instanceof GeoNumberValue) // function
					&& (ok[1] = arg[1].isGeoNumeric()) // first var
					&& (ok[2] = arg[2] instanceof GeoNumberValue) // from
					&& (ok[3] = arg[3] instanceof GeoNumberValue) // to
					&& (ok[4] = arg[4].isGeoNumeric()) // second var
					&& (ok[5] = arg[5] instanceof GeoNumberValue) // from
					&& (ok[6] = arg[6] instanceof GeoNumberValue) // to

			) {
				GeoElement[] ret = { kernelA.getManager3D().Function2Var(
						c.getLabel(), (GeoNumberValue) arg[0],
						(GeoNumeric) arg[1], (GeoNumberValue) arg[2],
						(GeoNumberValue) arg[3], (GeoNumeric) arg[4],
						(GeoNumberValue) arg[5], (GeoNumberValue) arg[6]) };
				return ret;
			} /*
			 * else if ( (ok[0] = (arg[0] instanceof GeoFunctionNVar))
			 * //function && (ok[1] = arg[1].isGeoNumeric()) //first var &&
			 * (ok[2] = arg[2].isNumberValue()) //from && (ok[3] =
			 * arg[3].isNumberValue()) //to && (ok[4] = arg[4].isGeoNumeric())
			 * //second var && (ok[5] = arg[5].isNumberValue()) //from && (ok[6]
			 * = arg[6].isNumberValue()) //to
			 * 
			 * ) { GeoElement[] ret = { kernel3D.Function2Var( c.getLabel(),
			 * (GeoFunctionNVar) arg[0], (NumberValue) arg[2], (NumberValue)
			 * arg[3], (NumberValue) arg[5], (NumberValue) arg[6] ) }; return
			 * ret; }
			 */

			throw argErr(app, c.getName(), getBadArg(ok, arg));

		case 5:
			arg = resArgs(c);
			if ((ok[0] = (arg[0] instanceof GeoFunctionNVar)) // function
					&& (ok[1] = arg[1] instanceof GeoNumberValue) // x from
					&& (ok[2] = arg[2] instanceof GeoNumberValue) // x to
					&& (ok[3] = arg[3] instanceof GeoNumberValue) // y from
					&& (ok[4] = arg[4] instanceof GeoNumberValue) // y to

			) {
				GeoElement[] ret = { kernelA.getManager3D().Function2Var(
						c.getLabel(), (GeoFunctionNVar) arg[0],
						(GeoNumberValue) arg[1], (GeoNumberValue) arg[2],
						(GeoNumberValue) arg[3], (GeoNumberValue) arg[4]) };
				return ret;
			}

			throw argErr(app, c.getName(), getBadArg(ok, arg));
		}

		return super.process(c);
	}

}
