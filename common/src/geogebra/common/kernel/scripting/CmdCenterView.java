package geogebra.common.kernel.scripting;

import geogebra.common.euclidian.EuclidianViewInterfaceSlim;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CmdScripting;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.MyError;

/**
 * ZoomIn
 */
public class CmdCenterView extends CmdScripting {
	/**
	 * Creates new ZooomOut command
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdCenterView(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected final void perform(Command c) throws MyError {
		int n = c.getArgumentNumber();

		switch (n) {
		case 1:
			arg = resArgs(c);
			if (arg[0].isGeoPoint()) {
				GeoPointND p = (GeoPointND) arg[0];

				EuclidianViewInterfaceSlim ev = app.getActiveEuclidianView();
				ev.centerView(p);
				return;

			}
			throw argErr(app, c.getName(), arg[0]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
