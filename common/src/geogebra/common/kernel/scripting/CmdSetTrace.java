package geogebra.common.kernel.scripting;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.Traceable;
import geogebra.common.main.MyError;

/**
 *SetFixed
 */
public class CmdSetTrace extends CmdScripting {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSetTrace(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected
	final void perform(Command c) throws MyError {
		int n = c.getArgumentNumber();

		switch (n) {
		case 2:
			arg = resArgs(c);
			if (arg[1].isGeoBoolean()) {

				GeoElement geo = arg[0];

				if (geo != null && geo.isTraceable()) {
					((Traceable) geo).setTrace(((GeoBoolean)arg[1]).getBoolean());
					geo.updateRepaint();
				}
				
				return;
			}
			throw argErr(app, c.getName(), arg[1]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
