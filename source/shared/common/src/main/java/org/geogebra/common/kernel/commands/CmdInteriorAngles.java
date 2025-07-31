package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.main.MyError;

/**
 * InteriorAngles[ &lt;GeoPolygon&gt; ]
 */
public class CmdInteriorAngles extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdInteriorAngles(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		if (n == 1) {
			GeoElement[] arg = resArgs(c, info);
			if (arg[0].isGeoPolygon()) {
				return process(c.getLabels(), (GeoPolygon) arg[0]);
			}
			throw argErr(c, arg[0]);
		}
		throw argNumErr(c);
	}

	/**
	 * 
	 * @param labels
	 *            labels
	 * @param poly
	 *            polygon
	 * @return interior angles for the polygon
	 */
	protected GeoElement[] process(String[] labels, GeoPolygon poly) {
		return getAlgoDispatcher().angles(labels, poly, true);
	}

}