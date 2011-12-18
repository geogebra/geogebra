package geogebra.common.kernel.commands;


import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoVector;
import geogebra.common.main.MyError;
import geogebra.common.kernel.AbstractKernel;


/**
 * OrthogonalVector[ <GeoLine> ] OrthogonalVector[ <GeoVector> ]
 */
public class CmdOrthogonalVector extends CommandProcessor {

		/**
		* Create new command processor
		* @param AbstractKernel kernel
		*/
		public CmdOrthogonalVector(AbstractKernel kernel) {
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
							kernelA.OrthogonalVector(
									c.getLabel(),
									(GeoLine) arg[0])};
					return ret;
				} else if (ok[0] = (arg[0] .isGeoVector())) {
					GeoElement[] ret =
					{
							kernelA.OrthogonalVector(
									c.getLabel(),
									(GeoVector) arg[0])};
					return ret;
				} else {
					if (!ok[0])
						throw argErr(app, c.getName(), arg[0]);
				}

			default :
				throw argNumErr(app, c.getName(), n);
			}
		}
	}


