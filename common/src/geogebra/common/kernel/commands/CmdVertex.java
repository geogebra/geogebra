package geogebra.common.kernel.commands;

import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoImage;
import geogebra.common.kernel.geos.GeoPolyLineInterface;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.main.MyError;
import geogebra.common.kernel.Kernel;;

/**
 * Vertex[ <GeoConic> ]
 */
public class CmdVertex extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdVertex(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		// Vertex[ <GeoConic> ]
		case 1:
			arg = resArgs(c);
			if (ok[0] = (arg[0].isGeoConic()))
				return kernelA.Vertex(c.getLabels(), (GeoConic) arg[0]);
			if (ok[0] = (arg[0] instanceof GeoPolyLineInterface))
				return kernelA.Vertex(c.getLabels(), (GeoPolyLineInterface) arg[0]);
			else if (ok[0] = (arg[0].isNumberValue())) {
				GeoElement[] ret = { kernelA.CornerOfDrawingPad(c.getLabel(),
						(NumberValue) arg[0], null) };
				return ret;
			} else
				throw argErr(app, c.getName(), arg[0]);

			// Corner[ <Image>, <number> ]
		case 2:
			arg = resArgs(c);
			if ((ok[0] = (arg[0] instanceof GeoPolyLineInterface))
					&& (ok[1] = (arg[1].isNumberValue()))) {
				GeoElement[] ret = { kernelA.Vertex(c.getLabel(),
						(GeoPolyLineInterface) arg[0], (NumberValue) arg[1]) };
				return ret;
			} else if ((ok[0] = (arg[0].isGeoImage()))
					&& (ok[1] = (arg[1].isNumberValue()))) {
				GeoElement[] ret = { kernelA.Corner(c.getLabel(),
						(GeoImage) arg[0], (NumberValue) arg[1]) };
				return ret;
			}
			// Michael Borcherds 2007-11-26 BEGIN Corner[] for textboxes
			// Corner[ <Text>, <number> ]
			else if ((ok[0] = (arg[0].isGeoText()))
					&& (ok[1] = (arg[1].isNumberValue()))) {
				GeoElement[] ret = { kernelA.Corner(c.getLabel(),
						(GeoText) arg[0], (NumberValue) arg[1]) };
				return ret;
				// Michael Borcherds 2007-11-26 END
			} else if ((ok[0] = (arg[0].isNumberValue()))
					&& (ok[1] = (arg[1].isNumberValue()))) {
				GeoElement[] ret = { kernelA.CornerOfDrawingPad(c.getLabel(),
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
