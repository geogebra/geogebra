package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Transform;
import org.geogebra.common.kernel.TransformDilate;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.Dilateable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.main.MyError;

/**
 * Dilate[ <GeoPoint>, <NumberValue>, <GeoPoint> ] Dilate[ <GeoLine>,
 * <NumberValue>, <GeoPoint> ] Dilate[ <GeoConic>, <NumberValue>, <GeoPoint> ]
 * Dilate[ <GeoPolygon>, <NumberValue>, <GeoPoint> ]
 */
public class CmdDilate extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdDilate(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c) throws MyError {
		String label = c.getLabel();
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c);

			// dilate point, line or conic
			if ((ok[0] = (arg[0] instanceof Dilateable || arg[0].isGeoPolygon() || arg[0]
					.isGeoList()))
					&& (ok[1] = (arg[1] instanceof GeoNumberValue))) {
				NumberValue phi = (GeoNumberValue) arg[1];
				return Dilate(label, arg[0], phi);
			}
			if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			throw argErr(app, c.getName(), arg[1]);

		case 3:
			arg = resArgs(c);

			// dilate point, line or conic
			if ((ok[0] = (arg[0] instanceof Dilateable || arg[0].isGeoList()))
					&& (ok[1] = (arg[1] instanceof GeoNumberValue))
					&& (ok[2] = (arg[2].isGeoPoint()))) {
				NumberValue phi = (GeoNumberValue) arg[1];
				return Dilate(label, arg[0], phi, arg[2]);
			}
			if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			throw argErr(app, c.getName(), arg[1]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

	/**
	 * dilate geoRot by r from origin
	 */
	final private GeoElement[] Dilate(String label, GeoElement geoDil,
			NumberValue r) {
		Transform t = new TransformDilate(cons, r);
		return t.transform(geoDil, label);
	}

	/**
	 * 
	 * @param label
	 *            label
	 * @param geoDil
	 *            dilated geo
	 * @param r
	 *            number value
	 * @param point
	 *            point
	 * @return result of dilate of geoDil about r, point
	 */
	protected GeoElement[] Dilate(String label, GeoElement geoDil,
			NumberValue r, GeoElement point) {

		return getAlgoDispatcher().Dilate(label, geoDil, r, (GeoPoint) point);
	}

}
