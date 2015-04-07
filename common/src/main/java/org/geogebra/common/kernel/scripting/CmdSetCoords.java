package org.geogebra.common.kernel.scripting;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CmdScripting;
import org.geogebra.common.kernel.geos.AbsoluteScreenLocateable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoTurtle;
import org.geogebra.common.kernel.geos.GeoVec3D;
import org.geogebra.common.kernel.geos.GeoVector;
import org.geogebra.common.main.MyError;

/**
 * SetCoords
 */
public class CmdSetCoords extends CmdScripting {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSetCoords(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected final void perform(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];

		switch (n) {
		case 3:
			arg = resArgs(c);
			// we don't want to change coords unless the point is free or
			// Point[path/region]
			if ((ok[0] = (arg[0] instanceof GeoVec3D && arg[0].isMoveable()))
					&& (ok[1] = (arg[1].isGeoNumeric()))
					&& (ok[2] = (arg[2].isGeoNumeric()))) {

				double x = ((GeoNumeric) arg[1]).getDouble();
				double y = ((GeoNumeric) arg[2]).getDouble();

				GeoElement geo = arg[0];

				if (geo.isGeoPoint()) {
					((GeoPoint) geo).setCoords(x, y, 1);
					geo.updateRepaint();
				} else if (geo.isGeoVector()) {
					((GeoVector) geo).setCoords(x, y, 0);
					geo.updateRepaint();
				} else
					throw argErr(app, c.getName(), arg[0]);

				return;

			} else if ((ok[0] = (arg[0].isGeoTurtle()))
					&& (ok[1] = (arg[1].isGeoNumeric()))
					&& (ok[2] = (arg[2].isGeoNumeric()))) {
				GeoTurtle geo = (GeoTurtle) arg[0];
				double x = ((GeoNumeric) arg[1]).getDouble();
				double y = ((GeoNumeric) arg[2]).getDouble();
				geo.setCoords(x, y);
				return;
			} else if ((ok[0] = (arg[0] instanceof AbsoluteScreenLocateable))
					&& (ok[1] = (arg[1].isGeoNumeric()))
					&& (ok[2] = (arg[2].isGeoNumeric()))) {
				double x = ((GeoNumeric) arg[1]).getDouble();
				double y = ((GeoNumeric) arg[2]).getDouble();

				AbsoluteScreenLocateable asl = (AbsoluteScreenLocateable) arg[0];

				if (asl.isAbsoluteScreenLocActive()) {
					asl.setAbsoluteScreenLoc((int) x, (int) y);
				} else {
					asl.setRealWorldLoc(x, y);
				}

				asl.updateRepaint();

				return;

			} else if (!ok[0]) {
				throw argErr(app, c.getName(), arg[0]);
			} else if (!ok[1]) {
				throw argErr(app, c.getName(), arg[1]);
			} else {
				throw argErr(app, c.getName(), arg[2]);
			}

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
