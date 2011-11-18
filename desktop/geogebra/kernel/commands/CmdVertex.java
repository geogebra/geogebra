package geogebra.kernel.commands;

import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.geos.GeoConic;
import geogebra.kernel.geos.GeoElement;
import geogebra.kernel.geos.GeoImage;
import geogebra.kernel.geos.GeoPolyLineInterface;
import geogebra.kernel.geos.GeoText;
import geogebra.main.MyError;

/**
 * Vertex[ <GeoConic> ]
 */
class CmdVertex extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdVertex(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		// Vertex[ <GeoConic> ]
		case 1:
			arg = resArgs(c);
			if (ok[0] = (arg[0].isGeoConic()))
				return kernel.Vertex(c.getLabels(), (GeoConic) arg[0]);
			if (ok[0] = (arg[0] instanceof GeoPolyLineInterface))
				return kernel.Vertex(c.getLabels(), (GeoPolyLineInterface) arg[0]);
			else if (ok[0] = (arg[0].isNumberValue())) {
				GeoElement[] ret = { kernel.CornerOfDrawingPad(c.getLabel(),
						(NumberValue) arg[0], null) };
				return ret;
			} else
				throw argErr(app, c.getName(), arg[0]);

			// Corner[ <Image>, <number> ]
		case 2:
			arg = resArgs(c);
			if ((ok[0] = (arg[0] instanceof GeoPolyLineInterface))
					&& (ok[1] = (arg[1].isNumberValue()))) {
				GeoElement[] ret = { kernel.Vertex(c.getLabel(),
						(GeoPolyLineInterface) arg[0], (NumberValue) arg[1]) };
				return ret;
			} else if ((ok[0] = (arg[0].isGeoImage()))
					&& (ok[1] = (arg[1].isNumberValue()))) {
				GeoElement[] ret = { kernel.Corner(c.getLabel(),
						(GeoImage) arg[0], (NumberValue) arg[1]) };
				return ret;
			}
			// Michael Borcherds 2007-11-26 BEGIN Corner[] for textboxes
			// Corner[ <Text>, <number> ]
			else if ((ok[0] = (arg[0].isGeoText()))
					&& (ok[1] = (arg[1].isNumberValue()))) {
				GeoElement[] ret = { kernel.Corner(c.getLabel(),
						(GeoText) arg[0], (NumberValue) arg[1]) };
				return ret;
				// Michael Borcherds 2007-11-26 END
			} else if ((ok[0] = (arg[0].isNumberValue()))
					&& (ok[1] = (arg[1].isNumberValue()))) {
				GeoElement[] ret = { kernel.CornerOfDrawingPad(c.getLabel(),
						(NumberValue) arg[1], (NumberValue) arg[0]) };
				return ret;
				
			} else {
				if (!ok[0])
					throw argErr(app, c.getName(), arg[0]);
				else
					throw argErr(app, c.getName(), arg[1]);
			}

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
