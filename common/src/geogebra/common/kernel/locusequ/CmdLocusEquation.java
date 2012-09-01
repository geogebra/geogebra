package geogebra.common.kernel.locusequ;


import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.algos.AlgoLocus;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoLocus;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.implicit.GeoImplicitPoly;
import geogebra.common.main.MyError;


/**
 * LocusEquation[ <GeoLocus> ] LocusEquation[ <GeoPoint>, <GeoPoint> ]
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

		switch (n) {
		case 1:
			if (arg[0].isGeoLocus() &&
					arg[0].getParentAlgorithm() != null &&
					arg[0].getParentAlgorithm().getClassName() == Algos.AlgoLocus) {
				GeoLocus locus = (GeoLocus) arg[0];
				AlgoLocus algo = (AlgoLocus) locus.getParentAlgorithm();
				locusPoint = algo.getLocusPoint();
				movingPoint = algo.getMovingPoint();
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
				throw argErr(app, c.getName(), getBadArg(ok,arg));
			}
			break;
			
		default:
			throw argNumErr(app, c.getName(), n);
		}
		
		return new GeoElement[] { LocusEquation(c.getLabel(), locusPoint, movingPoint) };
	}
	
	/**
	 * locus equation for Q dependent on P.
	 */
	final public GeoImplicitPoly LocusEquation(String label, GeoPoint locusPoint, GeoPoint movingPoint) {
		if (movingPoint.getPath() == null || locusPoint.getPath() != null || !movingPoint.isParentOf(locusPoint))
			return null;
		AlgoLocusEquation algo = new AlgoLocusEquation(cons, locusPoint, movingPoint);
		GeoImplicitPoly poly = algo.getPoly();
		
		poly.setLabel(label);
		return poly;
	}


}