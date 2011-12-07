package geogebra.kernel.commands;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.MyError;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.geos.GeoNumeric;

/**
 *SetTooltipMode
 */
class CmdSetTooltipMode extends CmdScripting {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSetTooltipMode(Kernel kernel) {
		super(kernel);
	}

	final public void perform(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c);
			if (arg[1].isGeoNumeric()) {

				GeoElement geo = (GeoElement) arg[0];

				geo.setTooltipMode((int) ((GeoNumeric) arg[1]).getDouble());
				geo.updateRepaint();

				
				return;
			} else
				throw argErr(app, c.getName(), arg[1]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
