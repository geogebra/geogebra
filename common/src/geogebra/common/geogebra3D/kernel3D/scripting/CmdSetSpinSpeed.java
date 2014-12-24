package geogebra.common.geogebra3D.kernel3D.scripting;

import geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CmdScripting;
import geogebra.common.kernel.geos.GeoNumberValue;
import geogebra.common.main.MyError;

/**
 * ZoomIn
 */
public class CmdSetSpinSpeed extends CmdScripting {
	/**
	 * Creates new ZooomOut command
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSetSpinSpeed(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected final void perform(Command c) throws MyError {
		int n = c.getArgumentNumber();

		switch (n) {
		case 1:
			arg = resArgs(c);
			if (arg[0] instanceof GeoNumberValue) {
				GeoNumberValue v = (GeoNumberValue) arg[0];

				EuclidianView3D view3D = (EuclidianView3D) app
						.getEuclidianView3D();

				view3D.setRotContinueAnimation(0, v.getDouble() * 0.01);

				return;

			}

			throw argErr(app, c.getName(), arg[0]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
