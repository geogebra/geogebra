package geogebra.kernel.commands;

import geogebra.kernel.GeoBoolean;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.main.MyError;

/*
 * Name[ <GeoElement> ]
 */
public class CmdText extends CommandProcessor {

	public CmdText(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 1:

			arg = resArgs(c);	
			GeoElement[] ret = { kernel.Text(c.getLabel(),
					arg[0]) };
			return ret;



		case 2:

			arg = resArgs(c);	
			if (arg[1].isGeoBoolean()) {
				GeoElement[] ret2 = { kernel.Text(c.getLabel(),
						arg[0], (GeoBoolean)arg[1]) };
				return ret2;
			}
			else if (arg[1].isGeoPoint()) {
				GeoElement[] ret2 = { kernel.Text(c.getLabel(),
						arg[0], (GeoPoint)arg[1]) };
				return ret2;
			}
			else
				throw argErr(app, c.getName(), arg[1]);     

		case 3:
			boolean ok;
			arg = resArgs(c);	
			if (ok = arg[1].isGeoPoint() && arg[2].isGeoBoolean()) {
				GeoElement[] ret2 = { kernel.Text(c.getLabel(),
						arg[0], (GeoPoint)arg[1], (GeoBoolean)arg[2]) };
				return ret2;
			}
			else
				throw argErr(app, c.getName(), ok ? arg[2] : arg[1]);     

		case 4:
			boolean ok1 = false;
			arg = resArgs(c);	
			if ((ok = arg[1].isGeoPoint()) && (ok1 = arg[2].isGeoBoolean()) && arg[3].isGeoBoolean()) {
				GeoElement[] ret2 = { kernel.Text(c.getLabel(),
						arg[0], (GeoPoint)arg[1], (GeoBoolean)arg[2], (GeoBoolean)arg[3]) };
				return ret2;
			}
			else
				throw argErr(app, c.getName(), ok ? (ok1 ? arg[3] : arg[2]) : arg[1]);     

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}
