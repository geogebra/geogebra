package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.kernel.geos.LabelManager;
import geogebra.common.main.MyError;

/**
 *Rename
 */
public class CmdRename extends CmdScripting {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdRename(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public void perform(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c);
			if (arg[1].isGeoText()) {

				GeoElement geo = (GeoElement) arg[0];

				if (LabelManager.checkName(geo, ((GeoText) arg[1]).getTextString())) {
					geo.rename(((GeoText) arg[1]).getTextString());
					geo.updateRepaint();

					
					return;
				} else {
					throw argErr(app, c.getName(), arg[1]);
				}
			} else
				throw argErr(app, c.getName(), arg[1]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
