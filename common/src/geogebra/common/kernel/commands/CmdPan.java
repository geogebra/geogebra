package geogebra.common.kernel.commands;

import geogebra.common.euclidian.EuclidianViewInterface2D;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.main.MyError;
import geogebra.common.euclidian.EuclidianController;
import geogebra.common.kernel.AbstractKernel;

/**
 *Pan
 */
public class CmdPan extends CmdScripting {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdPan(AbstractKernel kernel) {
		super(kernel);
	}

	final public void perform(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean ok;
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c);
			if (ok = arg[0].isGeoNumeric() && arg[1].isGeoNumeric()) {

				GeoNumeric x = (GeoNumeric) arg[0];
				GeoNumeric y = (GeoNumeric) arg[1];
				EuclidianViewInterface2D ev = (EuclidianViewInterface2D)app.getEuclidianView();
				ev.rememberOrigins();
				ev.setCoordSystemFromMouseMove((int) x.getDouble(), -(int) y
						.getDouble(), EuclidianController.MOVE_VIEW);

				
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
