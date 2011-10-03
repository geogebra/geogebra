package geogebra.kernel.commands;

import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.main.MyError;

/*
 * Column[ <GeoElement> ]
 */
public class CmdColumn extends CommandProcessor {

	public CmdColumn(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);			
			if (arg[0].getLabel() != null && GeoElement.isSpreadsheetLabel(arg[0].getLabel())) {

				GeoElement[] ret = { kernel.Column(c.getLabel(),
						arg[0]) };
				return ret;
			}
			else
			{
				throw argErr(app, c.getName(), arg[0]);
			}



		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}
