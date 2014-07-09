package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumberValue;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.kernelND.GeoConicNDConstants;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.MyError;

/**
 * Ellipse[ <GeoPoint>, <GeoPoint>, <NumberValue> ]
 */
public class CmdEllipseHyperbola extends CommandProcessor {
	
	protected int type; // ellipse or hyperbola

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 * @param type ellipse/hyperbola
	 */
	public CmdEllipseHyperbola(Kernel kernel, int type) {
		super(kernel);
		this.type = type;
	}

	@Override
	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 3:
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoPoint()))
					&& (ok[2] = (arg[2] instanceof GeoNumberValue))) {
				if (type == GeoConicNDConstants.CONIC_HYPERBOLA){
					return new GeoElement[]	{ getAlgoDispatcher().Hyperbola(c.getLabel(),
							(GeoPoint) arg[0], (GeoPoint) arg[1],
							(GeoNumberValue) arg[2]) };
				}
				return new GeoElement[]	{ getAlgoDispatcher().Ellipse(c.getLabel(),
						(GeoPoint) arg[0], (GeoPoint) arg[1],
						(GeoNumberValue) arg[2]) };
			} else if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoPoint()))
					&& (ok[2] = (arg[2].isGeoPoint()))) {
				GeoElement[] ret = { ellipse(c.getLabel(), (GeoPointND) arg[0],
								(GeoPointND) arg[1], (GeoPointND) arg[2]) };
				return ret;
			} else {
				throw argErr(app, c.getName(), getBadArg(ok,arg));
			}
			
		case 4:
			arg = resArgs(c);
			
			GeoElement[] ret = process4(c, arg, ok);
			
			if (ret != null){
				return ret;
			}

			// syntax error
			throw argErr(app, c.getName(), getBadArg(ok, arg));

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
	
	
	/**
	 * @param label label
	 * @param a first focus
	 * @param b second focus
	 * @param c point on ellipse
	 * @return ellipse
	 */
	protected GeoElement ellipse(String label, GeoPointND a, GeoPointND b, GeoPointND c){
		return getAlgoDispatcher().EllipseHyperbola(label, a, b, c, type);
	}
	
	
	/**
	 * process lwhen 4 arguments
	 * @param c command
	 * @param arg arguments
	 * @param ok ok array
	 * @return result (if one)
	 * @throws MyError in 2D, not possible with 4 args
	 */
	protected GeoElement[] process4(Command c, GeoElement[] arg, boolean[] ok) throws MyError{
		throw argNumErr(app, c.getName(), 4);
	}
}
