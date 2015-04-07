package org.geogebra.common.kernel.locusequ;


import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.implicit.GeoImplicitPoly;
import org.geogebra.common.main.MyError;


/**
 * Envelope [< Object >, <Mover point>]
 */
public class CmdEnvelope extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdEnvelope(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg = resArgs(c);
		GeoPoint movingPoint;
		GeoElement linear;

		switch (n) {
		case 2:
			if ((ok[0] = (arg[0].isGeoElement()))
					&& (ok[1] = (arg[1].isGeoPoint()))) {
				linear = (GeoElement) arg[0];
				movingPoint = (GeoPoint) arg[1];
			} else {
				throw argErr(app, c.getName(), getBadArg(ok,arg));
			}
			break;
			
		default:
			throw argNumErr(app, c.getName(), n);
		}
		
		return new GeoElement[] { Envelope(c.getLabel(), linear, movingPoint) };
	}
	
	/**
	 * locus equation for Q dependent on P.
	 */
	final public GeoImplicitPoly Envelope(String label, GeoElement linear, GeoPoint movingPoint) {
		// TODO: add check here if linear is a correct input
		if (movingPoint.getPath() == null || !movingPoint.isParentOf(linear))
			return null;
		AlgoEnvelope algo = new AlgoEnvelope(cons, linear, movingPoint);
		GeoImplicitPoly poly = algo.getPoly();
		
		poly.setLabel(label);
		return poly;
	}


}