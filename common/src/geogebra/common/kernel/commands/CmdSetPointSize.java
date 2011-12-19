package geogebra.common.kernel.commands;

import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.common.main.MyError;
import geogebra.common.kernel.AbstractKernel;

/**
 *SetPointSize
 */
public class CmdSetPointSize extends CmdScripting {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSetPointSize(AbstractKernel kernel) {
		super(kernel);
	}

	final public void perform(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		boolean ok;
		switch (n) {
		case 2:
			arg = resArgs(c);

			if (ok = arg[0].isGeoPoint() && arg[1].isNumberValue()) {

				GeoPoint2 point = (GeoPoint2) arg[0];

				int size = (int) ((NumberValue) arg[1]).getDouble();

				point.setPointSize(size);
				point.updateRepaint();

				
				return;
			} else if (!ok)
				throw argErr(app, c.getName(), arg[0]);
			else
				throw argErr(app, c.getName(), arg[1]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
