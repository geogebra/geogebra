package org.geogebra.common.kernel.locusequ;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoLocus;
import org.geogebra.common.kernel.arithmetic.BooleanValue;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLocus;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.implicit.GeoImplicit;
import org.geogebra.common.main.MyError;

/**
 * LocusEquation[ <GeoLocus> ] LocusEquation[ <GeoPoint>, <GeoPoint> ]
 * LocusEquation[ <Boolean Expression>, <Free Point> ] TODO: add this syntax to
 * command.properties
 */
public class CmdLocusEquation extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdLocusEquation(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg = resArgs(c);
		GeoPoint locusPoint, movingPoint;
		GeoElement implicitLocus;

		switch (n) {
		case 1:
			if (arg[0] instanceof GeoLocus
					&& arg[0].getParentAlgorithm() != null
					&& arg[0].getParentAlgorithm() instanceof AlgoLocus) {
				GeoLocus locus = (GeoLocus) arg[0];
				AlgoLocus algo = (AlgoLocus) locus.getParentAlgorithm();
				locusPoint = (GeoPoint) algo.getLocusPoint();
				movingPoint = (GeoPoint) algo.getMovingPoint();
			} else {
				throw argErr(app, c.getName(), arg[0]);
			}
			break;

		case 2:
			if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoPoint()))) {
				locusPoint = (GeoPoint) arg[0];
				movingPoint = (GeoPoint) arg[1];
			} else {
				if ((ok[0] = (arg[0] instanceof BooleanValue))
						// second parameter should be a free point
						&& (ok[1] = (arg[1].isGeoPoint() && arg[1]
								.getParentAlgorithm() == null))) {
					implicitLocus = arg[0];
					movingPoint = (GeoPoint) arg[1];
					return new GeoElement[] { LocusEquation(c.getLabel(),
							implicitLocus, movingPoint).toGeoElement() };

				} // else
				throw argErr(app, c.getName(), getBadArg(ok, arg));
			}
			break;

		default:
			throw argNumErr(app, c.getName(), n);
		}

		return new GeoElement[] { LocusEquation(c.getLabel(), locusPoint,
				movingPoint).toGeoElement() };
	}

	/**
	 * locus equation for Q dependent on P.
	 */
	final public GeoImplicit LocusEquation(String label, GeoPoint locusPoint,
			GeoPoint movingPoint) {
		if (movingPoint.getPath() == null || locusPoint.getPath() != null
				|| !movingPoint.isParentOf(locusPoint))
			return null;
		AlgoLocusEquation algo = new AlgoLocusEquation(cons, locusPoint,
				movingPoint);
		GeoImplicit poly = algo.getPoly();

		poly.setLabel(label);
		return poly;
	}

	/**
	 * locus equation for computing implicit locus
	 */
	final public GeoImplicit LocusEquation(String label,
			GeoElement implicitLocus, GeoPoint movingPoint) {
		AlgoLocusEquation algo = new AlgoLocusEquation(cons, implicitLocus,
				movingPoint);
		GeoImplicit poly = algo.getPoly();

		poly.setLabel(label);
		return poly;
	}

}