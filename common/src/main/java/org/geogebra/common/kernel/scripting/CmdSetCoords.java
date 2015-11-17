package org.geogebra.common.kernel.scripting;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CmdScripting;
import org.geogebra.common.kernel.geos.AbsoluteScreenLocateable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoTurtle;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;
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
	protected final GeoElement[] perform(Command c) throws MyError {
		int n = c.getArgumentNumber();

		switch (n) {
		case 3:
			GeoElement[] arg = resArgs(c);
			// we don't want to change coords unless the point is free or
			// Point[path/region]

			double x = getDouble(arg[1], c);
			double y = getDouble(arg[2], c);
			
			if(!setCoords(arg[0],x,y)){
				throw argErr(app, c.getName(), arg[0]);
			}
			return arg;
		case 4:
			arg = resArgs(c);
			// we don't want to change coords unless the point is free or
			// Point[path/region]

			x = getDouble(arg[1], c);
			y = getDouble(arg[2], c);
			double z = getDouble(arg[4], c);

			if (!setCoords(arg[0], x, y, z)) {
				throw argErr(app, c.getName(), arg[0]);
			}
			return arg;
		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

	private double getDouble(GeoElement geo, Command c) {
		if (!geo.isNumberValue()) {
			throw argErr(app, c.getName(), geo);
		}
		return geo.evaluateDouble();
	}

	/**
	 * @param geo
	 *            element
	 * @param x
	 *            x-coord
	 * @param y
	 *            y-coord
	 * @return whether it was successful
	 */
	public static boolean setCoords(GeoElement geo, double x, double y) {
		if (geo.isMoveable()) {
			if (geo.isGeoPoint()) {
				((GeoPointND) geo).setCoords(x, y, 1);
				geo.updateRepaint();
				return true;
			} else if (geo.isGeoVector()) {
				((GeoVectorND) geo).setCoords(x, y, 0);
				geo.updateRepaint();
				return true;
			} else
				return false;

		} else if (geo.isGeoTurtle()) {

			((GeoTurtle) geo).setCoords(x, y);
			return true;
		} else if (geo instanceof AbsoluteScreenLocateable) {

			AbsoluteScreenLocateable asl = (AbsoluteScreenLocateable) geo;

			if (asl.isAbsoluteScreenLocActive()) {
				asl.setAbsoluteScreenLoc((int) x, (int) y);
			} else {
				asl.setRealWorldLoc(x, y);
			}

			asl.updateRepaint();
		}
		return false;
	}

	public static boolean setCoords(GeoElement geo, double x, double y, double z) {
		if (geo.isMoveable() && geo instanceof GeoPointND) {
			((GeoPointND) geo).setCoords(x, y, z, 1);

		}
		if (geo.isMoveable() && geo instanceof GeoVectorND) {
			((GeoVectorND) geo).setCoords(x, y, z, 0);

		}
		if (geo.isMoveable() && geo instanceof GeoLine) {
			((GeoLine) geo).setCoords(x, y, z);

		}
		// ignore z-coord otherwise
		return setCoords(geo, x, y);
	}
}
