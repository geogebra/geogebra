package org.geogebra.common.kernel.scripting;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.commands.CmdScripting;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.geos.PointProperties;
import org.geogebra.common.kernel.kernelND.GeoPolyhedronInterface;
import org.geogebra.common.main.MyError;

/**
 * SetPointSize
 */
public class CmdSetPointSize extends CmdScripting {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSetPointSize(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected final void perform(Command c) throws MyError {
		int n = c.getArgumentNumber();
		switch (n) {
		case 2:
			arg = resArgs(c);

			boolean ok = false;
			if (arg[1] instanceof NumberValue) {
				ok = true;

				if (arg[0] instanceof PointProperties) {

					int size = (int) ((NumberValue) arg[1]).getDouble();

					if (size > 0) {
						arg[0].setEuclidianVisibleIfNoConditionToShowObject(true);
						((PointProperties) arg[0]).setPointSize(size);
					} else {
						arg[0].setEuclidianVisibleIfNoConditionToShowObject(false);
					}
					arg[0].updateRepaint();

					return;
				}

				if (arg[0] instanceof GeoPolyhedronInterface) {

					GeoPolyhedronInterface poly = (GeoPolyhedronInterface) arg[0];

					int size = (int) ((NumberValue) arg[1]).getDouble();

					poly.setPointSizeOrVisibility(size);

					return;
				}

				if (arg[0].isGeoPolygon()) {

					GeoPolygon poly = (GeoPolygon) arg[0];

					int size = (int) ((NumberValue) arg[1]).getDouble();

					poly.setPointSizeOrVisibility(size);

					return;
				}
			}

			if (!ok) {
				throw argErr(app, c.getName(), arg[1]);
			}

			throw argErr(app, c.getName(), arg[0]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
