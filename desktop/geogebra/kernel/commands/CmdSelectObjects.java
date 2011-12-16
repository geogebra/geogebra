package geogebra.kernel.commands;

import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CmdScripting;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.MyError;
import geogebra.kernel.Kernel;

/**
 *SelectObjects
 */
class CmdSelectObjects extends CmdScripting {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSelectObjects(Kernel kernel) {
		super(kernel);
	}

	final public void perform(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		

		app.clearSelectedGeos();

		if (n > 0) {
			arg = resArgs(c);
			for (int i = 0; i < n; i++) {
				if ((arg[i].isGeoElement())) {
					GeoElement geo = (GeoElement) arg[i];
					app.addSelectedGeo(geo, true);
				}
			}

		}
		return;

	}
}
