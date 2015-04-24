package org.geogebra.common.kernel.scripting;

import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CmdScripting;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.MyError;

/**
 * Pan
 */
public class CmdPan extends CmdScripting {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdPan(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected final GeoElement[] perform(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean ok;

		switch (n) {
		case 2:
			GeoElement[] arg = resArgs(c);
			if ((ok = arg[0].isGeoNumeric()) && arg[1].isGeoNumeric()) {

				GeoNumeric x = (GeoNumeric) arg[0];
				GeoNumeric y = (GeoNumeric) arg[1];
				EuclidianViewInterfaceCommon ev = app.getActiveEuclidianView();
				ev.rememberOrigins();
				ev.translateCoordSystemInPixels((int) x.getDouble(),
						-(int) y.getDouble(), 0, EuclidianController.MOVE_VIEW);

				return arg;
			} else if (!ok)
				throw argErr(app, c.getName(), arg[0]);
			else
				throw argErr(app, c.getName(), arg[1]);
		case 3:
			arg = resArgs(c);
			if ((ok = arg[0].isGeoNumeric()) && arg[1].isGeoNumeric()
					&& arg[2].isGeoNumeric()) {

				GeoNumeric x = (GeoNumeric) arg[0];
				GeoNumeric y = (GeoNumeric) arg[1];
				GeoNumeric z = (GeoNumeric) arg[2];
				EuclidianViewInterfaceCommon ev = app.getActiveEuclidianView();
				ev.rememberOrigins();
				ev.translateCoordSystemInPixels((int) x.getDouble(),
						-(int) y.getDouble(), (int) z.getDouble(),
						EuclidianController.MOVE_VIEW);

				return arg;
			} else if (!ok)
				throw argErr(app, c.getName(), arg[0]);
			else
				throw argErr(app, c.getName(), arg[1]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
