package geogebra.kernel.commands;


import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoLine;
import geogebra.kernel.GeoVector;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.main.MyError;


/**
 * OrthogonalVector[ <GeoLine> ] OrthogonalVector[ <GeoVector> ]
 */
public class CmdOrthogonalVector extends CommandProcessor {

		/**
		* Create new command processor
		* @param kernel kernel
		*/
		public CmdOrthogonalVector(Kernel kernel) {
			super(kernel);
		}

		public  GeoElement[] process(Command c) throws MyError {
			int n = c.getArgumentNumber();
			boolean[] ok = new boolean[n];
			GeoElement[] arg;

			switch (n) {
			case 1 :
				arg = resArgs(c);
				if (ok[0] = (arg[0] .isGeoLine())) {
					GeoElement[] ret =
					{
							kernel.OrthogonalVector(
									c.getLabel(),
									(GeoLine) arg[0])};
					return ret;
				} else if (ok[0] = (arg[0] .isGeoVector())) {
					GeoElement[] ret =
					{
							kernel.OrthogonalVector(
									c.getLabel(),
									(GeoVector) arg[0])};
					return ret;
				} else {
					if (!ok[0])
						throw argErr(app, "OrthogonalVector", arg[0]);
				}

			default :
				throw argNumErr(app, "OrthogonalVector", n);
			}
		}
	}


